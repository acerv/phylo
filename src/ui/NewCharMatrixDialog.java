package ui;

import informations.Infos;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class NewCharMatrixDialog extends javax.swing.JDialog {
    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;

    // Default
    private static String MATRIX_DEF = "matrix";
    private static String CHARACTERS_DEF = "10";
    private static String ALPHABET_DEF = "+, -, 0, ?";

    private String matrix = MATRIX_DEF;
    private int characters = 10;
    private ArrayList<String> alphabet;

    /** Creates new form NewCharMatrixDialog */
    public NewCharMatrixDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
    public int getReturnStatus() {
        return returnStatus;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jFormattedTextField2 = new javax.swing.JFormattedTextField();
        AlphabetLabel = new javax.swing.JLabel();
        AlphabetFormattedTextField = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();

        setTitle("New Character Matrix");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Matrix Name:");

        jLabel2.setText("Number of characters:");

        jFormattedTextField1.setText("matrix");
        jFormattedTextField1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextField1PropertyChange(evt);
            }
        });

        jFormattedTextField2.setText("10");
        jFormattedTextField2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextField2PropertyChange(evt);
            }
        });

        AlphabetLabel.setText("Alphabet:");

        AlphabetFormattedTextField.setText("+, -, 0, ?");
        AlphabetFormattedTextField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                AlphabetFormattedTextFieldPropertyChange(evt);
            }
        });

        jLabel4.setText("(*) characters separated by comma");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(55, 55, 55)
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jFormattedTextField1))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(layout.createSequentialGroup()
                                .add(73, 73, 73)
                                .add(AlphabetLabel)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jFormattedTextField2)
                            .add(AlphabetFormattedTextField)
                            .add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(13, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(225, Short.MAX_VALUE)
                .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cancelButton)
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {cancelButton, okButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jFormattedTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jFormattedTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(AlphabetFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(AlphabetLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 27, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(okButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        jFormattedTextField1PropertyChange(null);
        jFormattedTextField2PropertyChange(null);
        AlphabetFormattedTextFieldPropertyChange(null);
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void jFormattedTextField1PropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_jFormattedTextField1PropertyChange
    {//GEN-HEADEREND:event_jFormattedTextField1PropertyChange
        if(isVisible())
        {
            // serve per evitare l'inserimento di separatori di file nel nome dell'esperimento
            String name = jFormattedTextField1.getText();

            if ( name.matches("[\\s]+") ||
                 name.equals("") )
            {
                JOptionPane.showMessageDialog(this,
                        "Sorry, \"" + name + "\" " + "isn't a valid name.\n",
                        "Error", JOptionPane.ERROR_MESSAGE);
                jFormattedTextField1.setText(MATRIX_DEF);
            }
            else if ( name.length() >= 32 )
            {
                JOptionPane.showMessageDialog(this,
                        "Sorry, \"" + name + "\" " + "is too long (max 32char)\n",
                        "Error", JOptionPane.ERROR_MESSAGE);
                jFormattedTextField1.setText(MATRIX_DEF);
            }
            else if( name.contains(Infos.FILE_SEPARATOR) )
            {
                JOptionPane.showMessageDialog(this,
                        "Sorry, \"" + name + "\" " + "contains '"+Infos.FILE_SEPARATOR+"'\n",
                        "Error", JOptionPane.ERROR_MESSAGE);
                jFormattedTextField1.setText(MATRIX_DEF);
            }
            else if ( name.contains(".") )
            {
                JOptionPane.showMessageDialog(this,
                        "Sorry, \"" + name + "\" " + "contains '.'\n",
                        "Error", JOptionPane.ERROR_MESSAGE);
                jFormattedTextField1.setText(MATRIX_DEF);
            }
            else
            {
                this.matrix = jFormattedTextField1.getText();
            }
        }
    }//GEN-LAST:event_jFormattedTextField1PropertyChange

    private void jFormattedTextField2PropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_jFormattedTextField2PropertyChange
    {//GEN-HEADEREND:event_jFormattedTextField2PropertyChange
        if ( !jFormattedTextField2.isEditValid() )
            jFormattedTextField2.setText(CHARACTERS_DEF);
        else
        {
            String chars = jFormattedTextField2.getText();
            try
            {
                int charInt = Integer.parseInt(chars);

                if ( !(charInt >= 2 && charInt <= Integer.MAX_VALUE) || chars.equals("") )
                {
                    JOptionPane.showMessageDialog(this,
                            "Sorry, \"" + charInt + "\" " + "must be included from 2 to "+Integer.MAX_VALUE+" \n",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    jFormattedTextField2.setText(CHARACTERS_DEF);
                }

                this.characters = charInt;
            }
            catch ( NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(this,
                        "Sorry, \"" + chars + "\" " + "isn't a valid value.\n",
                        "Error", JOptionPane.ERROR_MESSAGE);
                jFormattedTextField2.setText(CHARACTERS_DEF);
            }
        }
    }//GEN-LAST:event_jFormattedTextField2PropertyChange

    private void AlphabetFormattedTextFieldPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_AlphabetFormattedTextFieldPropertyChange
    {//GEN-HEADEREND:event_AlphabetFormattedTextFieldPropertyChange
        String chars = AlphabetFormattedTextField.getText();
        chars = chars.replaceAll("\\s+", "");
        chars = chars.replaceAll("(?m)^ +", "");

        String[] alph = chars.split(",");
        String[] undef = Infos.UNDEFINED_CHARACTERS;

        // Remove undefined Phylo characters
        alphabet.addAll(Arrays.asList(alph));
        for(int i = 0; i < undef.length; i++)
        {
            if(alphabet.contains(undef[i]))
            {
                int j = alphabet.indexOf(undef[i]);
                alphabet.remove(j);
            }
        }
        
        if(alphabet.isEmpty())
        {
            JOptionPane.showMessageDialog(this,
                    "Alphabet is empty\n",
                    "Error", JOptionPane.ERROR_MESSAGE);

            AlphabetFormattedTextField.setText(ALPHABET_DEF);
        }
    }//GEN-LAST:event_AlphabetFormattedTextFieldPropertyChange

    public int getCharacters()
    {
        return this.characters;
    }

    public String getMatrixName()
    {
        return this.matrix;
    }

    public ArrayList<String> getAlphabet()
    {
        return alphabet;
    }

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewCharMatrixDialog dialog = new NewCharMatrixDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField AlphabetFormattedTextField;
    private javax.swing.JLabel AlphabetLabel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
