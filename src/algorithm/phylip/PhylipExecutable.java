package algorithm.phylip;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Questa classe astratta è stata creata per un unico motivo: unificare
 * in un'unica classe tutte le operazioni comuni alle classe che si "interfacciano"
 * agli algoritmi di Phylip. Notare bene che il metodo "exec()" astratto viene
 * implementato su queste ultime, e non in Phylip.java o qui.
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
abstract public class PhylipExecutable extends Phylip
{
    // Opzioni
    private ArrayList<String> options = new ArrayList<String>();

    public PhylipExecutable(
            String name,
            int value,
            String infile,
            String outfile,
            ArrayList<String> options ) throws IOException
    {
        for( int i = 0; i < options.size(); i++ )
            this.options.add(options.get(i));

        // Setto la classe Phylip
        setName(name);
        setValue(value);
        setInputName(infile);
        setOutputName(outfile);
    }

    /**
     * @return Le opzioni di configurazione del programma
     */
    public ArrayList<String> getOptions()
    {
        return this.options;
    }

    @Override
    abstract public void exec() throws Throwable;
}
