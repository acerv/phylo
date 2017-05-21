package algorithm.phylip.xml;

/**
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class NeighborXml
{
    public static final String tree = "Tree";
    public static final String outgroup = "Outgroup";
    public static final String randomize = "Randomize";
    public static final String dataSets = "DataSets";

    public static String[] treeTag = { "\t\t\t<"+tree+">", "</"+tree+">\r\n" };
    public static String[] outgroupTag = { "\t\t\t<"+outgroup+">", "</"+outgroup+">\r\n" };
    public static String[] randomizeTag = { "\t\t\t<"+randomize+">", "</"+randomize+">\r\n" };
    public static String[] dataSetsTag = { "\t\t\t<"+dataSets+">", "</"+dataSets+">\r\n" };
}
