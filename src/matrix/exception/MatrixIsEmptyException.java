/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matrix.exception;

/**
 *
 * @author Cervesato Andrea - sawk @ gmail.com
 */
public class MatrixIsEmptyException extends MatrixFormatException{
    @Override
    public String toString()
    {
        return getMessage() + "la matrice è vuota.";
    }
}
