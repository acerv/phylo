package utility;

import java.io.IOException;

/**
 * Kill a process running into the Operative System
 * @author Cervesato Andrea
 */
public class KillPid {
    /* Command to execute */
    private static String cmd;
    private static String pid;

    /* Windows */
    private static String taskkill = "taskkill /F /PID ";

    /* Linux */
    private static String kill = "kill -9 ";

    public KillPid(String pid, boolean windows) {
        if(windows) {
            cmd = taskkill + pid;
        }
        else {
            cmd = kill + pid;
        }

        KillPid.pid = pid;
    }

    public void kill() throws IOException, InterruptedException {
        Runtime.getRuntime().exec(cmd).waitFor();
        Debugger.println("> Process "+pid+" has been killed");
    }
}
