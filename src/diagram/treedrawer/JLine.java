
package diagram.treedrawer;

import tree.PhyloTreeNode;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

/**
 * Questa classe contiene le informazioni necessarie per disegnare un arco
 * nella classe Drawgram.java
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class JLine
{
    // Margine di selezione
    private static int SELECTION_MARGIN = 8;

    // Linea e area di selezione
    private Line2D line;
    private Rectangle selection;

    // Nodo associato alla linea
    private final PhyloTreeNode node;

    private Color currentColor;

    /**
     * Inizializza la classe JLine1 e disegna la linea
     * @param node Nodo associato alla linea (figlio, non padre)
     * @param line Coordinate della linea
     * @param color Colore della linea
     * @param horizontal Se true, la linea è disposta in senso orizzontale
     */
    public JLine(PhyloTreeNode node, Line2D line, Color color, boolean horizontal)
    {
        this.line = line;
        Rectangle rect = line.getBounds();

        if(horizontal)
        {
            this.selection = new Rectangle(
                    rect.x, rect.y-SELECTION_MARGIN,
                    rect.width, rect.height+(SELECTION_MARGIN*2)
                    );
        }
        else
        {
            this.selection = new Rectangle(
                    rect.x-SELECTION_MARGIN, rect.y,
                    rect.width+(SELECTION_MARGIN*2), rect.height
                    );
        }

        this.node = node;
        currentColor = color;
    }

    /**
     * Seleziona il colore con il quale dipingere la linea
     * @param color Colore corrente da usare
     */
    public void setColor(Color color)
    {
        this.currentColor = color;
    }

    /**
     * Ritorna il colore dell'arco
     * @return
     */
    public Color getColor()
    {
        return this.currentColor;
    }

    /**
     * Ritorna il numero di figli associati alla linea
     * @return
     */
    public int getNumberOfChildren()
    {
        return node.getNumberOfChildren();
    }

    /**
     * Ritorna l'area selezionabile della linea
     * @return
     */
    public Rectangle getSelection()
    {
        return this.selection;
    }

    /**
     * Ritorna i margini della linea
     * @return
     */
    public Rectangle getBounds()
    {
        return this.line.getBounds();
    }

    /**
     * Ritorna la linea
     * @return
     */
    public Line2D getLine()
    {
        return line;
    }

    /**
     * Ritorna il nodo associato all'arco
     * @return nodo associato all'arco
     */
    public PhyloTreeNode getNode()
    {
        return node;
    }

    /**
     * @param p punto di click
     * @return True se la linea è selezionata, false altrimenti
     */
    public boolean isSelected(Point p)
    {
        return this.selection.contains(p);
    }

    /**
     * Ritorna true questa linea è uguale ad un'altra
     * @param line Linea da comparare
     * @return
     */
    public boolean equalsToLine(Line2D line)
    {
        boolean res = false;

        if(line.getX1() == this.line.getX1() && line.getX2() == this.line.getX2() &&
           line.getY1() == this.line.getY1() && line.getY2() == this.line.getY2())
        {
            res = true;
        }

        return res;
    }
}
