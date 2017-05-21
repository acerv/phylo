package utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Returns a pid of a running process into the Operative System
 * @author Cervesato Andrea
 */
public class GetPid {
    private boolean windows = false;
    private static String name = "fitch.exe";

    /* command to execute */
    private static String cmd;

    /* LINUX-OSX */
    private static String pidof_lin = "pidof ";

    /* WINDOWS */
    private static String tasklist_win = "tasklist";

    public GetPid(String proc, boolean windows) {
        this.windows = windows;
        name = proc;
        
        if(windows) {
            cmd = tasklist_win;
        }
        else {
            cmd = pidof_lin + name;
        }
    }

    public String pid() throws IOException, InterruptedException {
        String pid = null;

        if(windows)
            pid = get_pid_win();
        else
            pid = get_pid_lin();

        return pid;
    }

    private static String get_pid_lin() throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(cmd);
        InputStreamReader isr = new InputStreamReader(proc.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        String pid = null;
        String line = null;

        while((line = br.readLine()) != null) {
            if(line.matches("[0-9]+")) {
                pid = line;
                break;
            }
        }

        // Valore di uscita ?
        int exitVal = proc.waitFor();
        if ( exitVal != 0 )
            System.out.println("\n> ERROR ON TAKING PID?!?!?\n");

        br.close();
        isr.close();
        proc.destroy();

        return pid;
    }

    private static String get_pid_win() throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(cmd);
        InputStreamReader isr = new InputStreamReader(proc.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        String pid = null;
        String line;
        
        while((line = br.readLine()) != null) {
            if(line.contains(name)) {
                String[] pid_s = line.split("\\s+");
                pid = pid_s[1];
                break;
            }
        }

        br.close();
        isr.close();

        // Valore di uscita ?
        int exitVal = proc.waitFor();
        if ( exitVal != 0 )
            System.out.println("\nERROR ON TAKING PID?!?!?\n");

        proc.destroy();

        return pid;
    }
}
