package jpad;

public class ExceptionEvent extends Event {
  /**
   * The handler that handled this event --can reference src (proxy) and dest (original object)
   * from this.
   */
  Handler handler;
  
  /**
   * Exception that triggered this event.
   */
  final Throwable throwable;
  
  /**
   * Method call event for method that threw the exception.
   */
  final MethodCallEvent methodEvent;
  ExceptionEvent(Handler h, MethodCallEvent m, Throwable t)
  {
    super();
    this.handler = h;
    throwable = t;
    methodEvent = m;
  }
  
  public String toString()
  {
    return this.methodEvent.method.getName() + " threw exception on " +
      this.handler.info;
  }

}
