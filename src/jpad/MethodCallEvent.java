package jpad;

import java.lang.reflect.Method;

class MethodCallEvent extends Event {

  /**
   * The handler that handled this event --can reference src (proxy) and dest (original object)
   * from this.
   */
  Handler handler;    
  Method method;  // method called
  Object[] args;  // arguments passed to the method.
  
  MethodCallEvent(Handler h, Method method, Object[] args)
  {
    super();
    this.handler = h;
    this.method = method;
    this.args = args;
  }
  
  public String toString()
  {
    return "method " + this.method.getName() + " invoked on " +
      this.handler.info;
  }
}
