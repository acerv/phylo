package parser.set.langcosts.table;

import parser.set.langrules.tree.RuleTree;
import parser.set.langrules.tree.RulesException;
import java.util.ArrayList;

/**
 * This class is used by parser to get information of costs for specified parameter.
 * It abstracts the formal definition of costs into the costs file:<br>
 * <code>
 * P1<br>
 * {<br>
 *      RULE => ASSIGNMENT OF TABLE ;<br>
 * }<br>
 * </code>
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class Parameter
{
    private int position;
    private ArrayList<CostsTable> tables;
    private CostsTable defaultTable;

    /**
     * Initialize this class
     * @param par Number of parameters
     * @param pos Position of the parameter
     * @param tab Table associated to this parameter
     * @param alp Alphabet of the language associated to the table
     * @throws CostsException
     */
    public Parameter(int par, int pos, ArrayList<CostsTable> tab)
            throws CostsException
    {
        if(pos > par)
            throw new CostsException("parameter P"+pos+" could not be "
                    + "initialized in a set of "+par+" max parameters");
        
        position = pos;
        tables = tab;

        for(int i = 0; i < tables.size(); i++)
            if(tables.get(i).isDefaultTable())
                defaultTable = tables.get(i);
    }

    /** @return tables for this parameter */
    public ArrayList<CostsTable> getTables() {
        return tables;
    }

    /** @return default table. null if it doesnt exist */
    public CostsTable getDefaultTable() {
        return defaultTable;
    }
    
    /**
     * Returns cost of mutation from character to another,
     * taking into account the rules for the table of costs. It includes
     * costs from default table and the mutations
     * could be self mutations (example: 'a' to 'a' character).
     * @param a character that should be changed
     * @param b character that should be the substitute
     * @param language Language associated to the cost
     * @return cost of mutation for specified parameters. If -1 it means that
     * this parameter has not an associated cost for this parameter
     * @throws RulesException
     * @throws CostsException
     */
    public int getCostFor(String a, String b, ArrayList<String> language)
            throws RulesException, CostsException
    {
        for(int i = 0; i < tables.size(); i++)
        {
            if(tables.get(i).isDefaultTable())
                continue;

            // Tree of rules
            RuleTree tree = tables.get(i).getRuleTree();

            // Get cost from defined table
            if(tree.isDefinedDependent(language))
            {
                if(tables.get(i).getCost(a, b) == 0)
                    break; // go to default table
                else
                    return tables.get(i).getCost(a, b);
            }
        }

        // Default table
        if(defaultTable != null)
            return defaultTable.getCost(a, b);
        else
            return -1;
    }

    /**
     * Returns cost of mutation from character to another,
     * taking into account the rules for the table of costs. It includes
     * costs from default table and the mutations
     * could be self mutations (example: 'a' to 'a' character).
     * @param a index of character that should be changed
     * @param b index of character that should be the substitute
     * @param language Language associated to the cost
     * @return cost of mutation for specified parameters. If -1 it means that
     * this parameter has not an associated cost for this parameter
     * @throws RulesException
     * @throws CostsException
     */
    public int getCostFor(int a, int b, ArrayList<String> language)
            throws RulesException, CostsException
    {
        for(int i = 0; i < tables.size(); i++)
        {
            if(tables.get(i).isDefaultTable())
                continue;

            // Tree of rules
            RuleTree tree = tables.get(i).getRuleTree();

            // Get cost from defined table
            if(tree.isDefinedDependent(language))
            {
                if(tables.get(i).getCost(a, b) == 0)
                    break; // go to default table
                else
                    return tables.get(i).getCost(a, b);
            }
        }

        // Default table
        if(defaultTable != null)
            return defaultTable.getCost(a, b);
        else
            return -1;
    }

    /**
     * @return Position of this parameter
     */
    public int getPosition()
    {
        return position;
    }

    /**
     * Print parameter into its own formatted language
     */
    public void print()
    {
        System.out.println("P"+position+" {");
        for(int i = 0; i < tables.size(); i++)
        {
            if(tables.get(i).isDefaultTable())
            {
                System.out.print("\tDEFAULT => ");
            }
            else
            {
                System.out.print("\t");
                tables.get(i).getRuleTree().print();
                System.out.print(" => ");
            }

            tables.get(i).print();
            System.out.println(";");
        }
        System.out.println("}\n");
    }
}
