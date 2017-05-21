package tree;

import java.util.ArrayList;

/**
 *
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public interface TreeNode<T>
{
    /**
     * @return true se è una foglia, false altrimenti
     */
    public boolean isLeaf();

    /**
     * @return true se è root
     */
    public boolean isRoot();

    /**
     * aggiunge un figlio
     * @param child figlio da agiiungere
     */
    public void addChild(T child);

    /**
     * Inserisce nella posizione i il nodo specificato
     * @param i Posizione
     * @param node Nodo da inserire
     */
    public void setChild(int i, T node);

    /**
     * @return Numero di foglie
     */
    public int getNumberOfChildren();

    /**
     * @param i Indice di un figlio
     * @return Figlio con indice i
     */
    public T getChild(int i);

    /**
     * Ritorna i figli del nodo
     * @return
     */
    public ArrayList<T> getChildren();

    /**
     * @return the score
     */
    public double getScore();

    /**
     * @param score the score to set
     */
    public void setScore(double score);

    /**
     * @return the name
     */
    public String getName();

    /**
     * @param name the name to set
     */
    public void setName(String name);

    /**
     * @return the parent
     */
    public T getParent();

    /**
     * @param parent the parent to set
     */
    public void setParent(T parent);
}
