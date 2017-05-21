package parser.set.langcosts.table;

import informations.Infos;
import parser.set.langrules.tree.RuleTree;
import java.util.ArrayList;
import utility.Debugger;

/**
 * This class uses a table to get informations of passage from character to
 * character in defined language. In Rows are defined characters that should
 * be changed, and in Columns are defined characters that we want to sub with
 * first ones.
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class CostsTable
{
    // Number of character for associated language
    private int sizeOfAbmittedChars;

    // language
    private ArrayList<String> alphabet;

    // abmitted chars = alphabet + undefined characters
    private ArrayList<String> abmittedChars = new ArrayList<String>();

    // Table of definition
    private int[][] table;

    // RuleTree
    private RuleTree definition;

    // true if it's a default table (no ruletree)
    private boolean def = false;

    /**
     * Initialize class with associated parameter and alphabet
     * @param alph Alphabet for the table
     * @param def True if this is a default table
     */
    public CostsTable(ArrayList<String> alph, boolean def)
    {
        alphabet = alph;
        abmittedChars = Infos.getAbmittedChars(alphabet);
        sizeOfAbmittedChars = abmittedChars.size();
        this.def = def;

        // initialize the table
        table = new int[sizeOfAbmittedChars][sizeOfAbmittedChars];
    }

    /**
     * Adds a tree which defines possibility to assign costs of mutations
     * @param t Tree with rules for costs table
     */
    public void addRuleTree(RuleTree t)
    {
        this.definition = t;
    }

    /**
     * Returs a tree which defines possibility to assign costs of mutations
     * @return
     */
    public RuleTree getRuleTree()
    {
        return definition;
    }

    /** @return the associated table */
    public int[][] getTable() {
        return table;
    }

    /**
     * Returns true if this table is a default table (no RuleTree)
     * @return
     */
    public boolean isDefaultTable()
    {
        return def;
    }

    /**
     * Populates the table
     * @param c Element of costs table
     * @throws CostsException
     */
    public void addElement(Cost c)
            throws CostsException
    {
        // Lets see if a and b are into the alphabet
        if(abmittedChars.contains(c.a) && abmittedChars.contains(c.b))
        {
            int i = abmittedChars.indexOf(c.a);
            int j = abmittedChars.indexOf(c.b);

            table[i][j] = c.cost;
        }
        else // outo the alphabet
        {
            throw new CostsException("language for this table doesn't contain "
                    + "'"+c.a+"' or '"+c.b+"' character");
        }
    }

    /**
     * Returns cost of mutation from a to b
     * @param a character that should be changed
     * @param b character that should be the substitute
     * @return
     * @throws CostsException 
     */
    public int getCost(String a, String b) throws CostsException
    {
        // Lets see if a and b are into the alphabet
        if(abmittedChars.contains(a) && abmittedChars.contains(b))
        {
            // Get indexes of characters
            int i = abmittedChars.indexOf(a);
            int j = abmittedChars.indexOf(b);

            return table[i][j];
        }
        else // outo the alphabet
        {
            if(abmittedChars.contains(a) && !abmittedChars.contains(b))
                throw new CostsException("language for this table doesn't contain "+b+"' character");
            else if(!abmittedChars.contains(a) && abmittedChars.contains(b))
                throw new CostsException("language for this table doesn't contain "+a+"' character");

            return -1;
        }
    }

    /**
     * Returns cost of mutation from a to b
     * @param a index of first character
     * @param b index of second character
     * @return cost of mutation
     * @throws CostsException
     */
    public int getCost(int a, int b) throws CostsException
    {
        // Lets see if a and b are into the alphabet
        if(a < abmittedChars.size() && b < abmittedChars.size())
        {
            return table[a][b];
        }
        else // outo the alphabet
        {
            if(a < abmittedChars.size() && !(b < abmittedChars.size()))
                throw new CostsException("language for this table doesn't contain "+b+"' character");
            else if(!(a < abmittedChars.size()) && b < abmittedChars.size())
                throw new CostsException("language for this table doesn't contain "+a+"' character");

            return -1;
        }
    }

    /**
     * Print table in its own formatted language
     */
    public void print()
    {
        int n = 0;
        int m = 0;

        // Count number of non zero costs in the table
        for(int i = 0; i < sizeOfAbmittedChars; i++)
            for(int j = 0; j < sizeOfAbmittedChars; j++)
                if(table[i][j] != 0) n++;

        for(int i = 0; i < sizeOfAbmittedChars; i++)
            for(int j = 0; j < sizeOfAbmittedChars; j++)
                if(table[i][j] != 0)
                {
                    System.out.print("("+abmittedChars.get(i)+","+abmittedChars.get(j)+"):"+table[i][j]);
                    m++;
                    if(n != m) System.out.print(", ");
                }
    }

    /**
     * Print table in system.out
     */
    public void printTable()
    {
        // Print horizontal alphabet
        if(isDefaultTable())
            Debugger.println("DEFAULT TABLE:");
        else
            Debugger.println("COSTS TABLE:");

        // Print vertical alphabet + table
        for(int i = 0; i < sizeOfAbmittedChars; i++)
        {
            Debugger.print(abmittedChars.get(i)+"| ");
            for(int j = 0; j < sizeOfAbmittedChars; j++)
            {
                Debugger.print(table[i][j]+" ");
            }
            Debugger.println();
        }
        Debugger.println("");
    }
}
