package algorithm.phylip.configuration;

/**
 * Configurazione di consense
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class ConsenseConfiguration
{
    /** Tipi di consensus tree:<br>
     * - MAJORITY_RULE_EXTENDED<br>
     * - STRICT<br>
     * - MAJORITY_RULE<br>
     * - ML_CONSENSUS<br>
     */
    public int consensusType = 0;

    /** Specie da non valutare (solo in caso di Trees to be treated as Rooted:  Yes) */
    public int outgroupSpece = 0;

    /** Albero da trattare come radicato ?*/
    public boolean rooted = false;
}
