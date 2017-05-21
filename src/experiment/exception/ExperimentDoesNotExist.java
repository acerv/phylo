package experiment.exception;

public class ExperimentDoesNotExist extends Exception {
    public ExperimentDoesNotExist()
    {
        super("Experiment: ");
    }

    @Override
    public String toString()
    {
        return getMessage() + " does not exist. Use mkNewExperiment() method first";
    }
}
