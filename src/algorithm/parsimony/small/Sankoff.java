package algorithm.parsimony.small;

import utility.ArrayUtils;
import algorithm.GenericAlgorithm;
import informations.Infos;
import java.io.File;
import tree.TreeException;
import java.util.ArrayList;
import matrix.reader.CharMatrixReader;
import tree.TreeInfos;
import tree.TreeIntoNewickFormat;
import utility.Debugger;

/**
 * Implements Sankoff algorithm
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class Sankoff extends GenericAlgorithm
{
    protected static int INFINITE = Integer.MAX_VALUE;

    protected LabeledTree tree;
    protected int[][] matrixOfCosts;
    protected CharMatrixReader matrix;
    protected ArrayList<String> alphabet;
    protected ArrayList<String> abmittedChars;

    // Current parameter into the assignment
    protected int currentParameter = 0;

    // Current node
    protected LabeledTreeNode currentNode;

    // Debug
    boolean PRINT_SCORES = false;
    boolean PRINT_OUTFILE = false;
    boolean PRINT_INFOFILE = false;

    // Used for the printer of table
    static String MARGIN = " ";
    static String H_LINE = "-";
    static String V_LINE = "|";
    static String NODE = "Node";
    static String CHARACTERS = "Characters";


    /**
     * Set the matrix of costs which defines costs of mutations
     * @param matrix
     */
    public void setMatrixOfCosts(int[][] matrix)
    {
        matrixOfCosts = matrix;
    }

    // Get index of a char into the alphabet
    protected int getIndexOfChar(String m)
    {
        return abmittedChars.indexOf(m);
    }

    /**
     * This method get costs frommatrix of costs.
     * This is the method that it should be Overriden to implement
     * a different kind of Sankoff algorithm.
     * @param m Chararacter that it should be changed
     * @param n Character that substitutes the first one
     * @return Cost of mutation from m to n
     */
    protected int getCost(String m, String n)
    {
        int i = getIndexOfChar(m);
        int j = getIndexOfChar(n);

        return matrixOfCosts[i][j];
    }

    /**
     * This method get costs frommatrix of costs.
     * @param m index of first character
     * @param n index of second character
     * @return cost of mutation
     * @throws Exception
     */
    protected int getCost(int m, int n) throws Exception
    {
        return matrixOfCosts[m][n];
    }

    /* *********************
     * ASSIGNMENT OF COSTS *
     ***********************/
    static ArrayList<Integer> getIndexOfMinCost(int[] array)
    {
        int min = INFINITE;
        int index = 0;
        ArrayList<Integer> mins = new ArrayList<Integer>();

        // Get index of the minimum element
        for(int i = 0; i < array.length; i++)
        {
            if(min > array[i])
            {
                index = i;
                min = array[i];
            }
        }

        mins.add(index);

        // Find other elements with the same value of minimum
        for(int i = 0; i < array.length; i++)
            if(array[i] == min && i != index)
                mins.add(i);

        return mins;
    }

    // Sn (i) = minj [cij + Sl (j)] + mink [cik + Sr (k)]
    void S(LabeledTreeNode node) throws Exception
    {
        // Nothing to do if it's macronode
        //if(!node.isMacroNode())
        {
            if(node.isLeaf())
            {
                String currentChar = node.getAssignment().get(currentParameter);

                for(int i = 0; i < abmittedChars.size(); i++)
                {
                    String a = abmittedChars.get(i);
                    if(a.equals(currentChar))
                    {
                        node.getCostsForParameter(currentParameter)[i] = 0;
                    }
                    else
                    {
                        node.getCostsForParameter(currentParameter)[i] = INFINITE;
                    }
                }
            }
            else
            {
                currentNode = node;

                for(int i = 0; i < node.getNumberOfChildren(); i++)
                    S(node.getChild(i));

                int[] costOf = new int[node.getNumberOfChildren()];

                for(int m = 0; m < abmittedChars.size(); m++)
                {
                    //String currentChar = abmittedChars.get(m);

                    for(int j = 0; j < node.getNumberOfChildren(); j++)
                    {
                        int[] bestCosts = new int[abmittedChars.size()];
                        LabeledTreeNode child = node.getChild(j);

                        // If child is a leaf, there's static 0 on current
                        // parameter of the leaf
                        if(child.isLeaf())
                        {
                            int indexOfMin = ArrayUtils.getIndexOfMin(child.getCostsForParameter(currentParameter));
                            //String childChar = abmittedChars.get(indexOfMin);

                            for(int n = 0; n < abmittedChars.size(); n++)
                                //bestCosts[n] = getCost(currentChar, childChar);
                                bestCosts[n] = getCost(m, indexOfMin);
                        }
                        else
                        {
                            // Get char which minimize the cost of mutation
                            for(int n = 0; n < abmittedChars.size(); n++)
                            {
                                int sankoffCost = node.getChild(j).getCostsForParameter(currentParameter)[n];
                                int costOfMutation = getCost(m,n);// getCost(currentChar, abmittedChars.get(n));

                                if(sankoffCost == INFINITE)
                                    bestCosts[n] = INFINITE;
                                else if(costOfMutation == INFINITE)
                                    bestCosts[n] = INFINITE;
                                else
                                    bestCosts[n] += (sankoffCost + costOfMutation);
                            }
                        }

                        // Get minimum from child
                        int bestChar = ArrayUtils.getIndexOfMin(bestCosts);
                        costOf[j] = bestCosts[bestChar];

                        
                        /* REALLY IMPORTANT:
                         * Save the best mutation from parent to child
                         * for the current m character of node.
                         * This is used by assignCharacters(..) method
                         * which recognize the best assignment of child
                         * from its parent node.
                         */
                        node.getChild(j).addTakenCharFromParent(m, bestChar);
                    }

                    int sumOfCost = 0;
                    for(int i = 0; i < node.getNumberOfChildren(); i++)
                    {
                        /* If at least one cost is infinite, assign infinite value.
                         * This means that the m character will never
                         * be taken into the assignment.
                         */
                        if(costOf[i] == INFINITE)
                        {
                            sumOfCost = INFINITE;
                            break;
                        }
                        
                        sumOfCost += costOf[i];
                    }

                    //String setChar = abmittedChars.get(m);
                    //node.setCost(currentParameter, setChar, sumOfCost);
                    node.setCost(currentParameter, m, sumOfCost);
                }
            }
        }
    }

    /* **************************
     * ASSIGNMENT OF PARAMETERS *
     ****************************/
    private void assignCharacters(LabeledTreeNode node)
            throws TreeException
    {
        //if(!node.isMacroNode())
        {
            if(!node.isLeaf())
            {
                int bestChar = 0;

                if(node.isRoot())
                {
                    // The char associated to minimum cost
                    int[] cost = node.getCostsForParameter(currentParameter);
                    bestChar = ArrayUtils.getIndexOfMin(cost);
                }
                else
                {
                    // Assign character found in S(..) method wich minimize the cost
                    // of mutation from parent to child
                    int assignedCharFromParent = ((LabeledTreeNode)node.getParent()).getAssignedChar();
                    bestChar = node.getTakenChar(assignedCharFromParent);
                }

                node.setAssignedChar(bestChar);
                node.assignCharToParameter(abmittedChars.get(bestChar), currentParameter);

                // Recursion
                for(int n = 0; n < node.getNumberOfChildren(); n++)
                    assignCharacters(node.getChild(n));
            }
        }
    }

    /**
     * Executes the Sankoff algorithm
     * @throws Exception
     */
    public void exec() throws Exception
    {
        if(PRINT_SCORES)
            printAlphabet(tree);
        
        // number of columns = number of parameters
        for(int i = 0; i < matrix.getColumns(); i++)
        {
            LabeledTreeNode root = tree.getRoot();
            
            // Parameter to assign
            currentParameter = i;

            // Get costs of mutations
            S(root);

            if(PRINT_SCORES)
                printTableOfCosts(tree, i);

            // Get assignments of internal nodes
            assignCharacters(root);
        }

        if(PRINT_SCORES)
            printAssignmentOfTree();

        if(PRINT_OUTFILE)
        {
            File out = new File(Infos.TEMPORARY_PATH,"sankoff_outtree.nwk");
            TreeIntoNewickFormat nwk = new TreeIntoNewickFormat(tree);
            nwk.drawToFileAsNwk(out.getAbsolutePath());
            setOutput(out);
        }
    }

    public void printAssignmentOfTree()
    {
        tree.print(false);
    }

    public void setPrintOutfile(boolean printOtfile)
    {
        PRINT_OUTFILE = printOtfile;
    }

    public void printScores(boolean printScores)
    {
        PRINT_SCORES = printScores;
    }

    @Override
    public void setInput(File input) throws Throwable
    {
        this.input = input;

        SkffReader read = new SkffReader();
        read.setUseRules(false);
        read.setInputFile(input);
        read.load();
        
        tree = read.tree;
        matrix = read.matrix;
        alphabet = read.infos.getAlphabet();

        // this is the same for everything
        abmittedChars = Infos.getAbmittedChars(alphabet);
    }


    /* ***********
     *   TABLES  *
     *************/
    static private void printAlphabet(LabeledTree t)
    {
        Debugger.print("Alphabet is ");
        for(int i = 0; i < t.getAbmittedChars().size(); i++)
            Debugger.print(t.getAbmittedChars().get(i)+"  ");

        Debugger.println("\n");
    }

    static private void printTableOfCosts(LabeledTree t, int p)
    {
        int MAX_STRING_LENGHT = TreeInfos.getMaxString(t).length();

        // First and second column
        int WIDTH_OF_NAME = MARGIN.length()*2 + NODE.length();
        int WIDTH_OF_NODE = MARGIN.length()*2 + MAX_STRING_LENGHT;
        int WIDTH_OF_CHAR = MARGIN.length()*2 + CHARACTERS.length();

        // Horizontal line of table
        int WIDTH_OF_TABLE;

        if(WIDTH_OF_NAME > WIDTH_OF_NODE)
            WIDTH_OF_TABLE = WIDTH_OF_NAME + WIDTH_OF_CHAR;
        else
            WIDTH_OF_TABLE = WIDTH_OF_NODE + WIDTH_OF_CHAR;

        String HORIZ_LINE  = replicateString(H_LINE, WIDTH_OF_TABLE);

        // SPACE for Node column
        String SPACE_LINE = replicateString(MARGIN, WIDTH_OF_NAME - NODE.length() -1);

        // Parameter
        Debugger.println("\n"+
                HORIZ_LINE+"\n"+
                SPACE_LINE+"Parameter P"+p);

        // Columns declarations + Node column + Characters column
        Debugger.println(
                HORIZ_LINE+"\n"+
                MARGIN+NODE+SPACE_LINE+V_LINE+MARGIN+CHARACTERS+MARGIN+"\n"+
                HORIZ_LINE);

        // Print table
        printCostsOfTree(t.getRoot(), p, WIDTH_OF_NAME, WIDTH_OF_CHAR);
    }

    static private void printCostsOfTree(LabeledTreeNode node, int p,
            int WIDTH_OF_NAME, int WIDTH_OF_CHAR)
    {
        // Name
        String name = MARGIN+node.getName()+MARGIN;

        // Recursion
        if(!node.isLeaf())
        {
            for(int i = node.getNumberOfChildren()-1; i >= 0; i--)
                printCostsOfTree((LabeledTreeNode) node.getChild(i), p,
                        WIDTH_OF_NAME, WIDTH_OF_CHAR);
        }

        // Print name
        String SPACE_FOR_NAME = replicateString(MARGIN, WIDTH_OF_NAME-name.length());
        Debugger.print(name+SPACE_FOR_NAME+V_LINE);

        // Print characters
        int[] costs = node.getCostsForParameter(p);
        for(int i = 0; i < costs.length; i++)
            if(costs[i] == INFINITE)
                Debugger.print(MARGIN+"inf");
            else
                Debugger.print(MARGIN+costs[i]);

        Debugger.println(MARGIN);
    }

    static private String replicateString(String a, int n)
    {
        String b = a;
        for(int i = 0; i < n; i++) b+=a;
        return b;
    }
}
