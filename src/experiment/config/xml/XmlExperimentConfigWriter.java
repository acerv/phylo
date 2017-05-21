package experiment.config.xml;

import algorithm.phylip.configuration.ConsenseConfiguration;
import algorithm.phylip.configuration.FitchConfiguration;
import algorithm.phylip.configuration.KitschConfiguration;
import algorithm.phylip.configuration.NeighborConfiguration;
import algorithm.phylip.Fitch;
import algorithm.phylip.Kitsch;
import algorithm.phylip.Neighbor;
import algorithm.phylip.Phylip;
import algorithm.phylip.xml.ConsenseXml;
import algorithm.phylip.xml.FitchXml;
import algorithm.phylip.xml.KitschXml;
import algorithm.phylip.xml.NeighborXml;
import experiment.ExperimentBootstrap;
import experiment.ExperimentCalcDistance;
import experiment.ExperimentLoadDistance;
import experiment.Experiment;
import experiment.GenericExperiment;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import utility.Debugger;

/**
 * Scrive le informazioni di un esperimento in un file xml
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class XmlExperimentConfigWriter extends XmlExperimentConfig
{
    /**
     * Crea un file xml contenente le informazioni dell'esperimento
     * @param exp Esperimento da cui estrarre le informazioni
     * @throws IOException Viene lanciata una eccezione se non si può creare il file
     */
    public static void writeConfig(Experiment exp)
            throws IOException
    {
        // Creazione del file
        File outFile = new File(exp.getExperimentPath(),Experiment.CONFIGURATION_FILE_NAME);
        OutputStream fout = new FileOutputStream(outFile);
        OutputStream bout = new BufferedOutputStream(fout);
        OutputStreamWriter out = new OutputStreamWriter(bout, "UTF8");
        
        // Inizio scrittura del file
        out.write(versionTag);
        out.write(encodingTag);
        out.write(experimentTag[OPEN]);
        out.write(nameTag[OPEN]+exp.getName()+nameTag[CLOSE]);
        out.write(dateTag[OPEN]+exp.getDate()+dateTag[CLOSE]);
        out.write(expTypeTag[OPEN]+exp.getExperimentType()+expTypeTag[CLOSE]);

        // Salvo la posizione della matrice
        String matrixFile = null;
        if(exp instanceof ExperimentCalcDistance)
        {
            ExperimentCalcDistance dist = (ExperimentCalcDistance) exp;

            // Matrice dei caratteri
            matrixFile = dist.getCharMatrix().getSaveFile().getName();
            out.write(expInputTag[OPEN]+matrixFile+expInputTag[CLOSE]);

            // L'esperimento è già istanziato ed eseguito quando viene generato
            // il file di configurazione
            out.write(distanceTag[OPEN]+dist.getDistanceMethod()+distanceTag[CLOSE]);
        }
        else if(exp instanceof ExperimentLoadDistance)
        {
            ExperimentLoadDistance dist = (ExperimentLoadDistance) exp;

            // Matrice delle distanze
            matrixFile = dist.getDistMatrix().getSaveFile().getName();
            out.write(expInputTag[OPEN]+matrixFile+expInputTag[CLOSE]);
        }
        else if(exp instanceof ExperimentBootstrap)
        {
            ExperimentBootstrap boot = (ExperimentBootstrap) exp;

            // Metodo di bootstrap
            out.write(bootsrapMethodTag[OPEN]+boot.getBootstrapMethod()+bootsrapMethodTag[CLOSE]);

            // Matrice dei caratteri
            matrixFile = boot.getCharMatrix().getSaveFile().getName();
            out.write(expInputTag[OPEN]+matrixFile+expInputTag[CLOSE]);
            out.write(bootsrapNumTag[OPEN]+boot.getBootstrapNum()+bootsrapNumTag[CLOSE]);

            // L'esperimento è già istanziato ed eseguito quando viene generato
            // il file di configurazione
            out.write(distanceTag[OPEN]+boot.getDistanceMethod()+distanceTag[CLOSE]);

            // Consense
            out.write(treeTag[OPEN]+"1"+treeTag[CLOSE]);
        }
        else if(exp instanceof GenericExperiment)
        {
            // Serve solo l'input
            out.write(expInputTag[OPEN]+exp.getInput().getName()+expInputTag[CLOSE]);
        }
        else
        {
            /* *********************************
             * INTEGRARE QUI NUOVI ESPERIMENTI *
             ***********************************/
        }

        // Tipo di albero. Per ora è supportato SOLO CONSENSE
        if (!(exp instanceof ExperimentBootstrap))
        {
            // 0 altrimenti (metodo standard)
            out.write(treeTag[OPEN]+"0"+treeTag[CLOSE]);
        }

        // Configurazione dell'algoritmo
        out.write(algorithmTag[OPEN]);
        out.write(algNameTag[OPEN]+exp.getAlgorithm().getValue()+algNameTag[CLOSE]);

        // Se si tratta di un programma phylip, bisogna salvare la configurazione
        // del programma
        if(exp.getAlgorithm() instanceof Phylip)
        {
            out.write(algConfigTag[OPEN]);
            
            // Fitch
            if(exp.getAlgorithm() instanceof Fitch)
            {
                FitchConfiguration cfg = ((Fitch) exp.getAlgorithm()).getConfiguration();
                
                // Metodo
                out.write(FitchXml.methodTag[OPEN]);
                out.write(String.valueOf(cfg.method));
                out.write(FitchXml.methodTag[CLOSE]);

                // Power
                out.write(FitchXml.powerTag[OPEN]);
                out.write(String.valueOf(cfg.power));
                out.write(FitchXml.powerTag[CLOSE]);

                // Outgroup
                out.write(FitchXml.outgroupTag[OPEN]);
                out.write(String.valueOf(cfg.outgroupSpece));
                out.write(FitchXml.outgroupTag[CLOSE]);

                // Randomize
                out.write(FitchXml.randomizeTag[OPEN]);
                if(cfg.randomizeInputOrderTimes)
                    out.write(String.valueOf(1));
                else
                    out.write(String.valueOf(0));
                out.write(FitchXml.randomizeTag[CLOSE]);

                // Data sets
                out.write(FitchXml.dataSetsTag[OPEN]);
                out.write(String.valueOf(cfg.multipleDataSets));
                out.write(FitchXml.dataSetsTag[CLOSE]);
            }
            // Kitsch
            else if(exp.getAlgorithm() instanceof Kitsch)
            {
                KitschConfiguration cfg = ((Kitsch) exp.getAlgorithm()).getConfiguration();

                // Metodo
                out.write(KitschXml.methodTag[OPEN]);
                out.write(String.valueOf(cfg.method));
                out.write(KitschXml.methodTag[CLOSE]);

                // Power
                out.write(KitschXml.powerTag[OPEN]);
                out.write(String.valueOf(cfg.power));
                out.write(KitschXml.powerTag[CLOSE]);

                // Randomize
                out.write(KitschXml.randomizeTag[OPEN]);
                if(cfg.randomizeInputOrderTimes)
                    out.write(String.valueOf(1));
                else
                    out.write(String.valueOf(0));
                out.write(KitschXml.randomizeTag[CLOSE]);

                // Data sets
                out.write(KitschXml.dataSetsTag[OPEN]);
                out.write(String.valueOf(cfg.multipleDataSets));
                out.write(KitschXml.dataSetsTag[CLOSE]);
            }
            // Neighbor
            else if(exp.getAlgorithm() instanceof Neighbor)
            {
                NeighborConfiguration cfg = ((Neighbor)exp.getAlgorithm()).getConfiguration();

                // Tipo di albero (neighbor o upgma)
                out.write(NeighborXml.treeTag[OPEN]);
                out.write(String.valueOf(cfg.tree));
                out.write(NeighborXml.treeTag[CLOSE]);

                // Outgroup
                out.write(NeighborXml.outgroupTag[OPEN]);
                out.write(String.valueOf(cfg.outgroupSpece));
                out.write(NeighborXml.outgroupTag[CLOSE]);

                // Randomize
                out.write(NeighborXml.randomizeTag[OPEN]);
                if(cfg.randomizeInput) out.write("1");
                else out.write("0");
                out.write(NeighborXml.randomizeTag[CLOSE]);

                // Data sets
                out.write(NeighborXml.dataSetsTag[OPEN]);
                out.write(String.valueOf(cfg.multipleDataSets));
                out.write(NeighborXml.dataSetsTag[CLOSE]);
            }
            else
            {
                /* **************************************
                 * INTEGRARE QUI NUOVI ALGORITMI PHYLIP *
                 ****************************************/
            }

            out.write(algConfigTag[CLOSE]);
        }
        else
        {
            // Inserire qui eventuali configurazioni per l'algoritmo filogenetico
            // che si vuole eseguire alla lettura del file.
        }

        out.write(algorithmTag[CLOSE]);

        // Programma per la generazione dell'albero
        if (exp instanceof ExperimentBootstrap)
        {
            ExperimentBootstrap boot = (ExperimentBootstrap) exp;
            ConsenseConfiguration cfg = boot.getConsense().getConfiguration();

            // Inizio
            out.write(consenseTag[OPEN]);
            
            // Tipo di consenso
            out.write(ConsenseXml.typeTag[OPEN]);
            out.write(String.valueOf(cfg.consensusType));
            out.write(ConsenseXml.typeTag[CLOSE]);

            // Outgroup
            out.write(ConsenseXml.outgroupTag[OPEN]);
            out.write(String.valueOf(cfg.outgroupSpece));
            out.write(ConsenseXml.outgroupTag[CLOSE]);

            // Rooted
            out.write(ConsenseXml.rootedTag[OPEN]);
            if(cfg.rooted) out.write("1");
            else out.write("0");
            out.write(ConsenseXml.rootedTag[CLOSE]);

            // Chiusura
            out.write(consenseTag[CLOSE]);
        }

        // Chiusura
        out.write(experimentTag[CLOSE]);

        // Chiusura file
        out.flush();
        out.close();

        Debugger.println("> Configuration file for the experiment '"+exp.getName()+"' is complete");
    }
}
