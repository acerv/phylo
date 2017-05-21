package tree;

/**
 *
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class PhyloTree implements Tree<PhyloTreeNode>
{
    protected PhyloTreeNode root;
    private double height;

    public PhyloTree()
    {
        root = new PhyloTreeNode();
    }

    public double getHeight()
    {
        return height;
    }

    public void setHeight(double height)
    {
        this.height = height;
    }

    public int getScaledHeight()
    {
        return getRoot().getScaledHeight();
    }

    public PhyloTreeNode getRoot()
    {
        return root;
    }
    
    public void setRoot(PhyloTreeNode root)
    {
        this.root = root;
    }
}
