package matrix.reader;


import informations.Infos;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import matrix.exception.AlphabetNotDefined;
import matrix.exception.LanguageException;
import matrix.exception.LanguageIsEmptyException;
import matrix.exception.LanguageNotAbmittedException;
import matrix.exception.LanguagesDifferentThanRowsException;
import matrix.exception.LanguagesDoesNotExistException;
import matrix.exception.MatrixCharactersNotAbmitted;
import matrix.exception.MatrixDimensionException;
import matrix.exception.MatrixFormatException;
import matrix.exception.MatrixIsEmptyException;
import utility.Debugger;

/**
 * Questa classe permette di gestire una matrice dei caratteri
 * associata a dei linguaggi. È necessario ricordare che dopo aver istanziato la
 * classe è bene caricare la matrice da un file o dalla memoria:<br><br>
 * <code>
 * charMatrixReader matrix = new charMatrixReader();   // Istanza<br>
 * matrix.loadMatrixFromFile(/home/pinco/matrice.txt); // Matrice dal file<br>
 * </code>
 * oppure<br>
 * <code>
 * charMatrixReader matrix = new charMatrixReader();   // Istanza<br>
 * matrix.setMatrix(alphabet, matrix, language );      // Matrice da memoria<br><br>
 * </code>
 * Una volta salvata la matrice è possibile memorizzarla su file.<br><br>
 * <b>note:</b> la matrice dei caratteri ha come <b>righe</b> i caratteri associati
 * ad un linguaggio. Numero di righe = Numero di linguaggi.
 *
 * @author Cervesato Andrea - sawk @ gmail.com
 * @version 2.0
 */
public class CharMatrixReader implements MatrixReader, Cloneable
{
    /* ****************
     * PARTE PRIVATA  *
     * ****************/

    // Numero di righe e di colonne della matrice
    private int rows = 0;
    private int columns = 0;

    // Contiene la matrice dei caratteri
    private ArrayList<ArrayList<String>> charMatrix =
            new ArrayList<ArrayList<String>>();

    // Contiene una lista dei linguaggi associati alla matrice
    private ArrayList<String> languages = new ArrayList<String>();

    // Caratteri ammessi
    private ArrayList<String> abmittedChar = new ArrayList<String>();

    // Alpfabeto
    private ArrayList<String> alphabet = new ArrayList<String>();

    // Nome del file di output generato da printMatrixOnFile()
    private String fileName = "OutCharacterMatrix.txt";

    // La cartella  predefinita in cui salvare il file è la home dell'utente
    private static File DEFAULT_SAVE_PATH = new File(Infos.HOME);

    // Lettura da file
    private static String INVERTED = "INVERTED";
    private static String ALPHABET = "ALPHABET";
    private static String ASSIGNMENT = "=";
    private static String SEPARATOR = ",";
    private static String NEWLINE = "[\\s]*";
    private static String[] SPACES = {"\\s+", "(?m)^ +"};
    private static String DIMENSION = "[\\s]*[0-9]+[\\s]*[0-9]+";
    private static String LANGUAGE = "[\\s]*[a-zA-Z]+";

    // Cartella in cui salvare la matrice
    private File savePath = DEFAULT_SAVE_PATH;

    // File da cui è stata prelevata (eventualmente) la matrice. Se si mantiene null,
    // allora la matrice è stata caricata dalla memoria
    private File matrixFile = null;

    // File in cui è stata salvata la matrice. Se è null, la matrice non è mai
    // stata salvata in memoria
    private File saveFile = null;

    public CharMatrixReader()
    {
        abmittedChar.addAll(Arrays.asList(Infos.UNDEFINED_CHARACTERS));
    }

    /* Parse alphabet into the matrix file */
    private boolean parseAlphabet(BufferedReader matrixRead)
            throws IOException, AlphabetNotDefined
    {
        boolean found = false;
        String matrixLine = null;

        // Prima riga dev'essere l'alfabeto
        while ( (matrixLine = matrixRead.readLine()) != null )
        {
            // Elimino gli a capo nel file
            if ( matrixLine.matches(NEWLINE))
                continue;

            if(matrixLine.toUpperCase().contains(ALPHABET))
            {
                matrixLine = matrixLine.replaceAll(SPACES[0], "");
                matrixLine = matrixLine.replaceAll(SPACES[1], "");
                String alp[] = matrixLine.split(ASSIGNMENT);
                String[] alph = alp[1].split(SEPARATOR);
                String[] undef = Infos.UNDEFINED_CHARACTERS;

                // Remove undefined Phylo characters
                alphabet.addAll(Arrays.asList(alph));
                for(int i = 0; i < undef.length; i++)
                {
                    if(alphabet.contains(undef[i]))
                    {
                        int j = alphabet.indexOf(undef[i]);
                        alphabet.remove(j);
                    }
                }

                if(!alphabet.isEmpty())
                {
                    Debugger.print("> Alphabet: ");
                    for(int i = 0; i < alphabet.size(); i++)
                    {
                        abmittedChar.add(alphabet.get(i));
                        Debugger.print(alphabet.get(i)+" ");
                    }
                    found = true;
                    break;
                }
                else
                {
                    throw new AlphabetNotDefined();
                }
            }
        }

        return found;
    }

    /* Parse a normal matrix with languages on rows */
    private void loadNormalMatrix(BufferedReader matrixRead)
            throws MatrixFormatException,
            IOException, LanguageException
    {
        int row = 0;
        String[] buff = null;
        String matrixLine;

        /* Parse the alphabet */
        boolean found = parseAlphabet(matrixRead);

        Debugger.println();
        if(!found) throw new AlphabetNotDefined();

        // Il resto del file
        while ( (matrixLine = matrixRead.readLine()) != null )
        {
            // Elimino gli a capo nel file
            if ( matrixLine.matches(NEWLINE))
            {
                continue;
            }

            // Elimino il controllo sulla prima riga contenente numero di righe e colonne
            // nella matrice. Forma "integer integer"
            if ( matrixLine.matches(DIMENSION))
            {
                Debugger.println("> Ignoring dimension of matrix found into the file");
                continue;
            }

            // Aggiungo una riga alla matrice
            this.getMatrix().add(new ArrayList<String>());

            // Salvataggio degli elementi della riga
            matrixLine = matrixLine.replaceAll(SPACES[0], " ");
            matrixLine = matrixLine.replaceAll(SPACES[1], "");
            buff = matrixLine.split(" ");

            Debugger.println("> Element: \""+buff[0]+"\"");

            // Controllo sulla correttezza dei linguaggi:
            // Linguaggi vuoti ?
            if ( buff[0].matches(NEWLINE) )
                throw new LanguageIsEmptyException();

            // Linguaggio ammesso ?
            if ( !buff[0].matches(LANGUAGE) )
                throw new LanguageNotAbmittedException(buff[0]);

            // Caratteri ammessi?
            for ( int i = 0; i < alphabet.size(); i++ )
            {
                if ( buff[0].contains(alphabet.get(i)) )
                    throw new LanguageNotAbmittedException(buff[0]);
            }

            // Salvo il nome della lingua
            this.getLanguages().add(buff[0]);

            // Caricamento dati nella matrice
            for (int column = 1; column < buff.length; column++) {
                // Aggiungo un elemento
                ((ArrayList<String>) this.getMatrix().get(row)).add(buff[column]);
            }

            row++;
        }

        // Verifica della matrice
        this.verifyMatrixAndSave(this.getMatrix(), this.getLanguages());

        Debugger.println("> Total elements: "+this.getRows()+"\n> Total characters: "+this.getColumns());
        Debugger.println("> Matrix is saved in memory");

        // chiudo il file
        matrixRead.close();
    }

    /* Parse matrix with languages on columns */
    private void loadInvertedMatrix(BufferedReader matrixRead)
            throws MatrixFormatException, IOException, LanguageException
    {
        String[] buff = null;
        String matrixLine;

        /* Parse the alphabet */
        boolean found = parseAlphabet(matrixRead);

        Debugger.println();
        if(!found) throw new AlphabetNotDefined();

        /* Read languages */
        while ( (matrixLine = matrixRead.readLine()) != null ) {
            if ( matrixLine.matches(NEWLINE))
                continue;

            if ( matrixLine.matches(DIMENSION)) {
                Debugger.println("> Ignoring dimension of matrix found into the file");
                continue;
            }

            /* First line is elements */
            matrixLine = matrixLine.replaceAll(SPACES[0], " ");
            matrixLine = matrixLine.replaceAll(SPACES[1], "");
            buff = matrixLine.split(" ");

            /* Populate array of elements */
            for(int i = 0; i < buff.length; i++) {
                if ( buff[i].matches(NEWLINE) )
                    throw new LanguageIsEmptyException();

                if ( !buff[i].matches(LANGUAGE) )
                    throw new LanguageNotAbmittedException(buff[i]);

                for ( int j = 0; j < alphabet.size(); j++ ) {
                    if ( buff[i].contains(alphabet.get(j)) )
                        throw new LanguageNotAbmittedException(buff[i]);
                }

                Debugger.println("> Element: \""+buff[i]+"\"");
                this.getLanguages().add(buff[i]);
            }

            if(buff.length == getLanguages().size()) break;
        }

        /* Initialize matrix */
        for(int i = 0; i < getLanguages().size(); i++)
            this.getMatrix().add(new ArrayList<String>());

        /* Populate matrix */
        while ( (matrixLine = matrixRead.readLine()) != null ) {
            matrixLine = matrixLine.replaceAll(SPACES[0], " ");
            matrixLine = matrixLine.replaceAll(SPACES[1], "");
            buff = matrixLine.split(" ");

            // Caricamento dati nella matrice
            for (int row = 0; row < getLanguages().size(); row++) {
                ((ArrayList<String>) this.getMatrix().get(row)).add(buff[row]);
            }
        }

        // Verifica della matrice
        this.verifyMatrixAndSave(this.getMatrix(), this.getLanguages());

        Debugger.println("> Total elements: "+this.getRows()+"\n> Total characters: "+this.getColumns());
        Debugger.println("> Matrix is saved in memory");

        // chiudo il file
        matrixRead.close();
    }

    /* Carica in memoria la matrice */
    private void loadMatrix() 
            throws MatrixFormatException,
            LanguageException,
            IOException
    {
        this.clear();

        BufferedReader matrixRead = new BufferedReader(new FileReader(this.matrixFile));
        String matrixLine = null; // Linea di una matrice

        Debugger.println("> Start loading matrix from "+this.matrixFile.getAbsolutePath());

        boolean inverted = false;

        /* First line is inverted declaration */
        while( (matrixLine = matrixRead.readLine()) != null ) {
            if ( matrixLine.matches(NEWLINE))
                continue;

            if(matrixLine.toUpperCase().contains(INVERTED)) {
                inverted = true;
                break;
            } else {
                inverted = false;
                break;
            }
        }

        /* Reset matrix reader to the first line of the file */
        matrixRead.close();
        matrixRead = new BufferedReader(new FileReader(this.matrixFile));
        
        /* Parse matrix file */
        if(inverted) {
            loadInvertedMatrix(matrixRead);
        } else {
            loadNormalMatrix(matrixRead);
        }
    }

    private void setRows(int n)
    {   
        this.rows = n;
    }

    private void setColumns(int n)
    {
        this.columns = n;
    }

    /*
     * Verifica la correttezza della matrice dei caratteri
     */
    private void verifyMatrixAndSave(ArrayList<ArrayList<String>> matrix, ArrayList<String> languages)
            throws MatrixFormatException, LanguageException
    {
        // Debugger.println("> Controllo di correttezza sulla matrice dei caratteri");
        int rowsSize = matrix.size();
        int languagesSize = languages.size();

        // Sono presenti linguaggi ?
        if ( languagesSize == 0 )
            throw new LanguagesDoesNotExistException();

        // Il numero di linguaggi è pari al numero di righe della matrice ?
        if ( languagesSize != rowsSize )
            throw new LanguagesDifferentThanRowsException(languagesSize,rowsSize);

        // La matrice è vuota ?
        if ( matrix.isEmpty() )
            throw new MatrixIsEmptyException();

        // Dimensione della prima riga
        int dim = matrix.get(0).size();

        // Dimensione della matrice
        int row_lenght    = matrix.size();
        int column_lenght = matrix.get(0).size();

        // La matrice ha dimensione corretta e contiene i caratteri ammessi ?
        for ( int i = 0; i < row_lenght; i++)
        {
            for ( int j = 0; j < column_lenght; j++ )
            {
                // Control if character is into the alphabet
                boolean notContained = true;
                
                for(int k = 0; k < abmittedChar.size(); k++)
                {
                    String a = matrix.get(i).get(j);
                    String b = abmittedChar.get(k);

                    if(a.equals(b))
                    {
                        notContained = false;
                        break;
                    }
                }

                if(notContained)
                    throw new MatrixCharactersNotAbmitted();

                // Controllo della dimensione
                if ( matrix.get(i).size() != dim )
                    throw new MatrixDimensionException();

                // Dimensione della riga corrente da comparare con la successiva
                dim = matrix.get(i).size();
            }
        }

        // La matrice è corretta e posso estrarre tutte le informazioni che mi servono
        this.getInformationsFromMatrix(matrix, languages);
    }

    // Estrae le informazioni dalla matrice
    private void getInformationsFromMatrix(ArrayList<ArrayList<String>> matrix, ArrayList<String> languages)
    {
        // Salvo matrice, linguaggi e dimensione
        this.charMatrix = matrix;
        this.languages = languages;
        this.setRows(matrix.size());
        this.setColumns(matrix.get(0).size());
    }


    /* *******************************************************
     * PARTE PUBBLICA E IMPLEMENTAZIONE DEI METODI ASTRATTI  *
     * *******************************************************/

    public void printMatrixOnFile() 
            throws IOException
    {
        // Apro il file
        FileOutputStream outMatrix = null;
        PrintStream defaultOut = new PrintStream(System.out);

        // Apro il file
        this.saveFile = new File(savePath.getAbsolutePath(), getFileName());

        outMatrix = new FileOutputStream(this.saveFile);

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

        Debugger.println("> Copied "+fileName+" in "+saveFile.getAbsolutePath());
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
            throws MatrixFormatException, LanguageException, IOException
    {
        // Salvo il riferimento al file
        this.matrixFile = inFile;
        this.loadMatrix();
    }

    public void setMatrix(ArrayList<String> alphabet, ArrayList<ArrayList<String>> matrix, ArrayList<String> language)
            throws MatrixFormatException, LanguageException
    {
        if( alphabet == null || alphabet.isEmpty())
            throw new AlphabetNotDefined();
        else
            this.alphabet = alphabet;

        // Adding undefined Phylo characters
        for(int i = 0; i < alphabet.size(); i++)
            abmittedChar.add(alphabet.get(i));

        // Verifico la correttezza dei dati in ingresso e salvo la matrice
        this.verifyMatrixAndSave(matrix, language);
    }

    public ArrayList<ArrayList<String>> getMatrix()
    {
        return this.charMatrix;
    }

    public ArrayList<String> getLanguages()
    {
        return this.languages;
    }

    public void printMatrix()
    {
        System.out.print(ALPHABET+" "+ASSIGNMENT+" ");
        for(int i = 0; i < alphabet.size(); i++)
        {
            System.out.print(alphabet.get(i));
            if(i != alphabet.size() -1)
                System.out.print(SEPARATOR+" ");
        }
        System.out.println();

        int i, j = 0; // i = righe, j = colonne

        System.out.println(this.getRows()+"  \t"+this.getColumns());
        
        for ( i = 0; i < this.getMatrix().size(); i++ )
        {
            System.out.print(this.getLanguages().get(i)+"  \t");
            for ( j = 0; j < ((ArrayList<String>)this.getMatrix().get(i)).size(); j++ )
            {
                System.out.print(((ArrayList<String>)this.getMatrix().get(i)).get(j) +"  \t");
            }
            System.out.println();
        }
    }

    public int getRows()
    {
        return rows;
    }

    public int getColumns()
    {
        return columns;
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

    public void clear() {
        this.charMatrix.clear();
        this.languages.clear();
        this.setColumns(0);
        this.setRows(0);
        this.setFileName("OutCharacterMatrix.txt");
        this.setSavePath(DEFAULT_SAVE_PATH);
    }

    public void setSavePath(File savePath) {
        this.savePath = savePath;
    }

    public File getSaveFile()
    {
        return saveFile;
    }

    /**
     * Returns alphabet used for this matrix
     * @return
     */
    public ArrayList<String> getAlphabet()
    {
        return alphabet;
    }

    @Override
    public Object clone()
            throws CloneNotSupportedException
    {
        return super.clone();
    }
}
