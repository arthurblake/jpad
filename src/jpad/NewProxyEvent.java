package jpad;

public class NewProxyEvent extends Event {

  // previous event for method call return that caused a new proxy to be created.
  // (through this event, the MethodCallEvent can be accessed)
  final ReturnEvent returnEvent;
  final Proxy proxy;
  
  public NewProxyEvent(ReturnEvent r, Proxy p)
  {
    super();
    proxy=p;
    returnEvent = r;
  }
  
  
}
