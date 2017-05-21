/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matrix.exception;

/**
 *
 * @author Cervesato Andrea - sawk @ gmail.com
 */
public class LanguagesDoesNotExistException extends LanguageException {
    @Override
    public String toString()
    {
        return getMessage() + "non sono presenti linguaggi";
    }
}
