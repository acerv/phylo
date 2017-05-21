package algorithm.parsimony.big;

import tree.Tree;
import utility.Debugger;

/**
 * Research tree where find the aproximate solution of the maximum parsimony problem.
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class ResearchTree implements Tree<ResearchTreeNode>
{
    private double cost = 0;
    ResearchTreeNode root;

    public ResearchTree()
    {
        root = new ResearchTreeNode();
    }

    public double getScore()
    {
        return cost;
    }

    public void setScore(double cost)
    {
        this.cost = cost;
    }

    public ResearchTreeNode getRoot()
    {
        return root;
    }

    public void setRoot(ResearchTreeNode root)
    {
        this.root = root;
    }

    int numOfTrees = 0;
    public void printChild(ResearchTreeNode node)
    {
        numOfTrees++;
        Debugger.println("\n> Tree "+numOfTrees);
        node.print(false);

        if(!node.isLeaf())
            for(int i = 0; i < node.getNumberOfChildren(); i++)
                printChild(node.getChild(i));
    }

    public void print()
    {
        numOfTrees = 0;
        printChild(root);
    }
}
