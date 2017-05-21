package diagram.treetextdrawer;

import tree.PhyloTree;
import tree.PhyloTreeNode;
import tree.TreeInfos;

/**
 * Draw a tree into the horizontal way or vertical way using ASCII art
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class TextualTreeDrawer
{
    // Graphics
    TextGraphics2D g;

    // Values
    private static int MARGIN = 3;
    private static int LEAF_LABEL_MARGIN = 2;
    private static int LEAF_WIDTH = 3;
    private static int LINE_LENGHT = 5;

    /**
     * Print an instance of PhyloTree class using ASCII art.
     * Output will be console/terminal
     * @param t Tree that has to be printed
     */
    public void draw(PhyloTree t, boolean horizontal)
    {
        // Works better for different visualization
        if(horizontal)
        {
            LEAF_WIDTH = 3;
            LINE_LENGHT = 5;
        }
        else
        {
            LEAF_WIDTH = 5;
            LINE_LENGHT = 3;
        }

        int treeHeight = t.getScaledHeight()*LINE_LENGHT;
        int treeWidth = (TreeInfos.getNumberOfLeaves(t) -1)*LEAF_WIDTH +1;

        int maxStringLenght = TreeInfos.getMaxString(t).length();
        int h_tree = treeHeight+ 2*MARGIN + LEAF_LABEL_MARGIN + maxStringLenght ;
        int w_tree = treeWidth + 2*MARGIN;

        IntegerPoint p;

        // x = altezza, y = lunghezza
        if(horizontal)
        {
            //System.out.println("Height = "+w_tree+"\nWidth = "+h_tree);
            g = new TextGraphics2D(h_tree, w_tree);
            p = new IntegerPoint(MARGIN - LEAF_WIDTH, MARGIN + treeHeight);
            horizontalDraw(g, p, t.getRoot());
        }
        else
        {
            //System.out.println("Height = "+h_tree+"\nWidth = "+w_tree);
            g = new TextGraphics2D(w_tree, h_tree);
            p = new IntegerPoint(MARGIN + treeHeight, MARGIN - LEAF_WIDTH);
            verticalDraw(g, p, t.getRoot());
        }

        g.paint();
    }

    public IntegerPoint verticalDraw(TextGraphics2D g, IntegerPoint startPosition, PhyloTreeNode node)
    {
        // Scrittura del nome della foglia
        if(node.isLeaf())
        {
            // Posizione orizzontale della stringa
            int stringXPosition = startPosition.x + LEAF_LABEL_MARGIN;
            startPosition.y += LEAF_WIDTH;
            g.drawString(node.getName(), stringXPosition, startPosition.y, false);

            return new IntegerPoint(stringXPosition - LEAF_LABEL_MARGIN, startPosition.y);
        }
        else
        {
            // Coordinata finale della prima riga orizzontale scritta
            IntegerPoint finalPoint = new IntegerPoint();

            // Coordinata finale dell'ultima riga orizzontale scritta
            IntegerPoint endPoint = new IntegerPoint();

            // Il sotto albero sinistro viene scritto come riferimento iniziale
            IntegerPoint startPoint = verticalDraw(g, startPosition, node.getChild(0));
            finalPoint.y = startPoint.y;
            finalPoint.x = startPoint.x - getHeightOf(node, node.getChild(0)) ;

            g.drawLine(startPoint, finalPoint);

            // Scrittura del sottoalbero destro
            for(int i = 1; i < node.getNumberOfChildren(); i++)
            {
                // Primo punto della linea da tracciare
                startPoint = verticalDraw(g, startPosition, node.getChild(i));
                endPoint.x = startPoint.x - getHeightOf(node, node.getChild(i));
                endPoint.y = startPoint.y;

                // Linea verticale
                g.drawLine(startPoint, endPoint);
            }

            // Piccolo trick/fix :)
            finalPoint.x = endPoint.x;

            // Linea orizzontale
            g.drawLine(finalPoint, endPoint);

            // Punto medio da cui cominciare a scrivere
            IntegerPoint mid = getVerticalMidLenght(finalPoint, endPoint);
            g.drawString(node.getName(), mid.x-1, mid.y-1, true);

            // Root line
            if(node.isRoot())
            {
                IntegerPoint end = new IntegerPoint(mid.x - LINE_LENGHT, mid.y);
                g.drawLine(mid, end);
            }

            // Ritorno la posizione a metà della riga appena tracciata
            return mid;
        }
    }

    public IntegerPoint horizontalDraw(TextGraphics2D g, IntegerPoint startPosition, PhyloTreeNode node)
    {
        // Scrittura del nome della foglia
        if(node.isLeaf())
        {
            // Posizione orizzontale della stringa
            int stringYPosition = startPosition.y + LEAF_LABEL_MARGIN;
            startPosition.x += LEAF_WIDTH;
            g.drawString(node.getName(), startPosition.x, stringYPosition, true);

            return new IntegerPoint(startPosition.x, stringYPosition - LEAF_LABEL_MARGIN);
        }
        else
        {
            // Coordinata finale della prima riga orizzontale scritta
            IntegerPoint finalPoint = new IntegerPoint();

            // Coordinata finale dell'ultima riga orizzontale scritta
            IntegerPoint endPoint = new IntegerPoint();

            // Il sotto albero sinistro viene scritto come riferimento iniziale
            IntegerPoint startPoint = horizontalDraw(g, startPosition, node.getChild(0));
            finalPoint.x = startPoint.x;
            finalPoint.y = startPoint.y - getHeightOf(node, node.getChild(0));

            g.drawLine(startPoint, finalPoint);
            
            // Scrittura del sottoalbero destro
            for(int i = 1; i < node.getNumberOfChildren(); i++)
            {
                // Primo punto della linea da tracciare
                startPoint = horizontalDraw(g, startPosition, node.getChild(i));
                endPoint.x = startPoint.x;
                endPoint.y = startPoint.y - getHeightOf(node, node.getChild(i));

                // Linea orizzontale
                g.drawLine(startPoint, endPoint);
            }

            // Piccolo trick/fix :)
            finalPoint.y = endPoint.y;
            
            // Linea verticale
            g.drawLine(finalPoint, endPoint);

            // Punto medio da cui cominciare a scrivere
            IntegerPoint mid = getHorizontalMidLenght(finalPoint, endPoint);
            g.drawString(node.getName(), mid.x-1, mid.y-1, true);

            // Root line
            if(node.isRoot())
            {
                IntegerPoint end = new IntegerPoint(mid.x, mid.y - LINE_LENGHT);
                g.drawLine(mid, end);
            }

            // Ritorno la posizione a metà della riga appena tracciata
            return mid;
        }
    }

    // Ritorna l'altezza riscalata per i nodi dell'albero che vanno disegnati
    private static int getHeightOf(PhyloTreeNode parent, PhyloTreeNode node)
    {
        int scaled = parent.getScaledHeight() - node.getScaledHeight();
        return scaled * LINE_LENGHT;
    }

    private static IntegerPoint getHorizontalMidLenght(IntegerPoint a, IntegerPoint b){return new IntegerPoint(a.x + (b.x-a.x)/2,a.y);}
    private static IntegerPoint getVerticalMidLenght(IntegerPoint a, IntegerPoint b){return new IntegerPoint(a.x,a.y + (b.y-a.y)/2);}
}
