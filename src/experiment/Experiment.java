package experiment;

import algorithm.GenericAlgorithm;
import files.utility.DeletePath;
import files.utility.ZipUtility;
import informations.Infos;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import matrix.exception.MatrixIsEmptyException;
import matrix.reader.MatrixReader;
import ui.configuration.PhyloConfig;
import utility.Debugger;

/**
 * Classe che rappresenta un esperimento da eseguire con Phylo
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public abstract class Experiment implements Cloneable
{
    public static String EXPERIMENT_EXTENSION = ".phylo";
    public static String CONFIGURATION_FILE_NAME = "config.xml";
    
    private File parentPath = new File(Infos.HOME,"PhyloExperiments");
    private File expPath;

    private String name = "experiment";
    private String absoluteName;
    private String date;
    private GenericAlgorithm algorithm;
    private File input;
    private File outtree;
    private boolean configured = false;
    private boolean created = false;
    private boolean loadedFromFile = false;

    private PhyloConfig cfg;

    /* PUBLIC */
    public Experiment(PhyloConfig cfg)
    {
        this.cfg = cfg;
        parentPath = new File(cfg.exp_path);

        // Creo la cartella degli esperimenti
        if ( !parentPath.exists() )
            parentPath.mkdir();
    }

    public PhyloConfig getPhyloConfig()
    {
        return cfg;
    }

    public String getName()
    {
        return name;
    }

    public String getDate()
    {
        return date;
    }
    // Setta la data di creazione
    private void setDate()
    {
        Calendar cl = new GregorianCalendar();
        int day   = cl.get(Calendar.DAY_OF_MONTH);
        int month = cl.get(Calendar.MONTH)+1;
        int year  = cl.get(Calendar.YEAR);

        date = day+"-"+month+"-"+year;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public GenericAlgorithm getAlgorithm()
    {
        return algorithm;
    }

    public void setAlgorithm(GenericAlgorithm alg)
    {
        this.algorithm = alg;
    }

    public String getAlgorithmName()
    {
        return algorithm.getName();
    }

    public File getInput()
    {
        return input;
    }

    public void setInput(File input)
    {
        this.input = input;
    }

    public File getOuttree()
    {
        return outtree;
    }

    public boolean isConfigured()
    {
        return configured;
    }

    public File getExperimentPath()
    {
        return expPath;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public void clean()
    {
        absoluteName = null;
        algorithm = null;
        input = null;
        outtree = null;
    }

    public void cleanAll() throws FileNotFoundException
    {
        clean();
        DeletePath.doDelete(expPath);
        expPath = null;
    }

    public void saveConfig(String file) throws IOException
    {
        Debugger.println("\n> Saving experiment in "+file);
        ZipUtility.zip(expPath, new File(file));
    }

    public void setLoadedFromFile(boolean load)
    {
        loadedFromFile = load;
    }

    /* PROTECTED */
    protected void setName(String name)
    {
        this.name = name;
        setAbsoluteName(name);
    }

    protected void setAbsoluteName(String name)
    {
        if(!loadedFromFile)
            setDate();

        absoluteName = date+"_"+name;
    }

    private String getAbsoluteName()
    {
        return absoluteName;
    }

    protected void setOuttree(File outtree)
    {
        this.outtree = outtree;
    }

    protected void setConfigured(boolean configured)
    {
        this.configured = configured;
    }

    protected boolean isCreated()
    {
        return created;
    }

    protected void setCreated(boolean created)
    {
        this.created = created;
    }

    protected void setExperimentPath(File path)
    {
        this.expPath = path;
    }

    // Inizializza il nome dell'esperimento
    protected void initializeName(String name)
            throws IOException
    {
        // setto il nome dell'esperimento
        if ( name != null )
            setName(name);

        // Creo la cartella dell'esperimento
        File newPathFile = new File(parentPath, absoluteName);

        expPath = searchWorkingPath(newPathFile);

        Debugger.println("> Making working path ("+expPath+")");

        if ( parentPath.canWrite() )
            expPath.mkdir();
        else
            throw new IOException();
    }

    // Inizializza la matrice
    protected void initializeMatrix(MatrixReader matrix)
            throws MatrixIsEmptyException, IOException
    {
        // La matrice è inizializzata ?
        if ( matrix.getMatrix() == null )
            throw new MatrixIsEmptyException();

        // Salvo la matrice nella cartella di lavoro
        matrix.setSavePath(getExperimentPath());
        matrix.setFileName("charmatrix_"+getName()+".txt");
        matrix.printMatrixOnFile();
        input = matrix.getRefererFile();
    }

    // Rinomina la cartella con un tag finale nel caso esista già
    private File searchWorkingPath(File newPathFile)
    {
        String name1 = name;
        int version = 1;

        Pattern ver = Pattern.compile("\\([0-9]+\\)$");
        Pattern num = Pattern.compile("[0-9]+");

        // Cerco se il nome esiste già
        while ( newPathFile.exists() )
        {   
            Matcher m1 = ver.matcher(name1);
            if(m1.find())
            {
                int start = m1.start();
                String line = m1.group();
                
                // Prendo il numero n
                Matcher m2 = num.matcher(line);
                m2.find();
                String number = m2.group();
                version = Integer.parseInt(number);
                version++;

                // Rimuovo il tag (n)
                name1 = name1.substring(0, start);
            }

            // La prima volta che si è nel while version = 1
            name1 = name1+"("+version+")";
            setAbsoluteName(name1);
            newPathFile = new File(parentPath, getAbsoluteName());
        }

        setName(name1);
        return newPathFile;
    }

    /* ABSTRACT */

    /**
     * @return Numero del metodo usato specificato in Infos.java
     */
    public abstract int getExperimentType();

    /**
     * @return Nome dell'esperimento usato
     */
    public abstract String getExperimentName();

    /**
     * Esegue l'esperimento.
     * @throws Throwable Viene lanciata una eccezione quando i thread non vengono terminati correttamente
     */
    public abstract void exec() throws Throwable;
}