package tree;

import utility.Debugger;

/**
 * Methods to get informations from a Tree instance
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class TreeInfos
{
    /* ****************
     * HEIGHT OF TREE *
     ******************/
    static double height;
    static public void calculateHeightOfTree(PhyloTree t)
    {
        height = 0;
        initializeSubtree(t.getRoot());
        findTreeHeight(t.getRoot());
        t.setHeight(height);
    }

    // Numero di nodo
    static int nullNode = 0;
    static private void initializeSubtree(PhyloTreeNode node)
    {
        if(node.isLeaf())
        {
            double score1 = node.getParent().getHeight();
            double score2 = node.getScore();
            if(score1 < 0) score1 *= -1;
            if(score2 < 0) score2 *= -1;

            node.setScaledHeight(1);
            node.setHeight(score1 + score2);
        }
        else
        {
            // fix
            if(node.isRoot()) nullNode = 0;
            
            // Assegnamento del nome al nodo
            nullNode++;
            node.setName(String.valueOf(nullNode));

            // Ricorsione + calcolo della distanza massima tra i nodi/foglie
            for (int i = 0; i < node.getNumberOfChildren(); i++)
            {
                // Normalizzo lo score > 0
                double score1 = node.getScore();
                if(score1 < 0) score1 *= -1;

                // Calcolo dell'altezza dalla radice
                if(!node.isRoot())
                {
                    // Normalizzo lo score > 0
                    double score2 = node.getParent().getHeight();
                    if(score2 < 0) score2 *= -1;

                    node.setHeight(score2 + score1);
                }
                else
                {
                    node.setHeight(score1);
                }

                // Scendo nell'albero
                initializeSubtree(node.getChild(i));

                // Calcolo altezza scalata
                node.setScaledHeight(getMaxSubtreeScaledHeight(node) + 1);
            }
        }
    }

    static private int getMaxSubtreeScaledHeight(PhyloTreeNode node)
    {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < node.getNumberOfChildren(); i++)
        {
            if (max < node.getChild(i).getScaledHeight())
                max = node.getChild(i).getScaledHeight();
        }

        return max;
    }

    static private void findTreeHeight(PhyloTreeNode node)
    {
        if(node.isLeaf())
        {
            if(height < node.getHeight())
                height = node.getHeight();

            // Debugger.println(node.getName()+"> "+node.getHeight());
        }
        else
        {
            for(int i = 0; i < node.getNumberOfChildren(); i++)
                findTreeHeight(node.getChild(i));
        }
    }

    /* **************
     * SORTING TREE *
     ****************/
    static public void sortTree(PhyloTree t)
    {
        sortTreeWithRoot(t.getRoot());
    }

    static private void sortTreeWithRoot(PhyloTreeNode node)
    {
        if(!node.isLeaf())
        {
            sortLeaves(node);

            for(int i = 0; i < node.getNumberOfChildren(); i++)
                sortLeaves(node.getChild(i));
        }
    }

    static private void sortLeaves(PhyloTreeNode node)
    {
        for(int i = 1; i < node.getNumberOfChildren(); i++)
        {
            int j = i;
            PhyloTreeNode B = node.getChild(i);
            while ((j > 0) && (node.getChild(j-1).getScore() < B.getScore()))
            {
                node.getChildren().set(j, node.getChild(j-1));
                j--;
            }
            node.getChildren().set(j, B);
        }
    }


    /* *******************
     * MAX AND MIN SCORE *
     *********************/
    static double maxScore;
    static double minScore;
    static public double getMaximumScore(PhyloTree t)
    {
        maxScore = Double.MIN_VALUE;
        minScore = Double.MAX_VALUE;
        findMaxScore(t.getRoot());
        return maxScore;
    }

    static public double getMinimumScore(PhyloTree t)
    {
        findMinScore(t.getRoot());
        return minScore;
    }

    static private void findMaxScore(PhyloTreeNode node)
    {
        if((node.isRoot() && node.getScore() != 0) || !node.isRoot())
            if(maxScore < node.getScore()) maxScore = node.getScore();
        
        for(int i = 0; i < node.getNumberOfChildren(); i++)
            findMaxScore(node.getChild(i));
    }

    static private void findMinScore(PhyloTreeNode node)
    {
        if((node.isRoot() && node.getScore() != 0) || !node.isRoot())
            if(minScore > node.getScore()) minScore = node.getScore();

        for(int i = 0; i < node.getNumberOfChildren(); i++)
            findMinScore(node.getChild(i));
    }

    /* ****************************
     * NUMBER OF NODES AND LEAVES *
     ******************************/
    static int numberOfLeaves;
    static int numberOfNodes;

    static public int getNumberOfLeaves(PhyloTree t)
    {
        numberOfLeaves = 0;
        findNumberOfLeaves(t.getRoot());
        return numberOfLeaves;
    }
    
    static public int getNumberOfNodes(PhyloTree t)
    {
        numberOfNodes = 0;
        findNumberOfNodes(t.getRoot());
        numberOfNodes++; // root
        return numberOfNodes;
    }

    static public void findNumberOfNodes(PhyloTreeNode node)
    {
        if(!node.isLeaf())
        {
            numberOfNodes += node.getNumberOfChildren();

            for(int i = 0; i < node.getNumberOfChildren(); i++)
            {
                findNumberOfNodes(node.getChild(i));
            }
        }
    }

    static private void findNumberOfLeaves(PhyloTreeNode node)
    {
        if(node.isLeaf())
        {
            numberOfLeaves++;
        }
        else
        {
            for(int i = 0; i < node.getNumberOfChildren(); i++)
                findNumberOfLeaves(node.getChild(i));
        }
    }

    /* ************************************
     * LONGEST STRING LENGHT FOR CHILDREN *
     **************************************/
    static int max_string_lenght;
    static String max_string;

    static public String getMaxString(PhyloTree t)
    {
        max_string_lenght = Integer.MIN_VALUE;
        max_string = null;
        findMaxString(t.getRoot());
        return max_string;
    }

    static private void findMaxString(PhyloTreeNode node)
    {
        if (node.isLeaf())
        {
            if(max_string_lenght < node.getName().length())
            {
                max_string_lenght = node.getName().length();
                max_string = node.getName();
            }
        }
        else
        {
            for(int i = 0; i < node.getNumberOfChildren(); i++)
                findMaxString(node.getChild(i));
        }
    }

    /* *******
     * DEBUG *
     *********/
    static public void printTree(PhyloTree t)
    {
        printTreeWithRoot(t.getRoot());
    }

    static public void printTreeWithRoot(PhyloTreeNode node)
    {
       if (node.isLeaf())
            Debugger.println("> Leaf '"+node.getName()+"' with score "+node.getScore());
       else
           Debugger.println("\n> Node with '"+node.getNumberOfChildren()+"' children and score "+node.getScore());

       for (int i = 0; i < node.getNumberOfChildren(); i++)
           printTreeWithRoot(node.getChild(i));
    }
}
