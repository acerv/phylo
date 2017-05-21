package files.utility;

import java.io.File;
import java.io.FileNotFoundException;

public class DeletePath
{
    /**
     * Elimina ricorsivamente tutti i file in una directory
     * @param path cartella che si vuole eliminare
     * @throws FileNotFoundException 
     */
    public static void doDelete(File path)
            throws FileNotFoundException
    {
        if ( path.exists() )
        {
            if ( path.isDirectory() )
            {
                File[] files = path.listFiles();
                for ( int i = 0; i < files.length; i++ ) doDelete(files[i]);
                path.delete();
            }
            else path.delete();
        } else
            throw new FileNotFoundException(path.getAbsolutePath()+" doesn't exist");
    }
}
