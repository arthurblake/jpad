package jpad;

import java.util.Date;

/**
 * TODO: test code just prints every event as fast as we can handle them
 * real event handler needs to do something useful.
 * 
 * TODO: strip events and pass them on to a second level processor?
 * 
 * stripping will make sure any mutable events are replaced by stand ins.  Immutable events
 * are safe to pass up the chain.
 * 
 * TODO: should this be an Observable pattern that any interested parties can register to
 * listen for the "stripped" events.
 * 
 * Event handler thread.
 * @author arthur
 */
public class EventHandler implements Runnable {

  boolean running;

  public void run() {
    running = true;
    Event event;
    try {
      while (running)
      {
        event = Handler.eventQueue.take();
        System.out.println(new Date(event.timeStamp) + " " + event);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
      running = false;
    }
  }
}
