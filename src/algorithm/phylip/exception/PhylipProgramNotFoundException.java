
package algorithm.phylip.exception;

public class PhylipProgramNotFoundException extends PhylipException {
    @Override
    public String toString()
    {
        return getMessage() + "the phylip program doesn't exist, or it's not supported yet";
    }
}
