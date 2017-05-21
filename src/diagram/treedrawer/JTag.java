package diagram.treedrawer;

import javax.swing.JLabel;
import tree.PhyloTreeNode;

/**
 *
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class JTag extends JLabel
{
    PhyloTreeNode node;

    /**
     * Inizializza la label corrispondente al linguaggio nella albero
     * @param node Foglia a cui è associato il label
     * @param vertical Se true la label viene scritta verticale
     */
    public JTag(PhyloTreeNode node, boolean vertical)
    {
        this.node = node;
        setText(node.getName());
        setForeground(Drawgram.FOREGROUND_COLOR);

        if(vertical)
            setUI(new VerticalLabelUI(true));
    }

    /**
     * ritorna il figlio a cui è associata la label
     * @return
     */
    public PhyloTreeNode getTreeNode()
    {
        return this.node;
    }
}
