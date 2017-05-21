package tree;

/**
 * A generic Tree
 * @param <T> 
 * @author Cervesato Andrea - sawk.ita @ gmail.com
 */
public interface Tree<T>
{
    static int LEFT_CHILD = 0;
    static int RIGHT_CHILD = 1;

    public T getRoot();

    public void setRoot(T root);
}
