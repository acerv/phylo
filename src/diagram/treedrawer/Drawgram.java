package diagram.treedrawer;

import tree.PhyloTreeNode;
import tree.PhyloTree;
import tree.TreeIntoNewickFormat;
import informations.Infos;
import ui.configuration.PhyloConfig;
import ui.PhyloExperimentPanel;
import ui.JSaveFileChooser;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import experiment.Experiment;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.border.LineBorder;
import tree.TreeInfos;
import utility.Debugger;

/**
 * Visualizza un albero filogenetico utilizzando le Java2D.<br>
 * Le modalità disponibili sono:<br>
 * - statica: menu, possibilità di cliccare archi e tags<br>
 * - dinamica: no menu, no possibilità di interagire con gli archi/tags, zoom e pan<br>
 * - d'esemio: dinamica con menu<br>
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class Drawgram extends JPanel
{
    // Colori
    public static Color BACKGROUND_COLOR = Color.WHITE;
    public static Color FOREGROUND_COLOR = Color.BLACK;
    public static Color SELECTED_COLOR = Color.MAGENTA;
    public static Color RIGHT_CLICK_COLOR = Color.CYAN;
    public static Color SWITCH_SELECT_COLOR = Color.GREEN;

    // Altezza della root
    private static int ROOT_HEIGHT = 20;
    
    // Generici
    private TreeDrawingParameters p;
    private PhyloTree tree;
    private int longestStringLenght;
    private int numOfLeaves;
    private int numOfNodes;
    private JLabel currentLbl;
    private ArrayList<JLabel> labels = new ArrayList<JLabel>();
    private ArrayList<JLine> edges = new ArrayList<JLine>();
    private PhyloExperimentPanel experimentPanel;
    private PhyloConfig phyloCfg;
    private boolean wasHorizontalTree;
    private boolean draw_scores;
    private Font usedFonts;

    // Modalità
    public static int DYNAMIC_VIEW = 0;
    public static int STATIC_VIEW = 1;
    public static int EXAMPLE_VIEW = 2;

    // Modalità usata
    private int USED_VIEW = 0;

    // Zoom e pan
    private ZoomAndPanListener drawerMouseListener;

    // Pop up menu
    private JPopupMenu menu;
    private JCheckBoxMenuItem itemHorizontalTree;
    private JCheckBoxMenuItem itemBranchLenght;
    private JMenuItem itemPngSave;
    private JMenuItem itemPdfSave;
    private JMenuItem itemNwkSave;

    // Switch menu
    private JPopupMenu switchMenu;
    private JMenuItem switchItem;
    private boolean SWITCH_EDGES_MODE = false;
    private int parentNode = -1;
    private ArrayList<Integer> switchSelection = new ArrayList<Integer>();

    // Salvataggio file
    private JSaveFileChooser saver;

    // Indice dell'arco selezionato
    private int currentEdg = -1;

    /**
     * Inizializza il JPanel
     * @param t Albero da visualizzare
     * @param cfg Configurazione del framework Phylo
     * @param experimentPanel Pannello degli esperimenti di Phylo
     * @param p Configurazione del drawer per l'albero associato
     * visualizzo l'albero con i rami scalati in unità (più ordinato)
     * @param view Tipo di modalità. Può essere: DYNAMIC_VIEW, STATIC_VIEW o EXAMPLE_VIEW
     */
    public Drawgram(PhyloTree t, final PhyloConfig cfg, PhyloExperimentPanel experimentPanel,
            final TreeDrawingParameters p, int view)
    {
        super();
        this.setBackground(BACKGROUND_COLOR);
        this.tree = t;

        // Dimensioni stringa più lunga
        String maxString = TreeInfos.getMaxString(tree);
        final JTag tag = new JTag(tree.getRoot(), false);
        java.awt.Font f = tag.getFont();
        FontMetrics m = tag.getFontMetrics(f);
        longestStringLenght = m.stringWidth(maxString);

        numOfLeaves = TreeInfos.getNumberOfLeaves(tree);
        numOfNodes = TreeInfos.getNumberOfNodes(tree);

        this.saver = new JSaveFileChooser(cfg.loading_path);

        this.phyloCfg = cfg;
        this.wasHorizontalTree = cfg.HorizontalTree;
        this.p = p;
        this.draw_scores = false;
        this.setLayout(null);
        this.experimentPanel = experimentPanel;
        this.usedFonts = new Font("SansSerif",Font.BOLD,p.labelFontSize);

        // Vista
        USED_VIEW = view;

        // Se la vista è dinamica abilito il zoom e il pan senza menu
        if(USED_VIEW == DYNAMIC_VIEW)
        {
            addZoomAndPan();
        }
        // Se la vista è statica aggiungo solo il menu
        else if (USED_VIEW == STATIC_VIEW)
        {
            addMenu();
            addScoreItemToMenu();
            addAutoScale();
            addMouseListenerToEdges();
        }
        // Se la vista è statica aggiungo zoom e pan + menu
        else if (USED_VIEW == EXAMPLE_VIEW)
        {
            addZoomAndPan();
            addMenu();
            addScoreItemToMenu();
            addAutoScale();
        }
    }

    // Aggiunge lo zoom e il pan
    private void addZoomAndPan()
    {
        this.drawerMouseListener = new ZoomAndPanListener(this);
        this.addMouseListener(drawerMouseListener);
        this.addMouseMotionListener(drawerMouseListener);
        this.addMouseWheelListener(drawerMouseListener);
    }

    // Aggiunge il menu
    private void addMenu()
    {
        menu = new JPopupMenu();
        itemHorizontalTree = new JCheckBoxMenuItem("HorizontalTree");
        itemBranchLenght = new JCheckBoxMenuItem("View branch Lenght");
        itemPngSave = new JMenuItem("Save as png");
        itemPdfSave = new JMenuItem("Save as pdf");
        itemNwkSave = new JMenuItem("Save as nwk");

        itemHorizontalTree.setSelected(wasHorizontalTree);
        itemBranchLenght.setSelected(p.useBranchLenghts);

        // Menu di switch
        switchMenu = new JPopupMenu();
        switchItem = new JMenuItem("Switch children");
        switchMenu.add(switchItem);
        
        // Popup menu
        this.menu.add(itemHorizontalTree);
        this.menu.add(itemBranchLenght);
        this.menu.add(new JSeparator());
        this.menu.add(itemPngSave);
        this.menu.add(itemPdfSave);
        this.menu.add(itemNwkSave);

        // Menus
        this.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                // Deseleziono l'arco selezionato
                if(currentEdgeExists() && !SWITCH_EDGES_MODE)
                {
                    edges.get(currentEdg).setColor(FOREGROUND_COLOR);
                    currentEdg = -1;
                    repaint();
                }

                // Menu generale
                if(e.getButton() == MouseEvent.BUTTON3)
                {
                    menu.show(e.getComponent(),e.getX(), e.getY());
                }
            }
        });

        this.itemHorizontalTree.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menu.setVisible(false);
                wasHorizontalTree = itemHorizontalTree.isSelected();
                phyloCfg.HorizontalTree = wasHorizontalTree;
                removeLabels();
                removeEdges();
                scaleTree(p, getHeight(), getWidth());
                repaint();
            }
        });

        this.itemBranchLenght.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menu.setVisible(false);
                p.useBranchLenghts = itemBranchLenght.isSelected();
                removeLabels();
                removeEdges();
                scaleTree(p, getHeight(), getWidth());
                repaint();
            }
        });

        this.itemPngSave.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menu.setVisible(false);
                File fileName = saver.show("Save as png...", ".png");
                if(fileName != null)
                {
                    try
                    {
                        drawToFileAsPng(fileName.getAbsolutePath());
                    } catch (Exception ex)
                    {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(null, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    JOptionPane.showMessageDialog(null, "Tree is saved as png", "Message", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        this.itemPdfSave.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menu.setVisible(false);
                File fileName = saver.show("Save as pdf...", ".pdf");
                if(fileName != null)
                {
                    try
                    {
                        drawToFileAsPdf(fileName.getAbsolutePath());
                    }
                    catch (Exception ex)
                    {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(null, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    JOptionPane.showMessageDialog(null, "Tree is saved as pdf", "Message", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        this.itemNwkSave.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menu.setVisible(false);
                File fileName = saver.show("Save as newick format...", ".nwk");
                if(fileName != null)
                {
                    try
                    {
                        drawToFileAsNwk(fileName.getAbsolutePath());
                    }
                    catch (Exception ex)
                    {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(null, this.getClass().getName()+": "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    JOptionPane.showMessageDialog(null, "Tree is saved as newick format", "Message", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        this.switchItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                switchMenu.setVisible(false);
                try
                {
                    if(currentEdgeExists())
                    {
                        PhyloTreeNode node = edges.get(currentEdg).getNode();

                        if(node.getNumberOfChildren() == 2)
                        {
                            switchEdges(node, 0, 1);
                        }
                        else if(node.getNumberOfChildren() >= 3)
                        {
                            JOptionPane.showMessageDialog(
                                    null, "This edge have 2 or more children.\n"
                                        + "Select two different children to switch.\n"
                                        + "\nTo exit from switch mode, just click twice on the same child.",
                                    "Message", JOptionPane.INFORMATION_MESSAGE);

                            // Coloro il padre
                            edges.get(currentEdg).setColor(SWITCH_SELECT_COLOR);
                            parentNode = currentEdg;
                            repaint();

                            // Modalità di selezione
                            SWITCH_EDGES_MODE = true;
                        }
                    }
                }
                catch (CloneNotSupportedException ex)
                {
                    Logger.getLogger(Drawgram.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Aggiunge la voce di score al menu
    private void addScoreItemToMenu()
    {
        final JCheckBoxMenuItem itemViewScores= new JCheckBoxMenuItem("View scores");
        this.menu.add(itemViewScores, 2);

        itemViewScores.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menu.setVisible(false);
                draw_scores = itemViewScores.isSelected();
                repaint();
            }
        });
    }

    // Aggiunge il repaint automatico quando la finestra cambia dimensione
    private void addAutoScale()
    {
        // Repaint quando viene cambiata la dimensione del panel
        this.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent evt)
            {
                removeEdges();
                removeLabels();
                scaleTree(p, getHeight(), getWidth());
                repaint();
            }
        });
    }

    // Non vengono contate le etichette
    private double getTreeHeight(TreeDrawingParameters p)
    {
        return p.leafLabelMargin + (p.lineLenght*tree.getHeight());
    }

    // Non vengono contate le etichette
    private double getScaledTreeHeight(TreeDrawingParameters p)
    {
        return p.leafLabelMargin + (p.lineLenght*tree.getScaledHeight());
    }

    // First time that component is painted
    private boolean first_time = true;
    private boolean init = true;
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setStroke(new BasicStroke(p.lineWidth));
        g2.setFont(this.usedFonts);

        g2.setBackground(BACKGROUND_COLOR);
        g2.setColor(FOREGROUND_COLOR);

        // Zoom e pan per la vista dinamica e d'esemio
        if(USED_VIEW == DYNAMIC_VIEW || USED_VIEW == EXAMPLE_VIEW)
        {
            if (init)
            {
                init = false;
                drawerMouseListener.setCoordTransform(g2.getTransform());
            }
            else
            {
                // Restore the viewport after it was updated by the ZoomAndPanListener
                g2.setTransform(drawerMouseListener.getCoordTransform());
            }
        }
        else // Abilito il rendering solo nella visualizzazione statica per migliori prestazioni
        {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        // Prima volta che si disegna ?
        if(this.first_time)
        {
            this.scaleTree(p, getHeight(), getWidth());
            this.first_time = false;
        }

        // Traccia il cambiamento da disegno in verticale a disegno in orizzontale
        if(wasHorizontalTree ^ phyloCfg.HorizontalTree)
        {
            // Ricalcolo lo scale
            this.scaleTree(p, getHeight(), getWidth());

            // Rimuovo i label precedentemente scritti
            this.removeLabels();

            // rimuovo gli archi
            this.removeEdges();
            
            this.wasHorizontalTree = phyloCfg.HorizontalTree;
        }

        // punto da cui compinciare a disegnare
        Point starting_point;

        // Visualizzo l'albero
        if(phyloCfg.HorizontalTree)
        {
            if(p.useBranchLenghts)
                starting_point = new Point((int) (p.marginSize + this.getTreeHeight(p)), p.marginSize);
            else
                starting_point = new Point((int) (p.marginSize + this.getScaledTreeHeight(p)), p.marginSize);

            // Albero in orizzontale
            this.horizontalDraw(g2, p, starting_point, tree.getRoot(), USED_VIEW);
        }
        else
        {
            if(p.useBranchLenghts)
                starting_point = new Point(p.marginSize,(int) (p.marginSize + this.getTreeHeight(p)));
            else
                starting_point = new Point(p.marginSize,(int) (p.marginSize + this.getScaledTreeHeight(p)));

            // Albero in verticale
            this.verticalDraw(g2, p, starting_point, tree.getRoot(), USED_VIEW);
        }
    }

    // Rimuove i labels scritti
    private void removeLabels()
    {
        // Rimuovo i label precedentemente scritti
        for(int i = 0; i < this.labels.size(); i++) this.remove(this.labels.get(i));
        this.labels.clear();
    }

    // Rimuove gli archi scritti
    private void removeEdges()
    {
        this.edges.clear();
    }

    // Riscala l'albero
    private void scaleTree(TreeDrawingParameters p, int height, int width)
    {
        // Dimensioni dell'albero
        double h = height - 2*p.marginSize;
        double w = width - 2*p.marginSize;

        // Diverse dimensioni dell'albero se si tiene conto della lunghezza dei rami
        if(p.useBranchLenghts)
        {
            if(phyloCfg.HorizontalTree)
            {
                w -= ROOT_HEIGHT;
                p.lineLenght = (int) ((w - longestStringLenght) / tree.getHeight());
                p.leafWidth  = (int) (h / numOfLeaves);
            }
            else
            {
                h -= ROOT_HEIGHT;
                p.lineLenght = (int) ((h - longestStringLenght) / tree.getHeight());
                p.leafWidth  = (int) (w / numOfLeaves);
            }
        }
        else
        {
            if(phyloCfg.HorizontalTree)
            {
                w -= ROOT_HEIGHT;
                p.lineLenght = (int) ((w - longestStringLenght) / tree.getScaledHeight());
                p.leafWidth  = (int) (h / numOfLeaves);
            }
            else
            {
                h -= ROOT_HEIGHT;
                p.lineLenght = (int) ((h - longestStringLenght) / tree.getScaledHeight());
                p.leafWidth  = (int) (w / numOfLeaves);
            }
        }
    }

    /* ***********
     * DRAWER    *
     *************/

    // Serve per la visualizzazione degli score
    private String score;

    /**
     * Disegna il sottoalbero in posizione verticale
     * @param g Disegnatore
     * @param p Configurazione per il disegnatore
     * @param node
     * @param startPosition Posizione da cui partire a disegnare (in basso a sinistra)
     * @param view Tipo di vista da gestire
     * @return Punto da cui partire a disegnare
     */
    public Point verticalDraw(Graphics2D g, TreeDrawingParameters p, Point startPosition, PhyloTreeNode node, int view)
    {
        // Scrittura del nome della foglia
        if(node.isLeaf())
        {
            score = String.valueOf(node.getScore());

            // Coordinate da cui partire a scrivere
            double stringXPosition = 0;
            double stringYPosition = startPosition.y + p.leafLabelMargin;
            if(p.useBranchLenghts)
            {
                int offset = (int) (this.getTreeHeight(p) - (node.getHeight() * p.lineLenght));
                stringYPosition = stringYPosition - offset;
            }
            
            startPosition.x += p.leafWidth;

            // Se sono in modalità dinamica o d'esempio disegno solo stringhe
            if(view == DYNAMIC_VIEW || view == EXAMPLE_VIEW)
            {
                g.transform(AffineTransform.getRotateInstance(Math.PI/2,startPosition.x,stringYPosition));
                g.drawString(node.getName(),(int) startPosition.x,(int) stringYPosition);
                g.transform(AffineTransform.getRotateInstance(-Math.PI/2,startPosition.x,stringYPosition));
            }
            else // Se sono in modalità statica uso l'interazione dei label con l'utente
            {
                // il numero delle etichette è uguale al numero dei labels
                // solo se i label sono già stati scritti tutti
                if(this.labels.size() < numOfLeaves)
                {
                    // Aggiungo una JLabel
                    JTag lbl = new JTag(node, true);
                    this.add(lbl);
                    this.labels.add(lbl);

                    // Configuro la JLabel
                    lbl.setFont(this.usedFonts);
                    Rectangle2D rect = lbl.getFont().getStringBounds(node.getName(),g.getFontRenderContext());

                    // Posizione verticale a metà del tag
                    stringXPosition = (int) (startPosition.x - (rect.getWidth() / 2));

                    rect.setRect(stringXPosition-2, stringYPosition-2, rect.getHeight()+4, rect.getWidth()+4);
                    lbl.setBounds(rect.getBounds());

                    // Aggiungo un listener
                    this.addMouseListenerToTag(lbl);
                }
            }
            
            return new Point(startPosition.x, stringYPosition - p.leafLabelMargin);
        }
        else
        {
            // Coordinata finale della prima riga verticale scritta
            Point finalPoint = new Point();

            // Coordinata finale dell'ultima riga verticale scritta
            Point endPoint = new Point();

            // Il sotto albero sinistro viene scritto come riferimento iniziale
            Point startPoint = verticalDraw(g, p, startPosition, node.getChild(0), view);
            finalPoint.x = startPoint.x;

            if(p.useBranchLenghts)
            {
                finalPoint.y = startPoint.y - getHeightOf(node, node.getChild(0)) * p.lineLenght;
            }
            else
            {
                finalPoint.y = startPoint.y - getScaledHeightOf(node, node.getChild(0)) * p.lineLenght;
            }

            if(view == DYNAMIC_VIEW || view == EXAMPLE_VIEW)
            {
                drawLine(g, startPoint, finalPoint);
            }
            else
            {
                drawJLine(g, node.getChild(0), startPoint, finalPoint, false);
                score = String.valueOf(node.getChild(0).getScore());
            }

            // Visualizzazione degli score sul grafico?
            if(this.draw_scores)
            {
                g.transform(AffineTransform.getRotateInstance(Math.PI/2,finalPoint.x,finalPoint.y));
                g.drawString(score,(int) finalPoint.x+5,(int) finalPoint.y-5);
                g.transform(AffineTransform.getRotateInstance(-Math.PI/2,finalPoint.x,finalPoint.y));

                score = String.valueOf(node.getScore());
            }
            
            // Scrittura del sottoalbero destro
            for(int i = 1; i < node.getNumberOfChildren(); i++)
            {
                // Primo punto della linea da tracciare
                startPoint = verticalDraw(g, p, startPosition, node.getChild(i), view);
                endPoint.x = startPoint.x;

                // Ultimo punto della linea da tracciare
                if(p.useBranchLenghts)
                {
                    endPoint.y = startPoint.y - getHeightOf(node, node.getChild(i)) * p.lineLenght;
                }
                else
                {
                    endPoint.y = startPoint.y - getScaledHeightOf(node, node.getChild(i)) * p.lineLenght;
                }

                // Linea verticale
                if(view == DYNAMIC_VIEW || view == EXAMPLE_VIEW)
                {
                    drawLine(g, startPoint, endPoint);
                }
                else
                {
                    drawJLine(g, node.getChild(i), startPoint, endPoint, false);
                    score = String.valueOf(node.getChild(i).getScore());
                }

                // Visualizzazione degli score sul grafico?
                if(this.draw_scores)
                {
                    g.transform(AffineTransform.getRotateInstance(Math.PI/2,endPoint.x,endPoint.y));
                    g.drawString(score,(int) endPoint.x+5,(int) endPoint.y-5);
                    g.transform(AffineTransform.getRotateInstance(-Math.PI/2,endPoint.x,endPoint.y));

                    score = String.valueOf(node.getScore());
                }
            }

             // Piccolo trick/fix :)
            finalPoint.y = endPoint.y;

            // Linea orizzontale
            drawLine(g, finalPoint, endPoint);

            Point mid = getHorizontalMidLenght(finalPoint, endPoint);

            // Draw the root line
            if(node.isRoot())
            {
                Point end = new Point(mid.x, mid.y - ROOT_HEIGHT);
                drawJLine(g, node, mid, end, false);
            }

            // Ritorno la posizione orizzontale a metà della riga appena tracciata
            return mid;
        }
    }

    /**
     * Disegna il sottoalbero relativo al nodo in orizzontale
     * @param g Disegnatore
     * @param p Parametri di configurazione per l'albero
     * @param startPosition
     * @param node Nodo da cui partire a scrivere l'albero
     * @param view Vista da visualizzare
     * @return Coordinata in cui si ha scritto
     */
    public Point horizontalDraw(Graphics2D g, TreeDrawingParameters p, Point startPosition, PhyloTreeNode node, int view)
    {
        // Scrittura del nome della foglia
        if(node.isLeaf())
        {
            score = String.valueOf(node.getScore());

            // Posizione orizzontale della stringa
            double stringXPosition = startPosition.x + p.leafLabelMargin;
            double stringYPosition = 0;
            startPosition.y += p.leafWidth;
            
            if(p.useBranchLenghts)
            {
                int offset = (int) (this.getTreeHeight(p) - node.getHeight() * p.lineLenght);
                stringXPosition = stringXPosition - offset;
            }

            // Se sono in modalità dinamica disegno solo stringhe
            if(view == DYNAMIC_VIEW || view == EXAMPLE_VIEW)
            {
                g.drawString(node.getName(),(int) stringXPosition,(int) startPosition.y);
            }
            else // Se sono in modalità statica uso l'interazione dei label con l'utente
            {
                // il numero delle etichette è uguale al numero dei labels
                // solo se i label sono già stati scritti tutti
                if(this.labels.size() < numOfLeaves)
                {
                    // Aggiungo una JLabel
                    JTag lbl = new JTag(node, false);
                    this.add(lbl);
                    this.labels.add(lbl);

                    // Configuro la JLabel
                    lbl.setFont(this.usedFonts);
                    Rectangle2D rect = lbl.getFont().getStringBounds(node.getName(),g.getFontRenderContext());

                    // Posizione verticale a metà del tag
                    stringYPosition = (int) (startPosition.y - (rect.getHeight() / 2));

                    rect.setRect(stringXPosition-2, stringYPosition-2, rect.getWidth()+4, rect.getHeight()+4);
                    lbl.setBounds(rect.getBounds());

                    // Aggiungo un listener
                    this.addMouseListenerToTag(lbl);
                }
            }
            return new Point(stringXPosition - p.leafLabelMargin, startPosition.y);
        }
        else
        {
            // Coordinata finale della prima riga orizzontale scritta
            Point finalPoint = new Point();

            // Coordinata finale dell'ultima riga orizzontale scritta
            Point endPoint = new Point();

            // Il sotto albero sinistro viene scritto come riferimento iniziale
            Point startPoint = horizontalDraw(g, p, startPosition, node.getChild(0), view);
            finalPoint.y = startPoint.y;

            if(p.useBranchLenghts)
            {
                finalPoint.x = startPoint.x - getHeightOf(node, node.getChild(0)) * p.lineLenght;
            }
            else
            {
                finalPoint.x = startPoint.x - getScaledHeightOf(node, node.getChild(0)) * p.lineLenght;
            }

            if(view == DYNAMIC_VIEW || view == EXAMPLE_VIEW)
            {
                drawLine(g, startPoint, finalPoint);
            }
            else
            {
                drawJLine(g, node.getChild(0), startPoint, finalPoint, true);
                score = String.valueOf(node.getChild(0).getScore());
            }

            // Visualizzazione degli score sul grafico ?
            if(this.draw_scores)
            {
                g.drawString(score,(int) finalPoint.x+2,(int) finalPoint.y-2);
                score = String.valueOf(node.getScore());
            }

            // Scrittura del sottoalbero destro
            for(int i = 1; i < node.getNumberOfChildren(); i++)
            {
                // Primo punto della linea da tracciare
                startPoint = horizontalDraw(g, p, startPosition, node.getChild(i), view);

                // Ultimo punto della linea da tracciare
                if(p.useBranchLenghts)
                {
                    endPoint.x = startPoint.x - getHeightOf(node, node.getChild(i)) * p.lineLenght;
                }
                else
                {
                    endPoint.x = startPoint.x - getScaledHeightOf(node, node.getChild(i)) * p.lineLenght;
                }

                endPoint.y = startPoint.y;

                // Disegno la linea orizzontale
                if(view == DYNAMIC_VIEW || view == EXAMPLE_VIEW)
                {
                    drawLine(g, startPoint, endPoint);
                }
                else
                {
                    drawJLine(g, node.getChild(i), startPoint, endPoint, true);
                    score = String.valueOf(node.getChild(i).getScore());
                }

                // Visualizzazione degli score sul grafico?
                if(this.draw_scores)
                {
                    g.drawString(score,(int) endPoint.x+2,(int) endPoint.y-2);
                    score = String.valueOf(node.getScore());
                }
            }

            // Piccolo trick/fix :)
            finalPoint.x = endPoint.x;

            // Linea verticale
            drawLine(g, finalPoint, endPoint);

            Point mid = getVerticalMidLenght(finalPoint, endPoint);

            // Draw the root line
            if(node.isRoot())
            {
                Point end = new Point(mid.x - ROOT_HEIGHT, mid.y);
                drawJLine(g, node, mid, end, true);
            }

            // Ritorno la posizione verticale a metà della riga appena tracciata
            return mid;
        }
    }

    // Aggiunge un listener ad una label. Quando questa label viene cliccata,
    // i suoi bordi si colorano
    private void addMouseListenerToTag(final JTag lbl)
    {
        lbl.addMouseListener(new MouseListener()
        {
            public void mouseClicked(MouseEvent e)
            {
                if(currentLbl != null)
                {
                    currentLbl.setBorder(null);
                }

                lbl.setBorder(new LineBorder(SELECTED_COLOR));
                currentLbl = lbl;

                // Deseleziono l'arco selezionato
                if(currentEdgeExists())
                {
                    edges.get(currentEdg).setColor(FOREGROUND_COLOR);
                    currentEdg = -1;
                    repaint();
                }

                // Scrivo le informazioni nel pannello
                experimentPanel.setInformationsOfLanguage(lbl.getTreeNode());
            }

            public void mousePressed(MouseEvent e){}
            public void mouseReleased(MouseEvent e){}
            public void mouseEntered(MouseEvent e){}
            public void mouseExited(MouseEvent e){}
        });
    }

    // Aggiunge il mouselistener agli archi
    private void addMouseListenerToEdges()
    {
        this.addMouseListener(new MouseListener()
        {
            public void mouseClicked(MouseEvent e)
            {                
                // Visualizzazione informazioni dell'arco
                if(e.getButton() == MouseEvent.BUTTON1)
                {
                    currentEdg = getSelectedEdge(e);

                    // Switch mode
                    if(SWITCH_EDGES_MODE)
                    {
                        if(switchSelection.size() <= 2 && currentEdgeExists())
                        {
                            JLine child = edges.get(currentEdg);

                            // Verifico che il figlio sia effettivamente figlio del padre
                            if(isChildOfSelectedParent(child.getNode()))
                            {
                                // Primo nodo
                                if(switchSelection.isEmpty())
                                {
                                    child.setColor(SWITCH_SELECT_COLOR);
                                    switchSelection.add(currentEdg);
                                    repaint();
                                }
                                // Secondo nodo
                                else if(switchSelection.size() == 1 && currentEdg != switchSelection.get(0))
                                {
                                    // Non c'è bisogno di colorare il secondo arco
                                    switchSelection.add(currentEdg);

                                    // A questo punto ho due nodi, quindi posso fare lo switch
                                    try
                                    {
                                        // Padre
                                        PhyloTreeNode parent = edges.get(parentNode).getNode();

                                        // Indici dei figli
                                        int firstNode = getSelectedChild(switchSelection.get(0));
                                        int secondNode = getSelectedChild(switchSelection.get(1));

                                        // Scambio i due figli. Qui viene già fatto il repaint
                                        switchEdges(parent, firstNode, secondNode);
                                    }
                                    catch (CloneNotSupportedException ex)
                                    {
                                        Logger.getLogger(Drawgram.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    // Esco dallo switch mode
                                    parentNode = -1;
                                    switchSelection.clear();
                                    SWITCH_EDGES_MODE = false;
                                }
                                // Esco dalla modalità di switch se lo stesso ramo viene cliccato due volte
                                else if(currentEdg == switchSelection.get(0))
                                {
                                    edges.get(currentEdg).setColor(FOREGROUND_COLOR);

                                    parentNode = -1;
                                    currentEdg = -1;

                                    switchSelection.clear();
                                    SWITCH_EDGES_MODE = false;

                                    repaint();
                                }
                            }
                        }
                    }
                    else
                    {
                        // Se non sono in switch mode visualizzo le informazioni del ramo
                        if(edges.size() == numOfNodes)
                        {
                            if(currentEdgeExists())
                            {
                                // Deseleziono il label selezionato
                                if(currentLbl != null )
                                {
                                    currentLbl.setBorder(null);
                                    currentLbl = null;
                                }

                                edges.get(currentEdg).setColor(SELECTED_COLOR);
                                repaint();

                                experimentPanel.setInformationsOfEdge(edges.get(currentEdg).getNode());
                            }
                        }
                    }
                }
                // Menu di switch
                else if(e.getButton() == MouseEvent.BUTTON3)
                {
                    currentEdg = getSelectedEdge(e);

                    if(edges.size() == numOfNodes)
                    {
                        if(currentEdgeExists())
                        {
                            if(!edges.get(currentEdg).getNode().isLeaf())
                            {
                                edges.get(currentEdg).setColor(RIGHT_CLICK_COLOR);
                                repaint();
                                
                                switchMenu.show(getComponentAt(e.getX(), e.getY()), e.getX(), e.getY());
                            }
                        }
                    }
                }
            }

            public void mousePressed(MouseEvent e){}
            public void mouseReleased(MouseEvent e){}
            public void mouseEntered(MouseEvent e){}
            public void mouseExited(MouseEvent e){}
        });
    }

    // Ritorna true se child è figlio del parente selezionato
    private boolean isChildOfSelectedParent(PhyloTreeNode child)
    {
        return edges.get(parentNode).getNode().getChildren().contains(child);
    }

    // Scambia due figli i e j di un nodo parent
    private void switchEdges(PhyloTreeNode parent, int i, int j)
            throws CloneNotSupportedException
    {
        // Salvo i nodi
        PhyloTreeNode first = (PhyloTreeNode) parent.getChild(i).clone();
        PhyloTreeNode second = (PhyloTreeNode) parent.getChild(j).clone();

        // scambio i nodi
        parent.setChild(j, first);
        parent.setChild(i, second);

        // Visualizzo il nuovo albero
        removeEdges();
        removeLabels();
        repaint();
    }

    /**
     * Ritorna l'indice dell'arco selezionato
     * @param Evento del mouse
     * @return -1 se non è selezionato nessun arco
     */
    private int getSelectedEdge(MouseEvent e)
    {
        int curr = -1;
        if(edges.size() == numOfNodes)
        {
            for(int i = 0; i < edges.size(); i++)
            {
                if(edges.get(i).isSelected(e.getPoint()))
                {
                    curr = i;
                    break;
                }
            }
        }

        return curr;
    }

    /**
     * Ritorna l'indice corrispondente al figlio del padre selezionato
     * @param i Indice del figlio selezionato
     * @return
     */
    private int getSelectedChild(int i)
    {
        PhyloTreeNode parent = edges.get(parentNode).getNode();
        return parent.getChildren().indexOf(edges.get(i).getNode());
    }

    // True se è presente un arco selezionato
    private boolean currentEdgeExists()
    {
        if(currentEdg != -1)
            if(currentEdg < edges.size())
                if(edges.get(currentEdg) != null)
                    return true;

        return false;
    }

    // Ritorna l'altezza riscalata per i nodi dell'albero che vanno disegnati
    private static int getScaledHeightOf(PhyloTreeNode parent, PhyloTreeNode node)
    {
        return parent.getScaledHeight() - node.getScaledHeight();
    }

    // Ritorna la distanza dai due nodi, normalizzata se negativi
    private static double getHeightOf(PhyloTreeNode parent, PhyloTreeNode node)
    {
        return node.getHeight() - parent.getHeight();
    }

    // Disegna una linea
    private static void drawLine(Graphics2D g, Point a, Point b)
    {
        g.draw(new Line2D.Double(a.x, a.y, b.x, b.y));
    }

    // Disegna un arco dell'albero
    private void drawJLine(Graphics2D g2, PhyloTreeNode node, Point a, Point b, boolean horizontal)
    {
        Line2D line2d = new Line2D.Double(a.x, a.y, b.x, b.y);
        JLine line = null;

        // Se sono in switch mode coloro padre e figli
        if(SWITCH_EDGES_MODE)
        {
            // Padre
            if(edges.get(parentNode).equalsToLine(line2d))
            {
                line = edges.get(parentNode);
            }
            // Primo figlio
            else if(switchSelection.size() == 1 &&
                    edges.get(switchSelection.get(0)).equalsToLine(line2d))
            {
                line = edges.get(switchSelection.get(0));
            }
            // Non c'è bisogno di considerare il secondo figlio
            // perché viene fatto prima lo switch
            else // Altrimenti è una linea normale
            {
                line = new JLine(node, line2d, FOREGROUND_COLOR, horizontal);
            }
        }
        else
        {
            // Prelevo il colore
            if(currentEdgeExists() && edges.get(currentEdg).equalsToLine(line2d))
            {
                line = edges.get(currentEdg);
            }
            else
            {
                line = new JLine(node, line2d, FOREGROUND_COLOR, horizontal);
            }
        }

        // Disegno la linea
        g2.setColor(line.getColor());
        g2.draw(line.getLine());
        g2.setColor(FOREGROUND_COLOR);

        if(edges.size() < numOfNodes)
        {
            this.edges.add(line);
        }
    }

    private static Point getHorizontalMidLenght(Point a, Point b){return new Point(a.x + (b.x-a.x)/2,a.y);}
    private static Point getVerticalMidLenght(Point a, Point b){return new Point(a.x,a.y + (b.y-a.y)/2);}


    /* **************
     * DRAW AS PNG  *
     ****************/

    /**
     * Scrive l'albero in un file
     * @param filename Directory del file da salvare
     * @throws IOException
     */
    public void drawToFileAsPng(String filename)
            throws IOException
    {
        TreeDrawingParameters param = new TreeDrawingParameters();
        param.useBranchLenghts = p.useBranchLenghts;
        param.viewScores = p.viewScores;

        // Altezza e base.
        // Sono valori indicativi per avere una grandezza statica dell'immagine
        int h = 532;
        int w = 1068;
        scaleTree(param, h, w);

        // Immagine da creare
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        
        Graphics2D g2 = img.createGraphics();
        g2.setBackground(BACKGROUND_COLOR);
        g2.setColor(FOREGROUND_COLOR);

        // Scrivo l'albero nell'immagine
        g2.setFont(this.usedFonts);
        g2.setStroke(new BasicStroke(param.lineWidth));

        // punto da cui compinciare a disegnare
        Point starting_point;

        // Visualizzo l'albero
        if(phyloCfg.HorizontalTree)
        {
            if(param.useBranchLenghts)
                starting_point = new Point((int) (param.marginSize + this.getTreeHeight(param)), param.marginSize);
            else
                starting_point = new Point((int) (param.marginSize + this.getScaledTreeHeight(param)), param.marginSize);

            // Albero in orizzontale
            this.horizontalDraw(g2, param, starting_point, tree.getRoot(), DYNAMIC_VIEW);
        }
        else
        {
            if(param.useBranchLenghts)
                starting_point = new Point(param.marginSize,(int) (param.marginSize + this.getTreeHeight(param)));
            else
                starting_point = new Point(param.marginSize,(int) (param.marginSize + this.getScaledTreeHeight(param)));

            // Albero in verticale
            this.verticalDraw(g2, param, starting_point, tree.getRoot(), DYNAMIC_VIEW);
        }

        // Output file
        File outfile = new File(filename);
        ImageIO.write(img, "png", outfile);

        Debugger.println("> Saved tree in .png format "+outfile.getAbsolutePath());
    }

    /* *************
     * SAVE AS PDF *
     ***************/
    
    /**
     * Scrive l'albero in un file pdf in cui vengono specificati 
     * nome dell'esperimento, data, metodo usato e algoritmo
     * @param pdfFile File pdf che si vuole salvare
     * @throws DocumentException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void drawToFileAsPdf(String pdfFile)
            throws DocumentException, FileNotFoundException, IOException
    {
        // Documento in cui scrivere
        Document pdf = new Document();
        PdfWriter.getInstance(pdf, new FileOutputStream(pdfFile));

        // Apro il file
        pdf.open();
        pdf.addAuthor(Infos.PHYLO_NAME+"-"+Infos.PHYLO_VERSION);
        pdf.addCreationDate();


        // Inserisco le informazioni dell'esperimento
        Experiment exp = experimentPanel.getSelectedExperiment();
        pdf.addTitle(exp.getName()+" experiment");

        // Data
        Calendar cal = new GregorianCalendar();
        String date = cal.get(Calendar.DAY_OF_MONTH)+"-"+
                      cal.get(Calendar.MONTH)+"-"+
                      cal.get(Calendar.YEAR);

        // Informazioni dell'esperimento
        pdf.add(new Paragraph("Date: "+date));
        pdf.add(new Paragraph("Experiment: "+exp.getName()));
        pdf.add(new Paragraph("Method: "+exp.getExperimentName()));
        pdf.add(new Paragraph("Algorithm: "+exp.getAlgorithmName()));

        // Inserisco il file immagine dell'albero
        File png = new File(Infos.TEMPORARY_PATH, "out.png");
        this.drawToFileAsPng(png.getAbsolutePath());
        Image img = Image.getInstance(png.getAbsolutePath());
        img.scalePercent(50);
        pdf.add(img);

        // Chiudo il documento ed elimino l'immagine
        pdf.close();
        png.delete();
        
        Debugger.println("> Saved tree in .pdf format "+pdfFile);
    }

    /* ***********************
     * SAVE AS NEWICK FORMAT
     *************************/

    /**
     * Scrive l'albero in un file nwk
     * @param nwkFile File in cui salvare
     * @throws IOException
     */
    public void drawToFileAsNwk(String nwkFile)
            throws IOException
    {
        TreeIntoNewickFormat nwk = new TreeIntoNewickFormat(tree);
        nwk.drawToFileAsNwk(nwkFile);
    }
}
