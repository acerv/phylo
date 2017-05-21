package utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Redireziona un buffer, visualizzando il contenuto a terminale
 * @author Cervesato Andrea - sawk @ gmail.com
 */
public class BufferRedirect
{
    InputStream is;
    String type;

    /**
     * Inizializza il buffer in input e il tipo di buffer da visualizzare
     * @param is InputStrem da redirezionare
     * @param type Tipo di buffer da visualizzare in formato String
     */
    public BufferRedirect(InputStream is, String type)
    {
        this.is = is;
        this.type = type;
    }

    /**
     * Esegue il redirect del buffer
     */
    public void start()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;

            while ( (line = br.readLine()) != null)
                Debugger.println(type + "> " + line);
            
            br.close();
            isr.close();
            is.close();
        }
        catch (IOException ex)
        {
                ex.printStackTrace();
        }
    }
}
