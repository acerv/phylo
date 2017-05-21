package algorithm.parsimony.small;

import diagram.treetextdrawer.TextualTreeDrawer;
import informations.Infos;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import matrix.exception.LanguageException;
import matrix.exception.MatrixFormatException;
import matrix.reader.CharMatrixReader;
import parser.newick.Newick;
import parser.newick.ParseException;
import tree.PhyloTree;
import tree.TreeException;
import utility.Debugger;

/**
 * This class is used by Sankoff algorithm. It's an extension of Tree class
 * wich contains possibility to assign labes and costs to each node.<br>
 * It's used to get best character of a language in the given position.
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class LabeledTree extends PhyloTree implements Cloneable
{
    private ArrayList<String> abmittedChars;
    private int languageLenght;
    private CharMatrixReader matrix;

    public LabeledTree()
    {
        root = new LabeledTreeNode();
    }

    /**
     * Load files of tree and matrix, and initialize this class
     * @param nwkFile File of tree in newick format
     * @param matrix File of the matrix which is used to get
     * language assignments
     * @throws FileNotFoundException
     * @throws ParseException
     * @throws MatrixFormatException
     * @throws LanguageException
     * @throws IOException
     * @throws TreeException
     */
    public void loadFromFile(File nwkFile, File matrix)
            throws FileNotFoundException, ParseException, MatrixFormatException,
            LanguageException, IOException, TreeException
    {
        this.matrix = new CharMatrixReader();
        this.matrix.loadMatrixFromFile(matrix);
        abmittedChars = Infos.getAbmittedChars(this.matrix.getAlphabet());
        languageLenght = this.matrix.getColumns();

        Newick parser = new Newick(System.in);
	FileInputStream in = new FileInputStream(nwkFile);
        parser.ReInit(in);
        parser.parseTree(this);
        initializeNodes((LabeledTreeNode) root);
    }

    protected void initializeNodes(LabeledTreeNode node)
            throws TreeException
    {
       node.initialize(abmittedChars, languageLenght);
       if(node.isLeaf())
       {
            String langName = node.getName();
            int indexOfLang = matrix.getLanguages().indexOf(langName);
            ArrayList<String> lang = matrix.getMatrix().get(indexOfLang);
            node.setAssignment(lang);
       }
       else
       {
           for(int i = 0; i < node.getNumberOfChildren(); i++)
               initializeNodes((LabeledTreeNode) node.getChild(i));
       }
    }

    public ArrayList<String> getAbmittedChars()
    {
        return ((LabeledTreeNode)root).getAbmittedChars();
    }

    /**
     * @return Matrix used to get the languages assignments
     */
    public CharMatrixReader getMatrix()
    {
        return matrix;
    }

    private void printInfos(LabeledTreeNode node, boolean costs)
    {
        // Recursion
        if(!node.isLeaf())
        {
            for(int i = node.getNumberOfChildren()-1; i >= 0; i--)
                printInfos((LabeledTreeNode) node.getChild(i), costs);
        }

        // Print properties
        Debugger.print(" "+node.getName()+" ");
        node.printAssignment();

        if(costs) {
            node.printCostOfNode();
        }
        else
            Debugger.println();
    }

    /**
     * Print on standard output the tree
     * @param includeCosts
     */
    public void print(boolean includeCosts)
    {
        Debugger.println("\n-------------------------------------------\n"
                         + "Total cost of the following tree is "+((LabeledTreeNode)root).getCostOfNode()+"\n"
                         + "-------------------------------------------");

        if(Debugger.getDebug())
        {
            TextualTreeDrawer drawer = new TextualTreeDrawer();
            drawer.draw(this, true);
        }

        printInfos((LabeledTreeNode) root, includeCosts);
        Debugger.println();
    }

    @Override
    public LabeledTreeNode getRoot()
    {
        return (LabeledTreeNode) root;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
