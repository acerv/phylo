package experiment.config.xml;

import algorithm.GenericAlgorithm;
import algorithm.phylip.configuration.ConsenseConfiguration;
import algorithm.phylip.configuration.FitchConfiguration;
import algorithm.phylip.configuration.KitschConfiguration;
import algorithm.phylip.configuration.NeighborConfiguration;
import bootstrap.Bootstrapper;
import distance.Distance;
import matrix.exception.LanguageException;
import matrix.exception.MatrixFormatException;
import matrix.reader.MatrixReader;
import matrix.reader.CharMatrixReader;
import matrix.reader.DistMatrixReader;
import algorithm.phylip.Consense;
import algorithm.phylip.exception.PhylipNoConfigurationException;
import algorithm.phylip.Fitch;
import algorithm.phylip.Kitsch;
import algorithm.phylip.Neighbor;
import algorithm.phylip.xml.ConsenseXml;
import algorithm.phylip.xml.FitchXml;
import algorithm.phylip.xml.KitschXml;
import algorithm.phylip.xml.NeighborXml;
import experiment.ExperimentBootstrap;
import experiment.ExperimentCalcDistance;
import experiment.ExperimentLoadDistance;
import experiment.Experiment;
import experiment.GenericExperiment;
import experiment.exception.PhyloIncorrectConfigurationFile;
import files.utility.FileCopy;
import files.utility.ZipUtility;
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
import ui.configuration.PhyloConfig;
import utility.Debugger;

/**
 * Legge un file xml contenente le configurazioni di un esperimento e lo configura
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class XmlExperimentConfigReader extends XmlExperimentConfig
{
    /**
     * Legge la configurazione del programma Phylo in formato xml e inizializza
     * l'esperimento istanziato e passatogli
     * @param phyloCfg
     * @param phyloFile
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws PhylipNoConfigurationException
     * @throws MatrixFormatException
     * @throws LanguageException
     * @throws PhyloIncorrectConfigurationFile
     */
    public static Experiment readConfig(PhyloConfig phyloCfg, File phyloFile)
            throws ParserConfigurationException, SAXException, IOException,
            PhylipNoConfigurationException, MatrixFormatException, LanguageException,
            PhyloIncorrectConfigurationFile
    {
        // Riconosco se è un file di configurazione di phylo
        if (phyloFile.getName().contains(Experiment.EXPERIMENT_EXTENSION))
        {
            Debugger.println("> Reading experiment file "+phyloFile.getName());
            
            // Cartelle temporanee
            File tmpPath = new File(new File(Infos.TEMPORARY_PATH), "exp_phylo");
            if ( tmpPath.exists() ) tmpPath.delete(); else tmpPath.mkdir();

            // Estraggo il file di configurazione
            ZipUtility.unzip(phyloFile, tmpPath);

            // Leggo la configurazione ed inizializzo l'esperimento
            File configFile = new File(tmpPath, Experiment.CONFIGURATION_FILE_NAME);

            // Matrice da caricare
            MatrixReader matrix = null;

            // Algoritmo da caricare nell'esperimento
            GenericAlgorithm alg = null;

            // Tipo di algoritmo per la generazione dell'albero
            int treeAlgorithm = 0;

            // Documento che bisogna elaborare
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = (Document) db.parse(configFile);

            // Prelevo l'esperimento
            Element root = dom.getDocumentElement();

            if ( !root.getNodeName().contains(experiment) )
                throw new ParserConfigurationException("missing <NewExperiment> main element");

            // Prelevo il nome e data dell'esperimento
            String experimentString = getTextValue(root,name);
            String dateString = getTextValue(root,date);

            /* *********************
             * METODO DA ESEGUIRE  *
             ***********************/
            String methodString = getTextValue(root,expType);
            int methodInt = Integer.parseInt(methodString);

            Debugger.println("> Experiment Name: "+experimentString+
                             "\n> Type: "+Infos.SUPPORTED_EXPERIMENTS[methodInt]+
                             "\n> Date: "+dateString);

            /* ****************************
             * CREAZIONE DELL'ESPERIMENTO *
             ******************************/
            String inputString = null;
            Experiment exp = null;

            // Riconoscimento del metodo
            if (Infos.BOOTSTRAP_EXP == methodInt)
            {
                exp = new ExperimentBootstrap(phyloCfg);
                exp.setDate(dateString);

                // Nome della matrice
                inputString = getTextValue(root,expInput);
                Debugger.println("> Characters matrix: "+inputString);
                matrix = new CharMatrixReader();

                // Tipo di bootstrap
                int bootMethod = Integer.parseInt(getTextValue(root, bootstrapMethod));
                Debugger.println("> Bootstrap method: "+Infos.SUPPORTED_BOOTSTRAP_METHODS[bootMethod]);

                // Numero di bootstraps
                int num = Integer.parseInt(getTextValue(root, bootsrapNum));
                Debugger.println("> Bootstrap numbers: "+num);

                // Metodo delle distanze
                int distanceMethod = Integer.parseInt(getTextValue(root, distance));
                Debugger.println("> Distance method: "+Infos.SUPPORTED_DISTANCES[distanceMethod]);

                // Metodo di bootstrap
                Bootstrapper bootstrapper = Infos.getBootstrapAlgorithm(bootMethod, num, distanceMethod);

                /* Carico la matrice in memoria. La configurazione sta
                 * nella stessa cartella della matrice da caricare.
                 */
                matrix.loadMatrixFromFile(new File(configFile.getParentFile(), inputString));

                // Creo l'esperimento
                ((ExperimentBootstrap)exp).mkNewExperiment(experimentString, bootstrapper, (CharMatrixReader) matrix);
            }
            else if (Infos.DISTANCE_EXP == methodInt)
            {
                exp = new ExperimentCalcDistance(phyloCfg);
                exp.setDate(dateString);

                inputString = getTextValue(root,expInput);
                Debugger.println("> Characters matrix: "+inputString);
                matrix = new CharMatrixReader();

                /* Carico la matrice in memoria. La configurazione sta
                 * nella stessa cartella della matrice da caricare.
                 */
                matrix.loadMatrixFromFile(new File(configFile.getParentFile(), inputString));

                // Creo l'esperimento
                int distanceMethod = Integer.parseInt(getTextValue(root, distance));
                Debugger.println("> Distance method: "+Infos.SUPPORTED_DISTANCES[distanceMethod]);

                Distance dist = Infos.getDistanceAlgorithm(distanceMethod);
                ((ExperimentCalcDistance)exp).mkNewExperiment(experimentString, dist, (CharMatrixReader) matrix);
            }
            else if (Infos.LOAD_DISTANCE_EXP == methodInt)
            {
                exp = new ExperimentLoadDistance(phyloCfg);
                exp.setDate(dateString);

                inputString = getTextValue(root,expInput);
                Debugger.println("> Distance matrix: "+inputString);
                matrix = new DistMatrixReader();

                /* Carico la matrice in memoria. La configurazione sta
                 * nella stessa cartella della matrice da caricare.
                 */
                matrix.loadMatrixFromFile(new File(configFile.getParentFile(), inputString));

                // Creo l'esperimento
                ((ExperimentLoadDistance)exp).mkNewExperiment(experimentString, (DistMatrixReader) matrix);
            }
            else if(Infos.GENERIC_EXPERIMENT == methodInt)
            {
                exp = new GenericExperiment(phyloCfg, experimentString);
                exp.setDate(dateString);

                inputString = getTextValue(root, expInput);
                Debugger.println("> Input: "+inputString);

                // Copio il file di input nella cartella di lavoro
                FileCopy f = new FileCopy();
                File input = new File(configFile.getParentFile(), inputString);
                File in = f.fileIntoDirectory(input, exp.getExperimentPath());

                exp.setInput(in);
            }
            else
            {
                /* *********************************
                 * INTEGRARE QUI NUOVI ESPERIMENTI *
                 ***********************************/
            }


            /* ************************
             * ALGORITMO DA ESEGUIRE  *
             **************************/
            NodeList nodes = root.getElementsByTagName(algorithm);

            // Leggo gli attributi
            if (nodes != null && nodes.getLength() > 0)
            {
                // Prelevo gli elementi
                Element attr = (Element) nodes.item(0);

                // Prelevo il nome dell'algoritmo
                int usedAlgorithm = Integer.parseInt(getTextValue(attr, algName));

                // Fitch
                if(usedAlgorithm == Infos.FITCH)
                {
                    Debugger.println("> Algorithm: Fitch");
                    
                    // Estraggo i parametri
                    String fitchMethod = getTextValue(attr, FitchXml.method);
                    String power = getTextValue(attr, FitchXml.power);
                    String outgroup = getTextValue(attr, FitchXml.outgroup);
                    String randomize = getTextValue(attr, FitchXml.randomize);
                    String dataSets = getTextValue(attr, FitchXml.dataSets);

                    // Creo la configurazione
                    FitchConfiguration cfg = new FitchConfiguration();
                    cfg.method = Integer.parseInt(fitchMethod);
                    cfg.power = Double.parseDouble(power);
                    cfg.outgroupSpece = Integer.parseInt(outgroup);
                    if(Integer.parseInt(randomize) == 1) cfg.randomizeInputOrderTimes = true;
                    cfg.multipleDataSets = Integer.parseInt(dataSets);

                    if(Fitch.FITCH_MARGOLIASH_METHOD == cfg.method)
                        Debugger.println("> Method: fitch-margoliash method");
                    else
                        Debugger.println("> Method: minimum evolution method");

                    Debugger.println("> Power: "+cfg.power+
                                     "\n> Outgroup: "+cfg.outgroupSpece+
                                     "\n> Randomize: "+cfg.randomizeInputOrderTimes+
                                     "\n> Data sets: "+cfg.multipleDataSets);

                    // Inizializzo l'algoritmo
                    alg = new Fitch(cfg);
                }
                // Kitsch
                else if(usedAlgorithm == Infos.KITSCH)
                {
                    Debugger.println("> Algorithm: Kitsch");

                    // Estraggo i parametri
                    String kitschMethod = getTextValue(attr, KitschXml.method);
                    String power = getTextValue(attr, KitschXml.power);
                    String randomize = getTextValue(attr, KitschXml.randomize);
                    String dataSets = getTextValue(attr, KitschXml.dataSets);

                    // Creo la configurazione
                    KitschConfiguration cfg = new KitschConfiguration();
                    cfg.method = Integer.parseInt(kitschMethod);
                    cfg.power = Double.parseDouble(power);
                    if(Integer.parseInt(randomize) == 1) cfg.randomizeInputOrderTimes = true;
                    cfg.multipleDataSets = Integer.parseInt(dataSets);

                    if(Kitsch.FITCH_MARGOLIASH_METHOD == cfg.method)
                        Debugger.println("> Method: fitch-margoliash method");
                    else
                        Debugger.println("> Method: minimum evolution method");

                    Debugger.println("> Power: "+cfg.power+
                                     "\n> Randomize: "+cfg.randomizeInputOrderTimes+
                                     "\n> Data sets: "+cfg.multipleDataSets);

                    // Inizializzo l'algoritmo
                    alg = new Kitsch(cfg);
                }
                // Neighbor
                else if(usedAlgorithm == Infos.NEIGHBOR)
                {
                    // Estraggo i parametri
                    String neighborTree = getTextValue(attr, NeighborXml.tree);
                    String outgroup = getTextValue(attr, NeighborXml.outgroup);
                    String randomize = getTextValue(attr, NeighborXml.randomize);
                    String dataSets = getTextValue(attr, NeighborXml.dataSets);

                    // Creo la configurazione
                    NeighborConfiguration cfg = new NeighborConfiguration();
                    cfg.tree = Integer.parseInt(neighborTree);
                    cfg.outgroupSpece = Integer.parseInt(outgroup);
                    if(randomize.equals("1")) cfg.randomizeInput = true;
                    else cfg.randomizeInput = false;
                    cfg.multipleDataSets = Integer.parseInt(dataSets);

                    if(Neighbor.NEIGHBOR_TREE == cfg.tree)
                        Debugger.println("> Algorithm: Neighbor");
                    else
                        Debugger.println("> Algorithm: UPGMA");

                    Debugger.println("> Outgroup: "+cfg.outgroupSpece+
                                     "\n> Randomize: "+cfg.randomizeInput+
                                     "\n> Data sets: "+cfg.multipleDataSets);

                    // Inizializzo l'algoritmo
                    alg = new Neighbor(cfg);
                }
                else
                {
                    if(usedAlgorithm >= Infos.STANDARD_ALGORITHMS.length)
                        alg = Infos.getGenericAlgorithm(usedAlgorithm); // null if it's not defined

                    
                    /* *****************************
                     * CONFIGURARE QUI L'ALGORITMO *
                     *******************************/
                }
            }
            
            // Assegno all'esperimento il tipo di algoritmo
            exp.setAlgorithm(alg);

            /* **********************
             * ALBERO DI CONSENSO ? *
             ************************/
            treeAlgorithm = Integer.parseInt(getTextValue(root, tree));
            if ( treeAlgorithm == CONSENSE_TREE )
            {
                // Estraggo la configurazione
                ConsenseConfiguration cfg = new ConsenseConfiguration();
                NodeList cons = root.getElementsByTagName(consense);
                Element attr = (Element) cons.item(0);

                String type = getTextValue(attr, ConsenseXml.type);
                String outgroup = getTextValue(attr, ConsenseXml.outgroup);
                String rooted = getTextValue(attr, ConsenseXml.rooted);

                cfg.consensusType = Integer.parseInt(type);
                cfg.outgroupSpece = Integer.parseInt(outgroup);
                if(rooted.equals("1")) cfg.rooted = true;
                else cfg.rooted = false;

                if(Consense.MAJORITY_RULE == cfg.consensusType)
                    Debugger.println("> Consensus type: Majority rule");
                else if(Consense.MAJORITY_RULE_EXTENDED == cfg.consensusType)
                    Debugger.println("> Consensus type: Majority rule extended");
                else if(Consense.ML_CONSENSUS == cfg.consensusType)
                    Debugger.println("> Consensus type: ML consensus");
                else if(Consense.STRICT == cfg.consensusType)
                    Debugger.println("> Consensus type: Strict");

                Debugger.println("> Outgroup spece: "+cfg.outgroupSpece+
                                 "\n> Rooted: "+cfg.rooted);

                // Setto il programma di consenso
                Consense consAlg = new Consense(cfg);

                // Necessariamente sarà un algoritmo di bootstrap
                ((ExperimentBootstrap)exp).setConsense(consAlg);
            }

            // Loaded from file
            exp.setLoadedFromFile(true);
            
            // Delete the temp path
            tmpPath.delete();

            return exp;
        }
        else
            throw new PhyloIncorrectConfigurationFile();
    }

    // Valore di un attributo
    private static String getTextValue(Element ele, String tagName)
    {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0)
        {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }
        return textVal;
    }
}
