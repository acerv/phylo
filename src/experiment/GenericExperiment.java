package experiment;

import experiment.config.xml.XmlExperimentConfigWriter;
import informations.Infos;
import java.io.File;
import java.io.IOException;
import ui.configuration.PhyloConfig;

/**
 * Esperimento generico di Phylo. Come input ha un file qualunque
 * e come output ha un file contenente un albero filogenetico in formato Newick.
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class GenericExperiment extends Experiment
{
    /**
     * Inizializza l'esperimento
     * @param cfg configurazione di phylo
     * @param name Nome dell'esperimento
     * @throws IOException
     */
    public GenericExperiment(PhyloConfig cfg, String name)
            throws IOException
    {
        super(cfg);
        initializeName(name);
    }

    @Override
    public int getExperimentType()
    {
        return Infos.GENERIC_EXPERIMENT;
    }

    @Override
    public String getExperimentName()
    {
        return Infos.SUPPORTED_EXPERIMENTS[Infos.GENERIC_EXPERIMENT];
    }

    @Override
    public void exec() throws Throwable
    {
        if(getInput() == null)
            throw new Exception("Input file not initialized");

        if(getAlgorithm() == null)
            throw new Exception("Algorithm not initialized");

        // Seleziono l'input dell'algoritmo
        if(getAlgorithm().getInput() == null)
            getAlgorithm().setInput(getInput());

        // Esecuzione
        getAlgorithm().exec();

        // Salvo l'input
        String inputName = getInput().getName();
        getAlgorithm().saveInput(getExperimentPath(), inputName);
        setInput(new File(getExperimentPath(), inputName));

        // Salvo l'output
        String outtreeName = getAlgorithm().getOutput().getName();
        File outfile = getAlgorithm().getOutput();
        getAlgorithm().saveOutput(getExperimentPath(), outtreeName);
        setOuttree(new File(getExperimentPath(), outtreeName));
        
        // Elimino il vecchio file di output che è stato copiato
        outfile.delete();

        // Salvo la configurazione e setto l'algoritmo come configurato
        XmlExperimentConfigWriter.writeConfig(this);
        setConfigured(true);
    }
}
