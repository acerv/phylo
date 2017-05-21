/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matrix.exception;

/**
 *
 * @author Cervesato Andrea - sawk @ gmail.com
 */
public class LanguagesDifferentThanRowsException extends LanguageException{
    int languageSize = 0;
    int matrixRowsSize = 0;

    public LanguagesDifferentThanRowsException(int languagesSize, int matrixRowsSize)
    {
        this.languageSize = languagesSize;
        this.matrixRowsSize = matrixRowsSize;
    }
    @Override
    public String toString()
    {
        return getMessage() + "il numero di linguaggi è "+this.languageSize+", ma il numero di righe è "+this.matrixRowsSize;
    }
}
