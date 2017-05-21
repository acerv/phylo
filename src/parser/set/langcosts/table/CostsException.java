
package parser.set.langcosts.table;

public class CostsException extends Exception
{
    public CostsException(String msg)
    {
        super("Error in costs table: "+msg);
    }
}
