package utility;

import java.util.Random;

/**
 * Genera un numero casuale dispari compreso fra 0 e un massimo
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class GenerateOddNumber
{
    /**
     * Ritorna un numero dispari random incluso fra 0 e il max
     * @param max Limite superiore al numero che si vuole generare
     * @return Numero casuale dispari compreso fra 0 e max
     */
    static public int get(int max)
    {
        Random random = new Random();

        // Numero dispari casuale
        return random.nextInt(max)*2 - 1 ;
    }
}
