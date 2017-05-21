package distance;

import informations.Infos;
import matrix.exception.LanguageException;
import matrix.exception.MatrixFormatException;
import matrix.reader.DistMatrixReader;
import matrix.reader.CharMatrixReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Classe che implementa l'interfaccia Distances con il calcolo della distanza
 * Jaccard ( distanza = numero_differenze / (numero_differenze + numero_identità )).
 * 
 * Esempio di utilizzo:<br><br>
 * <code>
 * charMatrixReader charMatrix = new charMatrixReader();<br>
 * Jaccard jac = new Jaccard(charMatrix);<br>
 * distMatrixReader distMatrix = jac.getMatrix();<br>
 * </code>
 * @author Cervesato Andrea - sawk @ gmail.com
 * @version 2.0
 */
public class Jaccard implements Distance
{   
    /* **************
     * PARTE PRIVATA
     * ***************/
    private ArrayList<ArrayList<String>> charMatrix =
            new ArrayList<ArrayList<String>>(); // Matrice dei caratteri
    private ArrayList<ArrayList<String>> distanceMatrix =
              new ArrayList<ArrayList<String>>(); // Matrice delle distanze
    private ArrayList<String> languages =
            new ArrayList<String>(); // Linguaggi

    // Matrici che forniscono informazioni sul numero di identità e differenze tra i linguaggi
    private ArrayList<ArrayList<Integer>> identitiesMatrix =
              new ArrayList<ArrayList<Integer>>();
    private ArrayList<ArrayList<Integer>> differenciesMatrix =
              new ArrayList<ArrayList<Integer>>();

    // Lista che fornisce il numero di caratteri non definiti per linguaggio
    private ArrayList<Integer> notDefinedCharacters =
              new ArrayList<Integer>();

    // Caratteri che rappresentano un carattere indefinito per i linguaggi
    private ArrayList<String> undefinedChars = new ArrayList<String>();

    // Oggetto per manipolare la matrice delle distanze
    private DistMatrixReader distMatrix = new DistMatrixReader();

    // Numero di cifre significative dei numeri
    private int PRECISION = 4; 

    public Jaccard()
    {
        undefinedChars.addAll(Arrays.asList(Infos.UNDEFINED_CHARACTERS));
    }

    //  Calcolo della distanza tra due lingue
    private BigDecimal computeJaccardDistance(int firstLang, int secondLang)
    {
        int diff = this.differenciesMatrix.get(firstLang).get(secondLang);
        int id   = this.identitiesMatrix.get(firstLang).get(secondLang);

        // Calcolo della distanza
        if ( diff != 0 )
        {
            BigDecimal dist = BigDecimal.ZERO.setScale(this.getPrecision()); // Distanza tra i linguaggi
            BigDecimal difference = new BigDecimal(diff).setScale(this.getPrecision()); // Numero di caratteri diversi
            BigDecimal identity   = new BigDecimal(id).setScale(this.getPrecision()); // Numero di caratteri uguali

            // Calcolo della distanza nella forma dist = a / (a+b)
            dist = difference.add(identity);
            dist = difference.divide(dist,RoundingMode.HALF_UP);
            
            return dist;
        }
        else
        {
            // Debugger.println("La lingua"+languages.get(firstLang)+" e "+languages.get(secondLang)+" sono uguali?!?!");
            return BigDecimal.ZERO.setScale(this.getPrecision());
        }
    }

    public void getInformationsFromMatrix()
    {
        // Numero di caratteri indefiniti.
        // Indice: 0 = prima lingua, 1 = seconda lingua
        int undef = 0;
        
        // Inizio a leggere le lingue
        for( int row = 0; row < charMatrix.size(); row++ )
        {
            this.differenciesMatrix.add(new ArrayList<Integer>());
            this.identitiesMatrix.add(new ArrayList<Integer>());

            // Creo le matrici delle distanze e delle differenze
            for ( int column = 0; column < row; column++ )
            {
                // Estraggo le righe (lingue) scelte
                ArrayList<String> firstRow  = (ArrayList<String>)charMatrix.get(row);
                ArrayList<String> secondRow = (ArrayList<String>)charMatrix.get(column);

                // Caratteri da confrontare
                String firstChar;
                String secondChar;

                // Numero di identità e differenze
                int id = 0;
                int diff = 0;

                undef = 0;

                // Trovo le identità e le differenze tra le lingue
                for ( int k = 0; k < charMatrix.get(0).size(); k++ )
                {
                    // Estraggo i caratteri
                    firstChar = firstRow.get(k);
                    secondChar = secondRow.get(k);


                    // Verifico se ci sono caratteri indefiniti
                    if (undefinedChars.contains(firstChar))
                    {
                        undef++;
                    }
                    else if(!undefinedChars.contains(secondChar))
                    {
                        // Se i caratteri sono uguali incremento l'identità di 1
                        // altrimenti incremento le differenze
                        if ( firstChar.equals(secondChar) )
                        {
                            id++;
                        }
                        else
                        {
                            diff++;
                        }
                    }
                }

                // Scrivo i risultati nelle matrici di identità e di differenze
                ((ArrayList<Integer>)differenciesMatrix.get(row)).add(diff);
                ((ArrayList<Integer>)identitiesMatrix.get(row)).add(id);

                // Completamento della matrice triangolare
                ((ArrayList<Integer>)differenciesMatrix.get(column)).add(diff);
                ((ArrayList<Integer>)identitiesMatrix.get(column)).add(id);
            }

            // Il numero di identità è massimo per lingue uguali
            ((ArrayList<Integer>)identitiesMatrix.get(row)).add(charMatrix.get(0).size());

            // Non ci sono differenze tra lingue uguali
            ((ArrayList<Integer>)differenciesMatrix.get(row)).add(0);

            // Salvo il numero di caratteri indefiniti per lingua
            this.notDefinedCharacters.add(undef);
        }
    }

    /* ***************
     * PARTE PUBBLICA
     * ***************/

    public void loadMatrix(CharMatrixReader Matrix)
            throws MatrixFormatException, LanguageException
    {
        // Pulizia
        this.distanceMatrix.clear();

        // Estraggo le informazioni dalla matrice dei caratteri
        this.charMatrix = Matrix.getMatrix();
        getInformationsFromMatrix();

        // Estraggo i linguaggi
        this.languages = Matrix.getLanguages();

        int dimension  = Matrix.getRows(); // Numero di linguaggi
        int row   = 0; // Rappresenta il primo linguaggio
        int column   = 0; // Rappresenta il secondo linguaggio

        // Distanza tra i linguaggi
        String doubleDist;

//        Debugger.println("> Calcolo delle distanze con il metodo Jaccard\n");

        // Inizio a leggere le lingue
        for( row = 0; row < dimension; row++ )
        {
            // Aggiungo una riga alle matrici
            this.distanceMatrix.add(new ArrayList<String>());

            // Creo la matrice triangolare inferiore e la completo
            for ( column = 0; column < row; column++ )
            {
                // Computo la distanza
                doubleDist = this.computeJaccardDistance(row, column).toString();

                // Aggiungo una colonna alla matrice
                ((ArrayList<String>)this.distanceMatrix.get(row)).add(doubleDist);

                // Completo la matrice triangolare
                ((ArrayList<String>)this.distanceMatrix.get(column)).add(doubleDist);
            }

            // CAMBIARE QUESTA RIGA PER LA PRECISIONE DELLO ZERO
            doubleDist = BigDecimal.ZERO.setScale(this.getPrecision()).toString();

            // Aggiungo uno zero perché le lingue sono uguali
            ((ArrayList<String>)this.distanceMatrix.get(row)).add(doubleDist);
        }
        // Configuro l'oggetto distMatrix istanziato
        distMatrix.setMatrix(null, this.distanceMatrix, this.languages);
    }

    public DistMatrixReader getDistMatrix()
    {
        return this.distMatrix;
    }

    /**
     * @return Numero di cifre significaive di un numero nella matrice.
     */
    public int getPrecision() {
        return PRECISION;
    }

    /**
     * @param precision Numero di cifre significaive di un numero nella matrice.
     */
    public void setPrecision(int precision) {
        this.PRECISION = precision;
    }

    public int getDistanceMethod()
    {
        return Infos.JACCARD;
    }
}
