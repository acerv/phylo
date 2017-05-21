package experiment.exception;

public class PhyloIncorrectConfigurationFile  extends Exception
{
    public PhyloIncorrectConfigurationFile()
    {
        super("Experiment: ");
    }

    @Override
    public String toString()
    {
        return getMessage() + " the configuration file is incorrect";
    }
}
