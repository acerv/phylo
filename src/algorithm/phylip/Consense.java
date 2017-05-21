
package algorithm.phylip;

import algorithm.AlgorithmException;
import algorithm.phylip.configuration.ConsenseConfiguration;
import algorithm.phylip.exception.PhylipException;
import informations.Infos;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Implementa le funzionalità di consense
 * @author Cervesato Andrea - sawk @ gmail.com
 */
public class Consense extends PhylipExecutable
{
    // Nome del programma
    private static String NAME = Infos.CONSENSE_EXE;

    // Nome del file di input
    private static String INFILE_NAME = "intree";

    // Nome del file di output
    private static String OUTFILE_NAME = "outtree";

    // Tipi di consensus tree
    public static int MAJORITY_RULE_EXTENDED = 0;
    public static int STRICT = 1;
    public static int MAJORITY_RULE = 2;
    public static int ML_CONSENSUS = 3;
    
    // Tutte le opzioni di consense
    private static ArrayList<String> OPTIONS =
            new ArrayList<String>(
            Arrays.asList
    (
    "C", //        Consensus type (MRe, strict, MR, Ml):  Majority rule (extended)
         //        Prevede 4 volte C per 4 tipi diversi
    "O", //                               Outgroup root:  No, use as outgroup species  1
         //                               Prevede un valore numerico
    "R", //               Trees to be treated as Rooted:  No
    "T", //          Terminal type (IBM PC, ANSI, none):  ANSI
    "1", //               Print out the sets of species:  Yes
    "2", //        Print indications of progress of run:  Yes
    "3", //                              Print out tree:  Yes
    "4"  //              Write out trees onto tree file:  Yes
    ));

    /**
     * Configura this.PROGRAM_NAME nella classe Phylip
     * @param cfg 
     * @throws IOException Viene lanciata una eccezione quando la cartella di lavoro non è leggibile o scrivibile
     */
    public Consense(ConsenseConfiguration cfg)
            throws IOException
    {
        super(NAME, Integer.MAX_VALUE, INFILE_NAME, OUTFILE_NAME, OPTIONS);
        this.cfg = cfg;
    }

    // Configurazione
    ConsenseConfiguration cfg;

    // Insiemi di consenso che compaiono nell'albero
    private ConsensusSets sets;

    /**
     * Ritorna la configurazione
     * @return
     */
    public ConsenseConfiguration getConfiguration()
    {
        return cfg;
    }

    @Override
    public void exec()
            throws AlgorithmException, IOException, InterruptedException, PhylipException
    {
        ArrayList<String> config = new ArrayList<String>();

        // Tipi di consensus
        for ( int i = 0; i < cfg.consensusType; i++)
            config.add("C");

        // Albero da trattare come radicato
        if ( cfg.rooted )
        {
            config.add("R");
        }
        else
        {
            // Spece da non valutare
            if ( cfg.outgroupSpece >  0 )
            {
                config.add("O");
                config.add(Integer.toString(cfg.outgroupSpece));
            }
        }

        // Elimino l'albero dall'outfile
        config.add("3");

        // Eseguo il programma
        this.execProgram(config);

        // Carico gli insiemi
        sets = new ConsensusSets(getOutfile());
    }

    /**
     * Ritorna gli insiemi contenuti nell'albero di consenso generato
     * @return
     */
    public ConsensusSets getSets()
    {
        return this.sets;
    }
}
