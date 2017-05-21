package tree;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Genera una stringa che rappresenta l'albero fornito in formato newick
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class TreeIntoNewickFormat
{
    private String newickString = "";

    public TreeIntoNewickFormat(PhyloTree t)
    {
        makeTreeInNewickFormat(t.getRoot());
    }

    /**
     * Ritorna la stringa che rappresenta l'albero fornito
     * nel formato Newick
     * @return
     */
    public String getString()
    {
        return newickString;
    }

    /**
     * Scrive l'albero in un file nwk
     * @param nwkFile File in cui salvare
     * @throws IOException
     */
    public void drawToFileAsNwk(String nwkFile)
            throws IOException
    {
        PrintStream defaultOut = new PrintStream(System.out);
        System.setOut(new PrintStream(new File(nwkFile)));
        System.out.println(newickString);
        System.setOut(defaultOut);
    }

    // Genera una stringa che rappresenta l'albero in formato newick
    private void makeTreeInNewickFormat(PhyloTreeNode node)
    {
        if(node.isLeaf())
        {
            newickString += node.getName()+":"+node.getScore();
        }
        else
        {
            newickString += "(";
            for(int i = 0; i < node.getNumberOfChildren(); i++)
            {
                makeTreeInNewickFormat(node.getChild(i));
                if(i < node.getNumberOfChildren()-1) newickString += ",";
            }

            newickString += ")";

            // Se la root ha uno score lo visualizzo
            if(node.isRoot() && node.getScore() != 0)
            {
                newickString += ":"+node.getScore();
                newickString += ";";
            }
            else if(node.isRoot())
            {
                newickString += ";";
            }
            else if(!node.isRoot()) // Se è un nodo interno visualizzo lo score
            {
                newickString += ":"+node.getScore();
            }
        }
    }
}
