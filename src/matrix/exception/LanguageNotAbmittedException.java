/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matrix.exception;

/**
 *
 * @author Cervesato Andrea - sawk @ gmail.com
 */
public class LanguageNotAbmittedException extends LanguageException{
    String languageNotAbmitted = null;

    public LanguageNotAbmittedException(String languageNotAbmitted)
    {
        this.languageNotAbmitted = languageNotAbmitted;
    }
    @Override
    public String toString()
    {
        return getMessage() + this.languageNotAbmitted+" è un linguaggio non ammesso";
    }
}
