package experiment;

import algorithm.AlgorithmException;
import algorithm.phylip.Phylip;
import distance.Distance;
import experiment.config.xml.XmlExperimentConfigWriter;
import experiment.exception.ExperimentDoesNotExist;
import informations.Infos;
import java.io.File;
import java.io.IOException;
import matrix.exception.LanguageException;
import matrix.exception.MatrixFormatException;
import matrix.reader.CharMatrixReader;
import matrix.reader.DistMatrixReader;
import ui.configuration.PhyloConfig;
import utility.Debugger;

/**
 *
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class ExperimentCalcDistance extends Experiment
{
    Distance distance;
    CharMatrixReader charMatrix;
    DistMatrixReader distMatrix;

    public ExperimentCalcDistance(PhyloConfig cfg)
    {
        super(cfg);
    }

    /**
     * Crea un nuovo esperimento con metodo delle distanze
     * @param name Nome dell'esperimento
     * @param distanceMethod Metodo per il calcolo delle distanze
     * @param matrix
     * @throws IOException Il file contenente la matrice deve poter essere letto
     * @throws MatrixFormatException La matrice deve soddisfare i requisiti richiesti
     * @throws LanguageException I linguaggi devono contenere i requisiti richiesti
     */
    public void mkNewExperiment(String name, Distance distanceMethod, CharMatrixReader matrix)
            throws IOException, MatrixFormatException, LanguageException
    {
        // Inizializzo la classe
        initializeName(name);
        initializeMatrix(matrix);
        distance = null;
        distance = distanceMethod;

        // Configuro le classi
        charMatrix = null;
        charMatrix = matrix;
        
        distMatrix = null;
        distMatrix = new DistMatrixReader();
        distMatrix.setSavePath(getExperimentPath());
        distMatrix.setFileName("distancematrix_"+getName()+".txt");

        Debugger.println("> Experiment type \""+getExperimentName()+"\" has been created");

        setCreated(true);
    }

    public int getDistanceMethod()
    {
        return distance.getDistanceMethod();
    }

    public CharMatrixReader getCharMatrix()
    {
        return charMatrix;
    }

    public DistMatrixReader getDistMatrix()
    {
        return distMatrix;
    }

    public Distance getDistanceAlgorithm()
    {
        return distance;
    }

    @Override
    public int getExperimentType()
    {
        return Infos.DISTANCE_EXP;
    }

    @Override
    public String getExperimentName()
    {
        return Infos.SUPPORTED_EXPERIMENTS[Infos.DISTANCE_EXP];
    }

    @Override
    public void exec() throws Throwable
    {
        // Se l'esperimento non è stato creato, lancio una eccezione
        if (!isCreated())
            throw new ExperimentDoesNotExist();

        // L'algoritmo è istanziato ?
        if ( getAlgorithm() == null )
            throw new AlgorithmException("algorithm is not instantied");

        Debugger.println("> Start executing experiment \""+getName()+"\"");

        // Calcolo le distanze
        distance.loadMatrix(getCharMatrix());

        /* Le implementazioni di Distances non prevedono un metodo per la scrittura
         * su file. Devo specificare esplicitamente la matrice per non perdere
         * i dati di salvataggio settati precedentemente in mkNewExperiment(..).
         */
        distMatrix.setMatrix
                (
                null,
                distance.getDistMatrix().getMatrix(),
                distance.getDistMatrix().getLanguages()
                );

        // Scrivo la matrice nel file
        distMatrix.printMatrixOnFile();

        // Salvo il riferimento al file di input
        setInput(distMatrix.getSaveFile());

        // Esecuzione dell'algoritmo
        getAlgorithm().setInput(getInput());
        getAlgorithm().exec();

        // Copio i file di output nella cartella dell'esperimento
        String alg_file_name = getAlgorithm().getOutput().getName()+"_"+getName()+".nwk";
        getAlgorithm().saveOutput(getExperimentPath(), alg_file_name);

        // Se algoritmo phylip salvo l'outfile nella cartella dell'esperimento ?
        if(getPhyloConfig().outfile_save)
        {
            if(getAlgorithm() instanceof Phylip)
            {
                File outfile = ((Phylip)getAlgorithm()).getOutfile();

                // Non dovrebbe succedere il contrario
                if(outfile.exists())
                {
                    String newOutfile = "outfile_"+getAlgorithmName()+"_"+getName()+".txt";

                    // Aggiorno la posizione di outfile
                    ((Phylip)getAlgorithm()).updateOutfile(getExperimentPath(), newOutfile);
                }
            }
        }

        // Verifico l'esistenza dell'albero di output
        File outFile = new File(getExperimentPath(), alg_file_name);

        // Se esiste l'albero lo metto nella cartella di lavoro
        if ( outFile.exists() )
            setOuttree(outFile);

        // Salvo la configurazione e setto il flag di esistenza della configurazione a true
        XmlExperimentConfigWriter.writeConfig(this);
        setConfigured(true);
        Debugger.println("> Experiment is done");
    }
}
