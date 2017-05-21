package bootstrap;

import distance.Distance;
import informations.Infos;
import matrix.exception.LanguageException;
import matrix.reader.CharMatrixReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import matrix.reader.DistMatrixReader;


/**
 * Classe astratta che implementa tutto ciò che non è strettamente dipendente
 * dall'algoritmo di bootstrap. Usare setDistance() e getDistance() per modificare
 * l'algoritmo di calcolo delle distanze, e setBootstraps() e getBootstraps()
 * per modificare le matrici di bootstrap.
 * @author Cervesato Andrea - sawk @ gmail.com
 */
abstract public class Bootstrapper
{
    protected int bootstrapsNumber = 10;
    protected int distanceMethod;
    protected String fileName = "bootstrap.txt";

    /* Cartella di salvataggio della matrice delle distanze generata.
     * Va modificata se non si ha intenzione di usare la home come
     * cartella di salvataggio predefinita     */
    protected File savePath = new File(Infos.HOME);

    // File in cui si salva il risultato
    protected File saveFile = null;

    // ArrayList di matrici delle distanze
    private ArrayList<DistMatrixReader> bootstraps =
            new ArrayList<DistMatrixReader>();

    // Metodo usato per calcolare le matrici delle distanze
    private Distance distance;

    /**
     * Initialize class
     * @param bootstrpNumber Number of bootstraps
     * @param dstMethod number of distance method assigned into Infos.java class
     */
    public Bootstrapper(int bootstrpNumber, int dstMethod)
    {
        bootstrapsNumber = bootstrpNumber;
        distanceMethod = dstMethod;
        distance = Infos.getDistanceAlgorithm(distanceMethod);
    }

    /**
     * @param fileName Setta il nome del file in cui salvare le matrici
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    /**
     * @return Metodo usato per calcolare le distanze. Può valere i valori
     * specificati nella classe Infos.java
     */
    public int getDistanceMethod()
    {
        return distanceMethod;
    }

    /**
     * @return Numero di matrici da generare.
     */
    public int getBootstrapsNumber()
    {
        return bootstrapsNumber;
    }

    /**
     * @param n Numero di matrici da generare.
     */
    public void setBootstrapsNumber(int n)
    {
        bootstrapsNumber = n;
    }

    /**
     * @return Nome del file in cui salvare le matrici
     */
    public String getFileName()
    {
        return this.fileName;
    }

    /**
     * Configura la classe per salvare la matrice in una data cartella. È una scelta
     * ridondante, ma è stata fatta per mantenere indipendenza tra l'inizializzazione
     * della classe e le operazioni che si possono fare con essa.
     * @param savePath Cartella in cui salvare le matrici
     */
    public void setSavePath(File savePath)
    {
        this.savePath = savePath;
    }


    /**
     * @return Il file in cui viene salvato il risultato. Se è null, il file non esiste
     */
    public File getSaveFile()
    {
        return saveFile;
    }

    /**
     * Visualizza le matrici di bootstrap generate
     */
    public void printMatrix()
    {
        for ( int i = 0; i < this.getBootstrapsNumber(); i++ )
        {
            this.getBootstraps().get(i).printMatrix();
            System.out.print("\n\n");
        }
    }

    /**
     * Scrive un file formattato con le matrici delle distanze da dare in pasto a Phylip.
     * @throws IOException Viene lanciata una eccezione se si verificano problemi con la
     * scrittura del file
     */
    public void printMatrixOnFile() throws IOException
    {
        // Cartella di salvataggio della matrice
        String dataPath = this.savePath.getAbsolutePath();

        FileOutputStream outMatrix = null;
        PrintStream defaultOut = new PrintStream(System.out);

        // Apro il file
        this.saveFile = new File(dataPath,this.getFileName());

        // Stream di output
        outMatrix = new FileOutputStream(this.saveFile);

        // Reindirizzo il flusso di println su System.out
        PrintStream print = new PrintStream(outMatrix);
        System.setOut(print);

        // Scrivo nel file la matrice
        this.printMatrix();

        print.flush();
        print.close();
        outMatrix.close();

        // riporto il buffer originale
        System.setOut(defaultOut);
    }

    /**
     * Carica una matrice dei caratteri e ne fa il bootstrap
     * @param matrix Matrice dei caratteri
     * @throws IOException Vengono lanciate eccezioni se si verificano problemi con il file.
     * @throws matrix.exception.MatrixFormatException
     * @throws LanguageException Eccezioni sul formato dei linguaggi
     */
    public abstract void loadMatrix(CharMatrixReader matrix)
            throws IOException, matrix.exception.MatrixFormatException, LanguageException;

    /**
     * @return tipo di bootstrap usato specificato nella classe Infos.java
     */
    public abstract int getBootstrapMethod();

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public ArrayList<DistMatrixReader> getBootstraps() {
        return bootstraps;
    }

    public void setBootstraps(ArrayList<DistMatrixReader> bootstraps) {
        this.bootstraps = bootstraps;
    }
}
