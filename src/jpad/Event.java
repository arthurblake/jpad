package jpad;

import java.util.Date;

class Event {
  final long timeStamp = System.currentTimeMillis();
  final long nanoStamp = System.nanoTime();

  Event() {}
  
  public String toString() {
    return "Event generated on " + new Date(timeStamp);
  }
}
