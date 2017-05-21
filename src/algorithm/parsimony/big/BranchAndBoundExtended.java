
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
import parser.set.langcosts.table.CostsTable;
import parser.set.langcosts.table.ParametersSet;
import parser.set.langrules.tree.RulesSet;
import tree.TreeException;
import tree.TreeInfos;
import tree.TreeIntoNewickFormat;
import utility.Debugger;
import utility.Timer;

/**
 *
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class BranchAndBoundExtended extends GenericAlgorithm
{
    static int LEFT_CHILD = 0;
    static int RIGHT_CHILD = 1;

    // Must be initialized
    int languageLenght;
    File inputFile;
    CharMatrixReader matrix;
    RulesSet rules;
    ParametersSet costs;
    ArrayList<String> alphabet;
    ArrayList<String> abmittedChars;
    ArrayList<String> languages;
    ArrayList<LabeledTreeNode> leaves;

    // Research tree
    ResearchTree searchTree;

    // List of costs for each level
    ArrayList<Integer> listOfCosts;

    // current Bound
    ResearchTreeNode bound = new ResearchTreeNode();

    // Found bounds
    int found_bounds = 0;

    // Configuration used by sankoff-extended algorithm
    SkffReader snkffReader = new SkffReader();

    boolean FIRST = true;

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

        // Initialize leaves list
        leaves = get_leaves_list();
    }

    public void exec() throws Throwable
    {
        Timer t = new Timer();
        t.start();

        Debugger.println("\n"
                        +"********************\n"
                        +"* BRANCH AND BOUND *\n"
                        +"********************");

        /* Tabella dei minimi costi per la funzione evaluate() */
        int[][] M = generate_table_of_min_costs(costs,
                abmittedChars.size(), languageLenght);

        Debugger.println("Table of minimum: ");
        print_table(M);

        /* Tabella dei cambiamenti da fare */
        boolean[][] P = generate_table_of_changes();

        Debugger.println("\nTable of changes:");
        print_table(P);

        /* Tabelle dei cambiamenti ad ogni livello dell'albero */
        ArrayList<boolean[][]> tablesOfChanges = generate_list_of_changes(P);

        /* Costi di cambiamento ad ogni livello dell'albero */
        listOfCosts = generate_list_of_costs(tablesOfChanges, M);

        /* Assegno al bound un valore sufficientemente grande */
        bound.setScore(Integer.MAX_VALUE);

        /* radice dell'albero di ricerca */
        ResearchTreeNode root = generate_root();
        bound = get_first_bound(root);

//        Debugger.println("\n\nFirst bound is the following one:");
//        bound.print(false);

	/* Aggiungo il bound alla blacklist */
        bound.setBlackList(true);

        Debugger.println("\nNumber of levels start from 2 (number of language in the current node):\n");

        /* Ricerco la soluzione ottima */
        backtrack(bound);

        Debugger.println("\nThe solution:");
        bound.print(false);

        t.end();

        Debugger.println("** Algorithm has found a solution in "+t.duration());

        /* Stampa del file di output */
        File out = new File(Infos.TEMPORARY_PATH,"global_branchbound_outtree.txt");
        TreeIntoNewickFormat nwk = new TreeIntoNewickFormat(bound);
        nwk.drawToFileAsNwk(out.getAbsolutePath());
        setOutput(out);
    }

    /* Ritorna il bound corrente se non trova una soluzione */
    ResearchTreeNode get_first_bound(ResearchTreeNode node) throws Exception
    {
        int addedLeaves = node.getNumberOfAddedLeaves();
        ResearchTreeNode new_bound = node;

        while(addedLeaves < leaves.size())
        {
            /* Questa funzione genera i figli per il nodo dato
             * e calcola il numero di cambiamenti che richiedono */
            generate_children(new_bound, addedLeaves, true);

            /* Con questa euristica cerco di avvicinarmi
             * ad una soluzione potenzialmente approssimata */
            new_bound = get_child_with_min_cost(new_bound);
            
            addedLeaves++;

            new_bound.setBlackList(true);
        }

        return new_bound;
    }

    void find_new_bound(ResearchTreeNode node) throws Exception
    {
        int addedLeaves = node.getNumberOfAddedLeaves();

        if(!node.isInBlackList())
        {
            ResearchTreeNode new_bound = node;

            if(addedLeaves < leaves.size())
            {
                /* Questa funzione genera i figli per il nodo dato
                 * e calcola il numero di cambiamenti che richiedono */
                generate_children(new_bound, addedLeaves, true);

                /* Ricerco il nuovo bound nel sottoalbero */
                for(int i = 0; i < new_bound.getNumberOfChildren(); i++)
                {
                    ResearchTreeNode child = new_bound.getChild(i);

                    if(child.getScore() < bound.getScore())
                    {
                        find_new_bound(child);
                    }
                    else
                    {
                        assign_null_to_subtree(child);
                    }
                }

                //System.gc();
//                Debugger.println("Total Memory"+Runtime.getRuntime().totalMemory());
//                Debugger.println("Free Memory"+Runtime.getRuntime().freeMemory());
            }

            /* Solution has been found */
            if(addedLeaves == leaves.size()) {
                bound = new_bound;
            }
            
            new_bound.setBlackList(true);
        }
    }

    ResearchTreeNode generate_root() throws Exception
    {
        ResearchTreeNode R = new ResearchTreeNode();
        LabeledTreeNode root = R.getRoot();
        root.initialize(abmittedChars, languageLenght);

        root.addChild(leaves.get(0));
        root.addChild(leaves.get(1));
        R.setNumberOfAddedLeaves(2);

        compute_cost_of_tree(R);
        
        return R;
    }

    void generate_children(ResearchTreeNode t, int leafToAdd,
                                boolean macroTree) throws Exception
    {
        /* Generate children of node */
        int numberOfNodes = get_nodes_list(t).size();
        LabeledTreeNode l_to_add = leaves.get(leafToAdd);

        for(int i = 0; i < numberOfNodes; i++)
        {
            ResearchTreeNode tree = clone_research_tree_node(t);
            
            ArrayList<LabeledTreeNode> nodes = get_nodes_list(tree);
            LabeledTreeNode leaf = new LabeledTreeNode();
            leaf.copyOf(l_to_add);

            ResearchTreeNode gTree = generate_tree(tree, nodes.get(i), leaf,
                    abmittedChars, languageLenght, macroTree);

            /* Add child */
            t.addChild(gTree);
        }

        /* calculate costs for children of node
         * with Sankoff Algorithm */
        calculate_cost_for_children(t);

        /* Sort children */
        sort_children(t);
    }

    void backtrack(ResearchTreeNode backNode) throws Exception
    {
        Timer timer = new Timer();
	backNode = backNode.getParent();
	ResearchTreeNode child;
	int evaluated_cost;

	/* Quando arrivo alla radice ho completato la ricerca */
	while( (backNode = backNode.getParent()) != null )
	{
            int level = backNode.getNumberOfAddedLeaves();
            Debugger.println("Moving in the research tree at level "+level);

            for(int i = 0; i < backNode.getNumberOfChildren(); i++)
            {
                child = backNode.getChild(i);
                
                if(!child.isInBlackList())
                {
                    // Debugger.println("Node has cost "+child.getScore()+" and bound has "+bound.getScore());

                    if( child.getScore() < bound.getScore() )
                    {
                        evaluated_cost = evaluate(child);

                        if(evaluated_cost < bound.getScore())
                        {
                            ResearchTreeNode oldBound = bound;

                            Debugger.print("Extimated cost for this subtree is "
                                    +evaluated_cost+" and bound has "+bound.getScore()+"\n"
                                    + "Looking for a better bound.... ");

                            timer.start();
                            find_new_bound(child);
                            timer.end();

                            Debugger.println(timer.duration());

                            /* Se il bound è cambiato continuo la ricerca
                             * partendo dal nuovo bound */
                            if( !oldBound.equals(bound) )
                            {
                                Debugger.println("FOUND NEW BOUND with score: "+bound.getScore());
                                Debugger.println("OLD BOUND HAD :"+oldBound.getScore());
                                
                                backNode = bound.getParent();

                                /* Free space */
                                free_space_from_old_bound(oldBound, bound);
                            }
                        }
                    }
                }
                else
                {
                    Debugger.println("node already explored");
                }
            }
	}
    }

    static void free_space_from_old_bound(ResearchTreeNode old_bound, ResearchTreeNode new_bound)
    {
        ResearchTreeNode prev_old = old_bound.getParent();
        ResearchTreeNode prev_new = new_bound.getParent();
        ResearchTreeNode common_child = prev_old;

        /* Both bounds are in the same level of research tree.
         * Research the common node for the bounds */
        while(!prev_new.equals(prev_old))
        {
            common_child = prev_old;
            
            prev_old = prev_old.getParent();
            prev_new = prev_new.getParent();
        }

        assign_null_to_upper_level(common_child);
    }

    ArrayList<boolean[][]> generate_list_of_changes(boolean[][] P)
    {
        ArrayList<boolean[][]> tables = new ArrayList<boolean[][]>();

        /* ************************************
         * Determino la tabella D per la root *
         **************************************/
        boolean[][] D_1;
        boolean[][] D_2;
        boolean[][] D;
        boolean[][] H;

        /* Prima lingua aggiunta */
        D_1 = generate_table(0);
        D_2 = generate_table(1);

        /* Determino H */
        D = sum_tables(D_1, D_2);
        H = subtract_tables(P, D);

        tables.add(H);

        /* Determino la tabella per gli n -2 linguaggi */
        for(int i = 2; i < leaves.size(); i++) {
            D_1 = generate_table(i);

            /* Determino H */
            D = sum_tables(D, D_1);
            H = subtract_tables(P, D);
            
            tables.add(H);
        }

        return tables;
    }

    static ArrayList<Integer> generate_list_of_costs(
            ArrayList<boolean[][]> tablesOfChanges, int[][] M)
    {
        ArrayList<Integer> tables = new ArrayList<Integer>();
        int[][] proj;
        int extimate_cost;

        for(int i = 0; i < tablesOfChanges.size(); i++)
        {
            /* Calcolo la differenza tra P e D */
            boolean[][] H = tablesOfChanges.get(i);

            /* Calcolo la proiezione di H in M */
            proj = project_table(H, M);

            /* Somma dei costi */
            extimate_cost = sum_all_elements(proj);

            /* Salvo la stima */
            tables.add(extimate_cost);
        }

        return tables;
    }

    static int[][] generate_table_of_min_costs(ParametersSet costs,
            int alp_size, int param)
    {
        int[][] min = new int[alp_size][param];
        int[] column;
        int[][] min_p;
        boolean def_costs;

        // far partire da 1 se si modifica
        for(int p = 0; p < param; p++)
        {
            /* Controllo i parametri definiti
             * del file dei costi */
            if(costs.contains(p))
                def_costs = true;
            else
                def_costs = false;

            /* Determino le tabelle */
            if(def_costs) {
                ArrayList<CostsTable> tables = costs.get(p).getTables();

                /* Controllo se c'è la tabella di default locale
                 * Se non c'è aggiungo quella globale */
                if(costs.get(p).getDefaultTable() == null)
                    tables.add(costs.getGlobalDefaultTable());
                else
                    tables.add(costs.get(p).getDefaultTable());

                /* Determino la tabella minima */
                min_p = get_min_table(tables);
            } else {
                CostsTable t = costs.getGlobalDefaultTable();
                min_p = t.getTable();
            }

            /* Colonna della tabella di minimo */
            column = get_min_column(min_p);

            for(int i = 0; i < alp_size; i++) {
                min[i][p] = column[i];
            }
        }

        return min;
    }
    
    static int[] get_min_column(int[][] min_p)
    {
        /* Matrice quadrata */
        int h = min_p.length;
        int[] column = new int[h];

        for(int i = 0; i < h; i++) {
            column[i] = Integer.MAX_VALUE;

            for(int j = 0; j < h; j++) {
                /* Diagonale nulla */
                if(i != j) {
                    if(column[i] > min_p[i][j])
                        column[i] = min_p[i][j];
                }
            }
        }

        return column;
    }

    static int[][] get_min_table(ArrayList<CostsTable> t)
    {
        int h = t.get(0).getTable().length;
        int w = t.get(0).getTable()[0].length;

        int[][] min = new int[h][w];

        for(int i = 0; i < h; i++) {
            for(int j = 0; j < w; j++) {
                min[i][j] = Integer.MAX_VALUE;

                /* Diagonale nulla */
                if(i != j) {
                    for(int table = 0; table < t.size(); table++) {
                        if(min[i][j] > t.get(table).getTable()[i][j])
                            min[i][j] = t.get(table).getTable()[i][j];
                    }
                }
                else
                     min[i][j] = 0;
            }
        }

        return min;
    }

    boolean[][] generate_table_of_changes() {
        int h = abmittedChars.size();
        int w = languageLenght;
        boolean[][] change = new boolean[h][w];

        for(int i = 0; i < leaves.size(); i++) {
            boolean[][] g = generate_table(i);
            change = sum_tables(change, g);
        }

        return change;
    }

    /* *****************************
     * The most important function *
     *******************************/
    int evaluate(ResearchTreeNode node)
    {
        int extimate_cost = 0;
        int addedLeaves = node.getNumberOfAddedLeaves();
        
	extimate_cost = listOfCosts.get(addedLeaves-2);

        return (int) (extimate_cost + node.getScore());
    }

    boolean[][] generate_table(int leaf)
    {
        ArrayList<String> ass = leaves.get(leaf).getAssignment();
        int alp = abmittedChars.size();
        
        boolean[][] D_k = new boolean[alp][languageLenght];
        
        for(int p = 0; p < languageLenght; p++)
        {
            String c = ass.get(p);
            int j = abmittedChars.indexOf(c);
            D_k[j][p] = true;
        }

        return D_k;
    }

    // OR is addiction
    static boolean[][] sum_tables(boolean[][] A, boolean[][] B)
    {
        int h = A.length;
        int w = A[0].length;
        boolean[][] sum = new boolean[h][w];

        for(int i = 0; i < h; i++)
            for(int j = 0; j < w; j++)
                sum[i][j] = A[i][j] || B[i][j];

        return sum;
    }

    // XOR is subtraction
    static boolean[][] subtract_tables(boolean[][] A, boolean[][] B)
    {
        int h = A.length;
        int w = A[0].length;
        boolean[][] diff = new boolean[h][w];

        for(int i = 0; i < h; i++)
            for(int j = 0; j < w; j++)
                diff[i][j] = A[i][j] ^ B[i][j];

        return diff;
    }

    // get cost in B where there's true in A
    static int[][] project_table(boolean[][] A, int[][] B)
    {
        int h = A.length;
        int w = A[0].length;
        int[][] proj = new int[h][w];

        for(int i = 0; i < h; i++) {
            for(int j = 0; j < w; j++) {
                if(A[i][j])
                    proj[i][j] = B[i][j];
                else
                    proj[i][j] = 0;
            }
        }
            
        return proj;
    }

    static int sum_all_elements(int[][] A)
    {
        int h = A.length;
        int w = A[0].length;
        int sum = 0;
        for(int i = 0; i < h; i++)
            for(int j = 0; j < w; j++)
                sum += A[i][j];

        return sum;
    }

    static void print_table(int[][] A) {
        for(int i = 0; i < A.length; i++) {
            for(int j = 0; j < A[0].length; j++)
                Debugger.print(" "+A[i][j]);

            Debugger.println();
        }
    }

    static void print_table(boolean[][] A) {
        for(int i = 0; i < A.length; i++) {
            for(int j = 0; j < A[0].length; j++)
                if(A[i][j])
                    Debugger.print(" 1");
                else
                    Debugger.print(" 0");

            Debugger.println();
        }
    }
    /* ***************************************************/

    
    ArrayList<LabeledTreeNode> get_leaves_list() throws TreeException
    {
        ArrayList<LabeledTreeNode> list = new ArrayList<LabeledTreeNode>();
        
        for(int i = 0; i < languages.size(); i++) {
            LabeledTreeNode node = get_node_from_language(i);
            list.add(node);
        }
        
        return list;
    }

    // Insertion sort
    static void sort_children(ResearchTreeNode node) throws TreeException
    {
        int j;
        for(int i = 1; i < node.getNumberOfChildren(); i++)
        {
            ResearchTreeNode child1 = clone_research_tree_node(node.getChild(i));
            j = i-1;

            while(j>=0 && node.getChild(j).getScore() > child1.getScore())
            {
                node.setChild(j+1, node.getChild(j));
                j--;
            }

            node.setChild(j+1, child1);
        }

        return;
    }

    LabeledTreeNode get_node_from_language(int index)
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

    static void assign_null_to_upper_level(ResearchTreeNode backNode)
    {
        /* Remove nodes from blacklist which are
         * already visited on higher level of the research tree*/
        for(int i = 0; i < backNode.getNumberOfChildren(); i++)
            assign_null_to_subtree(backNode.getChild(i));

        backNode.setBlackList(true);
    }

    static void assign_null_to_subtree(ResearchTreeNode node) {
        if(!node.isLeaf())
        {
            for(int i = 0; i < node.getNumberOfChildren(); i++)
                assign_null_to_subtree(node.getChild(i));
        }

        node = null;
    }

    static ResearchTreeNode generate_tree(ResearchTreeNode tree, LabeledTreeNode node,
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
                select_macro_nodes(new_parent, leafToAdd);

            root = (LabeledTreeNode) tree.getRoot();

            root.reset();
        }

        newTree.setRoot(root);
        newTree.setNumberOfAddedLeaves(tree.getNumberOfAddedLeaves()+1);

        // Used to print the tree
        TreeInfos.calculateHeightOfTree(newTree);

        //newTree.print(true);

        return newTree;
    }

    static void select_macro_nodes(LabeledTreeNode parent, LabeledTreeNode node)
    {
        int macroNodePosition = RIGHT_CHILD;

        parent.reset();

        // Get the child of parent to make as macro node
        if(parent.getChild(RIGHT_CHILD).equals(node))
            macroNodePosition = LEFT_CHILD;

        LabeledTreeNode child = parent.getChild(macroNodePosition);

        if(!child.isLeaf())
            child.setMacroNode(true);

        // Recursion to find all the others macronodes
        if(!parent.isRoot())
            select_macro_nodes((LabeledTreeNode) parent.getParent(), parent);
    }

    static ArrayList<LabeledTreeNode> get_nodes_list(ResearchTreeNode tree)
    {
        ArrayList<LabeledTreeNode> nodes = new ArrayList<LabeledTreeNode>();
        populate_nodes_list(nodes, tree.getRoot());

        return nodes;
    }

    static void populate_nodes_list(ArrayList<LabeledTreeNode> nodes,
            LabeledTreeNode node)
    {
        nodes.add(node);

        if(!node.isLeaf())
            for(int i = 0; i < node.getNumberOfChildren(); i++)
                populate_nodes_list(nodes, node.getChild(i));

    }

    static ResearchTreeNode clone_research_tree_node(ResearchTreeNode tree)
            throws TreeException
    {
        ResearchTreeNode clone = new ResearchTreeNode();
        clone.setScore(tree.getScore());
        clone.setNumberOfAddedLeaves(tree.getNumberOfAddedLeaves());
        copy_research_tree_node(clone.getRoot(), tree.getRoot());

        // Used to print tree
        TreeInfos.calculateHeightOfTree(clone);

        return clone;
    }

    static void copy_research_tree_node(LabeledTreeNode cloneNode, LabeledTreeNode treeNode)
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

                copy_research_tree_node(cloneNode.getChild(i), // clone
                                       treeNode.getChild(i)); // node of tree
            }
        }
    }

    void calculate_cost_for_children(ResearchTreeNode node)
            throws Exception
    {
        for(int i = 0; i < node.getNumberOfChildren(); i++)
            compute_cost_of_tree(node.getChild(i));
    }

    void compute_cost_of_tree(ResearchTreeNode node)
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

    static ResearchTreeNode get_child_with_min_cost(ResearchTreeNode node)
            throws TreeException
    {
        ResearchTreeNode minChild = node.getChild(0);

        for(int i = 1; i < node.getNumberOfChildren(); i++)
            if(node.getChild(i).getScore() < minChild.getScore())
                minChild = node.getChild(i);

        return minChild;
    }
}
