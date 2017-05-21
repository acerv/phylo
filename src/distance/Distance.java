package distance;


import matrix.exception.LanguageException;
import matrix.exception.MatrixFormatException;
import matrix.reader.DistMatrixReader;
import matrix.reader.CharMatrixReader;


/**
 * Interfaccia che carica in memoria la matrice dei caratteri da un file e
 * genera una matrice delle distanze. Input: charMatrixReader, Output: distMatrixReader
 * 
 * @author Cervesato Andrea - sawk @ gmail.com
 * @version 2.0
 */
public interface Distance
{
    /**
     * Calcola la matrice delle distanze da una matrice dei caratteri.
     * @param readMatrix Oggetto contenente la matrice dei caratteri.
     * @throws MatrixFormatException La matrice deve soddisfare le ipotesi richieste
     * @throws LanguageException I linguaggi devono soddisfare le ipotesi richieste
     */
    void loadMatrix(CharMatrixReader readMatrix)
         throws MatrixFormatException, LanguageException;
    
    /**
     * @return Matrice delle distanze
     */
    DistMatrixReader getDistMatrix();

    /**
     * @return the index associated of this distance method defined in Infos.java
     */
    int getDistanceMethod();
}
