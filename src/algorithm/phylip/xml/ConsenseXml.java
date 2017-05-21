package algorithm.phylip.xml;

/**
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class ConsenseXml
{
    public static final String type = "Type";
    public static final String outgroup = "Outgroup";
    public static final String rooted = "Rooted";
    
    public static String[] typeTag = { "\t\t<"+type+">", "</"+type+">\r\n" };
    public static String[] outgroupTag = { "\t\t<"+outgroup+">", "</"+outgroup+">\r\n" };
    public static String[] rootedTag = { "\t\t<"+rooted+">", "</"+rooted+">\r\n" };
}
