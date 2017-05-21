package algorithm.phylip.exception;

import informations.Infos;

public class PhylipException extends Exception {
    public PhylipException()
    {
        super("Error on phylip "+Infos.PHYLIP_VERSION+": ");
    }
    
    public PhylipException(String msg)
    {
        super("Error on phylip "+Infos.PHYLIP_VERSION+": "+msg);
    }
}
