package files.utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import utility.Debugger;

/**
 *
 * @author Cervesato Andrea - sawk @ gmail.com
 */
public class FileCopy
{    
    /**
     * Copia un file in una cartella
     * @param file File da copiare 
     * @param dir Cartella in cui copiare
     * @throws IOException Vengono rilevate eccezioni se i file non esistono
     */
    public File fileIntoDirectory(File file, File dir)
            throws IOException
    {
        // File da cui leggere
        InputStream in = new FileInputStream(file);

        // File in cui copiare
        dir = new File (dir, file.getName());
        OutputStream out = new FileOutputStream(dir);

        // Trasferimento dati da in ad out
        byte[] buf = new byte[1024];
        int len = 0;

        while ((len = in.read(buf)) > 0)
        {
            out.write(buf, 0, len);
            out.flush();
        }

        // Chiudo i buffers
        in.close();
        out.flush();
        out.close();

        Debugger.println("> Copied "+file.getAbsolutePath());
        return dir;
    }

    /**
     * Copia un file in una cartella con nome
     * @param file File da copiare
     * @param dir Cartella in cui copiare
     * @param name Nome con il quale copiare
     * @throws IOException Vengono rilevate eccezioni se i file non esistono
     */
    public File fileIntoDirectory(File file, File dir, String name)
            throws IOException
    {
        // File da cui leggere
        InputStream in = new FileInputStream(file);

        // File in cui copiare
        dir = new File(dir, name);
        OutputStream out = new FileOutputStream(dir);

        // Trasferimento dati da in ad out
        byte[] buf = new byte[1024];
        int len = 0;

        while ((len = in.read(buf)) > 0)
        {
            out.write(buf, 0, len);
            out.flush();
        }

        // Chiudo i buffers
        out.flush();
        out.close();
        in.close();

        Debugger.println("> Copied "+file.getAbsolutePath()+" with name "+name);

        return dir;
    }

    /**
     * Copia una cartella sorgente in una cartella di destinazione
     * @param srcPath Cartella sorgente
     * @param dstPath Cartella di destinzazione
     * @throws IOException Vengono rilevate eccezioni se i file non esistono
     */
    public void copyDirectory(File srcPath, File dstPath)
            throws IOException
    {
        // Serve per la ricorsione
        if (srcPath.isDirectory())
        {
            // Files all'interno della cartella da copiare
            String files[] = srcPath.list();

            // Copio i diversi file all'interno della cartella di destinazione
            for(int i = 0; i < files.length; i++)
            {
                // Lancio ricorsivamente la copia sui file
                copyDirectory(
                   new File(srcPath, files[i]),
                   new File(dstPath, files[i])
                   );
            }
        }
        else
        {
            // File da cui leggere
            InputStream in = new FileInputStream(srcPath);

            // File da cui copiare
            OutputStream out = new FileOutputStream(dstPath);

            // Trasferimento dati da in ad out
            byte[] buf = new byte[1024];
            int len = 0;

            while ((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
                out.flush();
            }

            // Chiudo i buffers
            in.close();
            out.flush();
            out.close();
        }

         Debugger.println("> Copied "+dstPath.getName());
    }

    /**
     * Copia una entry di un file zip in una cartella
     * @param zip File zip
     * @param entry Entry del file zip
     * @param dest Cartella di destinazione
     * @throws IOException Viene lanciata una eccezione se ci sono problemi con i files
     */
    public void copyFileFromZipFile(ZipFile zip, ZipEntry entry, File dest)
            throws IOException
    {
        InputStream in = new BufferedInputStream(zip.getInputStream(entry));

        // File di destinazione
        File efile = new File(dest, entry.getName());

        // Apro il file in cui scrivere
        OutputStream out = new BufferedOutputStream(new FileOutputStream(efile));
        byte[] buffer = new byte[2048];
        for (;;)
        {
            int nBytes = in.read(buffer);
            if (nBytes <= 0)
            {
                break;
            }
            out.write(buffer, 0, nBytes);
            out.flush();
        }
        
        // Chiudo i buffers
        in.close();
        out.flush();
        out.close();
    }
}
