package ui.configuration;

import informations.Infos;

/**
 *
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class PhyloConfig implements Cloneable
{
    // Base
    public static final String  CONFIG_NAME = "phylo.xml";

    // Directories
    public String phylip_path = Infos.HOME + Infos.FILE_SEPARATOR + "phylip";
    public String exp_path = Infos.HOME + Infos.FILE_SEPARATOR + "PhyloExperiments" + Infos.FILE_SEPARATOR;
    public String loading_path = Infos.HOME;

    // Booleans
    public boolean exp_saveBootstrap = false;
    public boolean HorizontalTree = false;
    public boolean tree_save = false;
    public boolean outfile_save = true;

    @Override
    public Object clone() 
            throws CloneNotSupportedException 
    {
        return super.clone();
    }
}
