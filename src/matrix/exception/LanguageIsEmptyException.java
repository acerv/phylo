package matrix.exception;

/**
 *
 * @author Cervesato Andrea - sawk @ gmail.com
 */
public class LanguageIsEmptyException extends LanguageException{
    @Override
    public String toString()
    {
        return getMessage() + "sono presenti linguaggi vuoti";
    }
}
