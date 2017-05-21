package algorithm.parsimony.big;

import algorithm.GenericAlgorithm;
import algorithm.parsimony.small.LabeledTreeNode;
import algorithm.parsimony.small.SankoffExtended;
import algorithm.parsimony.small.SkffReader;
import informations.Infos;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import matrix.reader.CharMatrixReader;
import parser.set.declare.LanguageInformations;
import parser.set.langcosts.table.ParametersSet;
import parser.set.langrules.tree.RulesSet;
import tree.TreeException;
import tree.TreeInfos;
import tree.TreeIntoNewickFormat;
import utility.Debugger;

/**
 * 
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class LocalBranchAndBound extends GenericAlgorithm
{
    static int LEFT_CHILD = 0;
    static int RIGHT_CHILD = 1;

    File inputFile;
    CharMatrixReader matrix;
    RulesSet rules;
    ParametersSet costs;
    ArrayList<String> alphabet;
    ArrayList<String> abmittedChars;
    int languageLenght;
    ArrayList<String> languages;

    // Research tree
    ResearchTree searchTree;

    // Current bound & current added leaf
    ResearchTreeNode bound;

    // Configuration used by sankoff-extended algorithm
    SkffReader snkffReader = new SkffReader();

    // Informations
    ArrayList<Integer> equalTrees = new ArrayList<Integer>();
    boolean PRINT_OUTPUT_FILE = false;

    /**
     * Initialize class
     */
    public LocalBranchAndBound()
    {
        setValue(Infos.LOCAL_BRANCH_AND_BOUND);
        setName(Infos.SUPPORTED_ALGORITHMS[getValue()]);
    }

    @Override
    public void setInput(File input) throws Throwable
    {
        this.input = input;

        BabReader read = new BabReader();
        read.setInputFile(input);
        read.load();
        
        alphabet = read.infos.getAlphabet();
        languageLenght = read.infos.getParameters();
        matrix = read.matrix;
        languages = matrix.getLanguages();
        rules = read.rules;
        costs = read.costs;

        // This is the same for everything
        abmittedChars = Infos.getAbmittedChars(alphabet);

        // Configuration of Sankoff algorithm
        snkffReader.costs = costs;
        snkffReader.infos = new LanguageInformations(alphabet, languageLenght);
        snkffReader.matrix = matrix;
        snkffReader.rules = rules;

        // Initialize research tree
        searchTree = new ResearchTree();
        searchTree.getRoot().getRoot().initialize(abmittedChars, languageLenght);
    }


    /* ****************
     * INITIALIZATION *
     ******************/
    void initializeResearchTree() throws Exception
    {
        //Debugger.println("> Making the first tree with '"+languages.get(0)+"' and '"+languages.get(1)+"'");
        LabeledTreeNode n1 = getLabeledTreeNodeFromLanguage(0);
        LabeledTreeNode n2 = getLabeledTreeNodeFromLanguage(1);

        ResearchTreeNode t = searchTree.getRoot();
        t.getRoot().addChild(n1);
        t.getRoot().addChild(n2);

        bound = searchTree.getRoot();

        //Debugger.println("> Adding new leaf '"+languages.get(2)+"'");
        LabeledTreeNode leafToAdd = (LabeledTreeNode) getLabeledTreeNodeFromLanguage(2);
        bound = findChildWithMinScore(bound, leafToAdd, false);
    }

    LabeledTreeNode getLabeledTreeNodeFromLanguage(int index)
            throws TreeException
    {
        LabeledTreeNode node = new LabeledTreeNode();

        // Initializazion
        node.setName(languages.get(index));
        node.initialize(abmittedChars, languageLenght);

        // Assignments
        ArrayList<String> lang = matrix.getMatrix().get(index);
        node.setAssignment(lang);

        return node;
    }
    

    /* ********
     * BRANCH *
     **********/
    void generateChildrenFor(ResearchTreeNode t, LabeledTreeNode leafToAdd,
                                boolean macroTree) throws TreeException
    {
        int numberOfNodes = getNodesList(t).size();

        for(int i = 0; i < numberOfNodes; i++)
        {
            ResearchTreeNode tree = cloneResearchTreeNode(t);
            ArrayList<LabeledTreeNode> nodes = getNodesList(tree);
            LabeledTreeNode leaf = new LabeledTreeNode();
            leaf.copyOf(leafToAdd);

            ResearchTreeNode gTree = generateTree(tree, nodes.get(i), leaf,
                    abmittedChars, languageLenght, macroTree);

            t.addChild(gTree);
        }
    }

    static ResearchTreeNode generateTree(ResearchTreeNode tree, LabeledTreeNode node,
                                  LabeledTreeNode leafToAdd, ArrayList<String> abmittedChars,
                                  int languageLenght, boolean macroTree) throws TreeException
    {
        ResearchTreeNode newTree = new ResearchTreeNode();
        LabeledTreeNode root;

        // First the root
        if(node.isRoot())
        {
            LabeledTreeNode new_root = new LabeledTreeNode();
            new_root.initialize(abmittedChars, languageLenght);

            // Children of the new root
            LabeledTreeNode rightNode = leafToAdd;
            LabeledTreeNode leftNode = node;
            
            if(macroTree)
                leftNode.setMacroNode(true);
            
            new_root.addChild(leftNode);
            new_root.addChild(rightNode);

            root = new_root;
        }
        // Leaves and internal nodes
        else
        {
            LabeledTreeNode parent = (LabeledTreeNode) node.getParent();
            LabeledTreeNode new_parent = new LabeledTreeNode();
            new_parent.initialize(abmittedChars, languageLenght);

            int nodePosition = LEFT_CHILD;
            
            if(parent.getChild(RIGHT_CHILD).equals(node))
                nodePosition = RIGHT_CHILD;

            // Add new parent's children
            new_parent.addChild(node);
            new_parent.addChild(leafToAdd);

            // Connect new parent with the rest of the tree
            parent.setChild(nodePosition, new_parent);

            // Create macro tree ?
            if(macroTree)
                selectMacroNodes(new_parent, leafToAdd);

            root = (LabeledTreeNode) tree.getRoot();
        }

        newTree.setRoot(root);
        newTree.setNumberOfAddedLeaves(tree.getNumberOfAddedLeaves()+1);
        
        // Used to print the tree
        TreeInfos.calculateHeightOfTree(newTree);
        
        return newTree;
    }

    static void selectMacroNodes(LabeledTreeNode parent, LabeledTreeNode node)
    {
        int macroNodePosition = RIGHT_CHILD;

        // Get the child of parent to make as macro node
        if(parent.getChild(RIGHT_CHILD).equals(node))
            macroNodePosition = LEFT_CHILD;

        LabeledTreeNode child = parent.getChild(macroNodePosition);
        
        if(!child.isLeaf())
            child.setMacroNode(true);

        // Recursion to find all the others macronodes
        if(!parent.isRoot())
            selectMacroNodes((LabeledTreeNode) parent.getParent(), parent);
    }

    static ArrayList<LabeledTreeNode> getNodesList(ResearchTreeNode tree)
    {
        ArrayList<LabeledTreeNode> nodes = new ArrayList<LabeledTreeNode>();
        populateNodesList(nodes, tree.getRoot());
        
        return nodes;
    }

    static void populateNodesList(ArrayList<LabeledTreeNode> nodes,
            LabeledTreeNode node)
    {
        nodes.add(node);
        
        if(!node.isLeaf())
            for(int i = 0; i < node.getNumberOfChildren(); i++)
                populateNodesList(nodes, node.getChild(i));

    }

    static ResearchTreeNode cloneResearchTreeNode(ResearchTreeNode tree)
            throws TreeException
    {
        ResearchTreeNode clone = new ResearchTreeNode();
        clone.setScore(tree.getScore());
        clone.setNumberOfAddedLeaves(tree.getNumberOfAddedLeaves());
        copyOfResearchTreeNode(clone.getRoot(), tree.getRoot());

        // Used to print tree
        TreeInfos.calculateHeightOfTree(clone);

        return clone;
    }

    static void copyOfResearchTreeNode(LabeledTreeNode cloneNode, LabeledTreeNode treeNode)
            throws TreeException
    {
        cloneNode.copyOf(treeNode);
        
        // Recursion
        if(!treeNode.isLeaf())
        {
            for(int i = 0; i < treeNode.getNumberOfChildren(); i++)
            {
                LabeledTreeNode child = new LabeledTreeNode();
                cloneNode.addChild(child);

                copyOfResearchTreeNode(cloneNode.getChild(i), // clone
                                       treeNode.getChild(i)); // node of tree
            }
        }
    }


    /* ***********
     * AND BOUND *
     *************/
    void calculateChildrenScoresFor(ResearchTreeNode node)
            throws Exception
    {
        for(int i = 0; i < node.getNumberOfChildren(); i++)
            calculateScoreOfTree(node.getChild(i));
    }

    void calculateScoreOfTree(ResearchTreeNode node)
            throws TreeException, IOException, Exception
    {
        SankoffExtended s = new SankoffExtended();
        snkffReader.tree = node;
        s.setInput(snkffReader);
        
        
        /* BE ATTENTION: use the debug or print scores mode could
         * decrease a lot the performances of the algorithm. 
         * Use them _OLNY_ in particular cases or if you need it. */
        //s.printScores(true);
        //s.setDebugMode(true);
        
        // Execute Sankoff
        s.exec();

        // Set the tree score
        node.setScore(node.getRoot().getCostOfNode());
    }

    ResearchTreeNode findChildWithMinScore(ResearchTreeNode node, LabeledTreeNode leafToAdd, boolean macroNodes)
            throws Exception
    {
        generateChildrenFor(node, leafToAdd, macroNodes);
        calculateChildrenScoresFor(node);
        ResearchTreeNode minChild = getChildWithMinimumScore(node);

        return minChild;
    }

    ResearchTreeNode localBacktrack(ResearchTreeNode parent, ResearchTreeNode bound,
            LabeledTreeNode leafToAdd, boolean macroNodes)
            throws Exception
    {
        ResearchTreeNode bestBound = bound;

        // Informations
        ArrayList<String> addedLeaf = new ArrayList<String>();
        ArrayList<Double> bestScore = new ArrayList<Double>();
        ArrayList<Double> newScore  = new ArrayList<Double>();
        int boundsForTheCurrentLeaf = 0;
        
        // Searching for new bound
        for(int i = 0; i < parent.getNumberOfChildren(); i++)
        {
            if(!parent.getChild(i).equals(bound.getParent()))
            {
                if(bestBound.getScore() >= parent.getChild(i).getScore())
                {
                    ResearchTreeNode newBound = findChildWithMinScore(parent.getChild(i), leafToAdd, macroNodes);

                    if(bestBound.getScore() > newBound.getScore())
                    {
                        // Save informations
                        addedLeaf.add(leafToAdd.getName());
                        bestScore.add(bestBound.getScore());
                        newScore.add(newBound.getScore());

                        // Update bound
                        bestBound = newBound;
                    }
                    else if(bestBound.getScore() == newBound.getScore()) {
                        boundsForTheCurrentLeaf++;
                    }
                    else
                    {
                        // It's needed to get free space
                        parent.setChild(i, null);
                    }
                }
            }
            
            // Launch garbage collector every 8 times
            if(i % 8 == 0)
                System.gc();
        }

        // Print informations
        if(!bestScore.isEmpty() && Debugger.getDebug())
        {
            Debugger.println(" Following bounds has been updated:\n\n"
                           + "--------------------------------------------------------\n"
                           + " Added Leaf\t| Prev. score\t| New score\n"
                           + "--------------------------------------------------------");

            for(int i = 0; i < bestScore.size(); i++)
                Debugger.println(" "+addedLeaf.get(i)+"\t\t| "
                        + bestScore.get(i)+"\t\t| "+newScore.get(i));

            Debugger.println();
        }

        // Save found equal trees during research of new bound
        equalTrees.add(boundsForTheCurrentLeaf);

        return bestBound;
    }

    // Insertion sort: not used anymore (too slow as method)
    static void sortChildrenOf(ResearchTreeNode node) throws TreeException
    {
        int j;
        for(int i = 1; i < node.getNumberOfChildren(); i++)
        {
            ResearchTreeNode child1 = cloneResearchTreeNode(node.getChild(i));
            j = i-1;

            while(j>=0 && node.getChild(j).getScore() > child1.getScore())
            {
                node.setChild(j+1, node.getChild(j));
                j--;
            }

            node.setChild(j+1, child1);
        }
    }

    static ResearchTreeNode getChildWithMinimumScore(ResearchTreeNode node)
            throws TreeException
    {
        ResearchTreeNode minChild = node.getChild(0);

        for(int i = 1; i < node.getNumberOfChildren(); i++)
            if(node.getChild(i).getScore() < minChild.getScore())
                minChild = node.getChild(i);

        return minChild;
    }

    /* ***********
     * EXECUTION *
     *************/
    @Override
    public void exec() throws Throwable
    {
        Debugger.println("\n"
                        +"**************************\n"
                        +"* LOCAL BRANCH AND BOUND *\n"
                        +"**************************");

        /* ****************************
         * FIRST BOUND INITIALIZATION *
         ******************************/
        initializeResearchTree();

        
        /* *******************************************
         * START TO FIND THE BOUND FROM THE 4rd LEAF *
         *********************************************/
        boolean optimized = true;
        
        for(int leaf = 3; leaf < languages.size(); leaf++)
        {
            // Generate the first level of the research tree
            Debugger.println(" Adding new leaf '"+languages.get(leaf)+"' ("+leaf+")");
            LabeledTreeNode leafToAdd = (LabeledTreeNode) getLabeledTreeNodeFromLanguage(leaf);

            
            /* *********************
             * GET THE FIRST BOUND *
             ***********************/
            ResearchTreeNode firstBound = bound.getParent();
            bound = findChildWithMinScore(bound, leafToAdd, optimized);

            
            /* ***********************
             * SEARCH THE BEST BOUND *
             *************************/
            bound = localBacktrack(firstBound, bound, leafToAdd, optimized);

            // It's needed to get free space
            //bound.getParent().setParent(null);

//            Debugger.println("Total Memory"+Runtime.getRuntime().totalMemory());
//            Debugger.println("Free Memory"+Runtime.getRuntime().freeMemory());
        }

        printInformations();

        // Save output file
        if(PRINT_OUTPUT_FILE)
        {
            File out = new File(Infos.TEMPORARY_PATH,"local_branchbound_outtree.txt");
            TreeIntoNewickFormat nwk = new TreeIntoNewickFormat(bound);
            nwk.drawToFileAsNwk(out.getAbsolutePath());
            setOutput(out);
        }

        // Get free space
        System.gc();
    }

    public void setPrintOutput(boolean print) {
        PRINT_OUTPUT_FILE = print;
    }

    private void printInformations()
    {
        // Print informations about number of bound per language
        Debugger.println("\n"
                       + " The following table shows how many best tree has been\n"
                       + " found during local backtrack operation for the given language:\n"
                       + "-------------------------------------\n"
                       + " Language | Number of best trees\n"
                       + "-------------------------------------");
        
        for(int i = 0; i <= 2; i++)
            Debugger.println(" "+languages.get(i)+"\t| *");

        for(int i = 0; i < equalTrees.size(); i++)
            Debugger.println(" "+languages.get(i+3)+"\t| "+equalTrees.get(i));

        // Tree
        Debugger.println("\n The tree with best score is the following one:");
        bound.print(false);
    }
}
