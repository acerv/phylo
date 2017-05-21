package ui;

import informations.Infos;
import matrix.reader.CharMatrixReader;
import matrix.exception.MatrixFormatException;
import matrix.reader.MatrixReader;
import ui.configuration.PhyloConfig;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import utility.Debugger;

/**
 *
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class PhyloCharMatrixPanel extends javax.swing.JPanel
{
    // Albero delle matrici
    DefaultMutableTreeNode rootNode;
    DefaultTreeModel model;

    // Numero di matrici
    private int numberOfMatrix = 0;

    // Matrici salvare
    private ArrayList<LoadedMatrix> matrix = new ArrayList<LoadedMatrix>();

    // Nome delle matrici caricate. Serve per controllo
    private ArrayList<String> matrixName = new ArrayList<String>();

    // Linguaggi salvati
    private ArrayList<ArrayList<String>> language = new ArrayList<ArrayList<String>>();

    // Hash che associa "nome matrice" -> "indice nell'arraylist matrix"
    private HashMap<String, Integer> HashMatrix = new HashMap<String, Integer>();

    // Configurazione di Phylo
    private PhyloConfig CONFIG;

    /** Creates new form PhyloCharMatrixPanel */
    public PhyloCharMatrixPanel()
    {
        initComponents();

        // Configuro l'albero delle matrici
        rootNode = (DefaultMutableTreeNode)m_tree.getModel().getRoot();
        model = (DefaultTreeModel)m_tree.getModel();

        m_tree.setEditable(false);
        m_tree.setSelectionRow(0);
        m_tree.setShowsRootHandles(true);
        m_tree.setRootVisible(false);
    }

    /**
     * setta la configurazione di phylo
     * @param cfg configurazione inizializzata
     */
    public void setPhyloConfig(PhyloConfig cfg)
    {
        this.CONFIG = cfg;
    }

    // Aggiunge un nodo all'albero delle matrici e restituisce il nodo appena creato
    private DefaultMutableTreeNode addMatrix(String name, int chars)
    {
        if(HashMatrix.containsKey(name))
        {
            JOptionPane.showMessageDialog(
                    this.getParent(), "This matrix '"+name+"' already exists",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        else
        {
            // Aggiornamento dell'albero
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
            model.insertNodeInto(newNode, rootNode, rootNode.getChildCount());
            javax.swing.tree.TreeNode[] nodes = model.getPathToRoot(newNode);
            TreePath path = new TreePath(nodes);
            m_tree.scrollPathToVisible(path);
            m_tree.setSelectionPath(path);
            m_tree.startEditingAtPath(path);

            // Aggiornamento HashMap
            HashMatrix.put(name, numberOfMatrix);

            // Creo una lista dei linguaggi per la matrice
            language.add(numberOfMatrix, new ArrayList<String>());

            // Aggiorno il numero di matrici
            numberOfMatrix++;

            //Debugger.println("> Aggiunta la matrice '"+name+"' ("+index+")");

            return newNode;
        }
    }

    // Aggiunge un linguaggio all'albero delle matrici
    private void addLanguage(DefaultMutableTreeNode parent, String name)
    {
        // Trovo la matrice di riferimento
        String matrix_name = parent.toString();
        int matrix_index = HashMatrix.get(matrix_name);

        if(language.get(matrix_index).contains(name))
        {
            JOptionPane.showMessageDialog(
                    this.getParent(), "Matrix '"+matrix_name+"' already has this language",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }
        else
        {
            // Aggiornamento dell'albero
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
            model.insertNodeInto(newNode, parent, parent.getChildCount());
            javax.swing.tree.TreeNode[] nodes = model.getPathToRoot(newNode);
            TreePath path = new TreePath(nodes);
            m_tree.scrollPathToVisible(path);
            m_tree.setSelectionPath(path);
            m_tree.startEditingAtPath(path);

            // Aggiungo una lingua alla matrice
            language.get(matrix_index).add(name);

            //Debugger.println("> Aggiunto il linguaggio '"+name+"' alla matrice '"+matrix_name+"' ("+matrix_index+")");
        }
    }

    // Rimuove un nodo dall'albero delle matrici
    private void removeMatrix(DefaultMutableTreeNode matrix_node)
    {
        // Nome della matrice
        String name = matrix_node.toString();

        int value = JOptionPane.showConfirmDialog(
                this.getParent(),
                "Are you sure to remove '"+name+"' matrix?\n"+
                "The assiciated data matrix will be removed too"
                );

        if(value == JOptionPane.OK_OPTION)
        {
            // Aggiorno l'albero
            model.removeNodeFromParent(matrix_node);

            // Rimuovo i linguaggi
            int matrix_index = HashMatrix.get(name);
            if(!language.get(matrix_index).isEmpty())
                language.get(matrix_index).clear();

            // Aggiorno l'hash
            HashMatrix.remove(name);

            // Rimuovo il pannello della matrice
            deleteTabWithName(name);

            //Aggiorno il numero di matrici
            numberOfMatrix--;

            // Disabilito i bottoni di caricamento/generazione esperimento
            LoadMatrixButton.setEnabled(false);
            GenerateExperimentButton.setEnabled(false);

            //Debugger.println("> Rimossa la matrice '"+name+"'");
        }
    }

    // Rimuove un linguaggio dall'albero delle matrici
    private void removeLanguage(DefaultMutableTreeNode lang_node)
    {
        // Cerco la matrice e il linguaggio
        String lang_name = lang_node.toString();
        String matrix_name = lang_node.getParent().toString();
        int matrix_index = HashMatrix.get(matrix_name);
        
        // Rimuovo la lingua dalla matrice
        language.get(matrix_index).remove(lang_name);

        // Aggiorno albero
        model.removeNodeFromParent(lang_node);

        // Rimuovo la colonna della tabella
        int index = MatrixTabPane.indexOfTab(matrix_name);
        CharMatrixTable pane = (CharMatrixTable) MatrixTabPane.getComponentAt(index);
        pane.removeLanguage(lang_name);

        //Debugger.println("> Rimosso il linguaggio '"+lang_name+"' dalla matrice "+matrix_name+"' ("+matrix_index+")");
    }
    
    // Elimina una tab con un dato nome
    private void deleteTabWithName(String name)
    {
        int index = MatrixTabPane.indexOfTab(name);
        if ( index != -1 ) MatrixTabPane.remove(index);
    }

    // Genera l'albero relativo alla matrice
    private void generateTree(CharMatrixReader reader, String name)
    {
        int chars = reader.getColumns();
        DefaultMutableTreeNode node = addMatrix(name, chars);

        for(int i = 0; i < reader.getRows(); i++)
            addLanguage(node, reader.getLanguages().get(i));
    }

    /**
     * Ritorna le matrici che sono state caricate in memoria
     * @return Lista delle matrici caricate in memoria
     */
    public ArrayList<LoadedMatrix> getMatrices()
    {
        return matrix;
    }

    // Aggiunge una matrice alla tabella delle data matrix
    private void addLoadedMatrix(LoadedMatrix matrix)
    {
        // Ogni matrice deve avere un nome univoco per evitare ambiguità
        String name = matrix.getName();
        int return_value = 0;
        if(!this.matrix.isEmpty())
        {
            if(matrixName.contains(name))
            {
                return_value = JOptionPane.showConfirmDialog(
                        this.getParent(),
                        "The matrix '"+name+"' is already loaded as data matrix.\n"+
                        "Reload it will overwrite data.\n\n"+
                        "Are you sure to overwrite it ?",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
            
            if(return_value == JOptionPane.OK_OPTION)
            {
                // Aggiornamento lista delle matrici
                for(int i = 0; i < this.matrix.size(); i++)
                {
                    if(this.matrix.get(i).getName().equals(name))
                    {
                        this.matrix.remove(i);
                        break;
                    }
                }
            }
        }

        this.matrix.add(matrix);

        // Il form di phylo
        PhyloForm phyloForm = Infos.getPhyloForm();

        // Aggiorno la lista delle matrici caricate
        phyloForm.updateLoadedMatrixDialog(this.matrix);
    }

    // Toglie una matrice dalla tabella delle data matrix
    private void delLoadedMatrix(String name)
    {
        int i; for(i = 0; i < matrix.size(); i++ )
            if(matrix.get(i).getName().equals(name)) break;

        if(i != matrix.size()) matrix.remove(i);

        // Il form di phylo
        PhyloForm phyloForm = Infos.getPhyloForm();

        // Aggiorno la lista delle matrici caricate
        phyloForm.updateLoadedMatrixDialog(this.matrix);

        // Rimuovo il nome dalla lista delle matrici caricate in memoria
        matrixName.remove(name);
    }

    /**
     * Carica una matrice da editare. La matrice non viene caricata
     * nella lista delle data matrix, ma solo nell'editor
     * @param matrix Matrice da editare
     * @throws MatrixFormatException
     */
    public void editMatrix(LoadedMatrix matrix)
            throws MatrixFormatException
    {
        // Attributi matrice
        CharMatrixReader reader = (CharMatrixReader) matrix.getMatrix();
        String name = matrix.getName();

        // Mi sassicuro che la matrice abbia nome univoco
        while(HashMatrix.containsKey(name))
        {
            // Leggo i tag
            String[] tags = name.split("_");
            String tag = tags[tags.length-1];
            int tagValue = 1;

            // Se è presente il tag lo incremento di 1
            if(tag.matches("[0-9]+"))
            {
                tagValue = Integer.valueOf(tag);
                tags[tags.length-1] = String.valueOf(tagValue + 1);
                name = tags[0]; for(int i = 1; i < tags.length; i++)
                name = name + "_"+ tags[i];
            }
            else // Si parte dal tag con valore 1
            {
                name = tags[0]; for(int i = 1; i < tags.length; i++)
                name = name + "_"+ tags[i];
                name += "_"+tagValue;
            }
        }

        // Setto il nuovo nome
        matrix.setName(name);

        // Genero l'albero relativo
        generateTree(reader,name);

        // Numero colonne = Numero di caratteri
        CharMatrixTable matrixTable = new CharMatrixTable(CONFIG, reader.getAlphabet(), reader.getRows());
        matrixTable.loadMatrix(reader);

        // Aggiungo la matrice alle tab
        MatrixTabPane.add(name, matrixTable);
        MatrixTabPane.setSelectedIndex(MatrixTabPane.indexOfTab(name));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MatrixMenu = new javax.swing.JPopupMenu();
        AddLanguageMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        ViewMatrixItem = new javax.swing.JMenuItem();
        RemoveMatrixItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        LoadMatrixInMemoryItem = new javax.swing.JMenuItem();
        GenerateExperimentMenuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        SaveMatrix = new javax.swing.JMenuItem();
        LanguageMenu = new javax.swing.JPopupMenu();
        RenameLanguageItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        RemoveLanguageItem = new javax.swing.JMenuItem();
        MatrixToolBar = new javax.swing.JToolBar();
        NewMatrixButton = new javax.swing.JButton();
        NewLanguageButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        LoadMatrixFromFileButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        LoadMatrixButton = new javax.swing.JButton();
        GenerateExperimentButton = new javax.swing.JButton();
        MatrixSplitPane = new javax.swing.JSplitPane();
        MatrixScrollPane = new javax.swing.JScrollPane();
        m_tree = new javax.swing.JTree();
        MatrixTabPane = new javax.swing.JTabbedPane();

        AddLanguageMenuItem.setText("Add Language");
        AddLanguageMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddLanguageMenuItemActionPerformed(evt);
            }
        });
        MatrixMenu.add(AddLanguageMenuItem);
        MatrixMenu.add(jSeparator1);

        ViewMatrixItem.setText("View Matrix");
        ViewMatrixItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewMatrixItemActionPerformed(evt);
            }
        });
        MatrixMenu.add(ViewMatrixItem);

        RemoveMatrixItem.setText("Remove Matrix");
        RemoveMatrixItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveMatrixItemActionPerformed(evt);
            }
        });
        MatrixMenu.add(RemoveMatrixItem);
        MatrixMenu.add(jSeparator2);

        LoadMatrixInMemoryItem.setText("Load data matrix");
        LoadMatrixInMemoryItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadMatrixInMemoryItemActionPerformed(evt);
            }
        });
        MatrixMenu.add(LoadMatrixInMemoryItem);

        GenerateExperimentMenuItem.setText("Generate experiment");
        GenerateExperimentMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GenerateExperimentMenuItemActionPerformed(evt);
            }
        });
        MatrixMenu.add(GenerateExperimentMenuItem);
        MatrixMenu.add(jSeparator6);

        SaveMatrix.setText("Save matrix");
        SaveMatrix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveMatrixActionPerformed(evt);
            }
        });
        MatrixMenu.add(SaveMatrix);

        RenameLanguageItem.setText("Rename Language");
        RenameLanguageItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RenameLanguageItemActionPerformed(evt);
            }
        });
        LanguageMenu.add(RenameLanguageItem);
        LanguageMenu.add(jSeparator5);

        RemoveLanguageItem.setText("Remove Language");
        RemoveLanguageItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveLanguageItemActionPerformed(evt);
            }
        });
        LanguageMenu.add(RemoveLanguageItem);

        MatrixToolBar.setRollover(true);

        NewMatrixButton.setText("New Matrix");
        NewMatrixButton.setFocusable(false);
        NewMatrixButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        NewMatrixButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        NewMatrixButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewMatrixButtonActionPerformed(evt);
            }
        });
        MatrixToolBar.add(NewMatrixButton);

        NewLanguageButton.setText("New Language");
        NewLanguageButton.setFocusable(false);
        NewLanguageButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        NewLanguageButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        NewLanguageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewLanguageButtonActionPerformed(evt);
            }
        });
        MatrixToolBar.add(NewLanguageButton);
        MatrixToolBar.add(jSeparator4);

        LoadMatrixFromFileButton.setText("Load Matrix from file");
        LoadMatrixFromFileButton.setFocusable(false);
        LoadMatrixFromFileButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LoadMatrixFromFileButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        LoadMatrixFromFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadMatrixFromFileButtonActionPerformed(evt);
            }
        });
        MatrixToolBar.add(LoadMatrixFromFileButton);
        MatrixToolBar.add(jSeparator3);

        LoadMatrixButton.setText("Load matrix in memory");
        LoadMatrixButton.setEnabled(false);
        LoadMatrixButton.setFocusable(false);
        LoadMatrixButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LoadMatrixButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        LoadMatrixButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadMatrixButtonActionPerformed(evt);
            }
        });
        MatrixToolBar.add(LoadMatrixButton);

        GenerateExperimentButton.setText("Generate new experiment");
        GenerateExperimentButton.setEnabled(false);
        GenerateExperimentButton.setFocusable(false);
        GenerateExperimentButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        GenerateExperimentButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        GenerateExperimentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GenerateExperimentButtonActionPerformed(evt);
            }
        });
        MatrixToolBar.add(GenerateExperimentButton);

        MatrixSplitPane.setDividerLocation(270);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Matrix");
        m_tree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        m_tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                m_treeMouseClicked(evt);
            }
        });
        m_tree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                m_treeKeyPressed(evt);
            }
        });
        MatrixScrollPane.setViewportView(m_tree);

        MatrixSplitPane.setLeftComponent(MatrixScrollPane);
        MatrixSplitPane.setRightComponent(MatrixTabPane);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(MatrixToolBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
            .add(MatrixSplitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(MatrixToolBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(MatrixSplitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void NewMatrixButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_NewMatrixButtonActionPerformed
    {//GEN-HEADEREND:event_NewMatrixButtonActionPerformed
        NewCharMatrixDialog dialog = new NewCharMatrixDialog(null, true);
        dialog.setLocationRelativeTo(Infos.getPhyloForm());
        dialog.setVisible(true);

        if(dialog.getReturnStatus() == NewCharMatrixDialog.RET_OK)
        {
            String matrix_name = dialog.getMatrixName();
            int characters = dialog.getCharacters();

            addMatrix(matrix_name, characters);

            // Aggiungo la tabella della matrice da riempire
            CharMatrixTable table = new CharMatrixTable(CONFIG, dialog.getAlphabet(), characters);
            MatrixTabPane.add(matrix_name, table);
        }
    }//GEN-LAST:event_NewMatrixButtonActionPerformed

    private void AddLanguageMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_AddLanguageMenuItemActionPerformed
    {//GEN-HEADEREND:event_AddLanguageMenuItemActionPerformed
            TreePath parentPath = m_tree.getSelectionPath();
            if (parentPath != null)
            {
                String lang_name = JOptionPane.showInputDialog(this.getParent(), "Language name:");

                if(lang_name != null)
                {
                    if(!lang_name.equals(""))
                    {
                        DefaultMutableTreeNode parent =
                                (DefaultMutableTreeNode)parentPath.getLastPathComponent();
                        this.addLanguage(parent, lang_name);

                        // Aggiungo una riga alla tabella
                        int index = MatrixTabPane.indexOfTab(parent.toString());
                        CharMatrixTable pane = (CharMatrixTable) MatrixTabPane.getComponentAt(index);
                        pane.addLanguage(lang_name);
                    }
                }
            }
    }//GEN-LAST:event_AddLanguageMenuItemActionPerformed

    private void RemoveMatrixItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RemoveMatrixItemActionPerformed
    {//GEN-HEADEREND:event_RemoveMatrixItemActionPerformed
        TreePath parentPath = m_tree.getSelectionPath();
        if (parentPath != null)
        {
            DefaultMutableTreeNode matrixNode =
                    (DefaultMutableTreeNode)parentPath.getLastPathComponent();
            removeMatrix(matrixNode);

            // Rimuovo la matrice dalla lista delle matrici caricate
            String name = matrixNode.toString();
            delLoadedMatrix(name);
        }
    }//GEN-LAST:event_RemoveMatrixItemActionPerformed

    private void RemoveLanguageItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RemoveLanguageItemActionPerformed
    {//GEN-HEADEREND:event_RemoveLanguageItemActionPerformed
        TreePath parentPath = m_tree.getSelectionPath();
        if (parentPath != null)
        {
            DefaultMutableTreeNode parent =
                    (DefaultMutableTreeNode)parentPath.getLastPathComponent();
            removeLanguage(parent);
        }
    }//GEN-LAST:event_RemoveLanguageItemActionPerformed

    private void ViewMatrixItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ViewMatrixItemActionPerformed
    {//GEN-HEADEREND:event_ViewMatrixItemActionPerformed
        TreePath parentPath = m_tree.getSelectionPath();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)parentPath.getLastPathComponent();
        String name = node.toString();
        MatrixTabPane.setSelectedIndex(MatrixTabPane.indexOfTab(name));
}//GEN-LAST:event_ViewMatrixItemActionPerformed

    private void LoadMatrixInMemoryItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_LoadMatrixInMemoryItemActionPerformed
    {//GEN-HEADEREND:event_LoadMatrixInMemoryItemActionPerformed
        TreePath path = m_tree.getSelectionPath();
        if (path != null)
        {
            DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)path.getLastPathComponent();

            CharMatrixTable matrixTable =
                    (CharMatrixTable) MatrixTabPane.getComponentAt(MatrixTabPane.indexOfTab(node.toString()));
        
            try
            {
                String name = node.toString();

                if(name != null)
                {
                    MatrixReader reader = matrixTable.getCharMatrix();
                    if(reader != null)
                    {
                        LoadedMatrix loadedMatrix = new LoadedMatrix(name, reader);
                        addLoadedMatrix(loadedMatrix);
                        Debugger.println("> Matrix '"+name+"' has been loaded");

                        JOptionPane.showMessageDialog(
                            this.getParent(), "'"+name+"' matrix is loaded",
                            "Information", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(
                        this.getParent(), this.getClass().getName()+": "+ex.toString(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_LoadMatrixInMemoryItemActionPerformed

    private void NewLanguageButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_NewLanguageButtonActionPerformed
    {//GEN-HEADEREND:event_NewLanguageButtonActionPerformed
        TreePath parentPath = m_tree.getSelectionPath();
        if (parentPath != null)
        {
            String lang_name = JOptionPane.showInputDialog(this.getParent(), "Language name:");

            if(lang_name != null)
            {
                if(!lang_name.equals(""))
                {
                    DefaultMutableTreeNode parent =
                            (DefaultMutableTreeNode)parentPath.getLastPathComponent();
                    String matrix_name = null;
                    
                    if(parent.getParent() == rootNode)
                    {
                        this.addLanguage(parent, lang_name);
                        matrix_name = parent.toString();
                    }
                    else
                    {
                        this.addLanguage((DefaultMutableTreeNode) parent.getParent(), lang_name);
                        matrix_name = parent.getParent().toString();
                    }

                    // Aggiungo una riga alla tabella
                    int index = MatrixTabPane.indexOfTab(matrix_name);
                    CharMatrixTable pane = (CharMatrixTable) MatrixTabPane.getComponentAt(index);
                    pane.addLanguage(lang_name);
                }
            }
        }
    }//GEN-LAST:event_NewLanguageButtonActionPerformed

    private void LoadMatrixFromFileButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_LoadMatrixFromFileButtonActionPerformed
    {//GEN-HEADEREND:event_LoadMatrixFromFileButtonActionPerformed
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(this.CONFIG.loading_path));
            int returnVal = fc.showOpenDialog(this);

            // Se è tutto ok, carico il riferimento al file e lo salvo
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File matrix_file = fc.getSelectedFile();
                String name = matrix_file.getName(); // Uso lo stesso nome del file caricato

                while(HashMatrix.containsKey(name))
                {
                    name = JOptionPane.showInputDialog(this.getParent(),
                            "The matrix name '"+name+"' already exists. It needs to be changed:");
                }

                if(name != null)
                {
                    CharMatrixReader reader = new CharMatrixReader();
                    try
                    {
                        // Carico la matrice in memoria
                        reader.loadMatrixFromFile(matrix_file);

                        // Numero colonne = Numero di caratteri
                        CharMatrixTable matrixTable = new CharMatrixTable(CONFIG, reader.getAlphabet(), reader.getColumns());

                        // Visualizzo albero e matrice
                        generateTree(reader, name);
                        matrixTable.loadMatrix(reader);

                        // Aggiungo la matrice alle tab
                        MatrixTabPane.add(name, matrixTable);
                        MatrixTabPane.setSelectedIndex(MatrixTabPane.indexOfTab(name));
                    }
                    catch (Exception ex)
                    {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(
                                this.getParent(), this.getClass().getName()+": "+ex.toString(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
    }//GEN-LAST:event_LoadMatrixFromFileButtonActionPerformed

    private void m_treeKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_m_treeKeyPressed
    {//GEN-HEADEREND:event_m_treeKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_DELETE)
        {
            TreePath parentPath = m_tree.getSelectionPath();
            if (parentPath != null)
            {
                DefaultMutableTreeNode parent =
                        (DefaultMutableTreeNode)parentPath.getLastPathComponent();
                if(parent.getParent() == rootNode)
                {
                    removeMatrix(parent);
                }
                else if(parent.isLeaf())
                {
                    removeLanguage(parent);
                }
            }
        }
}//GEN-LAST:event_m_treeKeyPressed

    private void m_treeMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_m_treeMouseClicked
    {//GEN-HEADEREND:event_m_treeMouseClicked
        TreePath parentPath = m_tree.getSelectionPath();

        if(evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 1)
        {
            if (parentPath != null)
            {
                DefaultMutableTreeNode parent =
                        (DefaultMutableTreeNode)parentPath.getLastPathComponent();

                if(parent.getParent() == rootNode)
                {
                    LoadMatrixButton.setEnabled(true);
                    GenerateExperimentButton.setEnabled(true);
                }
                else
                {
                    LoadMatrixButton.setEnabled(false);
                    GenerateExperimentButton.setEnabled(false);
                }
            }
        }
        else if(evt.getButton() == MouseEvent.BUTTON3)
        {
            /*
            TreePath p = m_tree.getPathForLocation(evt.getX(), evt.getY());
            if(!m_tree.getSelectionModel().isPathSelected(p)) {
                m_tree.getSelectionModel().setSelectionPath(p);
            }
            */

            if (parentPath != null)
            {
                DefaultMutableTreeNode parent =
                        (DefaultMutableTreeNode)parentPath.getLastPathComponent();

                if(parent.getParent() == rootNode)
                {
                    MatrixMenu.show(m_tree, evt.getX(), evt.getY());
                }
                else if(parent.isLeaf())
                {
                    LanguageMenu.show(m_tree, evt.getX(), evt.getY());
                }
            }
        }
        // Doppio click -> movimento sulla lingua interessata
        else if(evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2)
        {
            if (parentPath != null)
            {
                DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)parentPath.getLastPathComponent();

                if(node.isLeaf())
                {
                    String parent_name = node.getParent().toString();
                    MatrixTabPane.setSelectedIndex(MatrixTabPane.indexOfTab(parent_name));
                    CharMatrixTable table =
                            (CharMatrixTable)MatrixTabPane.getComponentAt(MatrixTabPane.indexOfTab(parent_name));

                    String node_name = node.toString();
                    table.selectColumn(node_name);
                }
            }
        }
}//GEN-LAST:event_m_treeMouseClicked

    private void GenerateExperimentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GenerateExperimentButtonActionPerformed
        TreePath path = m_tree.getSelectionPath();
        if (path != null)
        {
            DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)path.getLastPathComponent();

            if(!node.isLeaf())
            {
                CharMatrixTable matrixTable =
                        (CharMatrixTable) MatrixTabPane.getComponentAt(MatrixTabPane.indexOfTab(node.toString()));

                try
                {
                    String name = node.toString();

                    if(name != null)
                    {
                        MatrixReader reader = matrixTable.getCharMatrix();
                        if(reader != null)
                        {
                            LoadedMatrix loadedMatrix = new LoadedMatrix(name, reader);
                            addLoadedMatrix(loadedMatrix);
                            Debugger.println("> Matrix '"+name+"' has been loaded");

                            // Il form di phylo
                            PhyloForm phyloForm = Infos.getPhyloForm();

                            // Seleziono il pannello degli esperimenti
                            phyloForm.selectExperimentPanel();

                            // Pannello degli esperimenti di phylo
                            PhyloExperimentPanel expPanel = phyloForm.getPhyloExperimentPanel();

                            // eseguo un esperimento
                            expPanel.newExperimentWithLoadedCharMatrix(loadedMatrix);
                        }
                    }
                }
                catch (Exception ex)
                {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(
                            this.getParent(), this.getClass().getName()+": "+ex.toString(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        LoadMatrixButton.setEnabled(false);
        GenerateExperimentButton.setEnabled(false);
    }//GEN-LAST:event_GenerateExperimentButtonActionPerformed

    private void GenerateExperimentMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_GenerateExperimentMenuItemActionPerformed
    {//GEN-HEADEREND:event_GenerateExperimentMenuItemActionPerformed
        GenerateExperimentButtonActionPerformed(evt);
    }//GEN-LAST:event_GenerateExperimentMenuItemActionPerformed

    private void RenameLanguageItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RenameLanguageItemActionPerformed
    {//GEN-HEADEREND:event_RenameLanguageItemActionPerformed
        String newName = JOptionPane.showInputDialog("New language name:");
        if(newName != null)
        {
            if(!newName.equals(""))
            {
                TreePath leafPath = m_tree.getSelectionPath();
                if (leafPath != null)
                {
                    DefaultMutableTreeNode leaf =
                            (DefaultMutableTreeNode)leafPath.getLastPathComponent();

                    String matrix_name = leaf.getParent().toString();
                    int matrix_index = HashMatrix.get(matrix_name);

                    if(language.get(matrix_index).contains(newName))
                    {
                        JOptionPane.showMessageDialog(
                                this.getParent(), "Matrix '"+matrix_name+"' already has the '"+newName+"' language",
                                "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                    else
                    {
                        String oldName = leaf.toString();

                        // Aggiorno la lista dei linguaggi
                        int lang_index = language.get(matrix_index).indexOf(oldName);
                        language.get(matrix_index).set(lang_index, newName);

                        // Aggiorno la foglia
                        leaf.setUserObject(newName);

                        // Aggiorno la tabella
                        MatrixTabPane.setSelectedIndex(MatrixTabPane.indexOfTab(matrix_name));
                        CharMatrixTable table =
                                (CharMatrixTable)MatrixTabPane.getComponentAt(MatrixTabPane.indexOfTab(matrix_name));

                        table.renameColumn(oldName, newName);
                        m_tree.repaint();
                    }
                }
            }
        }
    }//GEN-LAST:event_RenameLanguageItemActionPerformed

    private void SaveMatrixActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_SaveMatrixActionPerformed
    {//GEN-HEADEREND:event_SaveMatrixActionPerformed
        TreePath path = m_tree.getSelectionPath();
        if (path != null)
        {
            DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)path.getLastPathComponent();

            if(!node.isLeaf())
            {
                CharMatrixTable matrixTable =
                        (CharMatrixTable) MatrixTabPane.getComponentAt(MatrixTabPane.indexOfTab(node.toString()));
            
                matrixTable.saveMatrix();
            }
        }
    }//GEN-LAST:event_SaveMatrixActionPerformed

    private void LoadMatrixButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadMatrixButtonActionPerformed
        LoadMatrixInMemoryItemActionPerformed(evt);
    }//GEN-LAST:event_LoadMatrixButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AddLanguageMenuItem;
    private javax.swing.JButton GenerateExperimentButton;
    private javax.swing.JMenuItem GenerateExperimentMenuItem;
    private javax.swing.JPopupMenu LanguageMenu;
    private javax.swing.JButton LoadMatrixButton;
    private javax.swing.JButton LoadMatrixFromFileButton;
    private javax.swing.JMenuItem LoadMatrixInMemoryItem;
    private javax.swing.JPopupMenu MatrixMenu;
    private javax.swing.JScrollPane MatrixScrollPane;
    private javax.swing.JSplitPane MatrixSplitPane;
    private javax.swing.JTabbedPane MatrixTabPane;
    private javax.swing.JToolBar MatrixToolBar;
    private javax.swing.JButton NewLanguageButton;
    private javax.swing.JButton NewMatrixButton;
    private javax.swing.JMenuItem RemoveLanguageItem;
    private javax.swing.JMenuItem RemoveMatrixItem;
    private javax.swing.JMenuItem RenameLanguageItem;
    private javax.swing.JMenuItem SaveMatrix;
    private javax.swing.JMenuItem ViewMatrixItem;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JTree m_tree;
    // End of variables declaration//GEN-END:variables

}
