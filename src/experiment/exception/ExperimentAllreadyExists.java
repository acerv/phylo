package experiment.exception;

/**
 * Eccezione che viene lanciata se l'esperimento esiste già.
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class ExperimentAllreadyExists extends Exception
{
    private String name = null;

    public ExperimentAllreadyExists(String name)
    {
        super("Can't create the experiment: ");
        this.name = name;
    }

    @Override
    public String toString()
    {
        return getMessage() + "\""+name+"\" allready exists";
    }
}
