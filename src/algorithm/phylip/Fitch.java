package algorithm.phylip;

import utility.GenerateOddNumber;
import algorithm.AlgorithmException;
import algorithm.phylip.configuration.FitchConfiguration;
import algorithm.phylip.exception.PhylipException;
import informations.Infos;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Implementa le funzionalità di Fitch
 * @author Cervesato Andrea - sawk @ gmail.com
 */
public class Fitch extends PhylipExecutable
{
    // Metodi disponibili per fitch
    public static int FITCH_MARGOLIASH_METHOD = 0;
    public static int MINIMUM_EVOLUTION_METHOD = 1;

    // Nome del programma
    private static String NAME = Infos.FITCH_EXE;

    // File di input
    private static String INFILE_NAME = "infile";

    // File di output
    private static String OUTFILE_NAME = "outtree" ;
    
    /* Le opzioni supportate da fitch. Per completezza sono state messe tutte,
     * ma generalmente vengono usate D, P, -, O, J, M
     */
    private static ArrayList<String> OPTIONS =
            new ArrayList<String>(
            Arrays.asList
    (
        "D", //     Method (F-M, Minimum Evolution)?  Fitch-Margoliash
        "U", //                Search for best tree?  Yes
        "P", //                               Power?  2.00000
             //                               Prevede un parametro double
        "-", //     Negative branch lengths allowed?  No
        "O", //                       Outgroup root?  No, use as outgroup species  1
             //                       Prevede un valore numerico
        "L", //        Lower-triangular data matrix?  No
        "R", //        Upper-triangular data matrix?  No
        "S", //                       Subreplicates?  No
        "G", //               Global rearrangements?  No
        "J", //    Randomize input order of species?  No. Use input order
             //    Prevede due valori numerici
        "M", //          Analyze multiple data sets?  No
             //          Prevede un valore numerico
        "0", //  Terminal type (IBM PC, ANSI, none)?  ANSI
        "1", //   Print out the data at start of run  No
        "2", // Print indications of progress of run  Yes
        "3", //                       Print out tree  Yes
        "4"  //       Write out trees onto tree file?  Yes
    ));

    // Inizializzo la classe
    public Fitch(FitchConfiguration cfg) throws IOException {
        super(NAME, Infos.FITCH, INFILE_NAME, OUTFILE_NAME, OPTIONS);

        // Configuro la classe
        this.cfg = cfg;
    }

    // Configurazione
    private FitchConfiguration cfg;

    /**
     * Ritorna la configurazione usata per l'algoritmo
     * @return
     */
    public FitchConfiguration getConfiguration()
    {
        return cfg;
    }

    @Override
    public void exec()
            throws AlgorithmException, IOException, InterruptedException, PhylipException
    {
        ArrayList<String> config = new ArrayList<String>();

        if ( cfg.method == MINIMUM_EVOLUTION_METHOD )
        {
            config.add("D");
        }

        if (cfg.power != 2)
        {
            config.add("P");
            config.add(Double.toString(cfg.power));
        }

        if ( cfg.multipleDataSets > 0 && cfg.randomizeInputOrderTimes)
        {
            // Numero dispari casuale
            int seed = GenerateOddNumber.get(657);

            config.add("M");
            config.add(Integer.toString(cfg.multipleDataSets));
            config.add(Integer.toString(seed));
        }
        else if ( cfg.randomizeInputOrderTimes )
        {
            // Numero dispari casuale
            int seed = GenerateOddNumber.get(657);

            config.add("J");
            config.add(Integer.toString(seed));
            config.add(Integer.toString(1));
        }

        if ( cfg.outgroupSpece > 0 )
        {
            config.add("O");
            config.add(Integer.toString(cfg.outgroupSpece));
        }

        // Eseguo il programma
        this.execProgram(config);
    }
}
