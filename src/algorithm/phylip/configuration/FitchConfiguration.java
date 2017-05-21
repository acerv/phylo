package algorithm.phylip.configuration;

import algorithm.phylip.Fitch;

/**
 * Configurazione di fitch
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class FitchConfiguration
{
    /** Metodo di Fitch. Può valere:<br>
     * - FITCH_MARGOLIASH_METHOD <br>
     * - MINIMUM_EVOLUTION_METHOD <br>
     */
    public int method = Fitch.FITCH_MARGOLIASH_METHOD;

    /** Power */
    public double power = 2;

    /** Specie da non valutare */
    public int outgroupSpece = 0;

    /** Seed (dispari) e Times */
    public boolean randomizeInputOrderTimes = false;

    /** Numero di matrici da analizzare */
    public int multipleDataSets = 0;
}
