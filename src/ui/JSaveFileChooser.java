/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class JSaveFileChooser
{
    // Cartella in cui aprire il JFileChooser
    String loadingPath;
    
    public JSaveFileChooser(String path)
    {
        this.loadingPath = path;
    }

    /**
     * Visualizza un file chooser e permette di salvare un file con estensione specificata
     * da tag
     * @param title Titolo finestra
     * @param ext Estensione del file da salvare
     * @return File in cui salvare
     */
    public File show(String title, String ext)
    {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(title);
        fc.setCurrentDirectory(new File(loadingPath));
        // fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showSaveDialog(null);
        File fileToSave = null;
        
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            fileToSave = new File(fc.getSelectedFile()+ext);

            if( fileToSave.exists() )
            {
                returnVal = JOptionPane.showConfirmDialog(null,"Replace existing file?");

                if (returnVal == JOptionPane.NO_OPTION)
                {
                    String name = JOptionPane.showInputDialog("Type the name of the file:");
                    fileToSave = new File(fc.getCurrentDirectory(), name+ext);
                }
            }
        }

        return fileToSave;
    }
}
