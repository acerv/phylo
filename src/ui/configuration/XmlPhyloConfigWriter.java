package ui.configuration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 *
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class XmlPhyloConfigWriter extends XmlPhyloConfig
{
    public void writeConfig(PhyloConfig cfg, String path)
            throws IOException
    {
        // Creazione del file
        File outFile = new File(path, PhyloConfig.CONFIG_NAME);
        OutputStream fout = new FileOutputStream(outFile);
        OutputStream bout = new BufferedOutputStream(fout);
        OutputStreamWriter out = new OutputStreamWriter(bout, "UTF8");

        // Inizio scrittura del file
        out.write(versionTag);
        out.write(encodingTag);
        out.write(phyloTag[0]);

        // Experiment path
        out.write(exp_pathTag[0]);
        out.write(cfg.exp_path);
        out.write(exp_pathTag[1]);

        // Phylip path
        out.write(phylip_pathTag[0]);
        out.write(cfg.phylip_path);
        out.write(phylip_pathTag[1]);

        // Loading path
        out.write(loading_pathTag[0]);
        out.write(cfg.loading_path);
        out.write(loading_pathTag[1]);

        // Save bootstrap
        out.write(save_bootstrapTag[0]);
        if(cfg.exp_saveBootstrap) out.write("1"); else out.write("0");
        out.write(save_bootstrapTag[1]);

        // Tree leaves on top
        out.write(tree_leaves_topTag[0]);
        if(cfg.HorizontalTree) out.write("1"); else out.write("0");
        out.write(tree_leaves_topTag[1]);

        // Save tree
        out.write(tree_saveTag[0]);
        if(cfg.tree_save) out.write("1"); else out.write("0");
        out.write(tree_saveTag[1]);

        // Save outfile
        out.write(outfile_saveTag[0]);
        if(cfg.outfile_save) out.write("1"); else out.write("0");
        out.write(outfile_saveTag[1]);

        // Chiusura file
        out.write(phyloTag[1]);
        out.flush();
        out.close();
    }
}
