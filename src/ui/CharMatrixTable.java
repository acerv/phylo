package ui;

import informations.Infos;
import matrix.reader.CharMatrixReader;
import matrix.exception.LanguageException;
import matrix.exception.MatrixFormatException;
import ui.configuration.PhyloConfig;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import utility.Debugger;

public class CharMatrixTable extends javax.swing.JPanel
{
    // Numero di righe (caratteri della matrice)
    private int ROWS = 0;

    final DefaultTableModel tableModel;

    // Caratteri usati nella matrice dei caratteri
    final JComboBox abmittedChars;

    private ArrayList<String> alphabet;

    // Editor
    final TableCellEditor tableEditor;

    // Modello delle colonne
    final XTableColumnModel columnModel;

    // Grandezza di una cella
    static int COLUMN_SIZE = 40;

    // configurazione di phylo
    private PhyloConfig cfg;

    /** Creates new form CharMatrixTable
     * @param cfg Configurazione di Phylo
     * @param rows
     * @param alphabet
     */
    public CharMatrixTable(PhyloConfig cfg, ArrayList<String> alphabet, int rows)
    {
        initComponents();

        this.cfg = cfg;

        // Salvo il numero di righe
        this.ROWS = rows;

        // Alfabeto associato
        this.alphabet = alphabet;

        // Inizializzo la tabella
        tableModel = (DefaultTableModel) MatrixTable.getModel();
        tableModel.setNumRows(ROWS);
        columnModel = new XTableColumnModel();
        MatrixTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        MatrixTable.setColumnModel(columnModel);
        MatrixTable.setColumnSelectionAllowed(true);
        MatrixTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        MatrixTable.createDefaultColumnsFromModel();

        // IMPORTANTISSIMO: senza di questo riappaiono le colonne rimosse
        MatrixTable.setAutoCreateColumnsFromModel(false);

        // Inizializzo la combobox
        abmittedChars = new JComboBox();

        int size = alphabet.size() + Infos.UNDEFINED_CHARACTERS.length;
        String[] alph = new String[size];

        for(int i = 0; i < alphabet.size(); i++) alph[i] = alphabet.get(i);
        System.arraycopy(Infos.UNDEFINED_CHARACTERS, 0, alph, alphabet.size(), Infos.UNDEFINED_CHARACTERS.length);

        for(int i = 0; i < alph.length; i++)
            abmittedChars.addItem(alph[i]);

        // Serve per visualizzare le informazioni sulla cella selezionata
        abmittedChars.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent evt)
            {
                int col = MatrixTable.getSelectedColumn();
                int row = MatrixTable.getSelectedRow()+1;

                if(col != -1 && row != -1)
                {
                    String lang = MatrixTable.getColumnName(col);
                    InfoLabel.setText("Language: "+lang+" Character: "+row);
                }
            }
        });

        // Editor delle celle
        tableEditor = new DefaultCellEditor(abmittedChars);
    }

    /**
     * Aggiunge un linguaggio alla tabella.
     * Soluzione al bug del refresh:
     * http://www.exampledepot.com/egs/javax.swing.table/AppendCol.html
     * @param name Nome linguaggio
     */
    public void addLanguage(String name)
    {
        TableColumn col = new TableColumn(tableModel.getColumnCount());

        // Ensure that auto-create is off
        if (MatrixTable.getAutoCreateColumnsFromModel())
        {
            throw new IllegalStateException();
        }

        // Titolo e editor
        col.setHeaderValue(name);
        col.setCellEditor(tableEditor);

        // Addo la colonna
        MatrixTable.addColumn(col);
        tableModel.addColumn(name.toString());
    }

    /**
     * Aggiunge un linguaggio alla tabella specificando i valori iniziali.
     * Soluzione al bug del refresh:
     * http://www.exampledepot.com/egs/javax.swing.table/AppendCol.html
     * @param name Nome linguaggio
     * @param chars Caratteri di inizializzazione
     */
    public void addLanguage(String name, Object[] chars)
    {
        TableColumn col = new TableColumn(tableModel.getColumnCount());

        // Ensure that auto-create is off
        if (MatrixTable.getAutoCreateColumnsFromModel())
            throw new IllegalStateException();

        // Titolo e editor
        col.setHeaderValue(name);
        col.setCellEditor(tableEditor);

        // Addo la colonna
        MatrixTable.addColumn(col);
        tableModel.addColumn(name.toString(), chars);
    }

    /**
     * Rimuove un linguaggio dalla tabella
     * @param name Nome della tabella
     */
    public void removeLanguage(String name)
    {
        int i = MatrixTable.getColumn(name).getModelIndex();
        columnModel.removeColumn(columnModel.getColumnByModelIndex(i));
    }

    /**
     * Ritorna la matrice associata alla tabella
     * @return Ritorna null se la matrice non è stata salvata correttamente
     * @throws MatrixFormatException
     * @throws LanguageException
     */
    public CharMatrixReader getCharMatrix()
            throws MatrixFormatException, LanguageException
    {
        int lang_num = MatrixTable.getColumnCount();

        // Dimensione della matrice
        if(lang_num < 2)
        {
            JOptionPane.showMessageDialog(
                    this.getParent().getParent().getParent(), "Languages are less than 2",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        final CharMatrixReader charMatrix = new CharMatrixReader();
        final ArrayList<ArrayList<String>> matrix = new ArrayList<ArrayList<String>>();
        final ArrayList<String> language = new ArrayList<String>();

        Debugger.println("> Reading table: ");
        String character = null;
        // Prelevo i caratteri
        for(int i = 0; i < lang_num; i++)
        {
            language.add(MatrixTable.getColumnName(i));
            //Debugger.print("> Linguaggio '"+MatrixTable.getColumnName(i)+"': ");

            matrix.add(new ArrayList<String>());
            for(int j = 0; j < MatrixTable.getRowCount(); j++)
            {
                character = (String) MatrixTable.getValueAt(j, i);

                //Debugger.print(character+" ");

                if(character == null)
                {
                    JOptionPane.showMessageDialog(
                            this.getParent().getParent().getParent(), "There is an empty character in the '"+
                            MatrixTable.getColumnName(i)+"' language",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                else
                {
                    character.replaceAll("(?m)^ +", "");
                    matrix.get(i).add(character);
                }
            }
            //Debugger.println();
        }

        // Setto la matrice
        charMatrix.setMatrix(alphabet, matrix, language);

        return charMatrix;
    }

    /**
     * Carica una matrice in memoria e la visualizza nella tabella
     * @param matrix Matrice da caricare
     * @throws MatrixFormatException
     */
    public void loadMatrix(CharMatrixReader matrix)
            throws MatrixFormatException
    {
        // Numero di linguaggi = Numero di righe
        int rows = matrix.getRows();

        // Lettura delle lingue
        for(int i = 0; i < rows; i++)
            addLanguage(matrix.getLanguages().get(i), matrix.getMatrix().get(i).toArray());
    }

    public void selectColumn(String name)
    {
        TableColumn column = MatrixTable.getColumn(name);
        int index = column.getModelIndex();
        MatrixTable.setColumnSelectionInterval(index, index);

        // Riposiziono la scrollbar
        int column_position = index*column.getWidth();
        JScrollBar bar = MatrixScrollPane.getHorizontalScrollBar();
        bar.setValue(column_position/2);
    }

    public void renameColumn(String oldName, String newName)
    {
        MatrixTable.getColumn(oldName).setIdentifier(newName);
        MatrixTable.getColumn(newName).setHeaderValue(newName);
    }

    /**
     * Salva la matrice in un file
     */
    public void saveMatrix()
    {
        try
        {
            CharMatrixReader reader = getCharMatrix();
            
            if(reader != null ) {
                JSaveFileChooser saver = new JSaveFileChooser(cfg.loading_path);
                File matrixFile = saver.show("Save matrix", ".txt");
                if(matrixFile != null) reader.printMatrixOnFile(matrixFile);
                JOptionPane.showMessageDialog(null, "Matrix '"+matrixFile.getName()+"' is saved");
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(CharMatrixTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        CharMatrixPopupMenu = new javax.swing.JPopupMenu();
        SaveMatrixItem = new javax.swing.JMenuItem();
        MatrixScrollPane = new javax.swing.JScrollPane();
        MatrixTable = new javax.swing.JTable();
        InfoLabel = new javax.swing.JLabel();

        SaveMatrixItem.setText("Save Matrix");
        SaveMatrixItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveMatrixItemActionPerformed(evt);
            }
        });
        CharMatrixPopupMenu.add(SaveMatrixItem);

        MatrixScrollPane.setAutoscrolls(true);

        MatrixTable.setRowSelectionAllowed(false);
        MatrixTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MatrixTableMouseClicked(evt);
            }
        });
        MatrixScrollPane.setViewportView(MatrixTable);

        InfoLabel.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        InfoLabel.setText(" ");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(MatrixScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(InfoLabel)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(MatrixScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(InfoLabel))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void MatrixTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MatrixTableMouseClicked
        if(evt.getButton() == MouseEvent.BUTTON3)
        {
            CharMatrixPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_MatrixTableMouseClicked

    private void SaveMatrixItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveMatrixItemActionPerformed
        this.saveMatrix();
    }//GEN-LAST:event_SaveMatrixItemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu CharMatrixPopupMenu;
    private javax.swing.JLabel InfoLabel;
    private javax.swing.JScrollPane MatrixScrollPane;
    private javax.swing.JTable MatrixTable;
    private javax.swing.JMenuItem SaveMatrixItem;
    // End of variables declaration//GEN-END:variables
}
