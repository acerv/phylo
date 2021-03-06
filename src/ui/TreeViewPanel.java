package ui;

import algorithm.phylip.Phylip;
import algorithm.phylip.PhylipExecutable;
import parser.newick.ParseException;
import matrix.exception.MatrixFormatException;
import diagram.treedrawer.Drawgram;
import parser.newick.Newick;
import tree.PhyloTree;
import diagram.treedrawer.TreeDrawingParameters;
import experiment.ExperimentBootstrap;
import experiment.ExperimentCalcDistance;
import experiment.ExperimentLoadDistance;
import experiment.Experiment;
import files.utility.FileCopy;
import informations.Infos;
import matrix.reader.CharMatrixReader;
import matrix.reader.DistMatrixReader;
import ui.configuration.PhyloConfig;
import java.awt.CardLayout;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import utility.Debugger;

/**
 *
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class TreeViewPanel extends javax.swing.JPanel
{
    CardLayout cl;
    int SelectedRow = 0;
    CharMatrixReader charMatrix = null;
    DistMatrixReader distMatrix = null;
    
    // Drawers
    Drawgram static_drawer;
    Drawgram dynamic_drawer;

    PhyloExperimentPanel expPanel;

    // Outfile di phylip
    File outfile;

    // Visualizzazione corrente statica o dinamica
    boolean STATIC_VIEW = true;

    // Configurazione di Phylo
    PhyloConfig CONFIGURATION;

    // Saver
    JSaveFileChooser saver;

    /** Creates new form TreeViewPanel
     * @param expPanel
     * @param cfg 
     */
    public TreeViewPanel(PhyloExperimentPanel expPanel, PhyloConfig cfg)
    {
        initComponents();
        cl = (CardLayout) ViewCards.getLayout();
        this.expPanel = expPanel;
        this.CONFIGURATION = cfg;
        saver = new JSaveFileChooser(CONFIGURATION.loading_path);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        charMatrixPopupMenu = new javax.swing.JPopupMenu();
        charMatrixSave = new javax.swing.JMenuItem();
        charMatrixEdit = new javax.swing.JMenuItem();
        distMatrixPopupMenu = new javax.swing.JPopupMenu();
        distMatrixSave = new javax.swing.JMenuItem();
        phylipOutfilePopup = new javax.swing.JPopupMenu();
        SaveOutfileItem = new javax.swing.JMenuItem();
        ViewToolbar = new javax.swing.JToolBar();
        ViewLabel = new javax.swing.JLabel();
        TreeButton = new javax.swing.JToggleButton();
        CharMatrixButton = new javax.swing.JToggleButton();
        DistMatrixButton = new javax.swing.JToggleButton();
        PhylipOutputButton = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        SelectionModeButton = new javax.swing.JToggleButton();
        PreviewModeButton = new javax.swing.JToggleButton();
        ViewCards = new javax.swing.JPanel();
        DistMatrixScrollPane = new javax.swing.JScrollPane();
        DistMatrixTextArea = new javax.swing.JTextArea();
        StaticTreeScrollPane = new javax.swing.JScrollPane();
        TreePane = new javax.swing.JPanel();
        CharMatrixScrollPane = new javax.swing.JScrollPane();
        CharMatrixTextArea = new javax.swing.JTextArea();
        DynamicTreeScrollPane = new javax.swing.JScrollPane();
        PhylipOutputScrollPane = new javax.swing.JScrollPane();
        PhylipOutputTextArea = new javax.swing.JTextArea();

        charMatrixSave.setText("Save Matrix");
        charMatrixSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                charMatrixSaveActionPerformed(evt);
            }
        });
        charMatrixPopupMenu.add(charMatrixSave);

        charMatrixEdit.setText("Edit matrix");
        charMatrixEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                charMatrixEditActionPerformed(evt);
            }
        });
        charMatrixPopupMenu.add(charMatrixEdit);

        distMatrixSave.setText("Save matrix");
        distMatrixSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                distMatrixSaveActionPerformed(evt);
            }
        });
        distMatrixPopupMenu.add(distMatrixSave);

        SaveOutfileItem.setText("Save phylip outfile");
        SaveOutfileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveOutfileItemActionPerformed(evt);
            }
        });
        phylipOutfilePopup.add(SaveOutfileItem);

        ViewToolbar.setRollover(true);

        ViewLabel.setText("View :    ");
        ViewToolbar.add(ViewLabel);

        TreeButton.setSelected(true);
        TreeButton.setText("Tree");
        TreeButton.setFocusable(false);
        TreeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        TreeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        TreeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TreeButtonActionPerformed(evt);
            }
        });
        ViewToolbar.add(TreeButton);

        CharMatrixButton.setText("Char Matrix");
        CharMatrixButton.setFocusable(false);
        CharMatrixButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        CharMatrixButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        CharMatrixButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CharMatrixButtonActionPerformed(evt);
            }
        });
        ViewToolbar.add(CharMatrixButton);

        DistMatrixButton.setText("Dist Matrix");
        DistMatrixButton.setFocusable(false);
        DistMatrixButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        DistMatrixButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        DistMatrixButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DistMatrixButtonActionPerformed(evt);
            }
        });
        ViewToolbar.add(DistMatrixButton);

        PhylipOutputButton.setText("Phylip Output");
        PhylipOutputButton.setFocusable(false);
        PhylipOutputButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        PhylipOutputButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        PhylipOutputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PhylipOutputButtonActionPerformed(evt);
            }
        });
        ViewToolbar.add(PhylipOutputButton);
        ViewToolbar.add(jSeparator1);

        SelectionModeButton.setSelected(true);
        SelectionModeButton.setText("Selection Mode");
        SelectionModeButton.setFocusable(false);
        SelectionModeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        SelectionModeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        SelectionModeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SelectionModeButtonActionPerformed(evt);
            }
        });
        ViewToolbar.add(SelectionModeButton);

        PreviewModeButton.setText("Preview Mode");
        PreviewModeButton.setFocusable(false);
        PreviewModeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        PreviewModeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        PreviewModeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PreviewModeButtonActionPerformed(evt);
            }
        });
        ViewToolbar.add(PreviewModeButton);

        ViewCards.setLayout(new java.awt.CardLayout());

        DistMatrixTextArea.setColumns(20);
        DistMatrixTextArea.setEditable(false);
        DistMatrixTextArea.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        DistMatrixTextArea.setRows(5);
        DistMatrixTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                DistMatrixTextAreaMouseClicked(evt);
            }
        });
        DistMatrixScrollPane.setViewportView(DistMatrixTextArea);

        ViewCards.add(DistMatrixScrollPane, "DistMatrixCard");

        StaticTreeScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        StaticTreeScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        org.jdesktop.layout.GroupLayout TreePaneLayout = new org.jdesktop.layout.GroupLayout(TreePane);
        TreePane.setLayout(TreePaneLayout);
        TreePaneLayout.setHorizontalGroup(
            TreePaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 713, Short.MAX_VALUE)
        );
        TreePaneLayout.setVerticalGroup(
            TreePaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 481, Short.MAX_VALUE)
        );

        StaticTreeScrollPane.setViewportView(TreePane);

        ViewCards.add(StaticTreeScrollPane, "StaticTreeCard");

        CharMatrixScrollPane.setFont(new java.awt.Font("Dialog", 1, 12));

        CharMatrixTextArea.setColumns(20);
        CharMatrixTextArea.setEditable(false);
        CharMatrixTextArea.setFont(new java.awt.Font("Monospaced", 1, 14));
        CharMatrixTextArea.setRows(5);
        CharMatrixTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CharMatrixTextAreaMouseClicked(evt);
            }
        });
        CharMatrixScrollPane.setViewportView(CharMatrixTextArea);

        ViewCards.add(CharMatrixScrollPane, "CharMatrixCard");
        ViewCards.add(DynamicTreeScrollPane, "DynamicTreeCard");

        PhylipOutputTextArea.setColumns(20);
        PhylipOutputTextArea.setEditable(false);
        PhylipOutputTextArea.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        PhylipOutputTextArea.setRows(5);
        PhylipOutputTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PhylipOutputTextAreaMouseClicked(evt);
            }
        });
        PhylipOutputScrollPane.setViewportView(PhylipOutputTextArea);

        ViewCards.add(PhylipOutputScrollPane, "PhylipOutputCard");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ViewToolbar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
            .add(ViewCards, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(ViewToolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ViewCards, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void TreeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TreeButtonActionPerformed
    {//GEN-HEADEREND:event_TreeButtonActionPerformed
        this.TreeButton.setSelected(true);
        this.CharMatrixButton.setSelected(false);
        this.DistMatrixButton.setSelected(false);
        this.PhylipOutputButton.setSelected(false);

        if(STATIC_VIEW)
        {
            this.SelectionModeButton.setSelected(true);
            this.cl.show(this.ViewCards, "StaticTreeCard");
        }
        else
        {
            this.PreviewModeButton.setSelected(true);
            this.cl.show(this.ViewCards, "DynamicTreeCard");
        }
    }//GEN-LAST:event_TreeButtonActionPerformed

    private void CharMatrixButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CharMatrixButtonActionPerformed
    {//GEN-HEADEREND:event_CharMatrixButtonActionPerformed
        this.CharMatrixButton.setSelected(true);
        this.TreeButton.setSelected(false);
        this.SelectionModeButton.setSelected(false);
        this.PreviewModeButton.setSelected(false);
        this.PhylipOutputButton.setSelected(false);
        this.DistMatrixButton.setSelected(false);
        this.cl.show(this.ViewCards, "CharMatrixCard");
    }//GEN-LAST:event_CharMatrixButtonActionPerformed

    private void DistMatrixButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_DistMatrixButtonActionPerformed
    {//GEN-HEADEREND:event_DistMatrixButtonActionPerformed
        this.DistMatrixButton.setSelected(true);
        this.TreeButton.setSelected(false);
        this.SelectionModeButton.setSelected(false);
        this.PreviewModeButton.setSelected(false);
        this.CharMatrixButton.setSelected(false);
        this.PhylipOutputButton.setSelected(false);
        this.cl.show(this.ViewCards, "DistMatrixCard");
    }//GEN-LAST:event_DistMatrixButtonActionPerformed

    private void DistMatrixTextAreaMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_DistMatrixTextAreaMouseClicked
    {//GEN-HEADEREND:event_DistMatrixTextAreaMouseClicked
        if(evt.getButton() == MouseEvent.BUTTON3)
            distMatrixPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        else
            distMatrixPopupMenu.setVisible(false);
    }//GEN-LAST:event_DistMatrixTextAreaMouseClicked

    private void CharMatrixTextAreaMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_CharMatrixTextAreaMouseClicked
    {//GEN-HEADEREND:event_CharMatrixTextAreaMouseClicked
        if(evt.getButton() == MouseEvent.BUTTON3)
            charMatrixPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        else
            charMatrixPopupMenu.setVisible(false);
}//GEN-LAST:event_CharMatrixTextAreaMouseClicked

    private void charMatrixSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_charMatrixSaveActionPerformed
    {//GEN-HEADEREND:event_charMatrixSaveActionPerformed
        charMatrixPopupMenu.setVisible(false);
        File fileName = saver.show("Save characters matrix", ".txt");
        if(fileName != null)
        {
            try
            {
                charMatrix.printMatrixOnFile(fileName);
            }
            catch (IOException ex)
            {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            JOptionPane.showMessageDialog(this, "Character matrix is saved", "Message", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_charMatrixSaveActionPerformed

    private void distMatrixSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_distMatrixSaveActionPerformed
    {//GEN-HEADEREND:event_distMatrixSaveActionPerformed
        distMatrixPopupMenu.setVisible(false);
        File fileName = saver.show("Save distance matrix", ".txt");
        if(fileName != null)
        {
            try
            {
                distMatrix.printMatrixOnFile(fileName);
            }
            catch (IOException ex)
            {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            JOptionPane.showMessageDialog(this, "Distance matrix is saved", "Message", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_distMatrixSaveActionPerformed

    private void charMatrixEditActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_charMatrixEditActionPerformed
    {//GEN-HEADEREND:event_charMatrixEditActionPerformed
        // Nome della matrice da caricare
        String name = expPanel.getSelectedExperimentTabName()+"_"+"matrix";

        // matrice da caricare
        LoadedMatrix matrix = new LoadedMatrix(name, charMatrix);

        // editor delle matrici
        PhyloForm phyloForm = Infos.getPhyloForm();
        PhyloCharMatrixPanel editor = phyloForm.getPhyloCharMatrixPanel();

        try
        {
            // Aggiungo la matrice da editare
            editor.editMatrix(matrix);

            // Seleziono il pannello dell'editor
            phyloForm.selectCharMatrixPanel();
        }
        catch (MatrixFormatException ex)
        {
            Logger.getLogger(TreeViewPanel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_charMatrixEditActionPerformed

    private void SelectionModeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SelectionModeButtonActionPerformed
        this.SelectionModeButton.setSelected(true);
        this.PreviewModeButton.setSelected(false);
        this.STATIC_VIEW = true;
        this.TreeButtonActionPerformed(evt);
    }//GEN-LAST:event_SelectionModeButtonActionPerformed

    private void PreviewModeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PreviewModeButtonActionPerformed
        this.PreviewModeButton.setSelected(true);
        this.SelectionModeButton.setSelected(false);
        this.STATIC_VIEW = false;
        this.TreeButtonActionPerformed(evt);
    }//GEN-LAST:event_PreviewModeButtonActionPerformed

    private void PhylipOutputButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_PhylipOutputButtonActionPerformed
    {//GEN-HEADEREND:event_PhylipOutputButtonActionPerformed
        this.PhylipOutputButton.setSelected(true);
        this.TreeButton.setSelected(false);
        this.SelectionModeButton.setSelected(false);
        this.PreviewModeButton.setSelected(false);
        this.CharMatrixButton.setSelected(false);
        this.DistMatrixButton.setSelected(false);
        this.cl.show(this.ViewCards, "PhylipOutputCard");
    }//GEN-LAST:event_PhylipOutputButtonActionPerformed

    private void PhylipOutputTextAreaMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_PhylipOutputTextAreaMouseClicked
    {//GEN-HEADEREND:event_PhylipOutputTextAreaMouseClicked
        if(evt.getButton() == MouseEvent.BUTTON3)
        {
            phylipOutfilePopup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_PhylipOutputTextAreaMouseClicked

    private void SaveOutfileItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_SaveOutfileItemActionPerformed
    {//GEN-HEADEREND:event_SaveOutfileItemActionPerformed
        phylipOutfilePopup.setVisible(false);
        File file = saver.show("Outfile saving...", ".txt");

        if(file != null)
        {
            String name = file.getName();
            try
            {
                new FileCopy().fileIntoDirectory(outfile, file.getParentFile(), name);
            }
            catch (IOException ex)
            {
                Logger.getLogger(TreeViewPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            JOptionPane.showMessageDialog(this, "Phylip outfile is saved", "Message", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_SaveOutfileItemActionPerformed

    private void setCharMatrix(CharMatrixReader matrixRead)
    {
        if ( matrixRead != null )
        {
            charMatrix = matrixRead;
            PrintStream defaultOut = new PrintStream(System.out);
            System.setOut(new PrintStream(new JTextAreaWriter(CharMatrixTextArea)));
            matrixRead.printMatrix();
            System.setOut(defaultOut);
        }
    }

    private void setDistMatrix(DistMatrixReader matrixRead)
    {
        if ( matrixRead != null )
        {
            distMatrix = matrixRead;
            PrintStream defaultOut = new PrintStream(System.out);
            System.setOut(new PrintStream(new JTextAreaWriter(DistMatrixTextArea)));
            matrixRead.printMatrix();
            System.setOut(defaultOut);
        }
    }

    // Setta la view dell'output phylip
    private void setPhylipOutput(PhylipExecutable exec)
            throws IOException
    {
        this.outfile = exec.getOutfile();
        FileReader fr = new FileReader(outfile);
        Scanner scan = new Scanner(fr);
        String storeAllStrings = "";
        while(scan.hasNextLine())
        {
            String temp = scan.nextLine()+"\n";
            storeAllStrings = storeAllStrings+temp;
        }

        this.PhylipOutputTextArea.append(storeAllStrings);

        // CHIUDI IL BUFFER PERDIO !!!
        scan.close();
    }

    /**
     * Visualizza l'albero e le matrici corrispondenti all'esperimento
     * @param exp Esperimento
     * @param cfg Configurazione di Phylo
     * @param useBranchLenghts Tiene conto della lunghezza dei rami per la visualizzazione dell'albero
     * @throws IOException Viene lanciata una eccezione se non esiste il file
     * riferito all'immagine
     * @throws ParseException 
     */
    public void setViews(Experiment exp, final PhyloConfig cfg, boolean useBranchLenghts)
            throws IOException, ParseException
    {
        // Visualizzo l'albero
        File outtree = exp.getOuttree();
        final PhyloTree t = new PhyloTree();
        Newick parser = new Newick(System.in);
	FileInputStream in = new FileInputStream(outtree);
        parser.ReInit(in);
        parser.parseTree(t);

        TreeDrawingParameters p = new TreeDrawingParameters();
        p.useBranchLenghts = useBranchLenghts;

        PhyloConfig cfgClone = null;
        try
        {
            cfgClone = (PhyloConfig) cfg.clone();
        }
        catch (CloneNotSupportedException ex)
        {
            Logger.getLogger(TreeViewPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        static_drawer = new Drawgram(t, cfgClone, expPanel, p, Drawgram.STATIC_VIEW);
        dynamic_drawer = new Drawgram(t, cfgClone, expPanel, p, Drawgram.DYNAMIC_VIEW);

        StaticTreeScrollPane.getViewport().add(static_drawer);
        DynamicTreeScrollPane.getViewport().add(dynamic_drawer);

        // Bisogna salvare l'albero ?
        if(cfgClone.tree_save)
        {
            String tree_name = exp.getName()+"_outtree.png";
            File save_img = new File(exp.getExperimentPath(), tree_name);
            static_drawer.drawToFileAsPng(save_img.getAbsolutePath());
        }

        // Visualizzo le matrici
        if(exp.getExperimentType() <= Infos.LOAD_DISTANCE_EXP)
        {
            if(exp instanceof ExperimentBootstrap)
            {
                ExperimentBootstrap boot = (ExperimentBootstrap)exp;
                this.setCharMatrix(boot.getCharMatrix());
            }
            else if(exp instanceof ExperimentCalcDistance)
            {
                ExperimentCalcDistance dist = (ExperimentCalcDistance)exp;
                this.setCharMatrix(dist.getCharMatrix());
            }
            else
            {
                // Disabilito il bottone di default
                this.CharMatrixButton.setEnabled(false);
            }

            if(exp instanceof ExperimentCalcDistance)
                this.setDistMatrix(((ExperimentCalcDistance)exp).getDistMatrix());
            else if(exp instanceof ExperimentLoadDistance)
                this.setDistMatrix(((ExperimentLoadDistance)exp).getDistMatrix());
            else
                this.DistMatrixButton.setEnabled(false);

            // Setto l'output di phylip se l'algoritmo usato è un algoritmo phylip
            if(exp instanceof ExperimentBootstrap)
            {
                this.setPhylipOutput(((ExperimentBootstrap)exp).getConsense());
            }
            else
            {
                if(exp.getAlgorithm() instanceof Phylip)
                    this.setPhylipOutput((PhylipExecutable) exp.getAlgorithm());
                else
                    PhylipOutputButton.setVisible(false);
            }

            this.SelectionModeButtonActionPerformed(null);
        }
        else
        {
            CharMatrixButton.setEnabled(false);
            DistMatrixButton.setEnabled(false);
            PhylipOutputButton.setVisible(false);
        }

        Debugger.println("> The experiment view has been initialized");
    }

    public JTextArea getDistMatrixTextArea()
    {
        return DistMatrixTextArea;
    }

    private PhyloExperimentPanel getPhyloExperimentPanel()
    {
        return expPanel;
    }

    public Drawgram getDrawgram()
    {
        return this.static_drawer;
    }

    public CharMatrixReader getCharMatrix()
    {
        return this.charMatrix;
    }

    public DistMatrixReader getDistMatrix()
    {
        return this.distMatrix;
    }

    /**
     * @return Riga selezionata nella matrice dei caratteri
     */
    public int getSelectedRow()
    {
        return this.SelectedRow;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton CharMatrixButton;
    private javax.swing.JScrollPane CharMatrixScrollPane;
    private javax.swing.JTextArea CharMatrixTextArea;
    private javax.swing.JToggleButton DistMatrixButton;
    private javax.swing.JScrollPane DistMatrixScrollPane;
    private javax.swing.JTextArea DistMatrixTextArea;
    private javax.swing.JScrollPane DynamicTreeScrollPane;
    private javax.swing.JToggleButton PhylipOutputButton;
    private javax.swing.JScrollPane PhylipOutputScrollPane;
    private javax.swing.JTextArea PhylipOutputTextArea;
    private javax.swing.JToggleButton PreviewModeButton;
    private javax.swing.JMenuItem SaveOutfileItem;
    private javax.swing.JToggleButton SelectionModeButton;
    private javax.swing.JScrollPane StaticTreeScrollPane;
    private javax.swing.JToggleButton TreeButton;
    private javax.swing.JPanel TreePane;
    private javax.swing.JPanel ViewCards;
    private javax.swing.JLabel ViewLabel;
    private javax.swing.JToolBar ViewToolbar;
    private javax.swing.JMenuItem charMatrixEdit;
    private javax.swing.JPopupMenu charMatrixPopupMenu;
    private javax.swing.JMenuItem charMatrixSave;
    private javax.swing.JPopupMenu distMatrixPopupMenu;
    private javax.swing.JMenuItem distMatrixSave;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPopupMenu phylipOutfilePopup;
    // End of variables declaration//GEN-END:variables

}
