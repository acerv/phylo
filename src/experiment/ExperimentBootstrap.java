package experiment;

import algorithm.AlgorithmException;
import algorithm.phylip.Consense;
import algorithm.phylip.Phylip;
import bootstrap.Bootstrapper;
import experiment.config.xml.XmlExperimentConfigWriter;
import experiment.exception.ExperimentDoesNotExist;
import files.utility.FileCopy;
import informations.Infos;
import java.io.File;
import java.io.IOException;
import matrix.exception.LanguageException;
import matrix.exception.MatrixFormatException;
import matrix.reader.CharMatrixReader;
import ui.configuration.PhyloConfig;
import utility.Debugger;

/**
 *
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class ExperimentBootstrap extends Experiment
{
    CharMatrixReader matrix;
    Bootstrapper bootstrapper;
    Consense consense;

    public ExperimentBootstrap(PhyloConfig cfg)
    {
        super(cfg);
    }
    
    /**
     * Crea un nuovo esperimento con il metodo di bootstrap
     * @param name Nome dell'esperimento
     * @param bootstrap La classe inizializzata che implementa Bootstrapper
     * @param charMatrix Matrice dei caratteri. Deve contenere già la matrice
     * @throws IOException Il file contenente la matrice deve poter esser letta
     * @throws MatrixFormatException La matrice deve soddisfare i requisiti richiesti
     * @throws LanguageException I linguaggi devono contenere i requisiti richiesti
     */
    public void mkNewExperiment(String name, Bootstrapper bootstrap, CharMatrixReader charMatrix)
            throws IOException, MatrixFormatException, LanguageException
    {
        // Inizializzo la classe
        initializeName(name);
        initializeMatrix(charMatrix);

        // Configuro le classi
        matrix = null;
        matrix = charMatrix;

        // Configuro bootstrap
        bootstrapper = null;
        bootstrapper = bootstrap;
        bootstrapper.setSavePath(getExperimentPath());
        bootstrapper.setFileName("bootstrap_"+name+".txt");
        
        Debugger.println("> Experiment type \""+getExperimentName()+"\" has been created");

        setCreated(true);
    }

    public int getDistanceMethod()
    {
        return bootstrapper.getDistanceMethod();
    }

    public int getBootstrapMethod()
    {
        return bootstrapper.getBootstrapMethod();
    }

    public CharMatrixReader getCharMatrix()
    {
        return matrix;
    }

    public int getBootstrapNum()
    {
        return bootstrapper.getBootstrapsNumber();
    }

    public Bootstrapper getBootstrapper()
    {
        return bootstrapper;
    }

    public void setConsense(Consense cons)
    {
        consense = cons;
    }
    
    public Consense getConsense()
    {
        return consense;
    }
    
    @Override
    public int getExperimentType()
    {
        return Infos.BOOTSTRAP_EXP;
    }

    @Override
    public String getExperimentName()
    {
        return Infos.SUPPORTED_EXPERIMENTS[Infos.BOOTSTRAP_EXP];
    }

    @Override
    public void exec() throws Throwable
    {
        if(!isCreated())
            throw new ExperimentDoesNotExist();

        if ( getAlgorithm() == null )
            throw new AlgorithmException("algorithm is not instantied");

        Debugger.println("> Start executing experiment \""+getName()+"\"");

        // Genero le matrici con il metodo di bootstrap
        bootstrapper.loadMatrix(matrix);

        // Scrivo il risultato su file
        bootstrapper.printMatrixOnFile();

        // Salvo il riferimento al file di input
        setInput(bootstrapper.getSaveFile());

        // Esecuzione dell'algoritmo
        getAlgorithm().setInput(getInput());
        getAlgorithm().exec();

        // Copio i file di output nella cartella dell'esperimento
        String alg_file_name = getAlgorithm().getOutput().getName()+"_"+getName()+".nwk";
        getAlgorithm().saveOutput(getExperimentPath(), alg_file_name);

        // Tengo o meno il bootstrap file ?
        if(!getPhyloConfig().exp_saveBootstrap)
            getInput().delete();

        // Se algoritmo phylip salvo l'outfile nella cartella dell'esperimento ?
        if(getAlgorithm() instanceof Phylip)
        {
            if(getPhyloConfig().outfile_save)
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
        {
            /* Se è stato richiesto (e settato) un albero di consenso,
             * genero l'albero con il programma relativo */
            if ( getConsense() != null )
            {
                Debugger.println("> Generate consensus tree");

                // Salvo l'albero di input
                new FileCopy().fileIntoDirectory(
                        outFile,
                        getExperimentPath(),
                        "outtree_bootstrap_"+getName()+".nwk"
                        );

                // Esecuzione di consense
                getConsense().setInput(outFile);
                getConsense().exec();

                // Copio i file di output di consense
                String consense_file_name = getConsense().getOutput().getName()+"_"+getName()+".nwk";
                getConsense().saveOutput(getExperimentPath(), consense_file_name);
                setOuttree(new File(getExperimentPath(), consense_file_name));

                // salvo l'outfile nella cartella dell'esperimento ?
                if(getPhyloConfig().outfile_save)
                {
                    // Non dovrebbe succedere il contrario
                    File outfile = ((Phylip)getConsense()).getOutfile();
                    if(outfile.exists())
                    {
                        String newOutfile = "outfile_consense_"+getName()+".txt";

                        // Aggiorno la posizione di outfile per una più sicura gestione
                        getConsense().updateOutfile(getExperimentPath(), newOutfile);
                    }
                }
            }
            else
                throw new Exception("consense algorithm is not initialized");
        }
        else
            throw new AlgorithmException("input does not exist");
        
        // Salvo la configurazione e setto il flag di esistenza della configurazione a true
        XmlExperimentConfigWriter.writeConfig(this);
        setConfigured(true);
        Debugger.println("> Experiment is done");
    }
}
