package parser.set.langrules.tree;

/**
 * This class is used to abstract the AND and OR operation into the RuleTree.
 * Every OperationNode has 2 input because of logic properties:<br>
 * <code>A AND B AND C AND D = ((A AND B) AND C) AND D<br>
 * A OR B OR C OR D = ((A OR B) OR C) OR D</code>
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class OperationNode implements RuleNode
{
    // Children
    private RuleNode leftChild;
    private RuleNode rightChild;

    // AND or OR node ?
    boolean AndNode = false;

    /**
     * Set left child of this node
     * @param node
     */
    public void setLeftChild(RuleNode node)
    {
        this.leftChild = node;
    }

    /**
     * Returns left child of this node
     * @return
     */
    public RuleNode getLeftChild()
    {
        return this.leftChild;
    }

    /**
     * Set right child of this node
     * @param node
     */
    public void setRightChild(RuleNode node)
    {
        this.rightChild = node;
    }

    /**
     * returns right child of this node
     * @return
     */
    public RuleNode getRightChild()
    {
        return this.rightChild;
    }

    /**
     * Set this node as And node
     */
    public void setAsAndNode()
    {
        this.AndNode = true;
    }

    /**
     * Set this node as Or node
     */
    public void setAsOrNode()
    {
        this.AndNode = false;
    }

    /**
     * Print if it's an AND or OR node
     */
    public void print()
    {
        String name;
        if(AndNode) name = " AND ";
        else name = " OR ";
        System.out.print(name);
    }

    public boolean getValue()
    {
        if(AndNode)
            return leftChild.getValue() && rightChild.getValue();
        else
            return leftChild.getValue() || rightChild.getValue();
    }
}
