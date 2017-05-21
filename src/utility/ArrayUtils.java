package utility;


/**
 * Utilities for arrays
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public class ArrayUtils
{
    static public int getIndexOfMin(int[] array)
    {
        int index = 0;
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < array.length; i++)
        {
            if(min > array[i])
            {
                min = array[i];
                index = i;
            }
        }

        return index;
    }
}
