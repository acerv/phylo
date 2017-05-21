
package ui;

import algorithm.phylip.configuration.ConsenseConfiguration;
import algorithm.phylip.configuration.FitchConfiguration;
import algorithm.phylip.configuration.KitschConfiguration;
import algorithm.phylip.configuration.NeighborConfiguration;
import experiment.Experiment;
import bootstrap.Bootstrapper;
import java.io.FileNotFoundException;
import matrix.exception.LanguageException;
import matrix.exception.MatrixFormatException;
import matrix.reader.MatrixReader;
import matrix.reader.CharMatrixReader;
import matrix.reader.DistMatrixReader;
import algorithm.phylip.Consense;
import algorithm.phylip.Fitch;
import algorithm.phylip.Kitsch;
import algorithm.phylip.Neighbor;
import distance.Distance;
import experiment.ExperimentBootstrap;
import experiment.ExperimentCalcDistance;
import experiment.ExperimentLoadDistance;
import files.utility.DeletePath;
import informations.Infos;
import ui.configuration.PhyloConfig;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import utility.Debugger;

/**
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class NewExperimentDialog extends javax.swing.JDialog
{
    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
    
    // Riferimento al file contenente la matrice da elaborare
    private File MATRIX_FILE;

    // Lettore della matrice
    private MatrixReader matrixReader;

    // Esperimento
    private Experiment EXPERIMENT = null;

    // Nome dell'esperimento
    private String NAME = null;

    // Sentinelle per gestire il CardLayout
    private int CARD = 0;
    private int MAX_CARD = 2;

    // Metodo usato per l'albero
    private boolean CONSENSE_TREE = false;

    // Valori predefiniti
    private static int MIN_BOOTSTRAP_NUMBER = 2;
    private static int MAX_BOOTSTRAP_NUMBER = 50000;
    private static String BOOTSTRAP_DEFAULT = "1000";
    private static String POWER_DEFAULT = "2,0000";

    // Configurazione di Phylo
    private PhyloConfig CONFIG;

    // Metodi utilizzabili dall'esperimento
    private int USED_EXP_METHOD = 1;
    private int USED_DISTANCE_METHOD = Infos.JACCARD;
    private int USED_BOOTSTRAP_METHOD = Infos.DEFAULT_BOOTSTRAP;

    // Parametri da passare agli algoritmi phylip
    private double POWER = 0;
    private int OUTGROUP = 0;
    private boolean RANDOMIZE = false;
    private int BOOTSTRAPS = 0;

    // Se la matrice non è ok, la setto errata. Serve per bloccare
    // il tasto di OK
    private boolean NICE_MATRIX = false;

    // Albero radicato ?
    private boolean ROOTED_TREE = false;

    // Visualizzare un albero con gli arichi di dimensione variabile ?
    private boolean USE_BRANCH_LENGHTS = false;

    // Supported algorithms
    private String[] ALGORITHMS;

    /* Messages */
    static private String MSG_OBJ = "Matrix";
    static private String MSG_SUB_MATRIX = "<html><u>Matrix</u></html>";
    static private String MSG_INCORRECT = "file is not correct";
    static private String MSG_CORRECT = "file is correct";
    static private String MSG_LOAD = "needs to be loaded";
    static private String MSG_RELOAD = "needs to be reloaded";

    public NewExperimentDialog(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        ALGORITHMS = Infos.getStandardAlgorithms();
        initComponents();
        MatrixLabel.setText(MSG_SUB_MATRIX);
    }

    /* Messages relative to matrix */
    private void setCorrectMatrixMsg() {
        MatrixLabel.setForeground(Color.green);
        ErrorMatrixLabel.setForeground(Color.green);
        ErrorMatrixLabel.setText(MSG_CORRECT);
    }

    private void setIncorrectMatrixMsg() {
        MatrixLabel.setForeground(Color.red);
        ErrorMatrixLabel.setForeground(Color.red);
        ErrorMatrixLabel.setText(MSG_INCORRECT);
    }

    private void setLoadMatrixMsg() {
        MatrixLabel.setForeground(Color.red);
        ErrorMatrixLabel.setForeground(Color.red);
        ErrorMatrixLabel.setText(MSG_LOAD);
    }

    private void setReloadMatrixMsg() {
        MatrixLabel.setForeground(Color.red);
        ErrorMatrixLabel.setForeground(Color.red);
        ErrorMatrixLabel.setText(MSG_RELOAD);
    }

    /**
     * Salva la configurazione
     * @param cfg Configurazione di phylo
     */
    public void setPhyloConfig(PhyloConfig cfg)
    {
        this.CONFIG = cfg;
    }

    /**
     * Genera automaticamente l'esperimento con la matrice caricata
     * @param reader Matrice da aggiungere
     */
    public void loadCharMatrix(LoadedMatrix reader)
    {
        if(reader != null)
        {
            String matrix_name = reader.getName();
            if(matrix_name != null )
            {
                // Carico la matrice
                matrixReader = (CharMatrixReader) reader.getMatrix();

                // Mostro la combobox di Data File
                chooseMatrixComboBox.setSelectedIndex(1);

                setCorrectMatrixMsg();

                // Salvo i linguaggi
                outGroupComboBox.removeAllItems();
                outGroupComboBox1.removeAllItems();

                // Estraggo i linguaggi e li aggiungo alle combobox
                ArrayList<String> languages = (ArrayList<String>) matrixReader.getLanguages();

                for ( int i = 0; i < languages.size(); i++ )
                {
                   this.outGroupComboBox.insertItemAt(languages.get(i), i);
                   this.outGroupComboBox1.insertItemAt(languages.get(i), i);
                }

                this.NICE_MATRIX = true;
                ErrorMatrixLabel.setVisible(true);
            }
            else
            {
                JOptionPane.showMessageDialog(this, "No loaded matrices",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Carica nell'interfaccia le proprietà di un esperimento
     * @param exp Esperimento da cui prelevare le proprietà
     * @throws CloneNotSupportedException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws MatrixFormatException
     * @throws LanguageException
     */
    public void loadExperimentProperties(Experiment exp)
            throws CloneNotSupportedException, FileNotFoundException,
            IOException, MatrixFormatException, LanguageException
    {
        // Configuro i parametri dell'esperimento
        ExperimentNameFormattedTextField.setText(exp.getName());

        // Configuro il wizard a seconda del metodo
        if(exp instanceof ExperimentCalcDistance)
        {
            ExperimentCalcDistance dist = (ExperimentCalcDistance) exp;

            USED_EXP_METHOD = Infos.DISTANCE_EXP;
            USED_DISTANCE_METHOD = dist.getDistanceMethod();
            matrixReader = new CharMatrixReader();
            matrixReader.loadMatrixFromFile(dist.getCharMatrix().getRefererFile());

            // configuro l'interfaccia
            CharMatrixRadioButton.setSelected(true);
            BootstrapRadioButton.setSelected(false);
            DistanceMethodCombobox.setSelectedIndex(USED_DISTANCE_METHOD);
        }
        else if(exp instanceof ExperimentLoadDistance)
        {
            ExperimentLoadDistance load = (ExperimentLoadDistance) exp;

            USED_EXP_METHOD = Infos.LOAD_DISTANCE_EXP;
            matrixReader = new DistMatrixReader();
            matrixReader.loadMatrixFromFile(load.getDistMatrix().getRefererFile());

            // configuro l'interfaccia
            DistanceRadioButton.setSelected(true);
            BootstrapRadioButton.setSelected(false);
        }
        else if(exp instanceof ExperimentBootstrap)
        {
            ExperimentBootstrap boot = (ExperimentBootstrap) exp;

            USED_EXP_METHOD = Infos.BOOTSTRAP_EXP;
            USED_DISTANCE_METHOD = boot.getDistanceMethod();
            USED_BOOTSTRAP_METHOD = boot.getBootstrapMethod();
            matrixReader = new CharMatrixReader();
            matrixReader.loadMatrixFromFile(boot.getCharMatrix().getRefererFile());

            // Configuro l'interfaccia
            CharMatrixRadioButton.setSelected(true);
            BootstrapRadioButton.setSelected(true);
            BootstrapperItemsComboBox.setSelectedIndex(USED_BOOTSTRAP_METHOD);
            BootstrapsNumberFormattedTextField.setText(String.valueOf(boot.getBootstrapNum()));
            DistanceMethodCombobox.setSelectedIndex(USED_DISTANCE_METHOD);
        }

        // Mostro la combobox di Data File
        chooseMatrixComboBox.setSelectedIndex(1);

        setCorrectMatrixMsg();

        // Salvo i linguaggi
        this.outGroupComboBox.removeAllItems();
        this.outGroupComboBox1.removeAllItems();

        // Estraggo i linguaggi e li aggiungo alle combobox
        ArrayList<String> languages = (ArrayList<String>) matrixReader.getLanguages();

        for ( int i = 0; i < languages.size(); i++ )
        {
           this.outGroupComboBox.insertItemAt(languages.get(i), i);
           this.outGroupComboBox1.insertItemAt(languages.get(i), i);
        }

        this.NICE_MATRIX = true;
        ErrorMatrixLabel.setVisible(true);

        // Configuro il wizard per gli algoritmo phylip
        if(exp.getAlgorithm() instanceof Fitch)
        {
            FitchConfiguration cfg = ((Fitch)exp.getAlgorithm()).getConfiguration();
            AlgorithmComboBox.setSelectedIndex(0);

            // Metodo
            if(cfg.method == Fitch.FITCH_MARGOLIASH_METHOD)
                FitchMargoliashRadio.setSelected(true);
            else if(cfg.method == Fitch.MINIMUM_EVOLUTION_METHOD)
                MinimumEvolutionRadio.setSelected(true);

            // Power
            PowerFormattedTextField.setText(String.valueOf(cfg.power));

            // Outgroup: -1 perché il programma phylip parte da 1 e l'interfacia da 0
            outGroupComboBox.setSelectedIndex(cfg.outgroupSpece-1);

            // Randomize
            RandomizeFitchCheckBox.setSelected(cfg.randomizeInputOrderTimes);
        }
        else if(exp.getAlgorithm() instanceof Kitsch)
        {
            KitschConfiguration cfg = ((Kitsch)exp.getAlgorithm()).getConfiguration();
            AlgorithmComboBox.setSelectedIndex(1);

            // Metodo
            if(cfg.method == Fitch.FITCH_MARGOLIASH_METHOD)
                FitchMargoliashRadio1.setSelected(true);
            else if(cfg.method == Fitch.MINIMUM_EVOLUTION_METHOD)
                MinimumEvolutionRadio1.setSelected(true);

            // Power
            PowerFormattedTextField1.setText(String.valueOf(cfg.power));

            // Randomize
            RandomizeKitschCheckBox.setSelected(cfg.randomizeInputOrderTimes);
        }
        else if(exp.getAlgorithm() instanceof Neighbor)
        {
            NeighborConfiguration cfg = ((Neighbor)exp.getAlgorithm()).getConfiguration();
            if(cfg.tree == Neighbor.NEIGHBOR_TREE)
            {
                AlgorithmComboBox.setSelectedIndex(2);

                // Outgroup: -1 perché il programma phylip parte da 1 e l'interfacia da 0
                outGroupComboBox1.setSelectedIndex(cfg.outgroupSpece-1);
                NeighborRandomizeCheckBox.setSelected(cfg.randomizeInput);
            }
            else if (cfg.tree == Neighbor.UPGMA_TREE)
            {
                AlgorithmComboBox.setSelectedIndex(3);

                // Randomize
                UpgmaRandomizeCheckBox.setSelected(cfg.randomizeInput);
            }
        }

        // Configuro il wizard per Consense
        if(exp instanceof ExperimentBootstrap)
        {
            ConsenseConfiguration cfg = ((ExperimentBootstrap)exp).getConsense().getConfiguration();
            if(cfg.consensusType == Consense.MAJORITY_RULE_EXTENDED)
            {
                MReButton.setSelected(true);
            }
            else if(cfg.consensusType == Consense.MAJORITY_RULE)
            {
                MRButton.setSelected(true);
            }
            else if(cfg.consensusType == Consense.ML_CONSENSUS)
            {
                MLButton.setSelected(true);
            }
            else if(cfg.consensusType == Consense.STRICT)
            {
                StrictButton.setSelected(true);
            }
        }

        // Elimino la cartella di lavoro precedentemente creata
        DeletePath.doDelete(exp.getExperimentPath());
    }

    public int getReturnStatus()
    {
        return this.returnStatus;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MethodButtonGroup = new javax.swing.ButtonGroup();
        FitchMethods = new javax.swing.ButtonGroup();
        KitschMethods = new javax.swing.ButtonGroup();
        NeighborMethods = new javax.swing.ButtonGroup();
        ConsenseMethods = new javax.swing.ButtonGroup();
        MatrixTypeButtonGroup = new javax.swing.ButtonGroup();
        NewExperimentCard = new javax.swing.JPanel();
        NewExperimentPanel = new javax.swing.JPanel();
        BootstrapperPanel = new javax.swing.JPanel();
        BootstrapperTypeLabel = new javax.swing.JLabel();
        BootstrapperItemsComboBox = new javax.swing.JComboBox();
        BootstrapsNumberLabel = new javax.swing.JLabel();
        BootstrapsNumberFormattedTextField = new javax.swing.JFormattedTextField();
        ExperimentNameLabel = new javax.swing.JLabel();
        ExperimentNameFormattedTextField = new javax.swing.JFormattedTextField();
        MaxInputChatLabel = new javax.swing.JLabel();
        UploadMatrixButton = new javax.swing.JButton();
        ErrorMatrixLabel = new javax.swing.JLabel();
        SelectMethodLabel = new javax.swing.JLabel();
        CharMatrixRadioButton = new javax.swing.JRadioButton();
        DistanceRadioButton = new javax.swing.JRadioButton();
        BootstrapRadioButton = new javax.swing.JRadioButton();
        RequiredMatrixLabel = new javax.swing.JLabel();
        chooseMatrixComboBox = new javax.swing.JComboBox();
        MatrixTypeLbl = new javax.swing.JLabel();
        SingleTreeRadioButton = new javax.swing.JRadioButton();
        SpinnerNumberModel numberModel = new SpinnerNumberModel(1,1,50,1);
        DuplicateSpinner = new javax.swing.JSpinner(numberModel);
        DuplicateExperimentCheckBox = new javax.swing.JCheckBox();
        TimesLabel = new javax.swing.JLabel();
        DistanceMethodLabel = new javax.swing.JLabel();
        DistanceMethodCombobox = new javax.swing.JComboBox();
        MatrixLabel = new javax.swing.JLabel();
        PhylipPanel = new javax.swing.JPanel();
        PhylipCard = new javax.swing.JPanel();
        FitchPanel = new javax.swing.JPanel();
        PowerLabel = new javax.swing.JLabel();
        MethodLabel = new javax.swing.JLabel();
        FitchMargoliashRadio = new javax.swing.JRadioButton();
        MinimumEvolutionRadio = new javax.swing.JRadioButton();
        OutGroupLabel = new javax.swing.JLabel();
        PowerFormattedTextField = new javax.swing.JFormattedTextField();
        outGroupComboBox = new javax.swing.JComboBox();
        RandomizeFitchCheckBox = new javax.swing.JCheckBox();
        KitschPanel = new javax.swing.JPanel();
        MethodLabel1 = new javax.swing.JLabel();
        FitchMargoliashRadio1 = new javax.swing.JRadioButton();
        PowerLabel1 = new javax.swing.JLabel();
        MinimumEvolutionRadio1 = new javax.swing.JRadioButton();
        PowerFormattedTextField1 = new javax.swing.JFormattedTextField();
        RandomizeKitschCheckBox = new javax.swing.JCheckBox();
        NeighborPanel = new javax.swing.JPanel();
        OutGroupLabel1 = new javax.swing.JLabel();
        outGroupComboBox1 = new javax.swing.JComboBox();
        NeighborRandomizeCheckBox = new javax.swing.JCheckBox();
        UPGMAPanel = new javax.swing.JPanel();
        UpgmaRandomizeCheckBox = new javax.swing.JCheckBox();
        PhylipProgramLabel = new javax.swing.JLabel();
        AlgorithmComboBox = new javax.swing.JComboBox();
        TreePanel = new javax.swing.JPanel();
        ConsensePanel = new javax.swing.JPanel();
        MethodLabel3 = new javax.swing.JLabel();
        MReButton = new javax.swing.JRadioButton();
        StrictButton = new javax.swing.JRadioButton();
        MRButton = new javax.swing.JRadioButton();
        MLButton = new javax.swing.JRadioButton();
        useBranchLenghtCheckBox = new javax.swing.JCheckBox();
        OKButton = new javax.swing.JButton();
        NextButton = new javax.swing.JButton();
        PreviousButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();

        setTitle("New Experiment");
        setResizable(false);

        NewExperimentCard.setMaximumSize(new java.awt.Dimension(502, 435));
        NewExperimentCard.setLayout(new java.awt.CardLayout());

        NewExperimentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("New Experiment"));
        NewExperimentPanel.setMaximumSize(new java.awt.Dimension(502, 435));
        NewExperimentPanel.setMinimumSize(new java.awt.Dimension(502, 435));
        NewExperimentPanel.setPreferredSize(new java.awt.Dimension(502, 435));

        BootstrapperTypeLabel.setText("Type of bootstrap:");

        BootstrapperItemsComboBox.setModel(new javax.swing.DefaultComboBoxModel(Infos.SUPPORTED_BOOTSTRAP_METHODS));
        BootstrapperItemsComboBox.setMaximumSize(new java.awt.Dimension(74, 24));

        BootstrapsNumberLabel.setText("Number of bootstraps:");

        BootstrapsNumberFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        BootstrapsNumberFormattedTextField.setText("1000");
        BootstrapsNumberFormattedTextField.setMaximumSize(new java.awt.Dimension(20, 19));
        BootstrapsNumberFormattedTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                BootstrapsNumberFormattedTextFieldFocusLost(evt);
            }
        });

        org.jdesktop.layout.GroupLayout BootstrapperPanelLayout = new org.jdesktop.layout.GroupLayout(BootstrapperPanel);
        BootstrapperPanel.setLayout(BootstrapperPanelLayout);
        BootstrapperPanelLayout.setHorizontalGroup(
            BootstrapperPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(BootstrapperPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(BootstrapperPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(BootstrapsNumberLabel)
                    .add(BootstrapperTypeLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(BootstrapperPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(BootstrapsNumberFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(BootstrapperItemsComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 139, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
        BootstrapperPanelLayout.setVerticalGroup(
            BootstrapperPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(BootstrapperPanelLayout.createSequentialGroup()
                .add(13, 13, 13)
                .add(BootstrapperPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(BootstrapperItemsComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(BootstrapperTypeLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(BootstrapperPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(BootstrapsNumberLabel)
                    .add(BootstrapsNumberFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ExperimentNameLabel.setText("( * )Experiment Name:");

        ExperimentNameFormattedTextField.setText("experiment");
        ExperimentNameFormattedTextField.setMaximumSize(new java.awt.Dimension(4, 19));
        ExperimentNameFormattedTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ExperimentNameFormattedTextFieldFocusLost(evt);
            }
        });

        MaxInputChatLabel.setText("(32 char max)");

        UploadMatrixButton.setText("Upload");
        UploadMatrixButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UploadMatrixButtonActionPerformed(evt);
            }
        });

        ErrorMatrixLabel.setForeground(new java.awt.Color(255, 0, 0));
        ErrorMatrixLabel.setText("needs to be loaded");
        ErrorMatrixLabel.setAutoscrolls(true);

        SelectMethodLabel.setText("Select Method:");

        MatrixTypeButtonGroup.add(CharMatrixRadioButton);
        CharMatrixRadioButton.setSelected(true);
        CharMatrixRadioButton.setText("Character Matrix");
        CharMatrixRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CharMatrixRadioButtonItemStateChanged(evt);
            }
        });

        MatrixTypeButtonGroup.add(DistanceRadioButton);
        DistanceRadioButton.setText("Distance Matrix");
        DistanceRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                DistanceRadioButtonItemStateChanged(evt);
            }
        });

        MethodButtonGroup.add(BootstrapRadioButton);
        BootstrapRadioButton.setText("Bootstrap");
        BootstrapRadioButton.setToolTipText("Carica in memoria la matrice dei caratteri specificata, ne fa il bootstrap e calcola le matrici delle distanze relative");
        BootstrapRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                BootstrapRadioButtonItemStateChanged(evt);
            }
        });

        RequiredMatrixLabel.setText("( * ) required");

        chooseMatrixComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "From File", "From Data" }));

        MatrixTypeLbl.setText("( * )Matrix type:");

        MethodButtonGroup.add(SingleTreeRadioButton);
        SingleTreeRadioButton.setSelected(true);
        SingleTreeRadioButton.setText("Single tree");
        SingleTreeRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                SingleTreeRadioButtonItemStateChanged(evt);
            }
        });

        DuplicateSpinner.setToolTipText("Number of times that experiment will be duplicated");

        DuplicateExperimentCheckBox.setText("Replicate Experiment");
        DuplicateExperimentCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DuplicateExperimentCheckBoxActionPerformed(evt);
            }
        });

        TimesLabel.setText("times");

        DistanceMethodLabel.setText("Distance algorithm:");

        DistanceMethodCombobox.setModel(new javax.swing.DefaultComboBoxModel(Infos.SUPPORTED_DISTANCES)
        );

        MatrixLabel.setForeground(new java.awt.Color(255, 0, 0));
        MatrixLabel.setText("Matrix");
        MatrixLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        MatrixLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MatrixLabelMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout NewExperimentPanelLayout = new org.jdesktop.layout.GroupLayout(NewExperimentPanel);
        NewExperimentPanel.setLayout(NewExperimentPanelLayout);
        NewExperimentPanelLayout.setHorizontalGroup(
            NewExperimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(NewExperimentPanelLayout.createSequentialGroup()
                .add(50, 50, 50)
                .add(DuplicateExperimentCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(DuplicateSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 85, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(TimesLabel)
                .add(57, 57, 57)
                .add(RequiredMatrixLabel)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, NewExperimentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(NewExperimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(BootstrapperPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(NewExperimentPanelLayout.createSequentialGroup()
                        .add(NewExperimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(MatrixTypeLbl)
                            .add(ExperimentNameLabel)
                            .add(DistanceMethodLabel)
                            .add(SelectMethodLabel))
                        .add(NewExperimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(NewExperimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(NewExperimentPanelLayout.createSequentialGroup()
                                    .add(12, 12, 12)
                                    .add(NewExperimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(NewExperimentPanelLayout.createSequentialGroup()
                                            .add(ExperimentNameFormattedTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                            .add(MaxInputChatLabel))
                                        .add(NewExperimentPanelLayout.createSequentialGroup()
                                            .add(chooseMatrixComboBox, 0, 253, Short.MAX_VALUE)
                                            .add(18, 18, 18)
                                            .add(UploadMatrixButton))
                                        .add(DistanceMethodCombobox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 214, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(NewExperimentPanelLayout.createSequentialGroup()
                                            .add(MatrixLabel)
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                            .add(ErrorMatrixLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 277, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                                .add(NewExperimentPanelLayout.createSequentialGroup()
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                    .add(NewExperimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(DistanceRadioButton)
                                        .add(CharMatrixRadioButton))))
                            .add(NewExperimentPanelLayout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(NewExperimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(BootstrapRadioButton)
                                    .add(SingleTreeRadioButton))))))
                .add(24, 24, 24))
        );
        NewExperimentPanelLayout.setVerticalGroup(
            NewExperimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(NewExperimentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(NewExperimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ExperimentNameLabel)
                    .add(ExperimentNameFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(MaxInputChatLabel))
                .add(18, 18, 18)
                .add(NewExperimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(MatrixTypeLbl)
                    .add(CharMatrixRadioButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(DistanceRadioButton)
                .add(12, 12, 12)
                .add(NewExperimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(UploadMatrixButton)
                    .add(chooseMatrixComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(NewExperimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(MatrixLabel)
                    .add(ErrorMatrixLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(14, 14, 14)
                .add(NewExperimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(DistanceMethodCombobox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(DistanceMethodLabel))
                .add(18, 18, 18)
                .add(NewExperimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(SelectMethodLabel)
                    .add(NewExperimentPanelLayout.createSequentialGroup()
                        .add(SingleTreeRadioButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(BootstrapRadioButton)))
                .add(18, 18, 18)
                .add(BootstrapperPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(NewExperimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(DuplicateExperimentCheckBox)
                    .add(DuplicateSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(TimesLabel)
                    .add(RequiredMatrixLabel))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        BootstrapperPanel.setVisible(false);
        DuplicateSpinner.setVisible(false);
        TimesLabel.setVisible(false);

        NewExperimentCard.add(NewExperimentPanel, "NewExperimentPanel");

        PhylipPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Phylogenetic Algorithm"));
        PhylipPanel.setMaximumSize(new java.awt.Dimension(502, 435));
        PhylipPanel.setMinimumSize(new java.awt.Dimension(502, 435));
        PhylipPanel.setPreferredSize(new java.awt.Dimension(502, 435));

        PhylipCard.setMaximumSize(new java.awt.Dimension(450, 345));
        PhylipCard.setLayout(new java.awt.CardLayout());

        FitchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Fitch (Phylip)"));
        FitchPanel.setMaximumSize(new java.awt.Dimension(450, 345));
        FitchPanel.setPreferredSize(new java.awt.Dimension(450, 345));

        PowerLabel.setText("Power:");

        MethodLabel.setText("Method:");

        FitchMethods.add(FitchMargoliashRadio);
        FitchMargoliashRadio.setSelected(true);
        FitchMargoliashRadio.setText("Fitch-Margoliash");

        FitchMethods.add(MinimumEvolutionRadio);
        MinimumEvolutionRadio.setText("Minimum Evolution");

        OutGroupLabel.setText("Out group:");

        PowerFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0000"))));
        PowerFormattedTextField.setText("2,0000");
        PowerFormattedTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                PowerFormattedTextFieldFocusLost(evt);
            }
        });

        RandomizeFitchCheckBox.setText("Randomize order of species");

        org.jdesktop.layout.GroupLayout FitchPanelLayout = new org.jdesktop.layout.GroupLayout(FitchPanel);
        FitchPanel.setLayout(FitchPanelLayout);
        FitchPanelLayout.setHorizontalGroup(
            FitchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(FitchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(FitchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(OutGroupLabel)
                    .add(PowerLabel)
                    .add(MethodLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(FitchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(RandomizeFitchCheckBox)
                    .add(FitchPanelLayout.createSequentialGroup()
                        .add(FitchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(outGroupComboBox, 0, 148, Short.MAX_VALUE)
                            .add(FitchMargoliashRadio)
                            .add(MinimumEvolutionRadio, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                            .add(PowerFormattedTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
                        .add(9, 9, 9)))
                .add(238, 238, 238))
        );
        FitchPanelLayout.setVerticalGroup(
            FitchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(FitchPanelLayout.createSequentialGroup()
                .add(FitchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(FitchMargoliashRadio)
                    .add(MethodLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(MinimumEvolutionRadio)
                .add(11, 11, 11)
                .add(FitchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(PowerLabel)
                    .add(PowerFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(FitchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(OutGroupLabel)
                    .add(outGroupComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(RandomizeFitchCheckBox)
                .addContainerGap(163, Short.MAX_VALUE))
        );

        PhylipCard.add(FitchPanel, "Fitch-Margoliash");

        KitschPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Kitsch (Phylip)"));
        KitschPanel.setMaximumSize(new java.awt.Dimension(450, 345));
        KitschPanel.setPreferredSize(new java.awt.Dimension(450, 345));

        MethodLabel1.setText("Method:");

        KitschMethods.add(FitchMargoliashRadio1);
        FitchMargoliashRadio1.setSelected(true);
        FitchMargoliashRadio1.setText("Fitch-Margoliash");

        PowerLabel1.setText("Power:");

        KitschMethods.add(MinimumEvolutionRadio1);
        MinimumEvolutionRadio1.setText("Minimum Evolution");

        PowerFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0000"))));
        PowerFormattedTextField1.setText("2,0000");
        PowerFormattedTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                PowerFormattedTextField1FocusLost(evt);
            }
        });

        RandomizeKitschCheckBox.setText("Randomize order of species");

        org.jdesktop.layout.GroupLayout KitschPanelLayout = new org.jdesktop.layout.GroupLayout(KitschPanel);
        KitschPanel.setLayout(KitschPanelLayout);
        KitschPanelLayout.setHorizontalGroup(
            KitschPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(KitschPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(KitschPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(PowerLabel1)
                    .add(MethodLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(KitschPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(RandomizeKitschCheckBox)
                    .add(KitschPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(FitchMargoliashRadio1)
                        .add(MinimumEvolutionRadio1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                        .add(PowerFormattedTextField1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)))
                .add(247, 247, 247))
        );
        KitschPanelLayout.setVerticalGroup(
            KitschPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(KitschPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(KitschPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(FitchMargoliashRadio1)
                    .add(MethodLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(MinimumEvolutionRadio1)
                .add(11, 11, 11)
                .add(KitschPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(PowerLabel1)
                    .add(PowerFormattedTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(RandomizeKitschCheckBox)
                .addContainerGap(187, Short.MAX_VALUE))
        );

        PhylipCard.add(KitschPanel, "Kitsch");

        NeighborPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Neighbor Joining (Phylip)"));
        NeighborPanel.setMaximumSize(new java.awt.Dimension(450, 345));
        NeighborPanel.setPreferredSize(new java.awt.Dimension(450, 345));

        OutGroupLabel1.setText("Out group:");

        NeighborRandomizeCheckBox.setText("Randomize order of species");

        org.jdesktop.layout.GroupLayout NeighborPanelLayout = new org.jdesktop.layout.GroupLayout(NeighborPanel);
        NeighborPanel.setLayout(NeighborPanelLayout);
        NeighborPanelLayout.setHorizontalGroup(
            NeighborPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(NeighborPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(OutGroupLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(NeighborPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(NeighborRandomizeCheckBox)
                    .add(outGroupComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 156, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(228, Short.MAX_VALUE))
        );
        NeighborPanelLayout.setVerticalGroup(
            NeighborPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(NeighborPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(NeighborPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(OutGroupLabel1)
                    .add(outGroupComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(NeighborRandomizeCheckBox)
                .addContainerGap(240, Short.MAX_VALUE))
        );

        PhylipCard.add(NeighborPanel, "Neighbor-Joining");

        UPGMAPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("UPGMA (Phylip)"));
        UPGMAPanel.setMaximumSize(new java.awt.Dimension(450, 345));

        UpgmaRandomizeCheckBox.setText("Randomize order of species");

        org.jdesktop.layout.GroupLayout UPGMAPanelLayout = new org.jdesktop.layout.GroupLayout(UPGMAPanel);
        UPGMAPanel.setLayout(UPGMAPanelLayout);
        UPGMAPanelLayout.setHorizontalGroup(
            UPGMAPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(UPGMAPanelLayout.createSequentialGroup()
                .add(30, 30, 30)
                .add(UpgmaRandomizeCheckBox)
                .addContainerGap(271, Short.MAX_VALUE))
        );
        UPGMAPanelLayout.setVerticalGroup(
            UPGMAPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(UPGMAPanelLayout.createSequentialGroup()
                .add(17, 17, 17)
                .add(UpgmaRandomizeCheckBox)
                .addContainerGap(272, Short.MAX_VALUE))
        );

        PhylipCard.add(UPGMAPanel, "UPGMA");

        PhylipProgramLabel.setText("Algorithm:");

        AlgorithmComboBox.setModel(new javax.swing.DefaultComboBoxModel(ALGORITHMS));
        AlgorithmComboBox.setMaximumSize(new java.awt.Dimension(138, 24));
        AlgorithmComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                AlgorithmComboBoxItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout PhylipPanelLayout = new org.jdesktop.layout.GroupLayout(PhylipPanel);
        PhylipPanel.setLayout(PhylipPanelLayout);
        PhylipPanelLayout.setHorizontalGroup(
            PhylipPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PhylipPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(PhylipProgramLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(AlgorithmComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 234, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(193, Short.MAX_VALUE))
            .add(PhylipPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(PhylipPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(PhylipCard, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 470, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        PhylipPanelLayout.setVerticalGroup(
            PhylipPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PhylipPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(PhylipPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(AlgorithmComboBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .add(PhylipProgramLabel))
                .addContainerGap(365, Short.MAX_VALUE))
            .add(PhylipPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(PhylipPanelLayout.createSequentialGroup()
                    .add(58, 58, 58)
                    .add(PhylipCard, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        NewExperimentCard.add(PhylipPanel, "PhylipPanel");

        TreePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Tree"));
        TreePanel.setMaximumSize(new java.awt.Dimension(502, 435));
        TreePanel.setMinimumSize(new java.awt.Dimension(502, 435));
        TreePanel.setPreferredSize(new java.awt.Dimension(502, 435));

        ConsensePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Consense Tree (Phylip)"));
        ConsensePanel.setMaximumSize(new java.awt.Dimension(450, 345));
        ConsensePanel.setPreferredSize(new java.awt.Dimension(450, 345));
        ConsensePanel.setVisible(false);

        MethodLabel3.setText("Method:");

        ConsenseMethods.add(MReButton);
        MReButton.setSelected(true);
        MReButton.setText("Majority Rule (extended)");

        ConsenseMethods.add(StrictButton);
        StrictButton.setText("Strict");

        ConsenseMethods.add(MRButton);
        MRButton.setText("Majority Rule");

        ConsenseMethods.add(MLButton);
        MLButton.setText("ML");

        org.jdesktop.layout.GroupLayout ConsensePanelLayout = new org.jdesktop.layout.GroupLayout(ConsensePanel);
        ConsensePanel.setLayout(ConsensePanelLayout);
        ConsensePanelLayout.setHorizontalGroup(
            ConsensePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ConsensePanelLayout.createSequentialGroup()
                .add(43, 43, 43)
                .add(MethodLabel3)
                .add(18, 18, 18)
                .add(ConsensePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(MLButton)
                    .add(StrictButton)
                    .add(MRButton)
                    .add(MReButton))
                .addContainerGap(212, Short.MAX_VALUE))
        );
        ConsensePanelLayout.setVerticalGroup(
            ConsensePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ConsensePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(ConsensePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(MethodLabel3)
                    .add(MReButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(MRButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(StrictButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(MLButton)
                .addContainerGap(213, Short.MAX_VALUE))
        );

        useBranchLenghtCheckBox.setText("Use Branch Lenght");
        useBranchLenghtCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useBranchLenghtCheckBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout TreePanelLayout = new org.jdesktop.layout.GroupLayout(TreePanel);
        TreePanel.setLayout(TreePanelLayout);
        TreePanelLayout.setHorizontalGroup(
            TreePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(TreePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(TreePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(useBranchLenghtCheckBox)
                    .add(ConsensePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE))
                .addContainerGap())
        );
        TreePanelLayout.setVerticalGroup(
            TreePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(TreePanelLayout.createSequentialGroup()
                .add(17, 17, 17)
                .add(useBranchLenghtCheckBox)
                .add(18, 18, 18)
                .add(ConsensePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                .addContainerGap())
        );

        NewExperimentCard.add(TreePanel, "TreeCard");

        OKButton.setText("OK");
        OKButton.setEnabled(false);
        OKButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        OKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKButtonActionPerformed(evt);
            }
        });

        NextButton.setText("Next");
        NextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NextButtonActionPerformed(evt);
            }
        });

        PreviousButton.setText("Previous");
        PreviousButton.setEnabled(false);
        PreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PreviousButtonActionPerformed(evt);
            }
        });

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(NewExperimentCard, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(PreviousButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(NextButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(CancelButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(OKButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {CancelButton, OKButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.linkSize(new java.awt.Component[] {NextButton, PreviousButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(NewExperimentCard, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE, false)
                    .add(OKButton)
                    .add(CancelButton)
                    .add(PreviousButton)
                    .add(NextButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {CancelButton, NextButton, OKButton, PreviousButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

        NewExperimentCard.getAccessibleContext().setAccessibleName("New Experiment");

        pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        java.awt.Dimension dialogSize = getSize();
        setLocation((screenSize.width-dialogSize.width)/2,(screenSize.height-dialogSize.height)/2);
    }// </editor-fold>//GEN-END:initComponents

    private void NextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NextButtonActionPerformed
        CardLayout cl = (CardLayout)NewExperimentCard.getLayout();

        if ( CARD < MAX_CARD )
        {
            this.CARD++;
            this.NextButton.setEnabled(true);
            cl.next(this.NewExperimentCard);
            this.OKButton.setEnabled(false);
        }

        if ( CARD == MAX_CARD )
        {
            this.NextButton.setEnabled(false);

            if ( NICE_MATRIX )
                this.OKButton.setEnabled(true);
        }

        this.PreviousButton.setEnabled(true);
    }//GEN-LAST:event_NextButtonActionPerformed

    private void PreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PreviousButtonActionPerformed
        CardLayout cl = (CardLayout)NewExperimentCard.getLayout();

        if ( CARD > 0 )
        {
            this.CARD--;
            this.PreviousButton.setEnabled(true);
            cl.previous(this.NewExperimentCard);
        }

        if ( CARD == 0 )
            this.PreviousButton.setEnabled(false);

        this.OKButton.setEnabled(false);
        this.NextButton.setEnabled(true);
    }//GEN-LAST:event_PreviousButtonActionPerformed

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_CancelButtonActionPerformed

    private void OKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKButtonActionPerformed
        /* 
         * -> Salvo nome dell'esperimento e ne creo uno nuovo
         * -> Memorizzo i metodi richiesti
         * -> Inizializzo il programma Phylip
         */

        // Nome dell'esperimento
        this.NAME = this.ExperimentNameFormattedTextField.getText();
        
        try {
            int returnValue = JOptionPane.OK_OPTION;

            if ( USED_EXP_METHOD == Infos.BOOTSTRAP_EXP ) {
                /* Number of bottstraps */
                BOOTSTRAPS = Integer.parseInt(BootstrapsNumberFormattedTextField.getText());
                
                if(BOOTSTRAPS > MAX_BOOTSTRAP_NUMBER) {
                    returnValue = JOptionPane.showConfirmDialog(this,
                            "Number of bootstraps is more than "+MAX_BOOTSTRAP_NUMBER+".\n"
                          + "In some situations, experiment could require too much memory,\n"
                          + "without possibility to execute it.\n"
                          + "\n"
                          + "Are you sure to continue?",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }

            if(returnValue == JOptionPane.OK_OPTION) {
                // Configuro l'esperimento in base alle configurazioni scelte
                this.configureMethod();
                this.configureAlgorithm();
                this.configureTree();
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, this.getClass().getName()+": "+ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Tutto finito
        doClose(RET_OK);
    }//GEN-LAST:event_OKButtonActionPerformed

    private void BootstrapRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_BootstrapRadioButtonItemStateChanged
        if ( ItemEvent.SELECTED == evt.getStateChange())
        {
            this.USED_EXP_METHOD = Infos.BOOTSTRAP_EXP;
            
            this.BootstrapperPanel.setVisible(true);
            CONSENSE_TREE = true;
            ConsensePanel.setVisible(true);

            // Disabilito i bottoni di raondom perché già presenti nella configurazione
            // di multiple data sets
            this.RandomizeFitchCheckBox.setSelected(true);
            this.RandomizeKitschCheckBox.setSelected(true);
            this.NeighborRandomizeCheckBox.setSelected(true);
            this.UpgmaRandomizeCheckBox.setSelected(true);

            this.RandomizeFitchCheckBox.setEnabled(false);
            this.RandomizeKitschCheckBox.setEnabled(false);
            this.NeighborRandomizeCheckBox.setEnabled(false);
            this.UpgmaRandomizeCheckBox.setEnabled(false);
        }
        else
        {
            this.BootstrapperPanel.setVisible(false);
            CONSENSE_TREE = false;
            ConsensePanel.setVisible(false);

            // Abilito i bottoni
            this.RandomizeFitchCheckBox.setSelected(false);
            this.RandomizeKitschCheckBox.setSelected(false);
            this.NeighborRandomizeCheckBox.setSelected(false);
            this.UpgmaRandomizeCheckBox.setSelected(false);

            this.RandomizeFitchCheckBox.setEnabled(true);
            this.RandomizeKitschCheckBox.setEnabled(true);
            this.NeighborRandomizeCheckBox.setEnabled(true);
            this.UpgmaRandomizeCheckBox.setEnabled(true);
        }
}//GEN-LAST:event_BootstrapRadioButtonItemStateChanged

    private void UploadMatrixButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UploadMatrixButtonActionPerformed

        // Se è selezionata l'importazione da file
        if ( this.chooseMatrixComboBox.getSelectedIndex() == 0 )
        {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(this.CONFIG.loading_path));
            int returnVal = fc.showOpenDialog(this);

            // Se è tutto ok, carico il riferimento al file e lo salvo
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                this.MATRIX_FILE = fc.getSelectedFile();

                // Riconosco il metodo
                if ( DistanceRadioButton.isSelected() )
                    matrixReader = new DistMatrixReader();
                else
                    matrixReader = new CharMatrixReader();

                try
                {
                    // Carico la matrice
                    matrixReader.loadMatrixFromFile(MATRIX_FILE);

                    setCorrectMatrixMsg();

                    // Salvo i linguaggi
                    this.outGroupComboBox.removeAllItems();
                    this.outGroupComboBox1.removeAllItems();

                    // Estraggo i linguaggi e li aggiungo alle combobox
                    ArrayList<String> languages = (ArrayList<String>) matrixReader.getLanguages();

                    for ( int i = 0; i < languages.size(); i++ )
                    {
                       this.outGroupComboBox.insertItemAt(languages.get(i), i);
                       this.outGroupComboBox1.insertItemAt(languages.get(i), i);
                    }

                    this.NICE_MATRIX = true;
                }
                catch (Exception ex)
                {
                    setIncorrectMatrixMsg();

                    this.NICE_MATRIX = false;
                    
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, MSG_OBJ+MSG_INCORRECT, "Error", JOptionPane.ERROR_MESSAGE);
                    matrixReader.clear();
                }

                ErrorMatrixLabel.setVisible(true);
            }
        }
        else // Caricamento dalla memoria
        {
            // Visualizzo le matrici caricate in memoria dall'editor di charMatrix
            PhyloForm form = Infos.getPhyloForm();

            if(form.viewLoadedMatrixDialog())
            {
                LoadedMatrixDialog dialog = form.getLoadedMatrixDialog();

                if(dialog.getReturnStatus() == LoadedMatrixDialog.RET_OK)
                {
                    LoadedMatrix matrix = dialog.getSelectedMatrix();
                    loadCharMatrix(matrix);
                }
            }
        }
}//GEN-LAST:event_UploadMatrixButtonActionPerformed

    private void ExperimentNameFormattedTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ExperimentNameFormattedTextFieldFocusLost
        // serve per evitare l'inserimento di separatori di file nel nome dell'esperimento
        String name = ExperimentNameFormattedTextField.getText();

        if ( name.matches("[\\s]+") ||
             name.equals("") )
        {
            ExperimentNameFormattedTextField.setText("experiment");
        }
        else if ( name.length() >= 32 )
        {
            this.viewErrorDialog("too long");
            ExperimentNameFormattedTextField.setText("experiment");
        }
        else if( name.contains(Infos.FILE_SEPARATOR) )
        {
            this.viewErrorDialog("contains "+Infos.FILE_SEPARATOR);
            ExperimentNameFormattedTextField.setText("experiment");
        }
        else if ( name.contains(".") )
        {
            this.viewErrorDialog("contains \".\"");
            ExperimentNameFormattedTextField.setText("experiment");
        }
}//GEN-LAST:event_ExperimentNameFormattedTextFieldFocusLost

    private void AlgorithmComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_AlgorithmComboBoxItemStateChanged
        CardLayout cl = (CardLayout)PhylipCard.getLayout();
        cl.show(PhylipCard, (String)evt.getItem());

        int algorithm = AlgorithmComboBox.getSelectedIndex();

        if (algorithm == Infos.FITCH || algorithm == Infos.NEIGHBOR)
            this.ROOTED_TREE = false;
        else
            this.ROOTED_TREE = true;
}//GEN-LAST:event_AlgorithmComboBoxItemStateChanged

    private void PowerFormattedTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_PowerFormattedTextFieldFocusLost
        if ( !PowerFormattedTextField.isEditValid() )
            PowerFormattedTextField.setText(POWER_DEFAULT);
    }//GEN-LAST:event_PowerFormattedTextFieldFocusLost

    private void PowerFormattedTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_PowerFormattedTextField1FocusLost
        if ( !PowerFormattedTextField1.isEditValid() )
            PowerFormattedTextField1.setText(POWER_DEFAULT);
    }//GEN-LAST:event_PowerFormattedTextField1FocusLost

    private void CharMatrixRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CharMatrixRadioButtonItemStateChanged
        if ( ItemEvent.SELECTED == evt.getStateChange() )
        {
            if ( this.USED_EXP_METHOD == Infos.LOAD_DISTANCE_EXP )
                this.verifyMatrixOnMethodChange();

            this.chooseMatrixComboBox.setEnabled(true);
            this.BootstrapRadioButton.setEnabled(true);
            this.DistanceMethodCombobox.setEnabled(true);
            this.DistanceMethodLabel.setEnabled(true);
            this.USED_EXP_METHOD = Infos.DISTANCE_EXP;
        }
    }//GEN-LAST:event_CharMatrixRadioButtonItemStateChanged

    private void DistanceRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DistanceRadioButtonItemStateChanged
        if ( ItemEvent.SELECTED == evt.getStateChange() )
        {
            if ( this.USED_EXP_METHOD == Infos.BOOTSTRAP_EXP || this.USED_EXP_METHOD == Infos.DISTANCE_EXP )
                this.verifyMatrixOnMethodChange();

            this.chooseMatrixComboBox.setEnabled(false);
            this.BootstrapRadioButton.setEnabled(false);
            this.DistanceMethodCombobox.setEnabled(false);
            this.DistanceMethodLabel.setEnabled(false);
            this.SingleTreeRadioButton.setSelected(true);
            this.USED_EXP_METHOD = Infos.LOAD_DISTANCE_EXP;
        }
    }//GEN-LAST:event_DistanceRadioButtonItemStateChanged

    private void BootstrapsNumberFormattedTextFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_BootstrapsNumberFormattedTextFieldFocusLost
    {//GEN-HEADEREND:event_BootstrapsNumberFormattedTextFieldFocusLost
        if ( !BootstrapsNumberFormattedTextField.isEditValid() )
            BootstrapsNumberFormattedTextField.setText(BOOTSTRAP_DEFAULT);
        else
        {
            try
            {
                String bts = BootstrapsNumberFormattedTextField.getText();
                int btsInt = Integer.parseInt(bts);

                if ( btsInt < MIN_BOOTSTRAP_NUMBER || bts.equals("") )
                {
                    BootstrapsNumberFormattedTextField.setText(BOOTSTRAP_DEFAULT);
                }
            }
            catch ( NumberFormatException ex)
            {
                BootstrapsNumberFormattedTextField.setText(BOOTSTRAP_DEFAULT);
            }
        }
    }//GEN-LAST:event_BootstrapsNumberFormattedTextFieldFocusLost

    private void SingleTreeRadioButtonItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_SingleTreeRadioButtonItemStateChanged
    {//GEN-HEADEREND:event_SingleTreeRadioButtonItemStateChanged
        if(DistanceRadioButton.isSelected())
            this.USED_EXP_METHOD = Infos.LOAD_DISTANCE_EXP;
        else if(CharMatrixRadioButton.isSelected())
            this.USED_EXP_METHOD = Infos.DISTANCE_EXP;
    }//GEN-LAST:event_SingleTreeRadioButtonItemStateChanged

    private void useBranchLenghtCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_useBranchLenghtCheckBoxActionPerformed
    {//GEN-HEADEREND:event_useBranchLenghtCheckBoxActionPerformed
        this.USE_BRANCH_LENGHTS = useBranchLenghtCheckBox.isSelected();
    }//GEN-LAST:event_useBranchLenghtCheckBoxActionPerformed

    private void DuplicateExperimentCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DuplicateExperimentCheckBoxActionPerformed
        if(DuplicateExperimentCheckBox.isSelected())
        {
            DuplicateSpinner.setVisible(true);
            TimesLabel.setVisible(true);
        }
        else
        {
            DuplicateSpinner.setVisible(false);
            TimesLabel.setVisible(false);
        }
    }//GEN-LAST:event_DuplicateExperimentCheckBoxActionPerformed

    private void MatrixLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MatrixLabelMouseClicked
        /* Some spaces are added to fix some problems with scale */
        JOptionPane.showMessageDialog(
                this,
                "A character matrix can be of two types:\n"
              + "1) Normal type:\n\n"
              + "    alphabet = +, -\n\n"
              + "    A    +    -    +\n"
              + "    B    +    +    -\n"
              + "    C    -     +    -\n"
              + "\n"
              + "2) Inverted type:\n\n"
              + "    inverted\n"
              + "    alphabet = +, -\n\n"
              + "    A    B    C\n"
              + "    +    +    -\n"
              + "    -     +    +\n"
              + "    +     -    -\n"
              + "\n"
              + "* both rapresent the same one.\n\n",
                "Matrix",
                JOptionPane.INFORMATION_MESSAGE
                );
    }//GEN-LAST:event_MatrixLabelMouseClicked

    // Configura il tipo di esperimento
    public  void configureMethod()
            throws IOException, MatrixFormatException, LanguageException
    {
        Debugger.println("\n/** CONFIGURING METHOD OF CALCULATION **/");
        // Solo un metodo dei seguenti può essere selezionato
        // Metodo di Bootstrap
        if ( USED_EXP_METHOD == Infos.BOOTSTRAP_EXP )
        {
            Bootstrapper bootstrapper = null;
            USED_BOOTSTRAP_METHOD = BootstrapperItemsComboBox.getSelectedIndex();
            USED_DISTANCE_METHOD = DistanceMethodCombobox.getSelectedIndex();
            bootstrapper = Infos.getBootstrapAlgorithm(USED_BOOTSTRAP_METHOD, BOOTSTRAPS, USED_DISTANCE_METHOD);

            EXPERIMENT = new ExperimentBootstrap(CONFIG);
            ((ExperimentBootstrap)EXPERIMENT).mkNewExperiment(NAME, bootstrapper,(CharMatrixReader) matrixReader);
        }
        // Calcolo delle distanze
        else if ( USED_EXP_METHOD == Infos.DISTANCE_EXP )
        {
            Distance distance = Infos.getDistanceAlgorithm(USED_DISTANCE_METHOD);

            EXPERIMENT = new ExperimentCalcDistance(CONFIG);
            ((ExperimentCalcDistance)EXPERIMENT).mkNewExperiment(NAME, distance,(CharMatrixReader) matrixReader);
        }
        // Metodo di caricamento della matrice delle distanze
        else if ( USED_EXP_METHOD == Infos.LOAD_DISTANCE_EXP )
        {
            EXPERIMENT = new ExperimentLoadDistance(CONFIG);
            ((ExperimentLoadDistance)EXPERIMENT).mkNewExperiment(NAME,(DistMatrixReader) matrixReader);
        }
    }

    /* *****************************************************************************
     * QUESTO METODO VA MODIFICATO OGNI QUAL VOLTA SI HA INTENZIONE DI AGGIUNGERE  *
     * UN NUOVO ALGORITMO FILOGENETICO                                             *
     *******************************************************************************/
    public void configureAlgorithm()
            throws IOException
    {
        Debugger.println("\n/** CONFIGURING ALGORITHM **/");
        
        // Nome dell'algoritmo
        int algorithm = AlgorithmComboBox.getSelectedIndex(); // getSelectedItem().toString();

        // Fitch-Margoliash
        if (algorithm == Infos.FITCH)
        {
            Debugger.println("> Method Fitch-Margoliash");
            FitchConfiguration cfg = new FitchConfiguration();

            // Se sono in modalità bootstrap, devo settare il numero di matrici nel file
            if ( USED_EXP_METHOD == Infos.BOOTSTRAP_EXP )
            {
                // Qui il randomize viene fatto automaticamente
                cfg.multipleDataSets = BOOTSTRAPS;
            }

            RANDOMIZE = RandomizeFitchCheckBox.isSelected();
            if(RANDOMIZE)
            {
                Debugger.println("> Randomize: "+RANDOMIZE);
                cfg.randomizeInputOrderTimes = RANDOMIZE;
            }

            // false -> Fitch, true -> minimum
            if ( MinimumEvolutionRadio.isSelected() )
            {
                Debugger.println("> Using Minimum-Evolution method");
                cfg.method = Fitch.MINIMUM_EVOLUTION_METHOD;
            }

            // Estraggo i valori
            POWER = this.getFormattedPower(PowerFormattedTextField.getText());
            this.setOutgroup(outGroupComboBox.getSelectedIndex());

            if ( this.OUTGROUP > 0 )
            {
                String outgroup = null;
                if(EXPERIMENT instanceof ExperimentBootstrap)
                    outgroup = ((ExperimentBootstrap)EXPERIMENT).getCharMatrix().getLanguages().get(OUTGROUP-1);
                else if(EXPERIMENT instanceof ExperimentCalcDistance)
                    outgroup = ((ExperimentCalcDistance)EXPERIMENT).getCharMatrix().getLanguages().get(OUTGROUP-1);
                else if(EXPERIMENT instanceof  ExperimentLoadDistance)
                    outgroup = ((ExperimentLoadDistance)EXPERIMENT).getDistMatrix().getLanguages().get(OUTGROUP-1);

                Debugger.println("\n> Outgroup: "+outgroup);
                
                cfg.outgroupSpece = OUTGROUP;
            }

            cfg.power = POWER;
            Debugger.println("> Power: "+POWER);

            Fitch fitch = new Fitch(cfg);
            EXPERIMENT.setAlgorithm(fitch);
        }
        // Kitsch
        else if (algorithm == Infos.KITSCH)
        {
            Debugger.println("> Using Kitsch method");
            KitschConfiguration cfg = new KitschConfiguration();

            // Se sono in modalità bootstrap, devo settare il numero di matrici nel file
            if ( USED_EXP_METHOD == Infos.BOOTSTRAP_EXP )
            {
                // Qui il randomize viene fatto automaticamente
                cfg.multipleDataSets = BOOTSTRAPS;
            }
            
            RANDOMIZE = RandomizeKitschCheckBox.isSelected();
            if(RANDOMIZE)
            {
                Debugger.println("> Randomize number: "+RANDOMIZE);
                cfg.randomizeInputOrderTimes = RANDOMIZE;
            }

            // false -> Fitch, true -> minimum
            if ( MinimumEvolutionRadio1.isSelected() )
            {
                Debugger.println("> Using Minimum-Evolution method");
                cfg.method = Kitsch.MINIMUM_EVOLUTION_METHOD;
            }

            // Power
            POWER = this.getFormattedPower(PowerFormattedTextField1.getText());
            Debugger.println("> Power: "+POWER);
            cfg.power = POWER;

            Kitsch kitsch = new Kitsch(cfg);
            EXPERIMENT.setAlgorithm(kitsch);
        }
        // Neighbor-Joining
        else if (algorithm == Infos.NEIGHBOR)
        {
            Debugger.println("> Using Neighbor-Joining method");
            NeighborConfiguration cfg = new NeighborConfiguration();

            // Se sono in modalità bootstrap, devo settare il numero di matrici nel file
            if ( USED_EXP_METHOD == Infos.BOOTSTRAP_EXP )
            {
                // Qui il randomize viene fatto automaticamente
                cfg.multipleDataSets = BOOTSTRAPS;
            }
            else
            {
                Debugger.println("> Randomize input order");
                RANDOMIZE = NeighborRandomizeCheckBox.isSelected();
                cfg.randomizeInput = RANDOMIZE;
            }

            this.setOutgroup(outGroupComboBox1.getSelectedIndex());

            if ( OUTGROUP > 0 )
            {
                String outgroup = null;
                if(EXPERIMENT instanceof ExperimentBootstrap)
                    outgroup = ((ExperimentBootstrap)EXPERIMENT).getCharMatrix().getLanguages().get(OUTGROUP-1);
                else if(EXPERIMENT instanceof ExperimentCalcDistance)
                    outgroup = ((ExperimentCalcDistance)EXPERIMENT).getCharMatrix().getLanguages().get(OUTGROUP-1);
                else if(EXPERIMENT instanceof ExperimentLoadDistance)
                    outgroup = ((ExperimentLoadDistance)EXPERIMENT).getDistMatrix().getLanguages().get(OUTGROUP-1);

                Debugger.println("> Outgroup: "+outgroup);
                cfg.outgroupSpece = OUTGROUP;
            }

            Neighbor neighbor = new Neighbor(cfg);
            EXPERIMENT.setAlgorithm(neighbor);
        }
        // UPGMA
        else if (algorithm == Infos.UPGMA)
        {
            Debugger.println("> Using UPGMA method");
            NeighborConfiguration cfg = new NeighborConfiguration();
            cfg.tree = Neighbor.UPGMA_TREE;

            // Se sono in modalità bootstrap, devo settare il numero di matrici nel file
            if ( USED_EXP_METHOD == Infos.BOOTSTRAP_EXP )
            {
                // Qui il randomize viene fatto automaticamente
                cfg.multipleDataSets = BOOTSTRAPS;
            }
            else
            {
                Debugger.println("> Randomize input order");
                RANDOMIZE = UpgmaRandomizeCheckBox.isSelected();
                cfg.randomizeInput = RANDOMIZE;
            }

            Neighbor UPGMA = new Neighbor(cfg);
            EXPERIMENT.setAlgorithm(UPGMA);
        }
        else
        {
            // OTHER ALGORITHMS
        }
    }

    // Configura l'esperimento in base all'albero che viene richiesto di generare
    private void configureTree() throws IOException
    {
        Debugger.println("\n/** CONFIGURING TREE **/");
        // Consense
        if ( CONSENSE_TREE )
        {
            ExperimentBootstrap exp = (ExperimentBootstrap) EXPERIMENT;

            ConsenseConfiguration cfg = new ConsenseConfiguration();
            String type = null;

            // Riconosco il tipo di consense
            if ( MReButton.isSelected() )
            {
                type = "Majority-Rule extended";
                cfg.consensusType = Consense.MAJORITY_RULE_EXTENDED;
            }
            else if ( StrictButton.isSelected() )

            {
                type = "Strict";
                cfg.consensusType = Consense.STRICT;
            }
            else if ( MRButton.isSelected() )
            {
                type = "Majority-Rule";
                cfg.consensusType = Consense.MAJORITY_RULE;
            }
            else if ( MLButton.isSelected() )
            {
                type = "ML";
                cfg.consensusType = Consense.ML_CONSENSUS;
            }

            Debugger.println("> Consensus type: "+type);

            // L'Outgroup di Consense non conincide con quello di FM o NJ
            // L'outgroup viene inserito ALL'ULTIMO posto dell'albero in formato
            // Newick, sia da FM che da NJ
            if ( OUTGROUP > 0 )
            {
                Debugger.println("> Outgroup: "+exp.getCharMatrix().getLanguages().get(OUTGROUP-1));
                
                // Ultimo posto (parte da 1 e non da 0, quindi non serve sottrarre 1)
                cfg.outgroupSpece = exp.getCharMatrix().getLanguages().size();
            }

            if ( ROOTED_TREE )
            {
                Debugger.println("> Trees to be treated as Rooted");
                cfg.rooted = ROOTED_TREE;
            }

            Debugger.println();
            
            Consense consense = new Consense(cfg);
            exp.setConsense(consense);
        }
    }

    /**
     * @return True se si deve tenere conto della dimensione degli archi
     * quando si disegna l'albero
     */
    public boolean useBranchLenght()
    {
        return USE_BRANCH_LENGHTS;
    }

    /**
     * Ritorna l'esperimento da eseguire
     * @return Esperimento da eseguire
     */
    public Experiment getExperiment()
    {
        return this.EXPERIMENT;
    }

    /**
     * Ritorna il numero di repliche da fare dell'esperimento
     * @return Numero di repliche
     */
    public int getNumberOfReplication()
    {
        if(DuplicateExperimentCheckBox.isSelected())
        {
            return ((SpinnerNumberModel)this.DuplicateSpinner.getModel()).getNumber().intValue();
        }
        else
        {
            return 0; // Serve per la funzione replicateExperiment() in PhyloExperimentPanel.java
        }
    }

    private void viewErrorDialog(String error)
    {
        JOptionPane.showMessageDialog(
                this,
                "Input string is not valid: "+error,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // Controlla che le matrici caricate non siano diverse
    private void verifyMatrixOnMethodChange()
    {
        if ( matrixReader != null )
        {
            Debugger.println("> Clean matrix");
            // Elimino la matrice caricata
            this.chooseMatrixComboBox.setSelectedIndex(0);
            this.matrixReader.clear();
            this.matrixReader = null;

            // Azzero i parametri dipendenti dalla matrice
            this.outGroupComboBox.removeAllItems();
            this.outGroupComboBox1.removeAllItems();

            setReloadMatrixMsg();

            this.NICE_MATRIX = false;
        }
        else if ( matrixReader == null && !this.ErrorMatrixLabel.getText().equals(""))
        {
            setLoadMatrixMsg();
        }
    }

    // Formatta il parametro "Power" prelevato dalla gui
    private double getFormattedPower(String value)
    {
        if ( value.contains(",") )
        {
            // Debugger.println("> contiene la virgola");
            value = value.replace(",", ".");
        }

        return Double.parseDouble(value);
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
                NewExperimentDialog dialog = new NewExperimentDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox AlgorithmComboBox;
    private javax.swing.JRadioButton BootstrapRadioButton;
    private javax.swing.JComboBox BootstrapperItemsComboBox;
    private javax.swing.JPanel BootstrapperPanel;
    private javax.swing.JLabel BootstrapperTypeLabel;
    private javax.swing.JFormattedTextField BootstrapsNumberFormattedTextField;
    private javax.swing.JLabel BootstrapsNumberLabel;
    private javax.swing.JButton CancelButton;
    private javax.swing.JRadioButton CharMatrixRadioButton;
    private javax.swing.ButtonGroup ConsenseMethods;
    private javax.swing.JPanel ConsensePanel;
    private javax.swing.JComboBox DistanceMethodCombobox;
    private javax.swing.JLabel DistanceMethodLabel;
    private javax.swing.JRadioButton DistanceRadioButton;
    private javax.swing.JCheckBox DuplicateExperimentCheckBox;
    private javax.swing.JSpinner DuplicateSpinner;
    private javax.swing.JLabel ErrorMatrixLabel;
    private javax.swing.JFormattedTextField ExperimentNameFormattedTextField;
    private javax.swing.JLabel ExperimentNameLabel;
    private javax.swing.JRadioButton FitchMargoliashRadio;
    private javax.swing.JRadioButton FitchMargoliashRadio1;
    private javax.swing.ButtonGroup FitchMethods;
    private javax.swing.JPanel FitchPanel;
    private javax.swing.ButtonGroup KitschMethods;
    private javax.swing.JPanel KitschPanel;
    private javax.swing.JRadioButton MLButton;
    private javax.swing.JRadioButton MRButton;
    private javax.swing.JRadioButton MReButton;
    private javax.swing.JLabel MatrixLabel;
    private javax.swing.ButtonGroup MatrixTypeButtonGroup;
    private javax.swing.JLabel MatrixTypeLbl;
    private javax.swing.JLabel MaxInputChatLabel;
    private javax.swing.ButtonGroup MethodButtonGroup;
    private javax.swing.JLabel MethodLabel;
    private javax.swing.JLabel MethodLabel1;
    private javax.swing.JLabel MethodLabel3;
    private javax.swing.JRadioButton MinimumEvolutionRadio;
    private javax.swing.JRadioButton MinimumEvolutionRadio1;
    private javax.swing.ButtonGroup NeighborMethods;
    private javax.swing.JPanel NeighborPanel;
    private javax.swing.JCheckBox NeighborRandomizeCheckBox;
    private javax.swing.JPanel NewExperimentCard;
    private javax.swing.JPanel NewExperimentPanel;
    private javax.swing.JButton NextButton;
    private javax.swing.JButton OKButton;
    private javax.swing.JLabel OutGroupLabel;
    private javax.swing.JLabel OutGroupLabel1;
    private javax.swing.JPanel PhylipCard;
    private javax.swing.JPanel PhylipPanel;
    private javax.swing.JLabel PhylipProgramLabel;
    private javax.swing.JFormattedTextField PowerFormattedTextField;
    private javax.swing.JFormattedTextField PowerFormattedTextField1;
    private javax.swing.JLabel PowerLabel;
    private javax.swing.JLabel PowerLabel1;
    private javax.swing.JButton PreviousButton;
    private javax.swing.JCheckBox RandomizeFitchCheckBox;
    private javax.swing.JCheckBox RandomizeKitschCheckBox;
    private javax.swing.JLabel RequiredMatrixLabel;
    private javax.swing.JLabel SelectMethodLabel;
    private javax.swing.JRadioButton SingleTreeRadioButton;
    private javax.swing.JRadioButton StrictButton;
    private javax.swing.JLabel TimesLabel;
    private javax.swing.JPanel TreePanel;
    private javax.swing.JPanel UPGMAPanel;
    private javax.swing.JCheckBox UpgmaRandomizeCheckBox;
    private javax.swing.JButton UploadMatrixButton;
    private javax.swing.JComboBox chooseMatrixComboBox;
    private javax.swing.JComboBox outGroupComboBox;
    private javax.swing.JComboBox outGroupComboBox1;
    private javax.swing.JCheckBox useBranchLenghtCheckBox;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;

    /**
     * Serve per tradurre da selezione grafica a selezione effettiva di phylip
     * @param OUTGROUP the OUTGROUP to set
     */
    public void setOutgroup(int OUTGROUP)
    {
        if ( OUTGROUP != -1 )
            this.OUTGROUP = OUTGROUP + 1;
        else
            this.OUTGROUP = 0;
    }
}
