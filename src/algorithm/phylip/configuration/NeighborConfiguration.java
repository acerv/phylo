package algorithm.phylip.configuration;

import algorithm.phylip.Neighbor;

/**
 * Neighbor-Joining configuration
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class NeighborConfiguration
{
    /** Tipo di albero. Può valere: <br>
     * - Neighbor.NEIGHBOR_TREE<br>
     * - Neighbor.UPGMA_TREE
     */
    public int tree = Neighbor.NEIGHBOR_TREE;

    /** Specie da non valutare */
    public int outgroupSpece = 0;

    /** Randomize input species */
    public boolean randomizeInput = false;

    /** Numero di matrici da analizzare */
    public int multipleDataSets = 0;
}
