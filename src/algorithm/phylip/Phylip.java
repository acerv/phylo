package algorithm.phylip;

import utility.BufferRedirect;
import algorithm.phylip.exception.PhylipNoConfigurationException;
import algorithm.phylip.exception.PhylipException;
import algorithm.phylip.exception.PhylipProgramNotFoundException;
import algorithm.AlgorithmException;
import algorithm.GenericAlgorithm;
import files.utility.FileCopy;
import files.utility.ZipUtility;
import files.utility.DeletePath;
import informations.Infos;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import utility.Debugger;
import utility.GetPid;

/**
 * Astrae l'insieme delle utility di Phylip e implementa metodi di base comuni
 * a tutti i programmi.
 * @author Cervesato Andrea - sawk @ gmail.com
 */
public abstract class Phylip extends GenericAlgorithm
{
    // Riferimento al file .jar compilato. Serve per prelevare i binari precompilati
    private File JAR_FILE = null;

    // File zip contenente i binari da copiare
    private static String ZIPPED_BINS = "phylip_bin.zip";
    private static String PROGRAM_BINS = "phylip_bin";

    // Numero di binari supportati
    private static final int BINS_NUM = Infos.PHYLIP_EXECUTABLES.length;

    // Unix & Mac-OSX
    private static String UNIX_BIN = "unix";

    // Windows
    private static String WINDOWS_BIN = "windows";

    // MacOSX
    private static String MACOSX_BIN = "mac";

    // Cartella contenente i binari. Viene settata dal costruttore
    private String BINARY_PATH = null;

    // Flag per il controllo del sistema operativo
    private boolean WINDOWS = false;
    private boolean UNIX = false;
    private boolean MACOSX = false;

    // Opzioni di esecuzione del programma da eseguire.
    private ArrayList<String> CONFIG = new ArrayList<String>();

    // Accettazione della configurazione
    private static String ACCEPT_OPTIONS = "Y";

    // File di output fi phylip
    private File phylipOutfile;

    // Cartella di lavoro di phylip
    private static File workingPath = new File(Infos.HOME, "phylip");

    // Nome del file di input e di output
    private String inputName;
    private String outputName;

    /* **************************************************
     * PID DEL PROCESSO: usato per uccidere il processo
     * in esecuzione.
     ****************************************************/
    private String PID;

    /*
     * PARTE PUBBLICA
     */
    /**
     * Se non è già presente, crea una cartella di lavoro inserendo al suo
     * interno gli eseguibili relativi al sistema operativo e
     * configura le variabili di avvio del il programma.
     * @throws IOException La cartella deve soddisfare tutti i requisiti richiesti
     * i file in unix
     * a rendere eseguibili i programmi in unix
     */
    public Phylip() throws IOException
    {
        Debugger.println("> Operative system: " + Infos.OS_NAME + " - " + Infos.ARCH_NAME);

        // Estraggo il nome della directory contenente il programma .jar
        String jar;
        try
        {
            jar = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            this.JAR_FILE = new File(jar);
        } catch (URISyntaxException ex)
        {
            Logger.getLogger(Phylip.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Riconoscimento del sistema operativo
        if (Infos.OS_NAME.indexOf( "win" ) >= 0)
        {
            // Setto il flag
            this.WINDOWS = true;

            // Setto la cartella con i binari
            this.BINARY_PATH = Phylip.PROGRAM_BINS
                    + Infos.FILE_SEPARATOR
                    + Phylip.WINDOWS_BIN;
        }
        else if (Infos.OS_NAME.indexOf( "nix" ) >= 0
                || Infos.OS_NAME.indexOf( "nux" ) >= 0)
        {
            // Setto il flag
            this.UNIX = true;

            // Cartella da copiare
            this.BINARY_PATH = Phylip.PROGRAM_BINS
                    + Infos.FILE_SEPARATOR
                    + Phylip.UNIX_BIN;
        }
        else if(Infos.OS_NAME.indexOf( "mac" ) >= 0)
        {
            this.MACOSX = true;

            // Cartella da copiare
            this.BINARY_PATH = Phylip.PROGRAM_BINS
                    + Infos.FILE_SEPARATOR
                    + Phylip.MACOSX_BIN;
        }

        // Creo/Aggiorno la cartella di lavoro
        this.verifyingWorkingPath();
        this.deleteInfile();
    }

    /**
     * Salva il file di input nella cartella di lavoro
     * @param input
     */
    @Override
    public void setInput(File input)
    {
        FileCopy f = new FileCopy();
        try
        {
            f.fileIntoDirectory(input, getWorkingPath(), inputName);
        } 
        catch (IOException ex)
        {
            Logger.getLogger(Phylip.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Questo metodo viene implementato dalle classi figlio
     * @throws Throwable Eccezione generica
     */
    @Override
    abstract public void exec() throws Throwable;

    /**
     * Permette di fornire le configurazioni
     * @param config Configurazioni di Phylip in formato ArrayList
     */
    private void setConfig(ArrayList<String> config)
    {
        this.CONFIG = config;
    }

    /**
     * @return Configurazioni usate per il programma phylip
     */
    public ArrayList<String> getConfig()
    {
        return this.CONFIG;
    }

    /**
     * Ritorna il file di output di phylip
     * @return
     */
    public File getOutfile()
    {
        return phylipOutfile;
    }

    public void setInputName(String input)
    {
        inputName = input;
    }

    public void setOutputName(String output)
    {
        outputName = output;
    }

    /**
     * Cambia il riferimento all'outfile salvandolo da un'altra parte.<br>
     * DA USARE SOLO se si sa cosa si sta facendo.
     * @param path Cartella in cui salvarlo
     * @param name Nome con cui salvarlo
     * @throws IOException
     */
    public void updateOutfile(File path, String name)
            throws IOException
    {
        File outfile = new File(path, name);
        
        new FileCopy().fileIntoDirectory(phylipOutfile,path,name);
        
        this.phylipOutfile.delete();
        this.phylipOutfile = outfile;
    }

    /*
     * PARTE PRIVATA
     */
     // Stampa a schermo le opzioni di configurazione utilizzate per il programma
    private void printConfig()
            throws PhylipNoConfigurationException
    {
        if (this.CONFIG.isEmpty())
        {
            throw new PhylipNoConfigurationException();
        }

        int lenght = this.CONFIG.size();

        for (int i = 0; i < lenght; i++)
        {
            System.out.println(this.CONFIG.get(i));
        }
    }

    private void copyConfig(File path, String name)
            throws IOException, PhylipNoConfigurationException
    {
        File out = new File(path, name);
        PrintStream defaultOut = new PrintStream(System.out);

        FileOutputStream outStream = new FileOutputStream(out);
        PrintStream print = new PrintStream(outStream);

        System.setOut(print);

        this.printConfig();
        print.flush();
        print.close();
        outStream.flush();
        outStream.close();

        System.setOut(defaultOut);
        Debugger.println("> Phylip configuration has been copied");
    }

    // Permette di fornire le opzioni di configurazione al programma
    private void redirectOptions(OutputStream outputstream)
            throws IOException
    {
        // Opzione da leggere
        String line = null;

        // Scrittore del buffer
        PrintWriter printwriter = new PrintWriter(outputstream);

        // Accetto la configurazione
        this.CONFIG.add(Phylip.ACCEPT_OPTIONS);

        // Inizio scrittura
        for (int i = 0; i < this.CONFIG.size(); i++)
        {
            // Scrittura
            line = this.CONFIG.get(i);
            printwriter.println(line);
            printwriter.flush();
        }

        // Pulizia
        printwriter.flush();
        printwriter.close();
        outputstream.flush();
        outputstream.close();
    }

    // Verifica l'esistenza dei file di input e output
    private void verifyingFilesExistence()
            throws AlgorithmException, FileNotFoundException
    {
        // File di input
        File inFile = new File(getWorkingPath(), inputName);

        // File di output precedentemente generati
        File outTree = null;
        File outFile = null;

        // Verifica l'esistenza dei file di input
        if (!inFile.exists())
            throw new AlgorithmException("input does not exist");

        // Verifico l'esistenza dei file di output
        outTree = new File(getWorkingPath(),outputName);
        if (outTree.exists())
        {
            outTree.delete();
            Debugger.println("> " + outputName + " already exists. Removed...");
        }

        outFile = new File(getWorkingPath(), "outfile");
        if (outFile.exists())
        {
            outFile.delete();
            Debugger.println("> outfile already exists. Removed...");
        }
    }

    // Elimina il file di input se esiste
    private void deleteInfile()
    {
        File infile = new File(getWorkingPath(), "infile");
        File intree = new File(getWorkingPath(), "intree");

        if (infile.exists())
        {
            infile.delete();
            Debugger.println("> infile already exists. Removed...");
        }

        if (intree.exists())
        {
            intree.delete();
            Debugger.println("> intree already exists. Removed...");
        }
    }

    // Verifica esistenza di tutti i binari binari e aggiorna (eventualmente)
    // la cartella di lavoro
    private void verifyingWorkingPath()
            throws IOException
    {
        // Cartella di lavoro
        File workPath = getWorkingPath();

        // Se la cartella non esiste, la creo e la copio
        if (!workPath.exists())
        {
            workPath.mkdir();
        }

        // La cartella deve essere leggibile e scrivibile
        if (!workPath.canRead() || !workPath.canWrite())
        {
            throw new IOException();
        }

        // Nomi dei file contenuti in workPath
        String[] bins = workPath.list();

        // Numero di eseguibili presenti nella cartella di lavoro di phylip
        int binsNum = 0;

        // Conto il numero di eseguibili all'interno della cartella di lavoro
        if (this.UNIX || this.MACOSX)
        {
            for (int i = 0; i < bins.length; i++)
            {
                for (int j = 0; j < BINS_NUM; j++)
                {
                    if (bins[i].contains(Infos.PHYLIP_EXECUTABLES[j]))
                    {
                        binsNum++;
                    }
                }
            }
        } else if (this.WINDOWS)
        {
            for (int i = 0; i < bins.length; i++)
            {
                if (bins[i].contains(".exe"))
                {
                    binsNum++;
                }
            }
        }

        // Aggiorno la cartella se il numero di eseguibili è diverso
        if (binsNum < BINS_NUM)
        {
            this.copyBinaryPathInWorkingPath();

            // Rendo i file eseguibili in unix
            if (this.UNIX || this.MACOSX)
            {
                String cmd = "/bin/chmod -R 777 " + workPath.getAbsolutePath();
                Process chmod = Runtime.getRuntime().exec(cmd);
                Debugger.println("> phylip programs has been executed");
            }
        }
    }

    // Copia i binari contenuti nell'archivio .jar del programma nella cartella di lavoro
    private void copyBinaryPathInWorkingPath()
            throws IOException
    {
        Debugger.println(this.JAR_FILE.getAbsolutePath());

        ZipFile zip = new ZipFile(this.JAR_FILE); // Riferimento all'archivio .jar
        ZipEntry entry = new ZipEntry(Phylip.ZIPPED_BINS); // Riferimento all'archivio contenente i binari
        File temp = new File(Infos.TEMPORARY_PATH); // Cartella dei temporali del sisitema operativo

        FileCopy flcpy = new FileCopy(); // Copia i file

        // Copio il file zippato contenente i binari
        flcpy.copyFileFromZipFile(zip, entry, temp);

        // Estraggo il file zip
        String zip_bins = Infos.TEMPORARY_PATH + Phylip.ZIPPED_BINS;
        String unzip_bins = Infos.TEMPORARY_PATH + Phylip.PROGRAM_BINS;
        ZipUtility.unzip(new File(zip_bins), new File(Infos.TEMPORARY_PATH));

        // Copio gli eseguibili relativi. BINARY_PATH e WORKING_PATH
        // devono essere inizializzate
        File temp_path = new File(temp, this.BINARY_PATH);
        flcpy.copyDirectory(temp_path, getWorkingPath());

        // Elimino i file per far spazio
        (new File(zip_bins)).deleteOnExit();
        DeletePath.doDelete(new File(unzip_bins));
    }

    // Salva il file di output di phylip
    private void setOutFile() throws FileNotFoundException, IOException
    {
        Random rand = new Random();
        int num = rand.nextInt(10000000);
        File outFile = new File(getWorkingPath(), "outfile");

        // Copio il file di output per evitare casini con la lettura del buffer
        FileCopy cpy = new FileCopy();
        String out_num =  "outfile"+num;
        cpy.fileIntoDirectory(outFile,new File(Infos.TEMPORARY_PATH), out_num);
        phylipOutfile = new File(Infos.TEMPORARY_PATH,out_num);
    }

    /**
     * @return Cartella di lavoro dell'algoritmo
     */
    public File getWorkingPath()
    {
        return workingPath;
    }

    /**
     * @return Pid of the phylip process
     */
    public String getPid() {
        return PID;
    }

    /*
     * PARTE PROTECTED
     */
    /**
     * Esegue il programma di phylip richiesto
     * @param config
     * @throws AlgorithmException Viene lanciata una eccezione se l'algoritmo incontra dei problemi
     * @throws IOException Viene lanciata una eccezione se la cartella di lavoro non è scrivibile o leggibile
     * @throws InterruptedException Viene lanciata una eccezione se il thread non termina correttamente
     * @throws PhylipException  Eccezione in caso di errori di Phylip
     */
    protected void execProgram(ArrayList<String> config)
            throws AlgorithmException, IOException, InterruptedException, PhylipException
    {
        setConfig(config);
        
        // L'algoritmo deve avere un nome
        if (getName() == null)
            throw new PhylipException("algorithm hasn't a name");

        // Verifico l'esistenza dei file
        this.verifyingWorkingPath();
        this.verifyingFilesExistence();

        // Stringhe per l'esecuzione del programma
        String shell = null;
        String binary = null;
        String cmd = null;

        // Linux(32bit&64bit) / MacOSX
        if (this.UNIX || this.MACOSX)
        {
            String name = getName();
            shell = "/bin/sh -c "+"./";
            binary = name;

            // Comando da eseguire
            cmd = shell + binary;
        } // Windows
        else if (this.WINDOWS)
        {
            shell = ""; // In windows non serve specificare la shell
            binary = getName() + ".exe";

            // Comando da eseguire
            cmd = getWorkingPath().getAbsolutePath() + Infos.FILE_SEPARATOR + binary;
        }
        else
        {
            throw new PhylipProgramNotFoundException();
        }

        // Cartella corrente
        File currentDir = getWorkingPath();

        // Avvio il processo
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(cmd, null, currentDir);

        // Passo la configurazione al programma
        Debugger.println("> Configuration of the program");
        this.redirectOptions(proc.getOutputStream());

        // Redirezione buffer di output ed error
        BufferRedirect inBuff = new BufferRedirect(proc.getInputStream(), getName().toUpperCase());
        BufferRedirect errBuff = new BufferRedirect(proc.getErrorStream(), "ERRORS");

        /* Waiting some seconds to start up the process and get pid */
        Debugger.println("> Wait few secs...");
        Thread.sleep(2000);

        /* Inizializzo il PID */
        PID = (new GetPid(binary, WINDOWS)).pid();

        // Esecuzione del programma
        Debugger.println("> Execution of \"" + cmd + "\" into " + getWorkingPath().getAbsolutePath()+" (PID: "+PID+")");

        // Esecuzione dei thread
        inBuff.start();
        errBuff.start();

        // Valore di uscita ?
        int exitVal = proc.waitFor();

        // exitVal dev'essere 0 per la corretta esecuzione del programma phylip
        if ( exitVal != 0 )
            throw new PhylipException("Oups ! There's a problem with '"+this.getName()+"' phylip program. Call a programmer :(\n"
                    + "If process has been killed, there's nothing wrong ! Then don't panic :)");

        Debugger.println("> ExitValue: " + exitVal);
        Debugger.println("> Execution done.");
        proc.destroy();

        // Creo il buffer per l'outfile di phylip
        this.setOutFile();

        // Setto il file di output
        setOutput(new File(getWorkingPath(),outputName));
    }
}
