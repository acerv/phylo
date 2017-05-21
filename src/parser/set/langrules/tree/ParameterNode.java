package parser.set.langrules.tree;

/**
 * Parameter of one generic language
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class ParameterNode implements RuleNode
{
    // Position of this parameter in language
    private int position;

    // Value of this node
    private String curr_value;

    // Value wich is compared to current value
    private String comp_value;

    /**
     * Assign current character to this node
     * @param pos Position of parameter
     * @param value
     */
    public ParameterNode(int pos, String value)
    {
        position = pos;
        curr_value = value;
    }

    /**
     * Sets the value wich this node should be compared to
     * @param value
     */
    public void setCompareValue(String value)
    {
        this.comp_value = value;
    }

    /**
     * Returns position of this parameter into language
     * @return
     */
    public int getPosition()
    {
        return position;
    }

    /**
     * Print name of this node
     */
    public void print()
    {
        System.out.print("P"+position+"."+curr_value);
    }

    public boolean getValue()
    {
        return curr_value.equals(comp_value);
    }

    public String getCurrentValue() {
        return curr_value;
    }
}
