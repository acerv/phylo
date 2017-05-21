package ui;

import diagram.treedrawer.Drawgram;
import parser.newick.Newick;
import parser.newick.ParseException;
import diagram.treedrawer.TreeDrawingParameters;
import informations.Infos;
import matrix.reader.CharMatrixReader;
import matrix.reader.DistMatrixReader;
import ui.configuration.PhyloConfig;
import ui.configuration.XmlPhyloConfigReader;
import ui.configuration.XmlPhyloConfigWriter;
import com.itextpdf.text.DocumentException;
import experiment.Experiment;
import experiment.ExperimentPdfWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import tree.PhyloTree;
import utility.Debugger;

/**
 * Framework filogenetico.
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class PhyloForm extends javax.swing.JFrame
{
    // Configurazione del programma
    private PhyloConfig CONFIGURATION = new PhyloConfig();
    private String CONFIGURATION_PATH = null;

    // Dialog con i nomi delle matrici caricate dall'editor
    private LoadedMatrixDialog loadedMatrixDialog;

    // Debug o no ?
    public boolean DEBUG = false;

    // Salvo file
    JSaveFileChooser saver;

    /** Creates new form PhyloForm
     * @throws URISyntaxException 
     */
    public PhyloForm() throws URISyntaxException
    {
        initComponents();
        setTitle(Infos.PHYLO_NAME+" "+Infos.PHYLO_VERSION);
        loadedMatrixDialog = new LoadedMatrixDialog(this, true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Configuro la cartella di salvataggio
        File cfg_path = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        this.CONFIGURATION_PATH = cfg_path.getAbsolutePath();
        File cfg = new File(cfg_path, PhyloConfig.CONFIG_NAME);

        // Verifico l'esistenza del file di configurazione
        if(!cfg_path.exists())
        {
            cfg_path.mkdir();
            try
            {
                (new XmlPhyloConfigWriter()).writeConfig(CONFIGURATION, CONFIGURATION_PATH);
            } catch (IOException ex)
            {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } 
        else if (cfg_path.exists() && !cfg.exists())
        {
            try
            {
                (new XmlPhyloConfigWriter()).writeConfig(CONFIGURATION, CONFIGURATION_PATH);
            } catch (IOException ex)
            {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (cfg.exists())
        {
            try
            {
                this.CONFIGURATION = (new XmlPhyloConfigReader()).readConfigFile(cfg);
            } 
            catch (IOException ex)
            {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            catch (ParserConfigurationException ex)
            {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            catch (SAXException ex)
            {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Icona del programma
        ImageIcon icon = new ImageIcon(this.getClass().getResource("phylo.png"));
        setIconImage(icon.getImage());

        // Configurazione di phylip
        this.PhyloExperimentPanel.setPhyloConfig(this.CONFIGURATION);
        this.phyloCharMatrixPanel.setPhyloConfig(this.CONFIGURATION);
        this.saver = new JSaveFileChooser(CONFIGURATION.loading_path);

        // Form
        Infos.setPhyloForm(this);
    }

    public void setDebug(boolean debug)
    {
        this.DEBUG = debug;
    }

    /**
     * Aggiorna la tabella delle matrici caricate
     * @param matrix
     */
    public void updateLoadedMatrixDialog(ArrayList<LoadedMatrix> matrix)
    {
        loadedMatrixDialog.setLoadedMatrices(matrix);
    }

    /**
     * Ritorna il dialog delle matrici caricate in memoria
     * @return dialog delle matrici caricate in memoria
     */
    public LoadedMatrixDialog getLoadedMatrixDialog()
    {
        return this.loadedMatrixDialog;
    }

    /**
     * Visualizza il dialog con le matrici caricate in memoria
     * @return false se ci sono matrici non caricate
     */
    public boolean viewLoadedMatrixDialog()
    {
        if(loadedMatrixDialog.getLoadedMatrix().isEmpty())
        {
            JOptionPane.showMessageDialog( this,
                        "There are not loaded data matrices",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            
            return false;
        }
        else
        {
            this.loadedMatrixDialog.setLocationRelativeTo(this);
            this.loadedMatrixDialog.setVisible(true);
            return true;
        }

    }

    /**
     * Ritorna il pannello degli esperimenti
     * @return
     */
    public PhyloExperimentPanel getPhyloExperimentPanel()
    {
        return this.PhyloExperimentPanel;
    }

    /**
     * Ritorna il pannello delle matrici
     * @return
     */
    public PhyloCharMatrixPanel getPhyloCharMatrixPanel()
    {
        return this.phyloCharMatrixPanel;
    }

    /**
     * Seleziona il pannello degli esperimenti
     */
    public void selectExperimentPanel()
    {
        this.PhyloTabs.setSelectedIndex(0);
    }

    /**
     * Seleziona il pannello delle matrici
     */
    public void selectCharMatrixPanel()
    {
        this.PhyloTabs.setSelectedIndex(1);
    }

    // serve per la barra di caricamento
    private class SavingExperimentAsPdf
            implements Runnable
    {
        PhyloProgressBar bar = null;
        Experiment exp;
        File pdf;

        public SavingExperimentAsPdf(Experiment exp, File pdf)
        {
            this.exp = exp;
            this.pdf = pdf;
            bar = new PhyloProgressBar("Saving experiment as pdf", false);
        }

        public void run()
        {
            bar.startBar();
            try
            {
                ExperimentPdfWriter writer = new ExperimentPdfWriter(exp, pdf);
                File png = new File(Infos.TEMPORARY_PATH, "out.png");
                PhyloExperimentPanel.getCurrentDrawgram().drawToFileAsPng(png.getAbsolutePath());
                writer.exportExperimentAsPdf(png, exp, pdf);
                png.delete();
            } 
            catch (DocumentException ex)
            {
                Logger.getLogger(PhyloForm.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex)
            {
                Logger.getLogger(PhyloForm.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            bar.stopBar();
        }
    }

    private class SwingWorkerExtend extends SwingWorker
    {
        PhyloForm form;
        Experiment exp;
        File pdf;

        public SwingWorkerExtend(PhyloForm form, Experiment exp, File pdf)
        {
            this.form = form;
            this.exp = exp;
            this.pdf = pdf;
        }
        
        @Override
        public Object construct()
        {
            SavingExperimentAsPdf save = new SavingExperimentAsPdf(exp, pdf);
            Thread t = new Thread(save);
            t.start();
            try
            {
                t.join();
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(PhyloForm.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                this.interrupt();
            }
            return "All done";
        }

        @Override
        public void finished()
        {
            super.finished();
            JOptionPane.showMessageDialog(null, "Experiment is saved as pdf", "Message", JOptionPane.INFORMATION_MESSAGE);
        }
    };

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PhyloTabs = new javax.swing.JTabbedPane();
        PhyloExperimentPanel = new ui.PhyloExperimentPanel();
        phyloCharMatrixPanel = new ui.PhyloCharMatrixPanel();
        MenuBar = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        SaveExperimentMenuItem = new javax.swing.JMenuItem();
        LoadExperimentMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        SaveTreeMenu = new javax.swing.JMenu();
        SaveTreePngMenuItem = new javax.swing.JMenuItem();
        SaveTreePdfMenuItem = new javax.swing.JMenuItem();
        SaveCharMatrixMenuItem = new javax.swing.JMenuItem();
        SaveDistMatrixMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        SaveExperimentAsPdfItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        ExitMenu = new javax.swing.JMenuItem();
        ViewMenu = new javax.swing.JMenu();
        ExperimentPanelMenuItem = new javax.swing.JMenuItem();
        CharMatrixPanelMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        LoadedMatricesMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        ViewTreeFromNwkFile = new javax.swing.JMenuItem();
        EditMenu = new javax.swing.JMenu();
        PropertiesMenuItem = new javax.swing.JMenuItem();
        HelpMenu = new javax.swing.JMenu();
        AboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        PhyloTabs.addTab("Experiments", PhyloExperimentPanel);
        PhyloTabs.addTab("CharMatrix Editor", phyloCharMatrixPanel);

        FileMenu.setText("File");

        SaveExperimentMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        SaveExperimentMenuItem.setText("Save Experiment");
        SaveExperimentMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveExperimentMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(SaveExperimentMenuItem);

        LoadExperimentMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        LoadExperimentMenuItem.setText("Load Experiment");
        LoadExperimentMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadExperimentMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(LoadExperimentMenuItem);
        FileMenu.add(jSeparator2);

        SaveTreeMenu.setText("Save tree");

        SaveTreePngMenuItem.setText("as png");
        SaveTreePngMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveTreePngMenuItemActionPerformed(evt);
            }
        });
        SaveTreeMenu.add(SaveTreePngMenuItem);

        SaveTreePdfMenuItem.setText("as pdf");
        SaveTreePdfMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveTreePdfMenuItemActionPerformed(evt);
            }
        });
        SaveTreeMenu.add(SaveTreePdfMenuItem);

        FileMenu.add(SaveTreeMenu);

        SaveCharMatrixMenuItem.setText("Save Character Matrix");
        SaveCharMatrixMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveCharMatrixMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(SaveCharMatrixMenuItem);

        SaveDistMatrixMenuItem.setText("Save Distance Matrix");
        SaveDistMatrixMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveDistMatrixMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(SaveDistMatrixMenuItem);
        FileMenu.add(jSeparator1);

        SaveExperimentAsPdfItem.setText("Save Experiment as pdf");
        SaveExperimentAsPdfItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveExperimentAsPdfItemActionPerformed(evt);
            }
        });
        FileMenu.add(SaveExperimentAsPdfItem);
        FileMenu.add(jSeparator4);

        ExitMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        ExitMenu.setText("Exit");
        ExitMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitMenuActionPerformed(evt);
            }
        });
        FileMenu.add(ExitMenu);

        MenuBar.add(FileMenu);

        ViewMenu.setText("View");

        ExperimentPanelMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        ExperimentPanelMenuItem.setText("Experiment Panel");
        ExperimentPanelMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExperimentPanelMenuItemActionPerformed(evt);
            }
        });
        ViewMenu.add(ExperimentPanelMenuItem);

        CharMatrixPanelMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        CharMatrixPanelMenuItem.setText("Data Matrix Editor");
        CharMatrixPanelMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CharMatrixPanelMenuItemActionPerformed(evt);
            }
        });
        ViewMenu.add(CharMatrixPanelMenuItem);
        ViewMenu.add(jSeparator3);

        LoadedMatricesMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        LoadedMatricesMenuItem.setText("Loaded Data Matrices");
        LoadedMatricesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadedMatricesMenuItemActionPerformed(evt);
            }
        });
        ViewMenu.add(LoadedMatricesMenuItem);
        ViewMenu.add(jSeparator5);

        ViewTreeFromNwkFile.setText("Visualize tree from newick file");
        ViewTreeFromNwkFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewTreeFromNwkFileActionPerformed(evt);
            }
        });
        ViewMenu.add(ViewTreeFromNwkFile);

        MenuBar.add(ViewMenu);

        EditMenu.setText("Edit");

        PropertiesMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        PropertiesMenuItem.setText("Properties");
        PropertiesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PropertiesMenuItemActionPerformed(evt);
            }
        });
        EditMenu.add(PropertiesMenuItem);

        MenuBar.add(EditMenu);

        HelpMenu.setText("Help");

        AboutMenuItem.setText("About");
        AboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AboutMenuItemActionPerformed(evt);
            }
        });
        HelpMenu.add(AboutMenuItem);

        MenuBar.add(HelpMenu);

        setJMenuBar(MenuBar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PhyloTabs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 851, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PhyloTabs, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 574, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleDescription("Phylogenetic Framework");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ExitMenuActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ExitMenuActionPerformed
    {//GEN-HEADEREND:event_ExitMenuActionPerformed
        dispose();
        System.exit(0);
    }//GEN-LAST:event_ExitMenuActionPerformed

    private void SaveExperimentMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_SaveExperimentMenuItemActionPerformed
    {//GEN-HEADEREND:event_SaveExperimentMenuItemActionPerformed
        // Esperimento selezionato
        Experiment exp = PhyloExperimentPanel.getSelectedExperiment();

        if ( exp != null )
        {
            if ( exp.isConfigured() )
            {
                File fileName = saver.show("Save experiment", Experiment.EXPERIMENT_EXTENSION);

                if (fileName != null )
                {
                    try
                    {
                        exp.saveConfig(fileName.getAbsolutePath());
                    } catch (IOException ex)
                    {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(this, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    JOptionPane.showMessageDialog(this, "Experiment is saved", "Message", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            else
            {
                JOptionPane.showMessageDialog(
                        this,
                        "An experiment should be executed first",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
        else
        {
                JOptionPane.showMessageDialog(
                        this,
                        "Select an experiment first",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_SaveExperimentMenuItemActionPerformed

    private void LoadExperimentMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadExperimentMenuItemActionPerformed
        PhyloExperimentPanel.openEsperiment();
    }//GEN-LAST:event_LoadExperimentMenuItemActionPerformed

    private void PropertiesMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_PropertiesMenuItemActionPerformed
    {//GEN-HEADEREND:event_PropertiesMenuItemActionPerformed
        PhyloPropertiesDialog dialog = new PhyloPropertiesDialog(null, true);
        dialog.setPhyloConfig(CONFIGURATION);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if(dialog.getReturnStatus() == PhyloPropertiesDialog.RET_OK)
        {
            if(dialog.configIsModified())
            {
                Debugger.println("> Phylip configuration has been changed. It must be rewritten...");
                this.CONFIGURATION = dialog.getPhyloConfig();
                try
                {
                    // Aggiorno la configurazione
                    File cfg = (new File(CONFIGURATION_PATH, PhyloConfig.CONFIG_NAME));
                    if(cfg.exists()) cfg.delete();
                    (new XmlPhyloConfigWriter()).writeConfig(CONFIGURATION, CONFIGURATION_PATH);
                }
                catch (IOException ex)
                {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else
            dialog.setPhyloConfig(CONFIGURATION);
    }//GEN-LAST:event_PropertiesMenuItemActionPerformed

    private void SaveTreePngMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_SaveTreePngMenuItemActionPerformed
    {//GEN-HEADEREND:event_SaveTreePngMenuItemActionPerformed
        Drawgram drawer = this.PhyloExperimentPanel.getCurrentDrawgram();
        if(drawer != null)
        {
            File fileName = saver.show("Save tree as png", ".png");
            if(fileName != null)
            {
                try
                {
                    drawer.drawToFileAsPng(fileName.getAbsolutePath());
                } catch (IOException ex)
                {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                JOptionPane.showMessageDialog(this, "Tree is saved as png", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        else
            JOptionPane.showMessageDialog(this, "An experiment should be executed first",  "Warning", JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_SaveTreePngMenuItemActionPerformed

    private void SaveCharMatrixMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_SaveCharMatrixMenuItemActionPerformed
    {//GEN-HEADEREND:event_SaveCharMatrixMenuItemActionPerformed
        CharMatrixReader matrix = this.PhyloExperimentPanel.getCurrentCharMatrix();
        if(matrix != null)
        {
            File fileName = saver.show("Save characters matrix", ".txt");
            if(fileName != null)
            {
                try
                {
                    matrix.printMatrixOnFile(fileName);
                }
                catch (IOException ex)
                {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                JOptionPane.showMessageDialog(this, "Character matrix is saved", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        else
            JOptionPane.showMessageDialog(this, "An experiment should be executed first", "Warning", JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_SaveCharMatrixMenuItemActionPerformed

    private void SaveDistMatrixMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_SaveDistMatrixMenuItemActionPerformed
    {//GEN-HEADEREND:event_SaveDistMatrixMenuItemActionPerformed
        DistMatrixReader matrix = this.PhyloExperimentPanel.getCurrentDistMatrix();
        if(matrix != null)
        {
            File fileName = saver.show("Save distance matrix", ".txt");
            if(fileName != null)
            {
                try
                {
                    matrix.printMatrixOnFile(fileName);
                }
                catch (IOException ex)
                {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                JOptionPane.showMessageDialog(this, "Distance matrix is saved", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        else
            JOptionPane.showMessageDialog(this, "An experiment should be executed first",  "Warning", JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_SaveDistMatrixMenuItemActionPerformed

    private void formWindowLostFocus(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowLostFocus
    {//GEN-HEADEREND:event_formWindowLostFocus
        this.repaint();
    }//GEN-LAST:event_formWindowLostFocus

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowGainedFocus
    {//GEN-HEADEREND:event_formWindowGainedFocus
        this.repaint();
    }//GEN-LAST:event_formWindowGainedFocus

    private void LoadedMatricesMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_LoadedMatricesMenuItemActionPerformed
    {//GEN-HEADEREND:event_LoadedMatricesMenuItemActionPerformed
        viewLoadedMatrixDialog();
    }//GEN-LAST:event_LoadedMatricesMenuItemActionPerformed

    private void CharMatrixPanelMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CharMatrixPanelMenuItemActionPerformed
    {//GEN-HEADEREND:event_CharMatrixPanelMenuItemActionPerformed
        this.selectCharMatrixPanel();
    }//GEN-LAST:event_CharMatrixPanelMenuItemActionPerformed

    private void ExperimentPanelMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ExperimentPanelMenuItemActionPerformed
    {//GEN-HEADEREND:event_ExperimentPanelMenuItemActionPerformed
        this.selectExperimentPanel();
    }//GEN-LAST:event_ExperimentPanelMenuItemActionPerformed

    private void SaveTreePdfMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_SaveTreePdfMenuItemActionPerformed
    {//GEN-HEADEREND:event_SaveTreePdfMenuItemActionPerformed
        Drawgram drawer = this.PhyloExperimentPanel.getCurrentDrawgram();
        if(drawer != null)
        {
            File fileName = saver.show("Save tree as pdf", ".pdf");
            if(fileName != null)
            {
                try
                {
                    drawer.drawToFileAsPdf(fileName.getAbsolutePath());
                }
                catch (Exception ex)
                {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                JOptionPane.showMessageDialog(this, "Tree is saved as pdf", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        else
            JOptionPane.showMessageDialog(this, "An experiment should be executed first",  "Warning", JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_SaveTreePdfMenuItemActionPerformed

    private void SaveExperimentAsPdfItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_SaveExperimentAsPdfItemActionPerformed
    {//GEN-HEADEREND:event_SaveExperimentAsPdfItemActionPerformed
        final Experiment exp = PhyloExperimentPanel.getSelectedExperiment();

        if(exp != null)
        {
            if(exp.isConfigured())
            {
                final File pdf = saver.show("Save experiment as pdf", ".pdf");
                if(pdf != null)
                {
                    // Visualizzo la barra di attesa
                    SwingWorkerExtend worker = new SwingWorkerExtend(this, exp, pdf);
                    worker.start();
                }
            }
            else
            {
                JOptionPane.showMessageDialog(this, "An experiment should be executed first",  "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "There isn't a selected experiment",  "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_SaveExperimentAsPdfItemActionPerformed

    private void ViewTreeFromNwkFileActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ViewTreeFromNwkFileActionPerformed
    {//GEN-HEADEREND:event_ViewTreeFromNwkFileActionPerformed
        JFileChooser fc = new JFileChooser(new File(CONFIGURATION.loading_path));
        int showOpenDialog = fc.showOpenDialog(this);
        if ( showOpenDialog == JFileChooser.APPROVE_OPTION )
        {
            File nwk = fc.getSelectedFile();
            final PhyloTree t = new PhyloTree();

            // Parso il file
            Newick parser = new Newick(System.in);
            try
            {
                FileInputStream in = new FileInputStream(nwk);
                parser.ReInit(in);
                parser.parseTree(t);
                TreeDrawingParameters p = new TreeDrawingParameters();
                PhyloConfig cfg = null;
                try {
                    cfg = (PhyloConfig) CONFIGURATION.clone();
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(PhyloForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                Drawgram drawer = new Drawgram(t, cfg, PhyloExperimentPanel, p, Drawgram.EXAMPLE_VIEW);
                TreeDialog dialog = new TreeDialog(null, true);
                dialog.setDrawer(drawer);
                dialog.setVisible(true);
            }
            catch (FileNotFoundException ex)
            {
                Logger.getLogger(PhyloForm.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            catch (ParseException ex)
            {
                Logger.getLogger(PhyloForm.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Parsing error: is it a newick file?", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_ViewTreeFromNwkFileActionPerformed

    private void AboutMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_AboutMenuItemActionPerformed
    {//GEN-HEADEREND:event_AboutMenuItemActionPerformed
        InfoDialog dialog = new InfoDialog(this, true);
        dialog.setAlwaysOnTop(true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }//GEN-LAST:event_AboutMenuItemActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run() 
            {
                try
                {
                    new PhyloForm().setVisible(true);
                } catch (URISyntaxException ex)
                {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AboutMenuItem;
    private javax.swing.JMenuItem CharMatrixPanelMenuItem;
    private javax.swing.JMenu EditMenu;
    private javax.swing.JMenuItem ExitMenu;
    private javax.swing.JMenuItem ExperimentPanelMenuItem;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenu HelpMenu;
    private javax.swing.JMenuItem LoadExperimentMenuItem;
    private javax.swing.JMenuItem LoadedMatricesMenuItem;
    private javax.swing.JMenuBar MenuBar;
    private ui.PhyloExperimentPanel PhyloExperimentPanel;
    private javax.swing.JTabbedPane PhyloTabs;
    private javax.swing.JMenuItem PropertiesMenuItem;
    private javax.swing.JMenuItem SaveCharMatrixMenuItem;
    private javax.swing.JMenuItem SaveDistMatrixMenuItem;
    private javax.swing.JMenuItem SaveExperimentAsPdfItem;
    private javax.swing.JMenuItem SaveExperimentMenuItem;
    private javax.swing.JMenu SaveTreeMenu;
    private javax.swing.JMenuItem SaveTreePdfMenuItem;
    private javax.swing.JMenuItem SaveTreePngMenuItem;
    private javax.swing.JMenu ViewMenu;
    private javax.swing.JMenuItem ViewTreeFromNwkFile;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private ui.PhyloCharMatrixPanel phyloCharMatrixPanel;
    // End of variables declaration//GEN-END:variables
}
