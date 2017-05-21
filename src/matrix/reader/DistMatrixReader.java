package matrix.reader;


import informations.Infos;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import matrix.exception.LanguageException;
import matrix.exception.LanguageIsEmptyException;
import matrix.exception.LanguageNotAbmittedException;
import matrix.exception.LanguagesDifferentThanRowsException;
import matrix.exception.LanguagesDoesNotExistException;
import matrix.exception.MatrixFormatException;
import matrix.exception.MatrixIsEmptyException;
import matrix.exception.MatrixPositiveException;
import matrix.exception.MatrixSquareException;
import matrix.exception.MatrixTriangularException;
import utility.Debugger;

/**
 * Questa classe permette di gestire facilmente matrici delle distanze associata
 * a dei linguaggi. La matrice può essere caricata da memoria o da file e si può
 * ricercare la distanza tra due lingue per nome della lingua o indice relativo
 * ad esse.
 * @author Cervesato Andrea - sawk @ gmail.com
 * @version 2.0
 */
public class DistMatrixReader implements MatrixReader, Cloneable {


    /* ****************
     * PARTE PRIVATA  *
     * ****************/

     // Matrice delle distanze
    private ArrayList<ArrayList<String>> distMatrix =
              new ArrayList<ArrayList<String>>();
    
    // Lingue all'interno della matrice
    private ArrayList<String> languages = new ArrayList<String>();

    private int dimension = 0; // Dimensione della matrice nxn delle distanze

    // Nome del file usato per memorizzare su disco la matrice delle distanze
    private String fileName = "OutDistanceMatrix.txt";

    // La cartella  predefinita in cui salvare il file è la home dell'utente
    private static File DEFAULT_SAVE_PATH = new File(Infos.HOME);

    // Cartella in cui salvare la matrice
    private File savePath = DEFAULT_SAVE_PATH;

    // File da cui è stata prelevata (eventualmente) la matrice. Se il suo valore
    // si mantiene null, allora la matrice è stata caricata dalla memoria
    private File matrixFile = null;

    // File in cui è stata salvata la matrice. Se è null, la matrice non è mai
    // stata salvata in memoria
    private File saveFile = null;


    /*
     * Carica la matrice specificata da refererFile
     */
     private void loadMatrix()
             throws MatrixFormatException,
             LanguageException,
             FileNotFoundException,
             IOException
     {
            this.clear();
            
            // Apro il file
            BufferedReader matrixRead = new BufferedReader(new FileReader(this.matrixFile));

            String matrixLine = null; // Riga di una matrice

            // Variabili di appoggio
            String buff[] = null;
            int row = 0;

            Debugger.println("> Start loading matrix from "+this.matrixFile.getAbsolutePath());

            // Inzio lettura del file
            while ((matrixLine = matrixRead.readLine()) != null) {

                // Elimino gli a capo nel file
                if ( matrixLine.matches("[\\s]*"))
                    continue;
                
                // Elimino il controllo sulla prima riga contenente numero di righe e colonne
                // nella matrice. Forma "integer integer"
                if ( matrixLine.matches("[\\s]*[0-9]+[\\s]*[0-9]+"))
                {
                    Debugger.println("> Ignoring dimension of matrix found into the file");
                    continue;
                }

                // Aggiungo una riga alla matrice
                this.getMatrix().add(new ArrayList<String>());
                // Parsing
                matrixLine = matrixLine.replaceAll("\\s+", " ");
                matrixLine = matrixLine.replaceAll("(?m)^ +", "");
                buff = matrixLine.split(" ");

                // Controllo sulla correttezza dei linguaggi:
                // Linguaggi vuoti ?
                if ( buff[0].matches("[\\s]*") )
                    throw new LanguageIsEmptyException();

                // Linguaggio ammesso ?
                if ( !buff[0].matches("[\\s]*[a-zA-Z]+") )
                    throw new LanguageNotAbmittedException(buff[0]);

                // Aggiungo la lista dei linguaggi
                this.getLanguages().add(buff[0]);

                for (int i = 1; i < buff.length; i++) {
                    ((ArrayList<String>) this.getMatrix().get(row)).add(buff[i]);
                }

                // Tiene traccia della dimensione della matrice
                row++;
            }
            
            // Verifico se la matrice rispetta le ipotesi
            this.verifyMatrix( this.getMatrix(), this.getLanguages() );

            // Dimensione della matrice
            this.setDimension(row);
            Debugger.println("> Total elements: "+this.getRows()+"\n> Total characters: "+this.getColumns());
            Debugger.println("> Matrix has been saved in memory");

            // Chiudo il file
            matrixRead.close();
     }

     private void fixString(String line)
     {
        line = line.replaceAll("\\s+", " ");
     }

    /*
     * Verifica la correttezza della matrice nel file. Ricordare che la matrice
     * deve essere triangolare, non vuota, quadrata e con distanze >= 0.
     */
     private void verifyMatrix(ArrayList<ArrayList<String>> matrix, ArrayList<String> language)
             throws MatrixFormatException, LanguageException
     {
         // Setto la dimensione della matrice
         int matrix_size = matrix.size();
         int languagesSize = language.size();

         // Debugger.println("> Controllo di correttezza sulla matrice delle distanze");
         
         // Sono presenti linguaggi all'interno della matrice ?
         if ( language.isEmpty() )
            throw new LanguagesDoesNotExistException();
         
         // Il numero di linguaggi è pari alla grandezza della matrice ?
         if ( matrix_size != languagesSize )
            throw new LanguagesDifferentThanRowsException(languagesSize, matrix_size);

         // La matrice è vuota ?
         if ( matrix.isEmpty() || matrix.isEmpty() )
            throw new MatrixIsEmptyException();

         // La matrice è quadrata ?
         else if ( matrix.size() != matrix.get(0).size() )
            throw new MatrixSquareException();

         // Verifico l'ipotesi di positività e triangolarità
         double parseDouble = 0;
         for ( int i = 0; i < matrix_size; i++ )
         {
            for ( int j = 0; j < matrix_size; j++ )
            {
                // La matrice è positiva ?
                parseDouble = Double.parseDouble((matrix.get(i)).get(j));
                if ( parseDouble < 0 )
                    throw new MatrixPositiveException();

                // La matrice è triangolare ?
                if ( !(matrix.get(i)).get(j).equals((matrix.get(j)).get(i)) )
                    throw new MatrixTriangularException();
            }
         }
     }


     /*
      * Dichiara la grandezza della matrice
      */
     private void setDimension(int n)
     {
         this.dimension = n;
     }


     
    /* ****************
     * PARTE PUBBLICA *
     * ***************/


     public void printMatrixOnFile() 
             throws IOException
     {

        Debugger.println("> Write file in "+this.savePath.getAbsolutePath());
        
        // Apro il file
        FileOutputStream outMatrix = null;
        PrintStream defaultOut = new PrintStream(System.out);

        this.saveFile = new File(savePath.getAbsolutePath(), getFileName());

        outMatrix = new FileOutputStream(this.saveFile);

        // Reindirizzo il flusso di println su System.out
        PrintStream print = new PrintStream(outMatrix);
        System.setOut(print);

        this.printMatrix();

        // Chiusura
        print.flush();
        print.close();
        outMatrix.close();
        
        // riporto il buffer originale
        System.setOut(defaultOut);
    }
     
    public void printMatrixOnFile(File file) throws IOException
    {
        // Apro il file
        FileOutputStream outMatrix = null;
        PrintStream defaultOut = new PrintStream(System.out);
        outMatrix = new FileOutputStream(file);

        // Reindirizzo il flusso di println su System.out
        PrintStream print = new PrintStream(outMatrix);
        System.setOut(print);

        // Scrivo nel file la matrice
        this.printMatrix();

        // Chiusura
        print.flush();
        print.close();
        outMatrix.close();

        // riporto il buffer originale
        System.setOut(defaultOut);
    }

    public void loadMatrixFromFile(File inFile)
        throws IOException, MatrixFormatException, LanguageException
    {
        this.matrixFile = inFile;
        
        this.loadMatrix();
    }

    public void setMatrix(ArrayList<String> alphabet, ArrayList<ArrayList<String>> matrix, ArrayList<String> language)
            throws MatrixFormatException, LanguageException
    {
        this.verifyMatrix(matrix, language);

        // La matrice
        this.distMatrix = matrix;

        // Dimensione della matrice (nxn)
        this.setDimension(matrix.size());

        // Lista dei linguaggi
        this.languages = language;
    }
    
    public int getColumns() {
        return this.dimension;
    }

    public int getRows() {
        return this.dimension;
    }

    public ArrayList<ArrayList<String>> getMatrix() {
        return this.distMatrix;
    }

    public ArrayList<String> getLanguages() {
        return this.languages;
    }

    /* Non si capisce bene per quale motivo, ma Phylip preleva in ingresso
     * una matrice che ha elementi distanziati con più di uno spazio.
     * Quindi, per necessità, la stampa è conforme a Phylip
     */
    public void printMatrix() {
        int i, j = 0; // i = righe, j = colonne

        System.out.println("     "+this.getRows());

        for ( i = 0; i < this.getMatrix().size(); i++ )
        {
            System.out.print("     "+this.getLanguages().get(i)+"     \t");
            for ( j = 0; j < ((ArrayList<String>)this.getMatrix().get(i)).size(); j++ )
            {
                System.out.print(((ArrayList<String>)this.getMatrix().get(i)).get(j) +"     \t");
            }
            System.out.println();
        }
    }


    public String getFileName() {
        return fileName;
    }

    public File getRefererFile()
    {
        return this.matrixFile;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Ritorna la distanza tra due linguaggi.
     * @param firstLanguage Indice del primo linguaggio
     * @param secondLanguage Indice del secondo linguaggio
     * @return Distanza tra due lingue.
     */
    public String getDistance(int firstLanguage, int secondLanguage)
    {
        return ((ArrayList<String>)this.getMatrix().get(firstLanguage)).get(secondLanguage);
    }

    /**
     * Ritorna la distanza tra due linguaggi.
     * @param firstLanguage Nome del primo linguaggio
     * @param secondLanguage Nome del secondo linguaggio
     * @return Distanza tra due lingue.
     */
    public String getDistance(String firstLanguage, String secondLanguage)
    {
        // Distanza tra i linguaggi
        String distance;

        // Estraggo gli indici dei linguaggi
        int firstIndex  = this.getLanguages().indexOf(firstLanguage);
        int secondIndex = this.getLanguages().indexOf(secondLanguage);

        // Trovo la distanza tra i linguaggi
        distance = this.getDistance(firstIndex, secondIndex);

        return distance;
    }

    public void clear() {
        this.getMatrix().clear();
        this.getLanguages().clear();
        this.setDimension(0);
        this.setFileName("OutDistanceMatrix.txt");
        this.setSavePath(DEFAULT_SAVE_PATH);
    }

    public void setSavePath(File savePath) {
        this.savePath = savePath;
    }

    public File getSaveFile()
    {
        return saveFile;
    }

    @Override
    public Object clone()
            throws CloneNotSupportedException
    {
        return super.clone();
    }
}
