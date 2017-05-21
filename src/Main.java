import java.net.URISyntaxException;
import java.util.Date;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import ui.PhyloForm;
import utility.Debugger;
import utility.LogStdStreams;


/**
 * @author Cervesato Andrea - sawk @ gmail.com
 */
public class Main
{
    public static void main(String[] args)
    {
        try
        {
            if(args.length == 0)
            {
                execPhylo();
            }
            else if(args[0].equals("--debug") || args[0].equals("-d"))
            {
                // Debug
                Debugger.setDebug(true);

                // Numero casuale
                Random random = new Random();
                int num = random.nextInt(5000);

                LogStdStreams.initializeErrorLogging("output"+num+".log",
                    "Phylo log ("+new Date()+")\n\n", true, true);

                execPhylo();
            }
            else if(args[0].equals("--verbose") || args[0].equals("-v"))
            {
                // Visualizza l'output di tutto
                Debugger.setDebug(true);

                execPhylo();
            }
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, "Main.java: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Esegue phylo
    private static void execPhylo() throws URISyntaxException
    {
        PhyloForm phylo = new PhyloForm();
        phylo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        phylo.pack();
        phylo.setLocationRelativeTo(null);
        phylo.setVisible(true);
    }
}
