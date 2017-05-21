package algorithm;

import files.utility.FileCopy;
import java.io.File;
import java.io.IOException;

/**
 * Questa classe permette di astrarre il concetto di algoritmo e
 * separare le due classi distinte di algoritmi interni a Phylo
 * e algoritmi interni a Phylip.
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
abstract public class GenericAlgorithm
{
    private int value;
    private String name = null;
    protected File input = null;
    private File output = null;

    /**
     * @param input file di input
     */
    abstract public void setInput(File input) throws Throwable;

    /**
     * Esegue l'algoritmo
     * @throws Throwable Eccezione generica
     */
    abstract public void exec() throws Throwable;

    /**
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * @param name Nome dell'algoritmo
     */
    protected void setName(String name)
    {
        this.name = name;
    }

    /**
     * file di output
     * @param output Nome dei file di output
     */
    protected void setOutput(File output)
    {
        this.output = output;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * @return Nome dell'algoritmo
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return file di input
     */
    public File getInput()
    {
        return this.input;
    }

    /**
     * @return File di output
     */
    public File getOutput()
    {
        return this.output;
    }

    /**
     * Salva il file di intput usato dall'algoritmo. Questo metodo può
     * essere utile se si vuole tener salvati nella cartella del progetto
     * tutti i file utilizzati.
     * @param path Cartella in cui salvare
     * @param name Nome del file di output da salvare
     * @throws  IOException Eccezione se la cartella non esiste o non si hanno
     * sufficienti permessi
     */
    public void saveInput(File path, String name) throws IOException, Throwable
    {
        if( path == null || !path.exists() || !path.canRead() || !path.canWrite() )
            throw new IOException("it's not possible to create files into "+path.getAbsolutePath());

        if(name.equals(""))
            throw new IOException("name file to create is null");

        FileCopy copy = new FileCopy();
        copy.fileIntoDirectory(input, path, name);
    }

    /**
     * Salva il file di output generato dall'algoritmo. Questo metodo può
     * essere utile se si vuole tener salvati nella cartella del progetto
     * tutti i file utilizzati.
     * @param path Cartella in cui salvare
     * @param name Nome del file di output da salvare
     * @throws  IOException Eccezione se la cartella non esiste o non si hanno
     * sufficienti permessi
     */
    public void saveOutput(File path, String name) throws IOException
    {
        if( path == null || !path.exists() || !path.canRead() || !path.canWrite() )
            throw new IOException("it's not possible to create files into "+path.getAbsolutePath());

        if(name.equals(""))
            throw new IOException("name file to create is null");

        FileCopy copy = new FileCopy();
        copy.fileIntoDirectory(output, path, name);
    }
}
