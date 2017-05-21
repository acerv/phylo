package tree;

import java.util.ArrayList;

/**
 *
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class PhyloTreeNode implements Cloneable, TreeNode<PhyloTreeNode>
{
    private String name;
    private double score;
    private double height;
    private int scaled_height;
    protected  PhyloTreeNode parent;
    protected ArrayList<PhyloTreeNode> children = new ArrayList<PhyloTreeNode>();

    /**
     * @return true se è una foglia, false altrimenti
     */
    public boolean isLeaf()
    {
        return children.isEmpty();
    }

    /**
     * @return true se è root
     */
    public boolean isRoot()
    {
        return getParent() == null;
    }

    /**
     * aggiunge un figlio
     * @param child figlio da agiiungere
     */
    public void addChild(PhyloTreeNode child)
    {
        children.add(child);
        child.setParent(this);
    }

    /**
     * Inserisce nella posizione i il nodo specificato
     * @param i Posizione
     * @param node Nodo da inserire
     */
    public void setChild(int i, PhyloTreeNode node)
    {
        children.set(i, node);
        node.setParent(this);
    }

    /**
     * @return Numero di foglie
     */
    public int getNumberOfChildren()
    {
        return children.size();
    }

    /**
     * @param i Indice di un figlio
     * @return Figlio con indice i
     */
    public PhyloTreeNode getChild(int i)
    {
        return children.get(i);
    }

    /**
     * Ritorna i figli del nodo
     * @return
     */
    public ArrayList<PhyloTreeNode> getChildren()
    {
        return children;
    }

    /**
     * @return the score
     */
    public double getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the parent
     */
    public PhyloTreeNode getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(PhyloTreeNode parent) {
        this.parent = parent;
    }

    /**
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @return the scaled_height
     */
    public int getScaledHeight() {
        return scaled_height;
    }

    /**
     * @param scaled_height the scaled_height to set
     */
    public void setScaledHeight(int scaled_height) {
        this.scaled_height = scaled_height;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
