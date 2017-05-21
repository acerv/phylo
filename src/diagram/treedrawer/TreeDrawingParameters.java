package diagram.treedrawer;

/**
 * Parametri per il disegno dell'albero associato
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class TreeDrawingParameters
{ 
    /** size of margins sorrounding the tree */
    public int marginSize = 50;

    /** space to give to each single leaf */
    public int leafWidth = 30;

    /** space for leaves label */
    public int leafLabelMargin = 5;

    /** label font size */
    public int labelFontSize = 12;

    /** line width */
    public float lineWidth = 2.5f;

    /** lenght of a line */
    public int lineLenght = 20;

    /** draw tree without branch lenght or not */
    public boolean useBranchLenghts = false;

    /** draw scores near arcs */
    public boolean viewScores = false;
}
