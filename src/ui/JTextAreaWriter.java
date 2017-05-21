package ui;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;

/**
 *
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class JTextAreaWriter extends OutputStream
{
    JTextArea txtArea;

    public JTextAreaWriter(JTextArea txtArea)
    {
        this.txtArea = txtArea;
    }
    
    @Override
    public void flush()
    {
        this.txtArea.repaint();
    }

    @Override
    public void write(int b) throws IOException
    {
        this.flush();
        txtArea.append(new String(new byte[] {(byte)b}));
    }
}
