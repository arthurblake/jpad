package jpad;

class ReturnEvent extends Event {
  /**
   * The handler that handled this event --can reference src (proxy) and dest (original object)
   * from this.
   */
  Handler handler;
  
  /**
   * Object that was returned.  This may be null
   * if the return type was void, or if the method in fact returned null...
   */
  final Object returnObject;
  
  /**
   * Method call event for method that returned.
   */
  final MethodCallEvent methodEvent;
  
  ReturnEvent(Handler h, MethodCallEvent m, Object r)
  {
    super();
    handler = h;
    returnObject = r;
    methodEvent = m;
  }
  
  public String toString()
  {
    return "method " + this.methodEvent.method.getName() + " returned on " +
      this.handler.info;
  }
}
