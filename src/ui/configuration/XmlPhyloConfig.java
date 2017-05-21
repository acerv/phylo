package ui.configuration;

/**
 *
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class XmlPhyloConfig
{
    // Definizione nomi dei tag
    public static final String phylo = "Phylo";
    public static final String exp_path = "Output";
    public static final String phylip_path = "Phylip";
    public static final String loading_path = "Loading";
    public static final String save_bootstrap = "SaveBootstrap";
    public static final String tree_leaves_top = "TreeLeaves";
    public static final String tree_save = "TreeSave";
    public static final String outfile_save = "OutfileSave";

    // Tags
    public static String versionTag = "<?xml version=\"1.0\" ";
    public static String encodingTag = "encoding=\"UTF-8\"?>\r\n";
    public static String[] phyloTag = { "<"+phylo+">\r\n", "</"+phylo+">\r\n" };
    public static String[] exp_pathTag = { "\t<"+exp_path+">", "</"+exp_path+">\r\n" };
    public static String[] phylip_pathTag = { "\t<"+phylip_path+">", "</"+phylip_path+">\r\n" };
    public static String[] loading_pathTag = { "\t<"+loading_path+">", "</"+loading_path+">\r\n" };
    public static String[] save_bootstrapTag = { "\t<"+save_bootstrap+">", "</"+save_bootstrap+">\r\n" };
    public static String[] tree_leaves_topTag = { "\t<"+tree_leaves_top+">", "</"+tree_leaves_top+">\r\n" };
    public static String[] tree_saveTag = { "\t<"+tree_save+">", "</"+tree_save+">\r\n" };
    public static String[] outfile_saveTag = { "\t<"+outfile_save+">", "</"+outfile_save+">\r\n" };
}
