package algorithm;

/**
 * Eccezione generica per la classe GenericAlgorithm.java
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class AlgorithmException extends Exception
{
    public AlgorithmException(String msg)
    {
        super("There's a problem with the algorithm: "+msg);
    }
}
