package parser.set.langcosts.table;

/**
 * Istance of this class is an element of the costs table
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class Cost
{
    /**
     * character that should be changed
     */
    public String a;

    /**
     * character that should be the substitute
     */
    public String b;

    /**
     * cost of substitution
     */
    public int cost;

    /**
     * Initialize an element of the costs table
     * @param a character that should be changed
     * @param b character that should be the substitute
     * @param cost cost of substitution 
     */
    public Cost(String a, String b, int cost)
    {
        this.a = a;
        this.b = b;
        this.cost = cost;
    }
}
