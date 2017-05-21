package algorithm.phylip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contiene gli insiemi tenuti in considerazione dall'esecuzione del programma
 * consense in phylip.
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class ConsensusSets
{
    // File contenente le informazioni sul consensus tree
    private File file;

    // Linguaggi in ordine di consenso
    private ArrayList<String> languages = new ArrayList<String>();

    // Numero di volte in cui l'insieme di lingue compare
    private ArrayList<String> times = new ArrayList<String>();

    // Matrice di consenso:
    // righe = insieme
    // colonne = linguaggio
    private ArrayList<ArrayList<Character>> consenseMatrix
            = new ArrayList<ArrayList<Character>>();

    // Caratteri usati per descrivere la presenza di un linguaggio in un insieme
    private static char included = '*';

    /**
     * Parsa l'outfile di consense prelevando la lista dei linguaggi in
     * ordine e generando la matrice di consenso
     * @param outfile File di output di consense
     * @throws IOException Eventuali eccezioni sul file
     */
    public ConsensusSets(File outfile)
            throws IOException
    {
        this.file = outfile;
        parseLanguages();
        parseConsenseMatrix();
    }

    // Estrae le lingue ordinate
    private void parseLanguages()
            throws IOException
    {
        String line = null;
        String startLang = "Species in order: ";
        String stopLang = "Sets included";
        BufferedReader read = new BufferedReader(new FileReader(file));

        // Raggiungo la linea da cui partire a leggere le lingue
        while(!(line = read.readLine()).contains(startLang)){}

        // Inizio a leggere i linguaggi
        while(!(line = read.readLine()).contains(stopLang))
        {
            // Non considero gli a capo
            if(line.matches("[\\s]*")) continue;

            // Leggo i linguaggi
            String[] lang = line.split("[\\.]");
            lang[1] = lang[1].trim();
            languages.add(lang[1]);
        }
        
        // CHIUDI SEMPRE PERDIO !!
        read.close();
    }

    // Estrae la matrice di consenso
    private void parseConsenseMatrix()
            throws IOException
    {
        String line;
        
        String startMatrix = "Set (species in order)";
        String stopMatrix = "Sets NOT included";
        String setsRegexp = "[[\\.\\*]*[\\s]*]*";
        String setsTimesRegexp = "[0-9]+[\\.][0-9]+";

        Pattern setsPat = Pattern.compile(setsRegexp);
        Pattern setsTimesPat = Pattern.compile(setsTimesRegexp);
        Matcher m;

        int j = 0;

        BufferedReader read = new BufferedReader(new FileReader(file));

        // Vado avanti con la lettura fino a trovare la matrice
        while(!(line = read.readLine()).contains(startMatrix)) {}

        // Inizio a leggere la matrice
        while(!(line = read.readLine()).contains(stopMatrix))
        {
            // Non considero gli a capo
            if(line.matches("[\\s]*")) continue;

            // Leggo l'insieme delle lingue che compaiono nel consensus tree
            m = setsPat.matcher(line);
            if(m.find())
            {
                // Prelevo la sottostringa della matrice
                int start = m.start();
                int end   = m.end();
                String set = line.substring(start, end);

                // Creo la matrice
                consenseMatrix.add(new ArrayList<Character>());

                for(int i = 0; i < set.length(); i++)
                {
                    char c = line.charAt(i);
                    if(c == ' ') continue;
                    
                    consenseMatrix.get(j).add(c);
                }
                j++;
            }

            // Leggo il numero di volte che compaiono gli insiemi di lingue
            m = setsTimesPat.matcher(line);
            if(m.find())
            {
                // Prelevo i valori
                int start = m.start();
                int end   = m.end();
                String time = line.substring(start, end);
                
                // Salvo il valore
                times.add(time);
            }
        }

        // CHIUDI SEMPRE PERDIO !!
        read.close();
    }

    /**
     * Ritorna gli indici degli insiemi in cui è contenuto il linguaggio
     * specificato
     * @param language Linguaggio che si vuole cercare negli insiemi
     * @return Indici degli insiemi che contengono la lingua spegificata
     */
    public ArrayList<Integer> getSetsIndexesOfLanguage(String language)
    {
        ArrayList<Integer> sets = new ArrayList<Integer>();
        int langIndex = languages.indexOf(language);

        for(int i = 0; i < consenseMatrix.size(); i++)
        {
            char c = consenseMatrix.get(i).get(langIndex);
            if(c == included)
            {
                sets.add(i);
            }
        }

        return sets;
    }

    /**
     * Ritorna la lista dei linguaggi nell'insieme con indice specificato
     * @param index Indice dell'insieme
     * @return Lista dei linguaggi in un insieme
     */
    public ArrayList<String> getSet(int index)
    {
        ArrayList<String> set = new ArrayList<String>();

        for(int i = 0; i < languages.size(); i++)
        {
            char c = consenseMatrix.get(index).get(i);
            if(c == included)
            {
                set.add(languages.get(i));
            }
        }
        
        return set;
    }

    /**
     * Ritorna il numero di volte in cui l'insieme compare nell'albero di consenso
     * @param index Indice dell'insieme
     * @return Volte in cui l'insieme compare nell'albero di consenso
     */
    public String getNumberOfTimesForSet(int index)
    {
        return times.get(index);
    }
}
