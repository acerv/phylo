package ui;

import algorithm.phylip.ConsensusSets;
import experiment.Experiment;
import java.awt.event.MouseEvent;
import diagram.treedrawer.Drawgram;
import matrix.reader.CharMatrixReader;
import matrix.reader.DistMatrixReader;
import experiment.CloneExperiment;
import experiment.ExperimentBootstrap;
import experiment.ExperimentCalcDistance;
import experiment.ExperimentLoadDistance;
import experiment.GenericExperiment;
import experiment.config.xml.XmlExperimentConfigReader;
import informations.Infos;
import java.awt.event.KeyEvent;
import parser.newick.ParseException;
import ui.configuration.PhyloConfig;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import tree.PhyloTreeNode;
import utility.Debugger;
import utility.QuickSort;

/**
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class PhyloExperimentPanel extends javax.swing.JPanel
{
    // lista esperimenti
    private ArrayList<Experiment> ExperimentList = new ArrayList<Experiment>();

    // Questo SwingWorker è essenziale che rimanga un attributo della classe
    // perché permette di accedere ai componenti del panel
    private SwingWorker execProgram;

    // Configurazione da usare
    private PhyloConfig CONFIGURATION = null;

    // Tenere in considerazione la lunghezza dei rami nella visualizzazione dell'albero
    private boolean useBranchLenght = false;

    /** Creates new form PhyloExperimentPanel */
    public PhyloExperimentPanel()
    {
        initComponents();
        ExperimentListTable.setRowSelectionAllowed(true);
    }

    public void setPhyloConfig(PhyloConfig cfg)
    {
        this.CONFIGURATION = cfg;
    }

    /**
     * Visualizza l'esperimento da creare con già presente la matrice caricata
     * @param matrix Matrice da caricare nell'esperimento
     */
    public void newExperimentWithLoadedCharMatrix(LoadedMatrix matrix)
    {
        PhyloForm phyloForm = Infos.getPhyloForm();

        NewExperimentDialog expUI = new NewExperimentDialog(phyloForm, true);
        expUI.setPhyloConfig(this.CONFIGURATION);
        expUI.loadCharMatrix(matrix);
        expUI.setVisible(true);

        if(expUI.getReturnStatus() == NewExperimentDialog.RET_OK)
        {
            // Prelevo l'esperimento
            Experiment exp = expUI.getExperiment();

            // Per il drawer
            this.useBranchLenght = expUI.useBranchLenght();

            // Verifico se è presente un metodo di bootstrap
            int dup = expUI.getNumberOfReplication();
            replicateExperiment(exp, dup);
        }
    }

    /**
     * Ritorna il nome dell'esperimento corrente
     * @return
     */
    public String getSelectedExperimentTabName()
    {
        return ExperimentsTabPanel.getTitleAt(ExperimentsTabPanel.getSelectedIndex());
    }

    // Genera più esperimenti per il metodo di bootstrap se viene richiesto
    private void replicateExperiment(Experiment exp, int dup)
    {
        if(dup == 0)
        {
            this.addNewExperiment(exp);
        }
        else
        {
            Debugger.println("> Will be generated "+dup+" identical experiment(s) of "+exp.getName());

            // Aggiungo l'esperimento principale
            this.addNewExperiment(exp);

            // Aggiungo i restanti esperimenti
            if(exp.getAlgorithm() != null)
            {
                for(int i = 1; i <= dup; i++)
                {
                    try
                    {
                        Experiment clone = CloneExperiment.clone(exp);
                        if(clone != null) this.addNewExperiment(clone);
                    }
                    catch (Exception ex)
                    {
                        Logger.getLogger(PhyloExperimentPanel.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(
                                    null,
                                    this.getClass().getName()+": \n"+ex.toString(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    public JTextArea getDistMatrixTextArea()
    {
        int index = ExperimentsTabPanel.getSelectedIndex();
        TreeViewPanel panel = (TreeViewPanel) ExperimentsTabPanel.getComponentAt(index);
        return panel.getDistMatrixTextArea();
    }

    private void startStandardExperimentDialog()
    {
        NewExperimentDialog expUI = new NewExperimentDialog(Infos.getPhyloForm(), true);
        expUI.setLocationRelativeTo(Infos.getPhyloForm());
        expUI.setPhyloConfig(this.CONFIGURATION);
        expUI.setVisible(true);

        if(expUI.getReturnStatus() == NewExperimentDialog.RET_OK)
        {
            Experiment exp = expUI.getExperiment();
            this.useBranchLenght = expUI.useBranchLenght();

            // Parte da zero
            int dup = expUI.getNumberOfReplication();

            // Eventuale duplicazione
            replicateExperiment(exp, dup);
        }
    }

    private void startGenericExperimentDialog()
    {
        NewGenericExperimentDialog expUI = new NewGenericExperimentDialog(Infos.getPhyloForm(), true);
        expUI.setLocationRelativeTo(Infos.getPhyloForm());
        expUI.setPhyloConfig(CONFIGURATION);
        expUI.setVisible(true);

        if(expUI.getReturnStatus() == NewExperimentDialog.RET_OK)
        {
            Experiment exp = expUI.getExperiment();

            // Parte da zero
            int dup = expUI.getNumberOfReplication();

            // Eventuale duplicazione
            replicateExperiment(exp, dup);
        }
    }

    private String getSelectedExperimentName(int selRow) {
        return (String) ExperimentListTable.getValueAt(selRow, 0);
    }

    private Experiment getSelectedExperiment(int selRow) {
        String name = getSelectedExperimentName(selRow);
        return getExperimentWithName(name);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ExperimentPopupMenu = new javax.swing.JPopupMenu();
        EditMenuItem = new javax.swing.JMenuItem();
        MenuSeparator = new javax.swing.JPopupMenu.Separator();
        DuplicateMenuItem = new javax.swing.JMenuItem();
        CancelMenuItem = new javax.swing.JMenuItem();
        PhyloToolBar = new javax.swing.JToolBar();
        ExperimentsLabel = new javax.swing.JLabel();
        NewExperimentButton = new javax.swing.JButton();
        ExecutingExperimentButton = new javax.swing.JButton();
        DeleteExperimentButton = new javax.swing.JButton();
        ExecuteAllButton = new javax.swing.JButton();
        OpenExperimentButton = new javax.swing.JButton();
        SplitPanel = new javax.swing.JSplitPane();
        ExperimentsTabPanel = new javax.swing.JTabbedPane();
        PropertiesSplitPane = new javax.swing.JSplitPane();
        ExperimentPropertiesTabPanel = new javax.swing.JTabbedPane();
        ExperimentListScrollPane = new javax.swing.JScrollPane();
        ExperimentListTable = new javax.swing.JTable();
        ExperimentPropertiesScrollPane = new javax.swing.JScrollPane();
        ExperimentPropertiesTable = new javax.swing.JTable();
        LanguagesPropertiesScrollPane = new javax.swing.JScrollPane();
        InformationsTextArea = new javax.swing.JTextArea();

        EditMenuItem.setText("Edit");
        EditMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditMenuItemActionPerformed(evt);
            }
        });
        ExperimentPopupMenu.add(EditMenuItem);
        ExperimentPopupMenu.add(MenuSeparator);

        DuplicateMenuItem.setText("Duplicate");
        DuplicateMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DuplicateMenuItemActionPerformed(evt);
            }
        });
        ExperimentPopupMenu.add(DuplicateMenuItem);

        CancelMenuItem.setText("Delete");
        CancelMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelMenuItemActionPerformed(evt);
            }
        });
        ExperimentPopupMenu.add(CancelMenuItem);

        PhyloToolBar.setBorder(null);
        PhyloToolBar.setRollover(true);

        ExperimentsLabel.setText("Experiments :   ");
        PhyloToolBar.add(ExperimentsLabel);

        NewExperimentButton.setText("New");
        NewExperimentButton.setFocusable(false);
        NewExperimentButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        NewExperimentButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        NewExperimentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewExperimentButtonActionPerformed(evt);
            }
        });
        PhyloToolBar.add(NewExperimentButton);

        ExecutingExperimentButton.setText("Execute");
        ExecutingExperimentButton.setFocusable(false);
        ExecutingExperimentButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ExecutingExperimentButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ExecutingExperimentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExecutingExperimentButtonActionPerformed(evt);
            }
        });
        PhyloToolBar.add(ExecutingExperimentButton);

        DeleteExperimentButton.setText("Delete");
        DeleteExperimentButton.setFocusable(false);
        DeleteExperimentButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        DeleteExperimentButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        DeleteExperimentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteExperimentButtonActionPerformed(evt);
            }
        });
        PhyloToolBar.add(DeleteExperimentButton);

        ExecuteAllButton.setText("Execute All");
        ExecuteAllButton.setFocusable(false);
        ExecuteAllButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ExecuteAllButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ExecuteAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExecuteAllButtonActionPerformed(evt);
            }
        });
        PhyloToolBar.add(ExecuteAllButton);

        OpenExperimentButton.setText("Load");
        OpenExperimentButton.setFocusable(false);
        OpenExperimentButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        OpenExperimentButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        OpenExperimentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenExperimentButtonActionPerformed(evt);
            }
        });
        PhyloToolBar.add(OpenExperimentButton);

        SplitPanel.setBorder(null);

        ExperimentsTabPanel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ExperimentsTabPanelStateChanged(evt);
            }
        });
        SplitPanel.setRightComponent(ExperimentsTabPanel);

        PropertiesSplitPane.setBorder(null);
        PropertiesSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        ExperimentPropertiesTabPanel.setMinimumSize(new java.awt.Dimension(270, 250));

        ExperimentListScrollPane.setMinimumSize(new java.awt.Dimension(270, 250));
        ExperimentListScrollPane.setPreferredSize(new java.awt.Dimension(270, 250));

        ExperimentListTable.setFont(new java.awt.Font("Dialog", 1, 12));
        ExperimentListTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ExperimentListTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ExperimentListTableMouseClicked(evt);
            }
        });
        ExperimentListTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ExperimentListTableKeyPressed(evt);
            }
        });
        ExperimentListScrollPane.setViewportView(ExperimentListTable);

        ExperimentPropertiesTabPanel.addTab("Experiments", ExperimentListScrollPane);

        ExperimentPropertiesScrollPane.setMinimumSize(new java.awt.Dimension(270, 250));
        ExperimentPropertiesScrollPane.setPreferredSize(new java.awt.Dimension(270, 250));

        ExperimentPropertiesTable.setFont(new java.awt.Font("Dialog", 1, 12));
        ExperimentPropertiesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Property", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ExperimentPropertiesScrollPane.setViewportView(ExperimentPropertiesTable);

        ExperimentPropertiesTabPanel.addTab("Properties", ExperimentPropertiesScrollPane);

        PropertiesSplitPane.setLeftComponent(ExperimentPropertiesTabPanel);

        LanguagesPropertiesScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Informations"));
        LanguagesPropertiesScrollPane.setMinimumSize(new java.awt.Dimension(270, 250));
        LanguagesPropertiesScrollPane.setPreferredSize(new java.awt.Dimension(270, 250));

        InformationsTextArea.setColumns(20);
        InformationsTextArea.setEditable(false);
        InformationsTextArea.setFont(new java.awt.Font("Dialog", 1, 14));
        InformationsTextArea.setRows(5);
        InformationsTextArea.setMinimumSize(new java.awt.Dimension(270, 250));
        LanguagesPropertiesScrollPane.setViewportView(InformationsTextArea);

        PropertiesSplitPane.setRightComponent(LanguagesPropertiesScrollPane);

        SplitPanel.setLeftComponent(PropertiesSplitPane);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SplitPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE)
            .add(PhyloToolBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(PhyloToolBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(SplitPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void NewExperimentButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_NewExperimentButtonActionPerformed
    {//GEN-HEADEREND:event_NewExperimentButtonActionPerformed
        PhyloForm phyloForm = Infos.getPhyloForm();

        if(Infos.getGenericAlgorithms().length != 0)
        {
            ChooseExperimentDialog choose = new ChooseExperimentDialog(phyloForm, true);
            choose.setLocationRelativeTo(Infos.getPhyloForm());
            choose.setVisible(true);

            if(choose.getReturnStatus() == ChooseExperimentDialog.RET_OK)
            {
                if(choose.getExperimentType() == ChooseExperimentDialog.GENERIC_EXP)
                {
                    startGenericExperimentDialog();
                }
                else
                {
                    startStandardExperimentDialog();
                }
            }
        }
        else
        {
            startStandardExperimentDialog();
        }
}//GEN-LAST:event_NewExperimentButtonActionPerformed

    private void ExecutingExperimentButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ExecutingExperimentButtonActionPerformed
    {//GEN-HEADEREND:event_ExecutingExperimentButtonActionPerformed
        int[] execRow = ExperimentListTable.getSelectedRows();
        String[] expNames = new String[execRow.length];

        if(execRow.length != 0) {
            for(int i = 0; i < execRow.length; i++)
                expNames[i] = getSelectedExperimentName(execRow[i]);
            
            execExperiment(expNames);
        }
}//GEN-LAST:event_ExecutingExperimentButtonActionPerformed

    private void DeleteExperimentButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_DeleteExperimentButtonActionPerformed
    {//GEN-HEADEREND:event_DeleteExperimentButtonActionPerformed
        deleteSelectedExperiments();
}//GEN-LAST:event_DeleteExperimentButtonActionPerformed

    private void ExecuteAllButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ExecuteAllButtonActionPerformed
    {//GEN-HEADEREND:event_ExecuteAllButtonActionPerformed
        int n = ExperimentList.size();
        if(n != 0)
        {
            Debugger.println("\n> Execution of all experiment(s)\n> Wait...\n");

            // Rimuovo tutte le tab
            ExperimentsTabPanel.removeAll();

            execAllExperiments();
            Debugger.println("> Were executed "+n+" experiment(s)\n");
        }
}//GEN-LAST:event_ExecuteAllButtonActionPerformed

    private void ExperimentListTableMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_ExperimentListTableMouseClicked
    {//GEN-HEADEREND:event_ExperimentListTableMouseClicked
         int selRow = ExperimentListTable.rowAtPoint(evt.getPoint());
         int numOfSelection = ExperimentListTable.getSelectedRows().length;

         if(evt.getButton() == MouseEvent.BUTTON1) {
             if(numOfSelection == 1)
             {
                 if ( selRow != -1 )
                 {
                     Experiment exp = getSelectedExperiment(selRow);
                     addExperimentProperties(exp);

                     // Al doppio click seleziono il tab corrispondente
                     if(evt.getClickCount() == 2)
                         selectTabWithName(exp.getName());
                 }
             }
         }
         else if(evt.getButton() == MouseEvent.BUTTON3) {
             ExperimentListTable.getSelectionModel().setSelectionInterval(selRow, selRow);
             ExperimentPopupMenu.show(ExperimentListTable, evt.getX(), evt.getY());
         }
    }//GEN-LAST:event_ExperimentListTableMouseClicked

    private void OpenExperimentButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_OpenExperimentButtonActionPerformed
    {//GEN-HEADEREND:event_OpenExperimentButtonActionPerformed
        openEsperiment();
    }//GEN-LAST:event_OpenExperimentButtonActionPerformed

    private void ExperimentsTabPanelStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_ExperimentsTabPanelStateChanged
    {//GEN-HEADEREND:event_ExperimentsTabPanelStateChanged
        int index = ExperimentsTabPanel.getSelectedIndex();
        if(index != -1) {
            String name = ExperimentsTabPanel.getTitleAt(index);
            int rows = ExperimentListTable.getRowCount();

            for(int i = 0; i < rows; i++) {
                Experiment exp = getSelectedExperiment(i);
                if(exp.getName().equals(name)) {
                    ExperimentListTable.setRowSelectionInterval(i, i);
                    addExperimentProperties(exp);
                    break;
                }
            }
        }
    }//GEN-LAST:event_ExperimentsTabPanelStateChanged

    private void CancelMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelMenuItemActionPerformed
        deleteSelectedExperiments();
    }//GEN-LAST:event_CancelMenuItemActionPerformed

    private void EditMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditMenuItemActionPerformed
        try {
            /* Clone the experiment */
            int selRow = ExperimentListTable.getSelectedRow();
            Experiment exp = (Experiment) getSelectedExperiment(selRow);
            exp = CloneExperiment.clone(exp);

            /* Launch wizard */
            NewExperimentDialog expUI = new NewExperimentDialog(Infos.getPhyloForm(), true);
            expUI.setLocationRelativeTo(Infos.getPhyloForm());
            expUI.setPhyloConfig(CONFIGURATION);
            expUI.loadExperimentProperties(exp);
            expUI.setVisible(true);
            if (expUI.getReturnStatus() == NewExperimentDialog.RET_OK) {
                // Prelevo l'esperimento
                Experiment experiment = expUI.getExperiment();
                // Per il drawer
                useBranchLenght = expUI.useBranchLenght();
                // Verifico se è presente un metodo di bootstrap
                int dup = expUI.getNumberOfReplication();
                replicateExperiment(experiment, dup);
            }
        } catch (Exception ex) {
            Logger.getLogger(PhyloExperimentPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_EditMenuItemActionPerformed

    private void ExperimentListTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ExperimentListTableKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_DELETE)
            deleteSelectedExperiments();
    }//GEN-LAST:event_ExperimentListTableKeyPressed

    private void DuplicateMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DuplicateMenuItemActionPerformed
        try {
            int selRow = ExperimentListTable.getSelectedRow();
            Experiment exp = (Experiment) getSelectedExperiment(selRow);
            exp = CloneExperiment.clone(exp);
            addNewExperiment(exp);
        } catch (Exception ex) {
            Logger.getLogger(PhyloExperimentPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_DuplicateMenuItemActionPerformed

    private void deleteSelectedExperiments() {
        int[] delRow = ExperimentListTable.getSelectedRows();

        for(int i = delRow.length - 1; i >= 0; i--) {
            String name = getSelectedExperimentName(delRow[i]);
            deleteExperiment(name);
        }

        if(ExperimentListTable.getRowCount() != 0) {
            ExperimentListTable.getSelectionModel().setSelectionInterval(0, 0);
            Experiment exp = getSelectedExperiment(0);
            addExperimentProperties(exp);
            selectTabWithName(exp.getName());
        }
    }

    // Elimina un esperimento con indice specificato
    private void deleteExperiment(String name)
    {
        Experiment exp = getExperimentWithName(name);
        if ( exp != null )
        {
            int retValue = JOptionPane.showConfirmDialog(this, "Are you sure to delete the '"+name+"' experiment?");

            if(retValue == JOptionPane.OK_OPTION)
            {
                int returnValue = JOptionPane.showConfirmDialog(this, "Do you want to delete the experiment path?");
                Debugger.println("> Remuve experiment: "+name);

                // Rimuovo il tab dell'esperimento se è presente
                deleteTabWithName(name);

                // Rimuovere tutta la cartella di lavoro o meno ?
                if ( returnValue == JOptionPane.OK_OPTION )
                {
                    try
                    {
                        exp.cleanAll();
                    } catch (IOException ex)
                    {
                        Logger.getLogger(PhyloExperimentPanel.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(
                                    null,
                                    this.getClass().getName()+": \n"+ex.toString(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                    }
                }
                else
                {
                    exp.clean();
                }

                // Pulizia
                flushPropertyTable();
                flushInformationTextArea();

                deleteExperimentFromList(name);
                Debugger.println("> There are "+this.ExperimentList.size()+" experiment(s)");
            }
        }
    }

    private Experiment deleteExperimentFromList(String name) {
        Experiment exp = null;
        DefaultTableModel def = ((DefaultTableModel)ExperimentListTable.getModel());
        for(int i = 0; i < def.getRowCount(); i++) {
            if(getSelectedExperimentName(i).equals(name)) {
                def.removeRow(i);
                exp = ExperimentList.remove(i);
                break;
            }
        }
        return exp;
    }

    /**
     * Apre un esperimento da un file e lo aggiunge alla lista degli esperimenti
     * da eseguire
     */
    public void openEsperiment()
    {
        JFileChooser fc = new JFileChooser();

        // Filter
        class PhyloFilter extends FileFilter
        {
            @Override
            public boolean accept(File f)
            {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".phylo");
            }

            @Override
            public String getDescription() {
                return "Phylo experiment files";
            }
        }

        // Filtro tutto quello che non è cartella o progetto di phylo
        PhyloFilter filter = new PhyloFilter();
        fc.setFileFilter(filter);
        fc.setAcceptAllFileFilterUsed(false);

        // Cartella di caricamento
        fc.setCurrentDirectory(new File(CONFIGURATION.loading_path));

        // Apro il selettore dei files
        int showOpenDialog = fc.showOpenDialog(this);
        if ( showOpenDialog == JFileChooser.APPROVE_OPTION )
        {
            File savedExp = fc.getSelectedFile();

            try
            {
                // Esperimento da salvare
                Experiment exp = XmlExperimentConfigReader.readConfig(CONFIGURATION, savedExp);

                int returnValue = JOptionPane.showConfirmDialog(
                        this,
                        "It is possible to modify the experiment before execution.\n"+
                        "Do you want to launch the wizard ?",
                        "Adding experiment...", 
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if(returnValue == JOptionPane.OK_OPTION)
                {
                    if(exp.getExperimentType() != Infos.GENERIC_EXPERIMENT)
                    {
                        NewExperimentDialog expUI = new NewExperimentDialog(Infos.getPhyloForm(), true);
                        expUI.setLocationRelativeTo(Infos.getPhyloForm());
                        expUI.setPhyloConfig(this.CONFIGURATION);
                        expUI.loadExperimentProperties(exp);
                        expUI.setVisible(true);

                        if(expUI.getReturnStatus() == NewExperimentDialog.RET_OK)
                        {
                            // Prelevo l'esperimento
                            Experiment experiment = expUI.getExperiment();

                            // Per il drawer
                            useBranchLenght = expUI.useBranchLenght();

                            // Verifico se è presente un metodo di bootstrap
                            int dup = expUI.getNumberOfReplication();
                            replicateExperiment(experiment, dup);
                        }
                    }
                    else
                    {
                        NewGenericExperimentDialog expUI = new NewGenericExperimentDialog(Infos.getPhyloForm(), true);
                        expUI.setLocationRelativeTo(Infos.getPhyloForm());
                        expUI.setPhyloConfig(CONFIGURATION);
                        expUI.loadExperimentProperties(exp);
                        expUI.setVisible(true);

                        if(expUI.getReturnStatus() == NewGenericExperimentDialog.RET_OK)
                        {
                            // Prelevo l'esperimento
                            Experiment experiment = expUI.getExperiment();
                            useBranchLenght = false;

                            // Verifico se è presente un metodo di bootstrap
                            int dup = expUI.getNumberOfReplication();
                            replicateExperiment(experiment, dup);
                        }
                    }
                }
                else
                {
                    // Salvo l'esperimento in memoria
                    addNewExperiment(exp);
                }
            }
            catch ( Exception ex )
            {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(
                            this,
                            this.getClass().getName()+": \n"+ex.toString(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);

                // Chiudo la selezione dei file
                fc.cancelSelection();
            }
        }
    }

    // Esegue gli esperimenti selezionati nella tabella
    private void execExperiment(String[] execRow)
    {
        // Lista degli esperimenti da eseguire
        final ArrayList<Experiment> List = new ArrayList<Experiment>();

        for(int i = 0; i < execRow.length; i++)
        {
            String name = execRow[i];
            deleteTabWithName(name);
            List.add(getExperimentWithName(name));
        }

        // Permette di non bloccare la ProgressBar
        execProgram = new SwingWorker()
        {
            String name;

            @Override
            public Object construct()
            {
                for ( int i = 0; i < List.size(); i++ )
                {
                    // Disabilito tutti i bottoni per evitare ripetuti click
                    enableButtons(false);

                    // Visualizzo informazioni sull'esperimento da eseguire
                    Experiment exp = List.get(i);
                    name = exp.getName();
                    String method = exp.getExperimentName();
                    String phylip = exp.getAlgorithmName();

                    Debugger.println("\n> Experiment name: "+name+
                            "\n> Experiment type: "+method+
                            "\n> Algorithm: "+phylip+
                            "\n\n> Wait..........");

                    // Eseguo l'esperimento
                    PhylipExecuting exec = new PhylipExecuting(exp);
                    Thread t = new Thread(exec);
                    t.start();
                    try
                    {
                        // Attendo il suo termine
                        t.join();
                    }
                    catch (InterruptedException ex)
                    {
                        Logger.getLogger(PhyloExperimentPanel.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(
                                    null,
                                    this.getClass().getName()+": \n"+ex.toString(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                    }

                    // Setto la view
                    try {
                        setTreePanel(exp);
                    } catch (IOException ex)
                    {
                        JOptionPane.showMessageDialog(
                                        null,
                                        this.getClass().getName()+": \n"+ex.toString(),
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                        Logger.getLogger(PhyloExperimentPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return "All done";
            }

            @Override
            public void finished()
            {
                // Disabilito tutti i bottoni per evitare ripetuti click
                enableButtons(true);

                // Seleziono l'ultimo esperimento eseguito
                selectTabWithName(name);
            }
        };
        execProgram.start();
    }

    // Esegue tutti gli esperimenti
    private void execAllExperiments()
    {
        // Lista degli esperimenti da eseguire
        final ArrayList<Experiment> List = this.ExperimentList;

        // Permette di non bloccare la ProgressBar
        execProgram = new SwingWorker()
        {
            String name;
            
            @Override
            public Object construct()
            {
                for ( int i = 0; i < List.size(); i++ )
                {
                    // Disabilito tutti i bottoni per evitare ripetuti click
                    enableButtons(false);

                    // Visualizzo informazioni sull'esperimento da eseguire
                    Experiment exp = List.get(i);
                    name = exp.getName();
                    String method = exp.getExperimentName();
                    String phylip = exp.getAlgorithmName();

                    Debugger.println("\n> Experiment name: "+name+
                            "\n> Experiment type: "+method+
                            "\n> Algorithm: "+phylip+
                            "\n\n> Wait..........");


                    // Eseguo l'esperimento
                    PhylipExecuting exec = new PhylipExecuting(exp);
                    Thread t = new Thread(exec);
                    t.start();
                    try
                    {
                        // Attendo il suo termine
                        t.join();
                    }
                    catch (InterruptedException ex)
                    {
                        Logger.getLogger(PhyloExperimentPanel.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(
                                    null,
                                    this.getClass().getName()+": \n"+ex.toString(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                    }

                    // Setto la view
                    try {
                        setTreePanel(exp);
                    } catch (IOException ex)
                    {
                        JOptionPane.showMessageDialog(
                                        null,
                                        this.getClass().getName()+": \n"+ex.toString(),
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                        Logger.getLogger(PhyloExperimentPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return "All done";
            }

            @Override
            public void finished()
            {
                // Disabilito tutti i bottoni per evitare ripetuti click
                enableButtons(true);

                // Seleziono l'ultimo esperimento eseguito
                selectLastRow();
                selectTabWithName(name);
            }
        };
        execProgram.start();
    }

    private void enableButtons(boolean enable)
    {
        ExecuteAllButton.setEnabled(enable);
        ExecutingExperimentButton.setEnabled(enable);
        DeleteExperimentButton.setEnabled(enable);
        NewExperimentButton.setEnabled(enable);
        OpenExperimentButton.setEnabled(enable);
    }

    private void selectTabWithName(String name)
    {
        int index = ExperimentsTabPanel.indexOfTab(name);
        if(index != -1) ExperimentsTabPanel.setSelectedIndex(index);
    }

    private Experiment getExperimentWithName(String name) {
        Experiment exp = null;
        for(int i = 0; i < ExperimentList.size(); i++) {
            if(ExperimentList.get(i).getName().equals(name)) {
                exp = ExperimentList.get(i);
                break;
            }
        }

        return exp;
    }

    private void selectLastRow()
    {
        int index = ExperimentListTable.getRowCount()-1;
        ExperimentListTable.setRowSelectionInterval(index, index);
        this.addExperimentProperties(getSelectedExperiment(index));
    }

    // Permette di eseguire il progetto e visualizzare allo stesso tempo una barra di avanzamento
    private class PhylipExecuting
            implements Runnable
    {
        PhyloProgressBar bar = null;
        boolean view = false;
        Experiment exp;
        boolean returnValue = false;

        public PhylipExecuting(Experiment exp)
        {
            this.exp = exp;
            bar = new PhyloProgressBar("Executing "+exp.getName(), true);
        }

        public void run()
        {
            try
            {
                bar.startBar(exp);
                enableButtons(true);
            }
            catch (Throwable ex)
            {
                Logger.getLogger(PhyloExperimentPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(
                            null,
                            this.getClass().getName()+": "+ex.toString(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
            }
            
            returnValue = true;
        }

        public boolean getReturnValue()
        {
            return returnValue;
        }
    }

    // Aggiunge un esperimento alla lista degli esperimenti da eseguire
    private void addNewExperiment(Experiment exp)
    {
        // Attributi del nuovo esperimento
        String name = exp.getName();
        String date = exp.getDate();

        // Riga da inserire nella tabella
        String[] row = {name, date};

        // Aggiungo il nuovo esperimento alla lista.
        ExperimentList.add(exp);

        // Visualizzo l'esperimento nella tabella
        ((DefaultTableModel)this.ExperimentListTable.getModel()).addRow(row);

        // seleziono l'esperimento aggiunto
        selectLastRow();

        // Aggiorno la tabella delle proprietà
        Debugger.println("> Added new experiment to the list");
    }

    // Aggiunge i nuovi tab relativi all'esperimento
    private void setTreePanel(Experiment exp)
            throws IOException
    {
        /* null if the experiment execution has been killed */
        if(exp.getOuttree() != null) {
            TreeViewPanel panel = new TreeViewPanel(this, CONFIGURATION);
            try {
                panel.setViews(exp, this.CONFIGURATION, this.useBranchLenght);

                // Aggiungo un tab contenente le viste
                ExperimentsTabPanel.addTab(exp.getName(), panel);

                // Bugfix
                panel.repaint();  
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(
                                null,
                                "There's an error to parse tree file.\n"
                              + "This could happen when matrix file is small.\n\n"
                              + "Solution: try another phylogenetic algorithm.",
                                "Warning",
                                JOptionPane.ERROR_MESSAGE);
                
                Logger.getLogger(PhyloExperimentPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // Aggiunge le proprietà di un esperimento nella property table
    private void addExperimentProperties(Experiment exp)
    {
        // Elimino il contenuto precedentemente inserito
        flushPropertyTable();

        // Formato "Proprietà" <-> "Valore"
        String[] name = { "Name", exp.getName() };
        String[] date = { "Date", exp.getDate() };
        String[] method = { "Method", exp.getExperimentName() };
        String[] phylip = { "Phylip Program", exp.getAlgorithmName() };

        // Matrice caricata in memoria o da file ?
        File input = exp.getInput();
        String matrixFileString = null;
        if(input != null)
            matrixFileString = input.getAbsolutePath();
        else
            matrixFileString = "Loaded by editor";

        String[] matrix = { "Input file", matrixFileString };

        // Aggiungo le proprietà nella tabella
        ((DefaultTableModel)ExperimentPropertiesTable.getModel()).addRow(name);
        ((DefaultTableModel)ExperimentPropertiesTable.getModel()).addRow(date);
        ((DefaultTableModel)ExperimentPropertiesTable.getModel()).addRow(method);
        ((DefaultTableModel)ExperimentPropertiesTable.getModel()).addRow(phylip);
        ((DefaultTableModel)ExperimentPropertiesTable.getModel()).addRow(matrix);
    }

    // Elimina il contenuto della property table
    private void flushPropertyTable()
    {
        int total = ((DefaultTableModel)ExperimentPropertiesTable.getModel()).getRowCount();
        int delrow = 0;

        // Rimuovo sempre il primo elemento
        for (int i = 0; i < total; i++ )
            ((DefaultTableModel)ExperimentPropertiesTable.getModel()).removeRow(delrow);
    }

    // Elimina una tab con un dato nome
    private void deleteTabWithName(String name)
    {
        int index = ExperimentsTabPanel.indexOfTab(name);
        if ( index != -1 )
            ExperimentsTabPanel.remove(index);
    }

    /**
     * @return Esperimento selezionato nel pannello. Ritorna null se non sono presenti
     * esperimenti da eseguire
     */
    public Experiment getSelectedExperiment()
    {
        int selRow = ExperimentListTable.getSelectedRow();
        if ( !ExperimentList.isEmpty() && selRow != -1)
            return ExperimentList.get(selRow);
        else
            return null;
    }

    public Drawgram getCurrentDrawgram()
    {
        if(ExperimentsTabPanel.getSelectedComponent() == null)
            return null;
        else
            return ((TreeViewPanel)ExperimentsTabPanel.getSelectedComponent()).getDrawgram();
    }

    public CharMatrixReader getCurrentCharMatrix()
    {
        if(ExperimentsTabPanel.getSelectedComponent() == null)
            return null;
        else
            return ((TreeViewPanel)ExperimentsTabPanel.getSelectedComponent()).getCharMatrix();
    }

    public DistMatrixReader getCurrentDistMatrix()
    {
        if(ExperimentsTabPanel.getSelectedComponent() == null)
            return null;
        else
            return ((TreeViewPanel)ExperimentsTabPanel.getSelectedComponent()).getDistMatrix();
    }

    // Rimuove il contenuto della Laguage properties
    private void flushInformationTextArea()
    {
        InformationsTextArea.setText("");
    }

    // Estrae un arraylist di stringhe in array double
    private double[] fromStringToDouble(ArrayList<String> list)
    {
        double[] new_row = new double[list.size()];

        for(int i = 0; i < list.size(); i++)
        {
            double value = Double.valueOf(list.get(i));
            new_row[i] = value;
        }
        return new_row;
    }
    
    private static String roundDecimal(double number)
    {
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(5, BigDecimal.ROUND_HALF_UP);
        return bd.toString();
    }

    /**
     * Visualizza le proprietà della lingua selezionata nel grafico
     */
    public void setInformationsOfLanguage(PhyloTreeNode node)
    {
        String languageName = node.getName();
        String languageHeight = roundDecimal(node.getHeight());
        String distFromParent = roundDecimal(node.getScore());
        
        // Flush
        this.flushInformationTextArea();

        // Inizio scrittura
        Experiment exp = ExperimentList.get(ExperimentListTable.getSelectedRow());

        if(exp instanceof GenericExperiment)
        {
            // Name of language
            PrintStream defaultOut = new PrintStream(System.out);
            System.setOut(new PrintStream(new JTextAreaWriter(InformationsTextArea)));
            System.out.println(
                    "\nLanguage "+languageName+":\n");

            System.out.println("\nHeight:  "+languageHeight+"\n");
            System.setOut(defaultOut);
        }
        else if(exp instanceof ExperimentBootstrap)
        {
            int bootstraps = 0;
            ExperimentBootstrap boot = (ExperimentBootstrap) exp;
            if(exp instanceof ExperimentBootstrap)
                bootstraps = boot.getBootstrapNum();
            
            ConsensusSets sets = boot.getConsense().getSets();
            ArrayList<Integer> indexes = sets.getSetsIndexesOfLanguage(languageName);

            PrintStream defaultOut = new PrintStream(System.out);
            System.setOut(new PrintStream(new JTextAreaWriter(InformationsTextArea)));

            if(indexes.isEmpty())
            {
                System.out.println("\nLanguage "+languageName+":\n"+
                        "\nHeight:  "+languageHeight+"\n"+
                        "\nDistance from parent: "+distFromParent+"\n"+
                        "\nIt doesn't appear in any set.");
            }
            else
            {
                System.out.println("\nLanguage "+languageName+":\n"+
                        "\nHeight:  "+languageHeight+"\n\n"+
                        "\nDistance from parent: "+distFromParent+"\n"+
                        "\nSets included in the consensus tree (times out of "+bootstraps+"):");

                for(int i = 0; i < indexes.size(); i++)
                {
                    int index = indexes.get(i);
                    ArrayList<String> lang = sets.getSet(index);
                    System.out.print("("+(i+1)+") ");

                    for(int j = 0; j < lang.size()-1; j++)
                    {
                        System.out.print(lang.get(j)+", ");
                    }

                    System.out.println(lang.get(lang.size()-1));

                    if(bootstraps != 0)
                    {
                        System.out.println("  |---> "+
                                sets.getNumberOfTimesForSet(index));
                    }

                    System.out.println();
                }

                if(bootstraps == 0)
                {
                    System.out.println("All sets are included in the consensus tree");
                }
            }

            System.setOut(defaultOut);
        }
        else
        {
            DistMatrixReader matrix = null;

            if(exp instanceof ExperimentCalcDistance)
                matrix = ((ExperimentCalcDistance)exp).getDistMatrix();
            else if (exp instanceof ExperimentLoadDistance)
                matrix = ((ExperimentLoadDistance)exp).getDistMatrix();

            // Non dovrebbe accadere il contrario
            if(matrix != null)
            {
                ArrayList<String> lang = matrix.getLanguages();
                int i = lang.indexOf(languageName);
                ArrayList<String> row =  matrix.getMatrix().get(i);

                // Prelevo due array senza (quindi) modificare gli elementi
                // del lettore della matrice
                double[] new_row = this.fromStringToDouble(row);
                Object[] new_lang = lang.toArray();

                // Ordino la riga
                QuickSort sorter = new QuickSort();
                sorter.sort(new_lang, new_row);

                // Non dovrebbe accadere il contrario
                if(new_lang.length == new_row.length)
                {
                    // Scrittura delle lingue
                    PrintStream defaultOut = new PrintStream(System.out);
                    System.setOut(new PrintStream(new JTextAreaWriter(InformationsTextArea)));
                    System.out.println(
                            "\nLanguage "+languageName+":\n"+
                            "\nHeight:  "+languageHeight+"\n"+
                            "\nDistance from parent: "+distFromParent+"\n"+
                            "\nDistance from other languages:\n"
                            );

                    for(int j = 0; j < new_lang.length; j++)
                    {
                        if(new_row[j] == 0) continue;
                        System.out.println(new_lang[j]+":\t"+new_row[j]);
                    }

                    System.setOut(defaultOut);
                }
            }
        }

        // Ripristino la barra verso l'alto
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run() {
                LanguagesPropertiesScrollPane.getVerticalScrollBar().setValue(0);
                LanguagesPropertiesScrollPane.getHorizontalScrollBar().setValue(0);
            }
        });
    }

    public void setInformationsOfEdge(PhyloTreeNode node)
    {
        // Flush
        flushInformationTextArea();
        
        // Scrittura delle lingue
        PrintStream defaultOut = new PrintStream(System.out);
        System.setOut(new PrintStream(new JTextAreaWriter(InformationsTextArea)));

        String score = roundDecimal(node.getScore());
        System.out.println("\nScore of the edge:\n\n"+score);
        
        System.setOut(defaultOut);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem CancelMenuItem;
    private javax.swing.JButton DeleteExperimentButton;
    private javax.swing.JMenuItem DuplicateMenuItem;
    private javax.swing.JMenuItem EditMenuItem;
    private javax.swing.JButton ExecuteAllButton;
    private javax.swing.JButton ExecutingExperimentButton;
    private javax.swing.JScrollPane ExperimentListScrollPane;
    public javax.swing.JTable ExperimentListTable;
    private javax.swing.JPopupMenu ExperimentPopupMenu;
    private javax.swing.JScrollPane ExperimentPropertiesScrollPane;
    private javax.swing.JTabbedPane ExperimentPropertiesTabPanel;
    public javax.swing.JTable ExperimentPropertiesTable;
    private javax.swing.JLabel ExperimentsLabel;
    private javax.swing.JTabbedPane ExperimentsTabPanel;
    private javax.swing.JTextArea InformationsTextArea;
    private javax.swing.JScrollPane LanguagesPropertiesScrollPane;
    private javax.swing.JPopupMenu.Separator MenuSeparator;
    private javax.swing.JButton NewExperimentButton;
    private javax.swing.JButton OpenExperimentButton;
    private javax.swing.JToolBar PhyloToolBar;
    private javax.swing.JSplitPane PropertiesSplitPane;
    private javax.swing.JSplitPane SplitPanel;
    // End of variables declaration//GEN-END:variables
}
