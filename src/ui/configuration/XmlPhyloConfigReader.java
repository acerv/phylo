package ui.configuration;

import informations.Infos;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class XmlPhyloConfigReader extends XmlPhyloConfig
{
    /**
     * Legge un file di confgiurazione di Phylo e salva la configurazione in memoria
     * @param cfg_file File di configurazione
     * @return Configurazione da salvare in memoria
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public PhyloConfig readConfigFile(File cfg_file)
            throws IOException, ParserConfigurationException, SAXException
    {
        // Serve per ripristinare la configurazione nel caso si passi con lo stesso
        // file di configurazione da linux a windows o viceversa
        PhyloConfig newCfg = new PhyloConfig();

        // Documento che bisogna elaborare
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document dom = (Document) db.parse(cfg_file);

        // Prelevo l'esperimento
        Element root = dom.getDocumentElement();
        if ( !root.getNodeName().contains(phylo) )
            throw new ParserConfigurationException("missing <Phylo> main element");

        // Genero la configurazione da salvare in memoria
        PhyloConfig cfg = new PhyloConfig();
        cfg.exp_path = getTextValue(root, exp_path);
        cfg.phylip_path = getTextValue(root, phylip_path);
        cfg.loading_path = getTextValue(root, loading_path);
        cfg.exp_saveBootstrap = getBoolFromString(XmlPhyloConfigReader.getTextValue(root, save_bootstrap));
        cfg.HorizontalTree = getBoolFromString(XmlPhyloConfigReader.getTextValue(root, tree_leaves_top));
        cfg.tree_save = getBoolFromString(XmlPhyloConfigReader.getTextValue(root, tree_save));
        cfg.outfile_save = getBoolFromString(XmlPhyloConfigReader.getTextValue(root, outfile_save));

        // Basta che una sola delle stringhe contenga :\ o meno
        if (Infos.OS_NAME.indexOf( "nix" ) >= 0 ||
            Infos.OS_NAME.indexOf( "nux" ) >= 0 ||
            Infos.OS_NAME.indexOf( "mac" ) >= 0)
        {
            if(cfg.exp_path.contains(":\\"))
            {
                cfg.phylip_path = newCfg.phylip_path;
                cfg.exp_path = newCfg.exp_path;
                cfg.loading_path = newCfg.loading_path;
            }
        }
        else if(Infos.OS_NAME.indexOf( "win" ) >= 0)
        {
            if(!cfg.exp_path.contains(":\\"))
            {
                cfg.phylip_path = newCfg.phylip_path;
                cfg.exp_path = newCfg.exp_path;
                cfg.loading_path = newCfg.loading_path;
            }
        }

        return cfg;
    }

    // Valore di un attributo
    private static String getTextValue(Element ele, String tagName)
            throws ParserConfigurationException
    {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0)
        {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }
        
        if(textVal.equals(""))
            throw new ParserConfigurationException("missing <"+tagName+"> element");
        
        return textVal;
    }

    // Valore booleano di un attributo
    private static boolean getBoolFromString(String str)
    {
        if(str.equals("1")) return true;
        else return false;
    }
}
