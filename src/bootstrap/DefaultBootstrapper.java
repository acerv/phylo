package bootstrap;


import matrix.exception.LanguageException;
import matrix.exception.MatrixFormatException;
import matrix.reader.DistMatrixReader;
import matrix.reader.CharMatrixReader;
import informations.Infos;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import utility.Debugger;

/**
 * Semplice bootstrapper per Phylo
 * @author Cervesato Andrea - sawk @ gmail.com
 */
public class DefaultBootstrapper extends Bootstrapper
{
    /**
     * Inizializza il numero di bootstrap. Questa imposizione è stata pensata per
     * non dimenticare mai di definire il numero di matrici da generare.
     * @param bootstrpsNumber Numero di bootstrap
     * @param distanceMethod Indice del metodo delle distanze specificato
     * in Infos.java
     */
    public DefaultBootstrapper(int bootstrpsNumber, int distanceMethod)
    {
        super(bootstrpsNumber, distanceMethod);
    }

    private ArrayList<ArrayList<String>> bootstrap(ArrayList<ArrayList<String>> matrix)
    {
        // Matrice di bootstrap
        ArrayList<ArrayList<String>> bootstrapMatrix = new ArrayList<ArrayList<String>>();

        // Generatore di numeri casuali
        Random randomGenerator = new Random();
        int randomNumber       = 0;

        int rows = matrix.size();
        int columns = matrix.get(0).size();

        // i = numero di colonna
        for ( int i = 0; i < columns; i++ )
        {
            // Numero di colonna da sostituire scelta a caso
            randomNumber = randomGenerator.nextInt(columns);
            // Debugger.println("-> Numero random generato: "+randomNumber);

            // k = numero di riga
            for ( int k = 0; k < rows; k++ )
            {
                // Nuova riga se è il primo ciclo
                if ( i == 0)
                   bootstrapMatrix.add(new ArrayList<String>());

                // Memorizzo i caratteri
                ((ArrayList<String>)bootstrapMatrix.get(k)).add(matrix.get(k).get(randomNumber));
            }
        }

        return bootstrapMatrix;
    }
    
    public void loadMatrix(CharMatrixReader charMatrix)
            throws IOException, MatrixFormatException, LanguageException
    {
        Debugger.println("> Generate bootstrap matrices");
        
        // Matrice dei caratteri
        ArrayList<ArrayList<String>> matrix = charMatrix.getMatrix();

        // Genero le matrici di bootstrap
        for ( int i = 0; i < this.getBootstrapsNumber(); i++ )
        {
            Debugger.print((i+1)+"..");

            // Matrice delle distanze
            DistMatrixReader distMatrix = new DistMatrixReader();

            // Classe per gestire la matrice bootstrappata ottenuta
            CharMatrixReader bootstrapMatrix = new CharMatrixReader();

            // Inserisco la matrice nell'oggetto
            bootstrapMatrix.setMatrix
                    (
                    charMatrix.getAlphabet(),
                    (ArrayList<ArrayList<String>>)this.bootstrap(matrix),
                    charMatrix.getLanguages()
                    );

            // Metodo per il calcolo della matrice delle distanze
            setDistance(Infos.getDistanceAlgorithm(distanceMethod));

            // Calcolo delle distanze
            getDistance().loadMatrix(bootstrapMatrix);
            distMatrix = getDistance().getDistMatrix();

            // Salvo la matrice delle distanze generata
            getBootstraps().add( new DistMatrixReader() );
            getBootstraps().get(i).setMatrix
                    (
                    charMatrix.getAlphabet(),
                    distMatrix.getMatrix(),
                    charMatrix.getLanguages()
                    );
        }
        Debugger.println("complete!");
        Debugger.println("> It has been created " + this.getBootstrapsNumber() + " matrices");
    }

    public int getBootstrapMethod()
    {
        return Infos.DEFAULT_BOOTSTRAP;
    }
}
