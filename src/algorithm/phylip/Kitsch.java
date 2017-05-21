
package algorithm.phylip;

import utility.GenerateOddNumber;
import algorithm.AlgorithmException;
import algorithm.phylip.configuration.KitschConfiguration;
import algorithm.phylip.exception.PhylipException;
import informations.Infos;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Implementa le funzionalità di Kitsch
 * @author Cervesato Andrea - sawk @ gmail.com
 */
public class Kitsch extends PhylipExecutable
{
    // Metodi disponibili per fitch
    public static int FITCH_MARGOLIASH_METHOD = 0;
    public static int MINIMUM_EVOLUTION_METHOD = 1;

    // Nome del programma
    private static String NAME = Infos.KITSCH_EXE;

    // File di input
    private static String INFILE_NAME = "infile";

    // File di output
    private static String OUTFILE_NAME = "outtree" ;

    // Tutte le opzioni supportate da kitsch
    private static ArrayList<String> OPTIONS =
            new ArrayList<String>(
            Arrays.asList
    (
    "D", //     Method (F-M, Minimum Evolution)?  Fitch-Margoliash
    "U", //                Search for best tree?  Yes
    "P", //                               Power?  2.00000
         //                               Prevede un parametro
    "-", //     Negative branch lengths allowed?  No
    "L", //        Lower-triangular data matrix?  No
    "R", //        Upper-triangular data matrix?  No
    "S", //                       Subreplicates?  No
    "J", //    Randomize input order of species?  No. Use input order
         //    Prevede due valori numerici
    "M", //          Analyze multiple data sets?  No
         //          Prevede un valore numerico
    "0", //  Terminal type (IBM PC, ANSI, none)?  ANSI
    "1", //   Print out the data at start of run  No
    "2", // Print indications of progress of run  Yes
    "3", //                       Print out tree  Yes
    "4" //      Write out trees onto tree file?  Yes
    ));

    /**
     * Configura this.PROGRAM_NAME nella classe Phylip
     * @param cfg 
     * @throws IOException Viene lanciata una eccezione quando la cartella di lavoro non è leggibile o scrivibile
     */
    public Kitsch(KitschConfiguration cfg)
            throws IOException
    {
        super(NAME, Infos.KITSCH, INFILE_NAME, OUTFILE_NAME, OPTIONS);
        this.cfg = cfg;
    }

    // configurazione
    private KitschConfiguration cfg;

    /**
     * Ritorna la configurazione di Kitsch
     * @return
     */
    public KitschConfiguration getConfiguration()
    {
        return cfg;
    }

    @Override
    public void exec() throws AlgorithmException, IOException, InterruptedException, PhylipException
    {
        ArrayList<String> config = new ArrayList<String>();

        if ( cfg.method == MINIMUM_EVOLUTION_METHOD )
        {
            config.add("D");
        }

        if ( cfg.power != 2 )
        {
            config.add("P");
            config.add(Double.toString(cfg.power));
        }

        if ( cfg.multipleDataSets > 0 && cfg.randomizeInputOrderTimes )
        {
            // Numero dispari casuale
            int seed = GenerateOddNumber.get(657);

            config.add("M");
            config.add(Integer.toString(cfg.multipleDataSets));
            config.add(Integer.toString(seed));
        }
        // Se sono abilitati entrambi bisogna rimuovere e riconfigurare
        // il randomize perché viene già configurato da multiple data sets
        else if ( cfg.randomizeInputOrderTimes )
        {
            // Numero dispari casuale
            int seed = GenerateOddNumber.get(657);

            config.add("J");
            config.add(Integer.toString(seed));
            config.add(Integer.toString(1));
        }

        // Eseguo il programma
        this.execProgram(config);
    }
}
