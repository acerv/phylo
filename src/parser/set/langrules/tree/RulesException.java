package parser.set.langrules.tree;

public class RulesException extends Exception
{
    public RulesException(String msg)
    {
        super("Error on sets definition: "+msg);
    }
}
