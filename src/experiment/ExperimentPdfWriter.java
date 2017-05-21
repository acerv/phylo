package experiment;



import algorithm.GenericAlgorithm;
import algorithm.phylip.configuration.ConsenseConfiguration;
import algorithm.phylip.configuration.FitchConfiguration;
import algorithm.phylip.configuration.KitschConfiguration;
import algorithm.phylip.configuration.NeighborConfiguration;
import algorithm.phylip.Consense;
import algorithm.phylip.Fitch;
import algorithm.phylip.Kitsch;
import algorithm.phylip.Neighbor;
import algorithm.phylip.Phylip;
import informations.Infos;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import matrix.reader.CharMatrixReader;
import matrix.reader.DistMatrixReader;
import matrix.reader.MatrixReader;

/**
 * Scrive un file pdf per l'esperimento specificato nel costruttore
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class ExperimentPdfWriter
{
    // FONTS
    Font defaultFont = new Font(Font.getFamily(BaseFont.COURIER), 9);
    Font defFontBold = new Font(Font.getFamily(BaseFont.COURIER_BOLDOBLIQUE), 9);
    java.awt.Font monospacedNormal = new java.awt.Font("Monospaced", Font.NORMAL, 14);
    java.awt.Font monospacedBold = new java.awt.Font("Monospaced", Font.BOLD, 14);

    Document doc;
    Experiment exp;
    String name;
    String date;
    String method;
    String algorithm;
    String tree_algorithm;
    MatrixReader matrix;
    int typeOfExperiment;
    static int MAX_WIDTH = 540;
    static int MIN_WIDTH = 200;

    /**
     * Inizializza lo scrittore del pdf per l'esperimento specificato
     * @param exp Esperimento da cui estrarre le informazioni
     * @param pdf File in cui salvare il file pdf
     * @throws DocumentException
     * @throws FileNotFoundException
     */
    public ExperimentPdfWriter(Experiment exp, File pdf)
            throws DocumentException, FileNotFoundException
    {
        this.exp = exp;
        doc = new Document();
        name = exp.getName();
        date = exp.getDate();
        method = exp.getExperimentName();
        algorithm = exp.getAlgorithmName();
        typeOfExperiment = exp.getExperimentType();

        // Matrix
        if(exp instanceof ExperimentBootstrap)
            matrix = ((ExperimentBootstrap)exp).getCharMatrix();
        else if(exp instanceof ExperimentCalcDistance)
            matrix = ((ExperimentCalcDistance)exp).getCharMatrix();
        else if(exp instanceof ExperimentLoadDistance)
            matrix = ((ExperimentLoadDistance)exp).getDistMatrix();
        else
            matrix = null;

        if(exp instanceof ExperimentBootstrap)
            tree_algorithm = "consense";
        else
            tree_algorithm = "default";

        PdfWriter.getInstance(doc, new FileOutputStream(pdf));
    }

    /**
     * Apre il documento pdf
     */
    public void openDocument()
    {
        doc.open();
        doc.addAuthor(Infos.PHYLO_NAME+"-"+Infos.PHYLO_VERSION);
        doc.addCreationDate();
    }

    /**
     * Chiude il documento pdf
     */
    public void closeDocument()
    {
        doc.close();
    }

    /**
     * Scrive le informazioni sull'esperimento
     * @throws DocumentException
     */
    public void printExperimentInfo()
            throws DocumentException
    {
        doc.add(new Paragraph("Date: "+date+
                            "\nExperiment: "+name+
                            "\nMethod: "+method+
                            "\nAlgorithm: "+algorithm,defaultFont));
    }

    String outgroup = "";
    /**
     * Scrive le informazioni dull'algoritmo
     * @throws DocumentException
     */
    public void printAlgorithmInfo()
            throws DocumentException
    {
        // Algoritmo phylip ?
        if(exp.getAlgorithm() instanceof Phylip)
        {
            doc.add(new Paragraph("\nPhylip Algorithm:",defFontBold));

            if(exp.getAlgorithm() instanceof Fitch)
            {
                FitchConfiguration cfg = ((Fitch)exp.getAlgorithm()).getConfiguration();

                if(cfg.outgroupSpece != 0)
                {
                    if(matrix != null)
                    {
                        if(typeOfExperiment == Infos.LOAD_DISTANCE_EXP)
                            outgroup = ((DistMatrixReader) matrix).getLanguages().get(cfg.outgroupSpece-1);
                        else
                            outgroup = ((CharMatrixReader) matrix).getLanguages().get(cfg.outgroupSpece-1);
                    }
                }
                else
                {
                    outgroup = "No outgroup";
                }
                
                String methodName = "";
                
                if(cfg.method == Fitch.FITCH_MARGOLIASH_METHOD)
                    methodName = "Fitch-Margoliash";
                else
                    methodName = "Minimum Evolution";

                doc.add(new Paragraph(
                        "Method: "+methodName+
                        "\nPower: "+cfg.power+
                        "\nOutgroup: "+outgroup+
                        "\nRandomize: "+cfg.randomizeInputOrderTimes+
                        "\nData Sets: "+cfg.multipleDataSets,defaultFont));
            }
            else if(exp.getAlgorithm() instanceof Kitsch)
            {
                KitschConfiguration cfg = ((Kitsch)exp.getAlgorithm()).getConfiguration();
                String methodName = "";
                
                if(cfg.method == Kitsch.FITCH_MARGOLIASH_METHOD)
                    methodName = "Fitch-Margoliash";
                else
                    methodName = "Minimum Evolution";

                doc.add(new Paragraph(
                        "Method: "+methodName+
                        "\nPower: "+cfg.power+
                        "\nRandomize: "+cfg.randomizeInputOrderTimes+
                        "\nData Sets: "+cfg.multipleDataSets,defaultFont));
            }
            else if(exp.getAlgorithm() instanceof Neighbor)
            {
                NeighborConfiguration cfg =((Neighbor)exp.getAlgorithm()).getConfiguration();
                String treeType = "";

                if(cfg.outgroupSpece != 0)
                {
                    if(matrix != null)
                    {
                        if(typeOfExperiment == Infos.LOAD_DISTANCE_EXP)
                            outgroup = ((DistMatrixReader) matrix).getLanguages().get(cfg.outgroupSpece-1);
                        else
                            outgroup = ((CharMatrixReader) matrix).getLanguages().get(cfg.outgroupSpece-1);
                    }
                }
                else
                {
                    outgroup = "No outgroup";
                }

                if(cfg.tree == Neighbor.NEIGHBOR_TREE)
                {
                    treeType = "Neighbor";
                    doc.add(new Paragraph(
                            "Tree type: "+treeType+
                            "\nOutgroup: "+outgroup+
                            "\nRandomize: "+cfg.randomizeInput+
                            "\nData Sets: "+cfg.multipleDataSets,defaultFont));
                }
                else
                {
                    treeType = "UPGMA";
                    doc.add(new Paragraph(
                            "Tree: "+treeType+
                            "\nRandomize: "+cfg.randomizeInput+
                            "\nData Sets: "+cfg.multipleDataSets,defaultFont));
                }
            }
        }
        else
        {
            /* Inserire qui eventuali parametri da aggiungere */
            doc.add(new Paragraph("Algorithm: "+exp.getAlgorithmName(),defaultFont));
        }

        // Consense
        if(exp instanceof ExperimentBootstrap)
        {
            ConsenseConfiguration cfg = ((ExperimentBootstrap)exp).getConsense().getConfiguration();
            int consType = cfg.consensusType;
            String type = "";
            if(consType == Consense.MAJORITY_RULE_EXTENDED)
                type = "Majority Rule Extended";
            else if(consType == Consense.MAJORITY_RULE)
                type = "Majority Rule";
            else if(consType == Consense.ML_CONSENSUS)
                type = "ML";
            else if(consType == Consense.STRICT)
                type = "strict";

            doc.add(new Paragraph("\nConsense:", defFontBold));
            doc.add(new Paragraph(
                    "Tree algorithm: "+tree_algorithm+
                    "\nConsensus Type: "+type+
                    "\nOutgroup: "+outgroup+
                    "\nRooted: "+cfg.rooted,defaultFont));
        }
    }

    /**
     * Scrive l'immagine dell'albero nel documento pdf
     * @param png Immagine dell'albero
     * @throws DocumentException
     * @throws BadElementException
     * @throws MalformedURLException
     * @throws IOException
     */
    public void printTreeInfo(File png)
            throws DocumentException, BadElementException, MalformedURLException, IOException
    {
        doc.add(new Paragraph("\n"));
        Image img = Image.getInstance(png.getAbsolutePath());
        scaleImage(img);
        doc.add(img);
    }

    /**
     * Scrive l'immagine della matrice dei caratteri nel documento pdf
     * @param png Immagine della matrice dei caratteri
     * @throws BadElementException
     * @throws IOException
     * @throws DocumentException
     */
    public void printCharMatrixInfo(File png)
            throws BadElementException, IOException, DocumentException
    {
        if(matrix != null)
        {
            doc.newPage();        
            doc.add(new Paragraph("\nCharacter matrix:\n", defFontBold));
            Image img = Image.getInstance(png.getAbsolutePath());
            scaleImage(img);
            doc.add(img);
        }
    }

    private void scaleImage(Image img)
    {
        if (img.getScaledWidth() >= MAX_WIDTH)
        {
            int scale = (int) ((MAX_WIDTH / img.getScaledWidth()) * 100);
            img.scalePercent(scale);
        }
    }

    /**
     * Scrive l'immagine della matrice delle distanze nel documento pdf
     * @param png Immagine della matrice delle distanze
     * @throws BadElementException
     * @throws IOException
     * @throws DocumentException
     */
    public void printDistMatrixInfo(File png)
            throws BadElementException, IOException, DocumentException
    {
        if(matrix != null)
        {
            doc.newPage();
            doc.add(new Paragraph("\n\nDistance matrix:\n", defFontBold));
            Image img = Image.getInstance(png.getAbsolutePath());
            scaleImage(img);
            doc.add(img);
        }
    }

    /**
     * Scrive l'outfile di phylip in una nuova pagina del pdf
     * @param outfile Outfile di phylip
     * @throws IOException
     * @throws DocumentException
     */
    public void printPhylipOutFile(File outfile)
            throws IOException, DocumentException
    {
        BufferedReader bread = new BufferedReader(new FileReader(outfile));
        doc.newPage();
        doc.add(new Paragraph("\n\nPhylip outfile:\n", defFontBold));

        String line = "";
        while((line = bread.readLine()) != null)
        {
            doc.add(new Paragraph(line, defaultFont));
        }
    }

    /**
     * Scrive l'outfile dell'algoritmo usato in una nuova pagina del pdf
     * @param outfile outfile dell'algoritmo usato
     * @throws IOException
     * @throws DocumentException
     */
    public void printAlgorithmOutput(File outfile)
            throws IOException, DocumentException
    {
        BufferedReader bread = new BufferedReader(new FileReader(outfile));
        doc.newPage();
        doc.add(new Paragraph("\n\nAlgorithm output file:\n", defFontBold));

        String line = "";
        while((line = bread.readLine()) != null)
        {
            doc.add(new Paragraph(line, defaultFont));
        }
    }

    /**
     * Esporta l'esperimento selezionato in pdf
     * @param tree 
     * @param exp Esperimento da salvare come pdf
     * @param pdf File in cui salvare
     * @throws DocumentException
     * @throws IOException
     */
    public void exportExperimentAsPdf(File tree, Experiment exp, File pdf)
            throws DocumentException, IOException
    {
        openDocument();
        printExperimentInfo();
        printAlgorithmInfo();

        // Inserisco il file immagine dell'albero
        printTreeInfo(tree);

        // Esperimenti conosciuti
        if(exp.getExperimentType() <= Infos.LOAD_DISTANCE_EXP)
        {
            if(matrix != null)
            {
                File matrixImg = saveMatrixAsImage(matrix);

                if(exp instanceof ExperimentBootstrap || exp instanceof ExperimentCalcDistance)
                    printCharMatrixInfo(matrixImg);
                else if(exp instanceof ExperimentLoadDistance)
                    printDistMatrixInfo(matrixImg);

                matrixImg.delete();
            }

            // Inserisco l'output di phylip
            GenericAlgorithm expAlgorithm = exp.getAlgorithm();
            if(expAlgorithm instanceof Phylip)
            {
                if(exp instanceof ExperimentBootstrap)
                {
                    int return_int = JOptionPane.showConfirmDialog(null,
                            "The experiment uses consense phylip algorithm.\n"+
                            "Adding consense output into pdf file could generate a file too big.\n\n"+
                            "Are you sure you want to add consense output?",
                            "Warning", JOptionPane.YES_NO_OPTION);

                    if(return_int == JOptionPane.YES_OPTION)
                        printPhylipOutFile(((ExperimentBootstrap)exp).getConsense().getOutfile());
                }
                else
                {
                    printPhylipOutFile(((Phylip)expAlgorithm).getOutfile());
                }
            }
        }
        else
        {
            /* Inserire qui eventuali supporti per nuovi esperimenti */
        }

        // Chiudo il documento
        closeDocument();
    }

    // salva la matrice dei caratteri in una immagine
    private File saveMatrixAsImage(MatrixReader reader) throws IOException
    {
        FontRenderContext frc = new FontRenderContext(null, false, false);
        String space = " ";

        // Cerco il linguaggio con nome più lungo
        ArrayList<String> languages = (ArrayList<String>) reader.getLanguages();
        int width = 0;
        int maxStringLenght = 0;
        for(int i = 0; i < languages.size(); i++)
        {
            int max = (int) monospacedNormal.getStringBounds(languages.get(i), frc).getWidth();
            if(maxStringLenght < max) maxStringLenght = max;
        }

        // Considero i tab
        width = maxStringLenght + (int) monospacedNormal.getStringBounds(space,frc).getWidth();

        // Considero la lunghezza di un carattere nella matrice
        String common = Infos.UNDEFINED_CHAR+space;
        int lenghtComm = (int) monospacedNormal.getStringBounds(common, frc).getWidth();
        ArrayList<ArrayList<String>> arrMatrix = (ArrayList<ArrayList<String>>) reader.getMatrix();
        width = width + lenghtComm*arrMatrix.get(0).size();

        // Locazione dell'immagine
        String tmp = Infos.TEMPORARY_PATH;
        String img = "img1.png";

        // Altezza di un carattere
        String example = Infos.UNDEFINED_CHAR;
        int exmpHeight =  (int) monospacedNormal.getStringBounds(example, frc).getHeight();
        int height =  exmpHeight;
        height = height * languages.size() + 1;

        // Creo l'immagine
        BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bimg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(monospacedNormal);

        // Background e foreground
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.setColor(Color.BLACK);

        // Disegno la matrice
        String line = "";
        int f_height = exmpHeight;
        int pos = 0;
        int spaceLenght = (int) monospacedNormal.getStringBounds(space, frc).getWidth();
        for(int i = 0; i < arrMatrix.size(); i++)
        {
            // Scrivo il nome della lingua
            pos += f_height;
            g2.setFont(monospacedBold);
            g2.drawString(languages.get(i), 0, pos);
            g2.setFont(monospacedNormal);

            // Scrivo i caratteri della lingua
            for(int j = 0; j < arrMatrix.get(0).size(); j++)
            {
                line = line + arrMatrix.get(i).get(j) + " ";
            }
            
            g2.drawString(line, maxStringLenght + spaceLenght, pos);
            line = "";
        }

        // Salvo l'immagine
        File outputfile = new File(tmp, img);
        ImageIO.write(bimg, "png", outputfile);

        return outputfile;
    }
}
