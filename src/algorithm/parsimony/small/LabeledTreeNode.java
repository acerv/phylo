package algorithm.parsimony.small;

import java.util.ArrayList;
import java.util.Collections;
import tree.PhyloTreeNode;
import tree.TreeException;
import utility.Debugger;

/**
 * Node of a tree used into Sankoff algorithm
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class LabeledTreeNode extends PhyloTreeNode implements Cloneable
{
    // Used by leaf assignment
    static public int INFINITE = Integer.MAX_VALUE;
    static public int ZERO = 0;

    // abmitted chars = alphabet + undefined phylo characters
    private ArrayList<String> abmittedChars;

    // Costs of mutations. Lenght is the abmittedChars lenght
    private int[][] costsOfMutations;

    // Assignment for node & cost associated
    private ArrayList<String> assignment;
    private int[] costOfAssignment;

    private int languageLenght = 0;

    // if true, this is a macronode
    private boolean macroNode = false;

    // Used to get minimum cost of mutation.
    // It has the lenght of alphabet
    private int[] takenChar;
    private int assignedChar;


    /* ****************
     * INITIALIZATION *
     ******************/
    public void initialize(ArrayList<String> abc, int langLenght)
    {
        abmittedChars = abc;
        takenChar = new int[abmittedChars.size()];
        setLanguageLenght(langLenght);
        costsOfMutations = new int[langLenght][abc.size()];
    }

    private void setLanguageLenght(int langLenght)
    {
        languageLenght = langLenght;
        assignment = new ArrayList<String>();
        costOfAssignment = new int[languageLenght];

        for(int i = 0; i < languageLenght; i++)
            assignment.add("");
    }

    public int getLanguageLenght()
    {
        return languageLenght;
    }

    
    /* **************************
     * USED BY BRANCH AND BOUND *
     ****************************/
    public void copyOf(LabeledTreeNode node)
            throws TreeException
    {
        // PhyloTreeNode
        setName(node.getName());
        setScore(node.getScore());

        // Initialize
        initialize(node.getAbmittedChars(), node.getLanguageLenght());

        // Assignments
        setAssignment(node.getAssignment());

        // Costs of assignment
        int[] costsOfAssign = node.getCostOfAssignment();
        System.arraycopy(costsOfAssign, 0, costOfAssignment, 0, costsOfAssign.length);

        // Costs of mutations
        copyArrayOfCosts(node.getCosts(), costsOfMutations);

        // Taken Chars
        copyArrayOfTakenChars(node.getArrayOfTakenChars(), takenChar);

        assignedChar = node.getAssignedChar();
    }

    static private void copyArrayOfCosts(int[][] costs, int[][] copy)
    {
        for(int i = 0; i < costs.length; i++)
            System.arraycopy(costs[i], 0, copy[i], 0, costs[i].length);
    }

    static private void copyArrayOfTakenChars(int[] takenChars, int[] copy) {
        System.arraycopy(takenChars, 0, copy, 0, takenChars.length);
    }

    public int[] getCostOfAssignment()
    {
        return costOfAssignment;
    }

    public void setMacroNode(boolean macroNode)
    {
        this.macroNode = macroNode;
    }

    public void reset()
    {
        for(int i = 0; i < costOfAssignment.length; i++)
            costOfAssignment[i] = 0;

        for(int i = 0; i < costsOfMutations.length; i++)
            for(int j = 0; j < costsOfMutations[0].length; j++)
                costsOfMutations[i][j] = 0;

        for(int i = 0; i < assignment.size(); i++)
            assignment.set(i, "");

        for(int i = 0; i < takenChar.length; i++)
            takenChar[i] = 0;
    }

    /**
     * Assign a label to this node
     * @param assignment
     * @throws TreeException
     */
    public void setAssignment(ArrayList<String> assignment)
            throws TreeException
    {
        if(assignment.size() != languageLenght)
        {
            throw new TreeException("the assignment is too long: it should be "+languageLenght+" long instead of "+assignment.size());
        }
        else
        {
            Collections.copy(this.assignment, assignment);
        }
    }

    public int[] getArrayOfTakenChars() {
        return takenChar;
    }

    /* *************************
     * METHODS USED BY SANKOFF *
     ***************************/
    public ArrayList<String> getAbmittedChars()
    {
        return abmittedChars;
    }

    /**
     * Assign a character into the assignment from taken char
     * @param character number of parameter
     * @param parameter position into the assignment
     */
    public void assignCharToParameter(String character, int parameter)
    {
        assignment.set(parameter, character);
        int index = abmittedChars.indexOf(character);
        int cost = costsOfMutations[parameter][index];
        costOfAssignment[parameter] = cost;
    }

    public int[][] getCosts()
    {
        return costsOfMutations;
    }

    /**
     * @param param number of parameter
     * @return Array of costs assigned to abmitted characters
     */
    public int[] getCostsForParameter(int param)
    {
        return costsOfMutations[param];
    }

    /**
     * @return Total cost of mutations for this node
     */
    public int getCostOfNode()
    {
        int totalCostOfNode = 0;
        for(int j = 0; j < costOfAssignment.length; j++)
        {
            if(costOfAssignment[j] != INFINITE)
                totalCostOfNode += costOfAssignment[j];
        }

        return totalCostOfNode;
    }

    public boolean isMacroNode()
    {
        return macroNode;
    }

    /**
     * Returns characters assignment to this node
     * @return
     */
    public ArrayList<String> getAssignment()
    {
        return assignment;
    }

    /**
     * Assign cost to character. Indexes are organized
     * to assign characters' indexes to costs' indexes.
     * All costs are initialized to INFINITE by default.
     * @param param number of parameter
     * @param character Character of language
     * @param cost Cost for character
     * @throws TreeException
     */
    public void setCost(int param, String character, int cost)
            throws TreeException
    {
        int i = abmittedChars.indexOf(character);

        if(i != -1)
            costsOfMutations[param][i] = cost;
        else
            throw new TreeException("'"+character+"' character is not specified into this node");
    }

    /**
     * Assign cost to character (from index). Indexes are organized
     * to assign characters' indexes to costs' indexes.
     * All costs are initialized to INFINITE by default.
     * @param param number of parameter
     * @param m index of character
     * @param cost cost of character
     * @throws TreeException
     */
    public void setCost(int param, int m, int cost)
            throws TreeException
    {
        if(m != -1)
            costsOfMutations[param][m] = cost;
        else
            throw new TreeException("'"+abmittedChars.get(m)+"' character is not specified into this node");
    }

    public int getTakenChar(int bestChoice)
    {
        return takenChar[bestChoice];
    }

    public void addTakenCharFromParent(int charOfParent, int character)
    {
        takenChar[charOfParent] = character;
    }

    public int getAssignedChar()
    {
        return assignedChar;
    }

    public void setAssignedChar(int assChar)
    {
        assignedChar = assChar;
    }

    public void print()
    {
        printAssignment();
        printCostOfNode();
    }

    public void printAssignment()
    {
        for(int i = 0; i < getAssignment().size(); i++)
            Debugger.print(getAssignment().get(i)+" ");
    }

    public void printCostOfNode()
    {
        Debugger.print("total cost of mutation: "+getCostOfNode());
        if(isMacroNode())
            Debugger.print(" MACRONODE ");
        Debugger.println();
    }

    @Override
    public LabeledTreeNode getChild(int i) {
        return (LabeledTreeNode) super.getChild(i);
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
