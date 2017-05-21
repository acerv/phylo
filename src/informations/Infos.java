package informations;

import algorithm.GenericAlgorithm;
import algorithm.parsimony.big.BranchAndBoundExtended;
import algorithm.parsimony.big.LocalBranchAndBound;
import algorithm.parsimony.small.SankoffExtended;
import bootstrap.Bootstrapper;
import bootstrap.DefaultBootstrapper;
import distance.Distance;
import distance.Jaccard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import ui.PhyloForm;

/**
 * Raccoglie tutte le informazioni necessarie a Phylo
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class Infos
{
    // Autore
    public static String AUTHOR = "Cervesato Andrea";
    public static String AUTHOR_EMAIL = "sawk.ita@gmail.com";

    // Phylo
    public static String PHYLO_NAME = "Phylo";
    public static String PHYLO_VERSION = "1.7";

    // iText
    public static String ITEXT_NAME = "iText";
    public static String ITEXT_VERSION = "5.0.5";

    // Jdk
    public static String JDK_NAME = "JDK";
    public static String JDK_VERSION = "1.5u22";

    // Phylip
    public static String PHYLIP_NAME = "Phylip";
    public static String PHYLIP_VERSION = "3.69";
    public static String PHYLIP_WORKING_PATH = Infos.HOME+Infos.FILE_SEPARATOR+"phylip";

    // Tipo di sistema operativo
    public static String OS_NAME = System.getProperty("os.name").toLowerCase();
    public static String ARCH_NAME = System.getProperty("os.arch");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    // Cartella dei file temporanei
    public static final String TEMPORARY_PATH = System.getProperty("java.io.tmpdir") + FILE_SEPARATOR;

    // Cartella di home
    public static final String HOME = System.getProperty("user.home");


    
    /* *************************************************************************
     * Algoritmi filogenetici in Phylo. Qui vanno inserite le variabili relative*
     * all'algoritmo che si vuole aggiungere in Phylo. Queste variabili sono   *
     * principalmente usate dal wizard per la creazione di un esperimento,     *
     * il salvataggio degli esperimenti e l'esecuzione dei binary (specificati *
     * tag _EXE finale).                                                       *
     ***************************************************************************/
    public final static int FITCH = 0;
    public final static int KITSCH = 1;
    public final static int NEIGHBOR = 2;
    public final static int UPGMA = 3;
    public final static int SANKOFF = 4;
    public final static int LOCAL_BRANCH_AND_BOUND = 5;
    public final static int GLOBAL_BRANCH_AND_BOUND = 6;

    // Phylogenetic algorithms which require as input a matrix file
    public static String[] SUPPORTED_ALGORITHMS =
    {
        "Fitch-Margoliash", // FITCH = 0
        "Kitsch",           // KITSCH = 1
        "Neighbor-Joining", // NEIGHBOR = 2
        "UPGMA",             // UPGMA = 3
        "Sankoff (Extended)",
        "Local Branch & Bound (Extended)",
        "Global Branch & Bound (Extended)"
    };

    // Algorithms which require matrix file as input
    public static int[] STANDARD_ALGORITHMS =
    {
        FITCH,
        KITSCH,
        NEIGHBOR,
        UPGMA
    };

    // Algorithms which require a generic input file
    public static int[] GENERIC_ALGORITHMS =
    {
        SANKOFF,
        LOCAL_BRANCH_AND_BOUND,
        GLOBAL_BRANCH_AND_BOUND
    };

    /**
     * Return instance of generic algorithm associated to value
     * @param algorithm Index associated to the generic algorithm
     * @return instance of a generic algorithm
     */
    public static GenericAlgorithm getGenericAlgorithm(int algorithm)
    {
        switch(algorithm)
        {
            case SANKOFF:
                SankoffExtended skff = new SankoffExtended();
                skff.setPrintOutfile(true);
                skff.printScores(true);
                //skff.setDebugMode(true);
                return skff;
            case LOCAL_BRANCH_AND_BOUND:
                LocalBranchAndBound bab = new LocalBranchAndBound();
                bab.setPrintOutput(true);
                return bab;
            case GLOBAL_BRANCH_AND_BOUND:
                return new BranchAndBoundExtended();

            // Nothing if algorithm index is not defined
            default: return null;
        }
    }

    
    
    /* *************************************************************************
     * Algoritmi filogenetici phylip integrati in Phylo. Vedere package        *
     * algorithm.phylip                                                        *
     ***************************************************************************/

    // Name of binaries
    public static final String FITCH_EXE = "fitch";
    public static final String KITSCH_EXE = "kitsch";
    public static final String NEIGHBOR_EXE = "neighbor";
    public static final String CONSENSE_EXE = "consense";
    
    // Phylip executables
    public static final String[] PHYLIP_EXECUTABLES =
    {
        Infos.FITCH_EXE,
        Infos.KITSCH_EXE,
        Infos.NEIGHBOR_EXE,
        Infos.CONSENSE_EXE
    };

    

    /* *************************************************************************
     * Algoritmi per il calcolo della matrice delle distanze.                  *
     * Qui vanno inserite le variabili relative  all'algoritmo che si vuole    *
     * aggiungere a Phylo. Queste variabili sono  principalmente usate dal     *
     * wizard per la creazione di un esperimento.                              *
     ***************************************************************************/
     public final static int JACCARD = 0;
     public static String[] SUPPORTED_DISTANCES =
     {
         "Jaccard" // JACCARD
     };

     /**
      * Metodo usato per il riconoscimento del metodo delle distanze nel package
      * ui e bootstrap
      * @param algorithm Indice del metodo delle distanze
      * @return istanza dell'algoritmo per il calcolo della matrice
      * delle distanze associato a method
      */
     public static Distance getDistanceAlgorithm(int algorithm)
     {
         switch(algorithm)
         {
             case JACCARD: return new Jaccard();

             // nothing if algorithm is not defined
             default: return null;
         }
     }
     

     
    /* *************************************************************************
     * Algoritmi di bootstrap. Qui vanno inserite le variabili relative        *
     * all'algoritmo che si vuole aggiungere a Phylo. Queste variabili sono    *
     * principalmente usate dal wizard per la creazione di un esperimento.     *
     ***************************************************************************/
    public final static int DEFAULT_BOOTSTRAP = 0;
    public static String[] SUPPORTED_BOOTSTRAP_METHODS =
    {
        "Default method" // DEFAULT_BOOTSTRAP
    };

    /**
     * Metodo usato per il riconoscimento dell'algoritmo di bootstrap
     * @param method Indice del metodo di bootstrap
     * @param bootstraps Numero di bootstrap
     * @param distMethod Indice del metodo delle distanze
     * @return Istanza dell'algoritmo di bootstrap associato all'indice method
     */
    public static Bootstrapper getBootstrapAlgorithm(int method, int bootstraps, int distMethod)
    {
        switch(method)
        {
            case DEFAULT_BOOTSTRAP: return new DefaultBootstrapper(bootstraps, distMethod);

            // Nothing if method is not defined
            default: return null;
        }
    }


    
    /* *************************************************************************
     * Tipi di esperimento supportati da Phylo
     ***************************************************************************/
    public static int BOOTSTRAP_EXP = 0;
    public static int DISTANCE_EXP = 1;
    public static int LOAD_DISTANCE_EXP = 2;
    public static int GENERIC_EXPERIMENT = 3;
    public static String[] SUPPORTED_EXPERIMENTS =
    {
        "Bootstrap experiment",
        "Calculate distance experiment",
        "Load distance matrix experiment",
        "Generic experiment"
    };



    /*----------- DON'T TOUCH THE FOLLOWING SECTION -----------*/

    /** It rapresents an undefined char for assignment */
    public final static String UNDEFINED_CHAR = "0";

    /** It rapresents a missing char for assignment */
    public final static String MISSING_CHAR = "?";

    /** Matrix undefined characters */
    public static String[] UNDEFINED_CHARACTERS = { UNDEFINED_CHAR, MISSING_CHAR };

    /** Returns the populated array with abmitted characters for the given alphabet
     * @param alphabet
     * @return
     */
    public static ArrayList<String> getAbmittedChars(ArrayList<String> alphabet)
    {
        ArrayList<String> abmitted = new ArrayList<String>();

        for(int i = 0; i < alphabet.size(); i++)
            abmitted.add(alphabet.get(i));
        
        abmitted.addAll(Arrays.asList(Infos.UNDEFINED_CHARACTERS));

        return abmitted;
    }

    /**
     * Returns alphabet from abmitted chars array
     * @param abmittedChars
     * @return
     */
    public static ArrayList<String> getAlphabet(ArrayList<String> abmittedChars)
    {
        ArrayList<String> alphabet = new ArrayList<String>();
        Collections.copy(abmittedChars, alphabet);

        alphabet.remove(UNDEFINED_CHAR);
        alphabet.remove(MISSING_CHAR);

        return alphabet;
    }
    
    /**
     * @return Array of standard supported algorithms
     */
    public static String[] getStandardAlgorithms()
    {
        String[] algorithms = new String[STANDARD_ALGORITHMS.length];
        int value;
        for(int i = 0; i < STANDARD_ALGORITHMS.length; i++)
        {
            value = STANDARD_ALGORITHMS[i];
            algorithms[i] = SUPPORTED_ALGORITHMS[value];
        }

        return algorithms;
    }

    /**
     * @return Array of generic supported algorithms
     */
    public static String[] getGenericAlgorithms()
    {
        if(GENERIC_ALGORITHMS.length != 0)
        {
            String[] algorithms = new String[GENERIC_ALGORITHMS.length];
            int value;
            for(int i = 0; i < GENERIC_ALGORITHMS.length; i++)
            {
                value = GENERIC_ALGORITHMS[i];
                algorithms[i] = SUPPORTED_ALGORITHMS[value];
            }

            return algorithms;
        }

        return null;
    }

    // Form di Phylo
    private static PhyloForm phyloForm;
    public static void setPhyloForm(PhyloForm form) { phyloForm = form; }
    public static PhyloForm getPhyloForm() { return phyloForm; }
}
