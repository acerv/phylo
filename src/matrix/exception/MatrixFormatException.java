/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matrix.exception;

/**
 *
 * @author Cervesato Andrea - sawk @ gmail.com
 */
public class MatrixFormatException extends Exception {
    public MatrixFormatException()
    {
        super("Problema con la matrice: \n");
    }
   
    @Override
    public String toString()
    {
        return getMessage() + "formato non corretto";
    }
}
