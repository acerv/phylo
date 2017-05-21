package ui;

import ui.configuration.PhyloConfig;
import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class PhyloPropertiesDialog extends javax.swing.JDialog
{
    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;

    // Configurazione di phylo. Ne uso due per evitare che la vecchia venga
    // sovrascritta se viene premuto il tasto "Cancel"
    private PhyloConfig old_config = new PhyloConfig();
    private PhyloConfig new_config = new PhyloConfig();

    // Un flag che informa se la configurazione è stata modificata
    private boolean CONFIG_IS_MODIFIED = false;

    /** Creates new form NewOkCancelDialog */
    public PhyloPropertiesDialog(java.awt.Frame parent, boolean modal)
    {
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
        PhyloWorkPathLbl = new javax.swing.JLabel();
        PhyloWorkingPathField = new javax.swing.JFormattedTextField();
        ChoosePhyloWorkingPathButton = new javax.swing.JButton();
        PhylipDefPathLbl = new javax.swing.JLabel();
        PhylipDefaultPathField = new javax.swing.JFormattedTextField();
        SaveBootstrapFile = new javax.swing.JCheckBox();
        TreeLeavesOnTop = new javax.swing.JCheckBox();
        SaveTreeImage = new javax.swing.JCheckBox();
        StaticLabel = new javax.swing.JLabel();
        LoadingPathLabel = new javax.swing.JLabel();
        PhyloLoadingPathField = new javax.swing.JFormattedTextField();
        ChooseLoadingPathButton = new javax.swing.JButton();
        saveOutfile = new javax.swing.JCheckBox();

        setTitle("Phylo Properties");
        setModal(true);
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

        PhyloWorkPathLbl.setText("Output path:");

        PhyloWorkingPathField.setEditable(false);

        ChoosePhyloWorkingPathButton.setText("Choose");
        ChoosePhyloWorkingPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChoosePhyloWorkingPathButtonActionPerformed(evt);
            }
        });

        PhylipDefPathLbl.setText("Phylip default path:");

        PhylipDefaultPathField.setEditable(false);

        SaveBootstrapFile.setText("Save bootstrapped distance matrix file");
        SaveBootstrapFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveBootstrapFileActionPerformed(evt);
            }
        });

        TreeLeavesOnTop.setText("Horizontal tree orientation");
        TreeLeavesOnTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TreeLeavesOnTopActionPerformed(evt);
            }
        });

        SaveTreeImage.setText("Save a tree .png image in the experiment path");
        SaveTreeImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveTreeImageActionPerformed(evt);
            }
        });

        StaticLabel.setText("(static)");

        LoadingPathLabel.setText("Loading path:");

        PhyloLoadingPathField.setEditable(false);

        ChooseLoadingPathButton.setText("Choose");
        ChooseLoadingPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChooseLoadingPathButtonActionPerformed(evt);
            }
        });

        saveOutfile.setText("Save phylip outfile when possible");
        saveOutfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveOutfileActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(saveOutfile)
                    .add(SaveTreeImage)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelButton))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(PhylipDefPathLbl)
                            .add(LoadingPathLabel)
                            .add(PhyloWorkPathLbl))
                        .add(24, 24, 24)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(PhyloLoadingPathField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                            .add(PhyloWorkingPathField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, PhylipDefaultPathField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(ChooseLoadingPathButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(ChoosePhyloWorkingPathButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(StaticLabel))
                        .add(7, 7, 7))
                    .add(TreeLeavesOnTop)
                    .add(SaveBootstrapFile))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {cancelButton, okButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(PhyloWorkPathLbl)
                    .add(PhyloWorkingPathField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ChoosePhyloWorkingPathButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(PhyloLoadingPathField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(LoadingPathLabel))
                    .add(ChooseLoadingPathButton))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(PhylipDefaultPathField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(StaticLabel)
                    .add(PhylipDefPathLbl))
                .add(18, 18, 18)
                .add(SaveBootstrapFile)
                .add(18, 18, 18)
                .add(TreeLeavesOnTop)
                .add(18, 18, 18)
                .add(SaveTreeImage)
                .add(18, 18, 18)
                .add(saveOutfile)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 154, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(okButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        this.copyConfiguration(new_config, old_config);
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void ChoosePhyloWorkingPathButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ChoosePhyloWorkingPathButtonActionPerformed
    {//GEN-HEADEREND:event_ChoosePhyloWorkingPathButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Select a directory");
        fc.setFileHidingEnabled(true);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showDialog(this, "Use");
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            File path = fc.getSelectedFile();
            this.new_config.exp_path = path.getAbsolutePath();
            this.PhyloWorkingPathField.setText(this.new_config.exp_path);
            
            if(!this.new_config.exp_path.equals(this.old_config.exp_path))
                this.CONFIG_IS_MODIFIED = true;
        }
    }//GEN-LAST:event_ChoosePhyloWorkingPathButtonActionPerformed

    private void SaveBootstrapFileActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_SaveBootstrapFileActionPerformed
    {//GEN-HEADEREND:event_SaveBootstrapFileActionPerformed
        this.new_config.exp_saveBootstrap = this.SaveBootstrapFile.isSelected();
        if(this.new_config.exp_saveBootstrap != this.old_config.exp_saveBootstrap)
            this.CONFIG_IS_MODIFIED = true;
    }//GEN-LAST:event_SaveBootstrapFileActionPerformed

    private void TreeLeavesOnTopActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TreeLeavesOnTopActionPerformed
    {//GEN-HEADEREND:event_TreeLeavesOnTopActionPerformed
        this.new_config.HorizontalTree = this.TreeLeavesOnTop.isSelected();
        if(this.new_config.HorizontalTree != this.old_config.HorizontalTree)
            this.CONFIG_IS_MODIFIED = true;
    }//GEN-LAST:event_TreeLeavesOnTopActionPerformed

    private void SaveTreeImageActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_SaveTreeImageActionPerformed
    {//GEN-HEADEREND:event_SaveTreeImageActionPerformed
        this.new_config.tree_save = this.SaveTreeImage.isSelected();
        if(this.new_config.tree_save != this.old_config.tree_save)
            this.CONFIG_IS_MODIFIED = true;
    }//GEN-LAST:event_SaveTreeImageActionPerformed

    private void ChooseLoadingPathButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ChooseLoadingPathButtonActionPerformed
    {//GEN-HEADEREND:event_ChooseLoadingPathButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Select a directory");
        fc.setFileHidingEnabled(true);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showDialog(this, "Use");
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            File path = fc.getSelectedFile();
            this.new_config.loading_path = path.getAbsolutePath();
            this.PhyloLoadingPathField.setText(this.new_config.loading_path);

            if(!this.new_config.loading_path.equals(this.old_config.loading_path))
                this.CONFIG_IS_MODIFIED = true;
        }
    }//GEN-LAST:event_ChooseLoadingPathButtonActionPerformed

    private void saveOutfileActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveOutfileActionPerformed
    {//GEN-HEADEREND:event_saveOutfileActionPerformed
        this.new_config.outfile_save = this.saveOutfile.isSelected();
        if(this.new_config.outfile_save != this.old_config.outfile_save)
            this.CONFIG_IS_MODIFIED = true;
    }//GEN-LAST:event_saveOutfileActionPerformed

    /**
     * Carica la configurazione di Phylo
     * @param cfg Configurazione di phylo
     */
    public void setPhyloConfig(PhyloConfig cfg)
    {
        this.old_config = cfg;
        this.copyConfiguration(old_config, new_config);

        PhyloWorkingPathField.setText(cfg.exp_path);
        PhyloLoadingPathField.setText(cfg.loading_path);
        PhylipDefaultPathField.setText(cfg.phylip_path);

        if(cfg.exp_saveBootstrap) SaveBootstrapFile.setSelected(true);
        if(cfg.HorizontalTree) TreeLeavesOnTop.setSelected(true);
        if(cfg.tree_save) SaveTreeImage.setSelected(true);
        if(cfg.outfile_save) saveOutfile.setSelected(true);
    }

    private void copyConfiguration(PhyloConfig old_cfg, PhyloConfig new_cfg)
    {
        new_cfg.exp_path = old_cfg.exp_path;
        new_cfg.loading_path = old_cfg.loading_path;
        new_cfg.exp_saveBootstrap = old_cfg.exp_saveBootstrap;
        new_cfg.phylip_path = old_cfg.phylip_path;
        new_cfg.HorizontalTree = old_cfg.HorizontalTree;
        new_cfg.tree_save = old_cfg.tree_save;
        new_cfg.outfile_save = old_cfg.outfile_save;
    }

    public PhyloConfig getPhyloConfig()
    {
        return this.old_config;
    }

    public boolean configIsModified()
    {
        return this.CONFIG_IS_MODIFIED;
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
                PhyloPropertiesDialog dialog = new PhyloPropertiesDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton ChooseLoadingPathButton;
    private javax.swing.JButton ChoosePhyloWorkingPathButton;
    private javax.swing.JLabel LoadingPathLabel;
    private javax.swing.JLabel PhylipDefPathLbl;
    private javax.swing.JFormattedTextField PhylipDefaultPathField;
    private javax.swing.JFormattedTextField PhyloLoadingPathField;
    private javax.swing.JLabel PhyloWorkPathLbl;
    private javax.swing.JFormattedTextField PhyloWorkingPathField;
    private javax.swing.JCheckBox SaveBootstrapFile;
    private javax.swing.JCheckBox SaveTreeImage;
    private javax.swing.JLabel StaticLabel;
    private javax.swing.JCheckBox TreeLeavesOnTop;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton okButton;
    private javax.swing.JCheckBox saveOutfile;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}