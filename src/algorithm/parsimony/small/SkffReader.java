package algorithm.parsimony.small;

import algorithm.parsimony.big.BabReader;
import informations.Infos;
import parser.set.declare.LanguageInformations;
import parser.set.langcosts.CostsParser;
import parser.set.langrules.RuleParser;
import java.io.File;
import java.util.Collections;

/**
 * Read a file in the .skff format and initialize all public istances used
 * by Sankoff-Extended algorithm
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class SkffReader extends BabReader
{
    public LabeledTree tree;
    static public String EXT_SKFF = ".sfkk";
    static public String TREE_FILE = "tree.txt";

    boolean USE_RULES = true;

    public void setUseRules(boolean useRules)
    {
        USE_RULES = useRules;
    }

    @Override
    public void load() throws Exception
    {
        // Extract files
        File tmp = extract();

        // Tree & Matrix
        File tFile = new File(tmp,TREE_FILE);
        File mFile = new File(tmp,MATRIX_FILE);
        LabeledTree t = new LabeledTree();
        t.loadFromFile(tFile, mFile);
        tree = t;
        matrix = tree.getMatrix();

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

        if(USE_RULES)
        {
            // Rules & Costs
            rules = RuleParser.parse(infos,rulesPath+RULES_FILE);
            rules.checkConsistencyOfMatrix(matrix);
            costs = CostsParser.parse(infos, rulesPath+COSTS_FILE);
        }

        // Delete extracted files
        delExtractedPath();
    }
}
