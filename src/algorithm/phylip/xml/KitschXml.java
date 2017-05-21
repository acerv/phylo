package algorithm.phylip.xml;

/**
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class KitschXml
{
    public static final String method = "Method";
    public static final String power = "Power";
    public static final String randomize = "Randomize";
    public static final String dataSets = "DataSets";

    public static String[] methodTag = { "\t\t\t<"+method+">", "</"+method+">\r\n" };
    public static String[] powerTag = { "\t\t\t<"+power+">", "</"+power+">\r\n" };
    public static String[] randomizeTag = { "\t\t\t<"+randomize+">", "</"+randomize+">\r\n" };
    public static String[] dataSetsTag = { "\t\t\t<"+dataSets+">", "</"+dataSets+">\r\n" };
}
