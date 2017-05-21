package utility;

/**
 * Used for debugging output
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class Debugger
{
    static private boolean debug = false;
    static public void setDebug(boolean mode) { debug = mode; }
    static public boolean getDebug(){ return debug; }

    static public void print(Object msg) {
        if(debug) System.out.print(msg);
    }

    static public void println(Object msg){
        if(debug) System.out.println(msg);
    }

    static public void println(){
        if(debug) System.out.println();
    }

    static public void printf(String format, Object msg){
        if(debug) System.out.printf(format, msg);
    }
}
