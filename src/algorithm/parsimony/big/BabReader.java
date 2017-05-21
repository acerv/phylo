package algorithm.parsimony.big;

import files.utility.DeletePath;
import files.utility.ZipUtility;
import informations.Infos;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import matrix.reader.CharMatrixReader;
import parser.set.declare.LanguageInformations;
import parser.set.langcosts.CostsParser;
import parser.set.langcosts.table.ParametersSet;
import parser.set.langrules.RuleParser;
import parser.set.langrules.tree.RulesSet;

/**
 *
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class BabReader
{
    final static private String EXT_FILES = ".txt";

    static public String RULES_PATH = "rules";
    static public String EXT_BAB = ".babs";
    static public String MATRIX_FILE = "matrix"+EXT_FILES;
    static public String ALPH_FILE = "alphabet"+EXT_FILES;
    static public String RULES_FILE = "rules"+EXT_FILES;
    static public String COSTS_FILE = "costs"+EXT_FILES;

    protected File inputFile;
    protected File dest;

    public CharMatrixReader matrix;
    public LanguageInformations infos;
    public RulesSet rules;
    public ParametersSet costs;

    public void setInputFile(File inputFile)
    {
        this.inputFile = inputFile;

        // Destination of files after extract
        File path = new File(Infos.TEMPORARY_PATH);
        dest = new File(path, "bab_phylo_temp");
    }

    /**
     * Extract this file into temporary directory
     * @return Extracted path
     * @throws IOException
     */
    public File extract() throws IOException
    {
        dest.mkdir();
        ZipUtility.unzip(inputFile, dest);
        return dest;
    }

    /**
     * Delete path with extracted files made by extract() method
     * @throws FileNotFoundException
     */
    public void delExtractedPath()
            throws FileNotFoundException
    {
        DeletePath.doDelete(dest);
    }
    /**
     *
     * @param file
     * @throws Exception
     */
    public void load() throws Exception
    {
        // Extract files
        File tmp = extract();

        // Matrix
        File mFile = new File(tmp,MATRIX_FILE);
        matrix = new CharMatrixReader();
        matrix.loadMatrixFromFile(mFile);

        // Rules path
        String rulesPath = tmp.getAbsolutePath()+Infos.FILE_SEPARATOR+RULES_PATH+Infos.FILE_SEPARATOR;

        // Alphabet
        infos = new LanguageInformations(rulesPath+ALPH_FILE);

        // Position of characters into the language array must be the same
        int numOfChars = 0;
        for(int i = 0; i < infos.getAlphabet().size(); i++)
            if(matrix.getAlphabet().contains(infos.getAlphabet().get(i)))
                numOfChars++;

        if(numOfChars != infos.getAlphabet().size())
        {
            throw new Exception("BabFile error: matrix and information language "
                    + "files have different defined alphabet");
        }
        else
        {
            Collections.copy(infos.getAlphabet(), matrix.getAlphabet());
        }
        
        // Rules & Costs
        rules = RuleParser.parse(infos,rulesPath+RULES_FILE);
        rules.checkConsistencyOfMatrix(matrix);        
        costs = CostsParser.parse(infos, rulesPath+COSTS_FILE);

        // Delete extracted files
        delExtractedPath();
    }
}
