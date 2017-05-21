package algorithm.phylip.exception;

public class PhylipNoConfigurationException extends PhylipException{
    @Override
    public String toString()
    {
        return getMessage() + "configuration doesn't exist";
    }
}
