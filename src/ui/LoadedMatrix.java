
package ui;

import matrix.reader.MatrixReader;

/**
 * Associa un nome ad una matrice
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class LoadedMatrix
{
    private String name;
    private MatrixReader matrix;

    /**
     * Carica una matrice e le assegna un nome
     * @param name Nome della matrice
     * @param matrix Matrice da caricare
     */
    public LoadedMatrix(String name, MatrixReader matrix)
    {
        this.name = name;
        this.matrix = matrix;
    }

    /**
     * Ritorna il nome della matrice
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Setta il nome della matrice
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Ritorna la matrice
     * @return
     */
    public MatrixReader getMatrix()
    {
        return matrix;
    }
}
