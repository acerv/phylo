package algorithm.phylip.configuration;

import algorithm.phylip.Kitsch;

/**
 * Configurazione di Kitsch
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class KitschConfiguration
{
    /** Metodo di Fitch. Può valere:<br>
     * - FITCH_MARGOLIASH_METHOD <br>
     * - MINIMUM_EVOLUTION_METHOD <br>
     */
    public int method = Kitsch.FITCH_MARGOLIASH_METHOD;

    /** Power */
    public double power = 2;

    /** Seed (dispari) e Times */
    public boolean randomizeInputOrderTimes = false;

    /** Numero di matrici da analizzare */
    public int multipleDataSets = 0;
}
