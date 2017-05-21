package experiment.config.xml;

/**
 * Contiene tutti i tag per il file di configurazione di Phylo in formato xml
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class XmlExperimentConfig
{
    public static int DEFAULT_TREE = 0;
    public static int CONSENSE_TREE = 1;

    // Open e Close per un tag Xml
    public static int OPEN = 0;
    public static int CLOSE = 1;

    // Definizioni dei nomi dei tag: usati nella lettura
    public static final String experiment = "Experiment";
    public static final String name = "Name";
    public static final String date = "Date";
    public static final String expType = "Type";
    public static final String expInput = "Input";
    public static final String bootstrapMethod = "BootstrapMethod";
    public static final String bootsrapNum = "BootstrapNum";
    public static final String distance = "Distance";
    public static final String tree = "Tree";
    public static final String algorithm = "Algorithm";
    public static final String algName = "Type";
    public static final String algConfig = "Config";
    public static final String consense = "Consense";

    // Definizioni dei tag: usati nella scrittura
    public static String versionTag = "<?xml version=\"1.0\" ";
    public static String encodingTag = "encoding=\"UTF-8\"?>\r\n";
    public static String[] experimentTag = { "<"+experiment+">\r\n", "</"+experiment+">\r\n" };
    public static String[] nameTag = { "\t<"+name+">", "</"+name+">\r\n" };
    public static String[] dateTag = { "\t<"+date+">", "</"+date+">\r\n" };
    public static String[] expTypeTag = { "\t<"+expType+">", "</"+expType+">\r\n" };
    public static String[] expInputTag = { "\t<"+expInput+">", "</"+expInput+">\r\n" };
    public static String[] bootsrapMethodTag = { "\t<"+bootstrapMethod+">", "</"+bootstrapMethod+">\r\n" };
    public static String[] bootsrapNumTag = { "\t<"+bootsrapNum+">", "</"+bootsrapNum+">\r\n" };
    public static String[] distanceTag = { "\t<"+distance+">", "</"+distance+">\r\n" };
    public static String[] treeTag = { "\t<"+tree+">", "</"+tree+">\r\n" };
    public static String[] algorithmTag = { "\t<"+algorithm+">\r\n", "\t</"+algorithm+">\r\n" };
    public static String[] algNameTag = { "\t\t<"+algName+">", "</"+algName+">\r\n" };
    public static String[] algConfigTag = { "\t\t<"+algConfig+">\r\n", "\t\t</"+algConfig+">\r\n" };
    public static String[] consenseTag = { "\t<"+consense+">\r\n", "\t</"+consense+">\r\n" };
}
