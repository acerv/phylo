package diagram.treetextdrawer;

/**
 * Graphics2D ported for ASCII art
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class TextGraphics2D
{
    private char[][] g;

    private static char EMPTY_CHAR = new Character(' ');
    private static char NEW_LINE_CHAR = new Character('\n');
    private static char V_LINE_CHAR = new Character('-');
    private static char H_LINE_CHAR = new Character('|');

    private int height;
    private int width;

    /**
     * Initialize class
     * @param w width of ascii space
     * @param h height of ascii space
     */
    public TextGraphics2D(int w, int h)
    {
        height = h;
        width = w;
        g = new char[h][w];
        
        initializeGraphics();
    }

    private void initializeGraphics()
    {
        for(int i = 0; i < height; i++)
            for(int j = 0; j < width; j++)
                g[i][j] = EMPTY_CHAR;

        for(int i = 0; i < height; i++)
            g[i][width-1] = NEW_LINE_CHAR;
    }

    /**
     * Draw a string into the graphics
     * @param name string to print
     * @param x horizontal position
     * @param y vertical position
     * @param horizontal if true, it prints string in horizontal way
     */
    public void drawString(String name, int x, int y, boolean horizontal)
    {
        int charPosition = 0;
        if(horizontal)
        {
            for(int i = y; i < y+name.length(); i++)
            {
                g[x][i] = name.charAt(charPosition);
                charPosition++;
            }
        }
        else
        {
            for(int i = x; i < x+name.length(); i++)
            {
                g[i][y] = name.charAt(charPosition);
                charPosition++;
            }
        }
    }

    /**
     * Draw a line into the graphics
     * @param a first point
     * @param b second point
     */
    public void drawLine(IntegerPoint a, IntegerPoint b)
    {
        boolean horizontalLine = false;

        if(a.x == b.x) horizontalLine = true;
        else if(a.y == b.y) horizontalLine = false;

        if(horizontalLine)
        {
            if(a.y < b.y)
                for(int i = a.y; i <= b.y; i++)
                    g[a.x][i] = V_LINE_CHAR;
            else
                for(int i = a.y; i >= b.y; i--)
                    g[a.x][i] = V_LINE_CHAR;
        }
        else
        {
            if(a.x < b.x)
                for(int i = a.x; i <= b.x; i++)
                    g[i][a.y] = H_LINE_CHAR;
            else
                for(int i = a.x; i >= b.x; i--)
                    g[i][a.y] = H_LINE_CHAR;
        }
    }

    /**
     * Visualize graphics
     */
    public void paint()
    {
        int h_lenght = g[0].length;
        int w_lenght = g.length;

        for(int i = 0; i < w_lenght; i++)
            for(int j = 0; j < h_lenght; j++)
                System.out.print(g[i][j]);
    }

    public int getHeight(){return height;}
    public int getWidth() {return width;}
}
