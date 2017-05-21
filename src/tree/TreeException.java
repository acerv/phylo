package tree;

public class TreeException extends Exception
{
    public TreeException(String msg)
    {
        super("Error on tree: "+msg);
    }
}
