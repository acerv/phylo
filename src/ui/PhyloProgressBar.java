package ui;

import experiment.Experiment;
import informations.Infos;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PhyloProgressBar
{
    /* Bar with cancel button ? */
    boolean proc_bar = true;

    private ProgressBar bar;
    private JProgress bar1;
    private SwingWorker progressbar;

    public PhyloProgressBar(String something, boolean proc_bar)
    {
        if(proc_bar) {
            bar1 = new JProgress(Infos.getPhyloForm(), false);
            bar1.setExecutingText(something);
        }
        else {
            bar  = new ProgressBar();
            bar.setExecutingText(something);
            progressbar = new SwingWorker()
            {
                @Override
                public Object construct()
                {
                    bar.setProgressBar(true);
                    bar.setVisible(true);
                    return "done";
                }
            };
        }

        this.proc_bar = proc_bar;
    }

    public void startBar()
    {
        progressbar.start();
    }

    public void startBar(Experiment exp)
            throws Throwable
    {
        if(proc_bar) {
            bar1.setLocationRelativeTo(Infos.getPhyloForm());
            bar1.setVisible(true);
            bar1.setExperiment(exp);
            bar1.start();
        } else {
            progressbar.start();
            exp.exec();
        }
    }

    public void stopBar()
    {
        if(!proc_bar) {
            bar.setProgressBar(false);
            bar.setVisible(false);
        }
    }

    ActionListener interruptListener = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            progressbar.interrupt();
        }
    };
}
