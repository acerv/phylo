package experiment;

import bootstrap.Bootstrapper;
import distance.Distance;
import java.io.IOException;
import matrix.exception.LanguageException;
import matrix.exception.MatrixFormatException;
import matrix.reader.CharMatrixReader;
import matrix.reader.DistMatrixReader;

/**
 * Clone an experiment renaming its own name
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class CloneExperiment {
    static public Experiment clone(Experiment exp)
        throws IOException, MatrixFormatException, 
        LanguageException, CloneNotSupportedException
    {
        Experiment clone = null;

        if(exp instanceof ExperimentBootstrap) {
            clone = (Experiment) exp.clone();

            ExperimentBootstrap boot = (ExperimentBootstrap)clone;
            Bootstrapper bootstrapper = boot.getBootstrapper();
            CharMatrixReader matrix = (CharMatrixReader) boot.getCharMatrix().clone();

            boot.mkNewExperiment(exp.getName(), bootstrapper, matrix);
        }
        else if(exp instanceof ExperimentCalcDistance) {
            clone = (Experiment) exp.clone();

            ExperimentCalcDistance dist = (ExperimentCalcDistance)clone;
            Distance distance = dist.getDistanceAlgorithm();
            CharMatrixReader matrix = (CharMatrixReader) dist.getCharMatrix().clone();

            dist.mkNewExperiment(exp.getName(), distance, matrix);
        }
        else if(exp instanceof ExperimentLoadDistance) {
            clone = (Experiment) exp.clone();

            ExperimentLoadDistance load = (ExperimentLoadDistance)clone;
            DistMatrixReader matrix = (DistMatrixReader) load.getDistMatrix().clone();

            load.mkNewExperiment(exp.getName(), matrix);
        }
        else if(exp instanceof GenericExperiment) {
            clone = new GenericExperiment(exp.getPhyloConfig(), exp.getName());
            clone.setInput(exp.getInput());
            clone.setAlgorithm(exp.getAlgorithm());
        }

        return clone;
    }
}
