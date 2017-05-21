
package algorithm.phylip;

import utility.GenerateOddNumber;
import algorithm.AlgorithmException;
import algorithm.phylip.configuration.NeighborConfiguration;
import algorithm.phylip.exception.PhylipException;
import informations.Infos;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Implementa le funzionalità di Neighbor-Joining
 * @author Cervesato Andrea - sawk @ gmail.com
 */
public class Neighbor extends PhylipExecutable
{
    // metodi che si possono usare con Neighbor
    public static int NEIGHBOR_TREE = 0;
    public static int UPGMA_TREE = 1;

    // Nome del programma
    private static String NAME = Infos.NEIGHBOR_EXE;

    // File di input
    private static String INFILE_NAME = "infile";

    // File di output
    private static String OUTFILE_NAME = "outtree";

    // Tutte le opzioni supportate da neighbor
    private static ArrayList<String> OPTIONS =
            new ArrayList<String>(
            Arrays.asList
    (
    "N", //      Neighbor-joining or UPGMA tree?  Neighbor-joining
    "O", //                        Outgroup root?  No, use as outgroup species  1
         //                        Prevede un valore numerico
    "L", //         Lower-triangular data matrix?  No
    "R", //         Upper-triangular data matrix?  No
    "S", //                        Subreplicates?  No
    "J", //     Randomize input order of species?  No. Use input order
         //     Prevede due valori numerici
    "M", //           Analyze multiple data sets?  No
         //           Prevede un valore numerico
    "0", //   Terminal type (IBM PC, ANSI, none)?  ANSI
    "1", //    Print out the data at start of run  No
    "2", //  Print indications of progress of run  Yes
    "3", //                        Print out tree  Yes
    "4"  //       Write out trees onto tree file?  Yes
    ));

    /**
     * Configura this.PROGRAM_NAME nella classe Phylip
     * @param cfg 
     * @throws IOException Viene lanciata una eccezione quando la cartella di lavoro non è leggibile o scrivibile
     */
    public Neighbor(NeighborConfiguration cfg)
            throws IOException
    {
        super(NAME, Infos.NEIGHBOR, INFILE_NAME, OUTFILE_NAME, OPTIONS);
        this.cfg = cfg;
    }

    // Configurazione
    private NeighborConfiguration cfg;

    /**
     * Ritorna la configurazione usata
     * @return
     */
    public NeighborConfiguration getConfiguration()
    {
        return cfg;
    }

    @Override
    public void exec()
            throws AlgorithmException, IOException, InterruptedException, PhylipException
    {
        ArrayList<String> config = new ArrayList<String>();

        if ( cfg.tree == UPGMA_TREE )
        {
            config.add("N");
        }
        else
        {
            if ( cfg.outgroupSpece > 0 )
            {
                config.add("O");
                config.add(Integer.toString(cfg.outgroupSpece));
            }
        }

        // settando il multiple data sets viene già usato randomize
        if ( cfg.multipleDataSets > 0 )
        {
            // Numero dispari casuale
            int seed = GenerateOddNumber.get(653);

            config.add("M");
            config.add(Integer.toString(cfg.multipleDataSets));
            config.add(Integer.toString(seed));
        }
        else if ( cfg.randomizeInput )
        {
            // Numero dispari casuale
            int seed = GenerateOddNumber.get(653);

            config.add("J");
            config.add(Integer.toString(seed));
        }

        // Eseguo il programma
        this.execProgram(config);
    }
}
