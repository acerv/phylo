
package utility;

/* Conta il tempo impiegato per una esecuzione.
 * Soluzione trovata qui: http://www.rgagnon.com/javadetails/java-0132.html*/
public final class Timer {
  private long start;
  private long end;

  public Timer() {
    reset();
  }

  public void start() {
    start = System.currentTimeMillis();
  }

  public void end() {
    end = System.currentTimeMillis();
  }

  public String duration(){
    long mills = end - start;
    int seconds = (int) ((mills / 1000) % 60);
    int minutes = (int) ((mills / 1000) / 60);
    String time;
    
    if(seconds == 0 && minutes == 0)
        time = mills+"ms";
    else
        time = minutes+" m "+seconds+" s ";

    return time;
  }

  public void reset() {
    start = 0;
    end   = 0;
  }
}