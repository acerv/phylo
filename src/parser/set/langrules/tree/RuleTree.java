package parser.set.langrules.tree;

import java.util.ArrayList;

/**
 * This class contains tree wich is the set of rules
 * to get definition of parameters into language.<br>
 * The root is always an OperationNode ( AND or OR )
 * and leaves are always ParameterNode
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class RuleTree
{
    private RuleNode root;
    private int def_parm = -1;
    private int numberOfParameters;

    /**
     * Set the root of tree and get the number of parameters
     * @param p number of parameters
     * @param root root of the tree
     */
    public RuleTree(int p, RuleNode root)
    {
        numberOfParameters = p;
        this.root = root;
    }

    /**
     * Sets the associated character of this tree. If it hasnt,
     * associated parameter value is -1
     * @param def
     */
    public void setAssociatedParameter(int def)
    {
        def_parm = def;
    }

    /**
     * Returns index of the associated parameter to this tree
     * @return
     */
    public int getAssociatedParameter()
    {
        return def_parm;
    }

    /**
     * @param p parameter
     * @return null if there's not this parameter
     */
    public String getCharForParameter(int p) {
        String value = value_of_leaf(root, p);
        return value;
    }

    /**
     * Returns true if the parameter associated to this tree is defined
     * with specified language
     * @param language Specified language that contains information to know
     * if associated parameter to this tree is defined
     * @return
     * @throws RulesException 
     */
    public boolean isDefinedDependent(ArrayList<String> language)
            throws RulesException
    {
        if(language.size() > numberOfParameters)
        {
            throw new RulesException("language has more character("
                    + ""+language.size()+") that it should have("+numberOfParameters+")");
        }
        else
        {
            assign(language, root);
            boolean result = root.getValue();
            return result;
        }
    }

    // Recursive function to set tree with specified language
    private static void assign(ArrayList<String> language, RuleNode node)
    {
        if(node instanceof OperationNode)
        {
            RuleNode left = ((OperationNode) node).getLeftChild();
            RuleNode right = ((OperationNode) node).getRightChild();

            assign(language, left);
            assign(language, right);
        }
        else
        {
            ParameterNode pnode = (ParameterNode) node;
            int i = pnode.getPosition();
            pnode.setCompareValue(language.get(i));
        }
    }

    // Recursive function to set tree with specified language
    private static String value_of_leaf(RuleNode node, int p)
    {
        if(node instanceof OperationNode)
        {
            RuleNode left = ((OperationNode) node).getLeftChild();
            RuleNode right = ((OperationNode) node).getRightChild();

            value_of_leaf(left, p);
            value_of_leaf(right, p);
        }
        else
        {
            ParameterNode pnode = (ParameterNode) node;
            int i = pnode.getPosition();

            if(i == p)
                return pnode.getCurrentValue();
        }

        return null;
    }

    /**
     * Prints the rule associated to this tree (parameter)
     * with its own language
     * @param node
     */
    private static void printRule(RuleNode node)
    {
        if(!(node instanceof OperationNode))
        {
            ((ParameterNode)node).print();
        }
        else
        {
            System.out.print("(");
            printRule(((OperationNode)node).getLeftChild());
            ((OperationNode)node).print();

            printRule(((OperationNode)node).getRightChild());
            System.out.print(")");
        }
    }

    /**
     * Print the tree formatted in its own language
     */
    public void print()
    {
        printRule(root);

        if(def_parm != -1)
            System.out.println(" => P"+def_parm+";");
    }
}
