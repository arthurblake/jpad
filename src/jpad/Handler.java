package jpad;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.Statement;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Maintain any needed intermediate state about the object being proxied to.
 * Generate async method lifecycle events.
 * Decide what downstream objects need proxies and create those new proxies
 * on demand as required.
 * 
 * @author arthur
 */
class Handler implements InvocationHandler
{
  /**
   * The interface types that we generally want to proxy.
   */
  private static final Class<?>[] proxyClasses = 
    // note that order is important here because some interfaces are sub interfaces of others
    // and we want to preferentially proxy the outermost extension of the interfaces

    new Class<?>[] {
    //  ResultSet.class,
      CallableStatement.class,
      PreparedStatement.class,
      Statement.class,
      Connection.class
    };
  
  // sequence to assign a unique id to each newly created Proxy 
  private static int sequence=0;
  
  // events are placed on this queue whenever they occur
  // in this way, the main application thread is impacted minimally
  // and event processing happens in another thread.
  static BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>();
  
  // the actual proxy that implements the specified interface type of the destination object.
  // this also implements the Proxy interface in this package so that we can easily know that it
  // is in fact an object that is being Proxied.
  final Proxy src;
  
  // the real object being proxied to
  final Object dest;
  
  // the proxied interface being implemented.
  final Class<?> interfaceType;
  
  // a unique id assigned to this proxy
  final int id;
  
  // information about this handler
  final String info;
  
  /**
   * Create a new Proxy for the given destination object.
   * @param returnEvent optional return event of method call that generated this new handler
   *  (this will be null for Connection creation events and non-null for all other types)
   * @param dest
   * @param interfaceType
   */
  Handler(ReturnEvent returnEvent, Object dest, Class<?> interfaceType)
  {
    this.dest = dest;
    this.interfaceType = interfaceType;
    this.id = ++sequence;
    
    info = interfaceType.getSimpleName() + " " + id;
    
    Class<?>[] interfaces = new Class<?>[] {interfaceType, Proxy.class};
    this.src = (Proxy) java.lang.reflect.Proxy.newProxyInstance(dest.getClass().getClassLoader(),
      interfaces, this);
    eventQueue.add(new NewProxyEvent(returnEvent, this.src));
  }
  
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
  {
    // TODO: properly handle wrapper interface!

    final Object returnObject;
    final MethodCallEvent firstEvent = new MethodCallEvent(this, method, args);
    
    eventQueue.add(firstEvent);
    try
    {
      returnObject = method.invoke(dest, args);  // invoke the real backing object
    }
    catch (Throwable t)
    {
      eventQueue.add(new ExceptionEvent(this, firstEvent, t));
      throw t;
    }

    final ReturnEvent returnEvent = new ReturnEvent(this, firstEvent, returnObject); 
    eventQueue.add(returnEvent);

    // see if the return object is another object that we would like to proxy 
    // (and that is not already being proxied)
    
    // TODO: might be faster to just unroll this loop
    if (returnObject != null && !(returnObject instanceof Proxy))
    {
      for (Class<?> cls : proxyClasses)
      {
        if (cls.isAssignableFrom(returnObject.getClass()))
        {
          return (new Handler(returnEvent, returnObject, cls)).src;
        }
      }
    }
    return returnObject;
  }
}
