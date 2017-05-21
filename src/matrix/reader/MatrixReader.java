package matrix.reader;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import matrix.exception.LanguageException;
import matrix.exception.MatrixFormatException;

/*
 * Questa interfaccia è stata creata per semplificare l'utilizzo delle seguenti interfacce
 *
 * Distances.java
 * Bootstrapper.java
 *
 * Questa necessità nasce dal fatto che entrambe devono mantenere un insieme minimale
 * di operazioni, per l'organizzazione degli algoritmi che le implementeranno.
 * Quindi, si è deciso di far ricadere la gestione delle matrici ad una nuova
 * classe MatrixReader.
 */

/**
 * Interfaccia che permette la gestione di una matrice dei linguaggi.
 * @author Cervesato Andrea - sawk @ gmail.com
 * @version 1.0
 */
public interface MatrixReader {

    /**
     * Caricare in memoria una matrice direttamente da un file.
     * @param inFile File in cui è contenuta la matrice
     * @throws IOException Il file può presentare diversi problemi di formattazione
     * e impossibilità di lettura.
     * @throws MatrixFormatException La matrice deve soddisfare tutti i requisiti richiesti
     * @throws LanguageException I linguaggi devono soddisfare i requisiti richiesti
     */
    void loadMatrixFromFile(File inFile)
            throws IOException, MatrixFormatException, LanguageException;

    /**
     * Carica una matrice dalla memoria.
     * @param alphabet Alfabeto associato alla matrice. Può valere null
     * @param matrix Matrice da caricare in forma di arraylist multiplo
     * @param languages Linguaggi da caricare in forma di arraylist
     * @throws MatrixFormatException La matrice deve soddisfare tutti i requisiti richiesti
     * @throws LanguageException I linguaggi devono soddisfare i requisiti richiesti
     */
    void setMatrix(ArrayList<String> alphabet, ArrayList<ArrayList<String>> matrix, ArrayList<String> languages )
            throws MatrixFormatException, LanguageException;

    /**
     * @return Numero di colonne della matrice caricata
     */
    int getColumns();

    /**
     * @return Numero di righe della matrice caricata
     */
    int getRows();

    /**
     * @return Matrice dei linguaggi
     */
    Object getMatrix();

    /**
     * @return Linguaggi salvati
     */
    Object getLanguages();

    /**
     * Visualizza sullo standard output la matrice salvata in memoria con formato Phylip.
     */
    void printMatrix();

    /**
     * Scrive in un file la matrice formattata per Phylip. Il nome con cui si
     * salva è definito da setFileName() e la cartella da setSavePath()
     * @throws IOException Avvengono eccezioni se si riscontrano problemi con la
     * scrittura del file
     */
    void printMatrixOnFile() throws IOException;

    /**
     * Scrive in un file la matrice formattata per Phylip
     * @param file File in cui salvare
     * @throws IOException Avvengono eccezioni se si riscontrano problemi con la
     * scrittura del file
     */
    void printMatrixOnFile(File file) throws IOException;
    
    /**
     * Configura la classe per salvare la matrice in una data cartella. È una scelta
     * ridondante, ma è stata fatta per mantenere indipendenza tra l'inizializzazione
     * della classe e le operazioni che si possono fare con essa.
     * @param savePath Cartella in cui salvare la matrice
     */
    void setSavePath(File savePath);

    /**
     * @param fileName Nome con cui salvare la matrice.
     */
    void setFileName(String fileName);
    
    /**
     * @return Nome del file su cui è salvata la matrice, o deve essere salvata.
     */
    String getFileName();

    /**
     * @return Il file da cui è stata prelevata la matrice. Se il metodo ritorna
     * una valore null, allora la matrice è stata prelevata dalla memoria.
     */
    File getRefererFile();

    /**
     * @return Il file in cui è stata salvata la matrice. Se è null, la matrice
     * non è mai stata scritta su file
     */
    File getSaveFile();

    /**
     * Ripristina le caratteristiche iniziali dell'oggetto.
     */
    void clear();
}
