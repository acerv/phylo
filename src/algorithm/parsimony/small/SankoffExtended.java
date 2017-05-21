package algorithm.parsimony.small;

import informations.Infos;
import java.io.File;
import parser.set.langcosts.table.CostsException;
import parser.set.langcosts.table.ParametersSet;
import parser.set.langrules.tree.RulesSet;
import parser.set.langrules.tree.RulesException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements the Sankoff algorithm with an extended part using
 * costs assignment and characters definition. This is possible by
 * using the .skff file which contains:<br>
 * 1. the file of defined tree in the newick format<br>
 * 2. the file with character matrix<br>
 * 3. the file of parameters definitions<br>
 * 4. the file of costs<br>
 * 5. the file which contains the using alphabet<br>
 * Otherway it's possible to use it with setInput(SkffReader read) method,
 * which permits to use this class into others algorithms.
 * @author Cervesato Andrea sawk.ita @ gmail.com
 */
public class SankoffExtended extends Sankoff
{
    private RulesSet rules;
    private ParametersSet parameters;

    public SankoffExtended()
    {
        setValue(Infos.SANKOFF);
        setName(Infos.SUPPORTED_ALGORITHMS[Infos.SANKOFF]);
    }

    @Override
    public void setInput(File input) throws Throwable
    {
        this.input = input;
        
        SkffReader read = new SkffReader();
        read.setInputFile(input);
        read.load();

        tree = read.tree;
        matrix = read.matrix;
        alphabet = read.infos.getAlphabet();
        rules = read.rules;
        parameters = read.costs;

        // This is the same for everything
        abmittedChars = Infos.getAbmittedChars(alphabet);
    }

    /**
     * Initialize class from memory. It's used by the branch & bound algorithm
     * @param read .skff file reader
     */
    public void setInput(SkffReader read)
    {
        tree = read.tree;
        matrix = read.matrix;
        alphabet = read.infos.getAlphabet();
        rules = read.rules;
        parameters = read.costs;

        // This is the same for everything
        abmittedChars = Infos.getAbmittedChars(alphabet);
    }
    
    /**
     * This method is used by Sankoff standard method to get
     * matrix of costs. In SankoffExtended algorithm this method
     * becomes deprecated because usages of RuleSet and ParametersSet classes
     * @param matrix Matrix of costs
     * @deprecated Not useful into SankoffExtended algorithm
     */
    @Deprecated
    @Override
    public void setMatrixOfCosts(int[][] matrix)
    {
        matrixOfCosts = matrix;
    }
    
    // Overriding this method it's possible to use the set of rules and
    // set of costs into the Sankoff algorithm
    @Override
    protected int getCost(int m, int n) throws RulesException
    {
        int cost = 0;
        ArrayList<String> lang = currentNode.getAssignment();

        // If true, don't consider 0
        if(!rules.contains(currentParameter))
        {
            // Independent parameter can't be 0
            if(abmittedChars.get(m).equals(Infos.UNDEFINED_CHAR) ||
               abmittedChars.get(n).equals(Infos.UNDEFINED_CHAR))
            {
                cost = INFINITE;
                return cost;
            }
        }
        
        cost = getCostFromCurrentParameter(m, n, lang);
        return cost;
    }

    private int getCostFromCurrentParameter(int m, int n, ArrayList<String> language)
    {
        int cost = 0;

        // If true let's consider costs defined in the parameter definition
        if(parameters.contains(currentParameter))
        {
            try
            {
                // Get cost from parameter definition
                cost = parameters.get(currentParameter).getCostFor(m, n, language);

                // Get cost from global default table if it's -1
                if(cost == -1)
                    cost = parameters.getGlobalDefaultTable().getCost(m, n);
            }
            catch (RulesException ex)
            {
                Logger.getLogger(SankoffExtended.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (CostsException ex)
            {
                Logger.getLogger(SankoffExtended.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else // let's consider only global table
        {
            try
            {
                cost = parameters.getGlobalDefaultTable().getCost(m, n);
            }
            catch (CostsException ex)
            {
                Logger.getLogger(SankoffExtended.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return cost;
    }
}
