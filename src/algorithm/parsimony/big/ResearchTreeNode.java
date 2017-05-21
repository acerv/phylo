package algorithm.parsimony.big;

import algorithm.parsimony.small.LabeledTree;
import java.util.ArrayList;
import tree.TreeNode;

/**
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class ResearchTreeNode extends LabeledTree implements TreeNode<ResearchTreeNode>
{
    ResearchTreeNode parent;
    String name;
    ArrayList<ResearchTreeNode> children = new ArrayList<ResearchTreeNode>();
    double score;
    boolean[][] langTable;
    boolean isInBlackList = false;

    // Used by Branch And bound to compute
    // languages whic has to be added on this node
    private int addedLeaves;

    public void setNumberOfAddedLeaves(int num)
    {
        this.addedLeaves = num;
    }

    public int getNumberOfAddedLeaves()
    {
        return addedLeaves;
    }

    public boolean isInBlackList() {
        return isInBlackList;
    }

    public void setBlackList(boolean alreadySeen) {
        this.isInBlackList = alreadySeen;
    }

    public boolean isLeaf()
    {
        return children.isEmpty();
    }

    public boolean isRoot()
    {
        return parent == null;
    }

    public int getNumberOfChildren()
    {
        return children.size();
    }

    public double getScore()
    {
        return score;
    }

    public void setScore(double score)
    {
        this.score = score;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void addChild(ResearchTreeNode child)
    {
        children.add(child);

        if(child != null)
            child.setParent(this);
    }

    public void setChild(int i, ResearchTreeNode node)
    {
        children.set(i,node);

        if(node != null)
            node.setParent(this);
    }

    public ResearchTreeNode getChild(int i)
    {
        return children.get(i);
    }

    public ArrayList<ResearchTreeNode> getChildren()
    {
        return children;
    }

    public ResearchTreeNode getParent()
    {
        return parent;
    }

    public void setParent(ResearchTreeNode parent)
    {
        this.parent = parent;
    }

    boolean[][] getLangTable() {
        return langTable;
    }

    void setLangTable(boolean[][] langTable) {
        this.langTable = langTable;
    }
}
