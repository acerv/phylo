package matrix.exception;

public class AlphabetNotDefined extends LanguageException
{
    @Override
    public String toString()
    {
        return getMessage() + "l'alfabeto non è definito.\n"
                + "Se si tratta di caricamento da file, verificare che il file, in cui è definita\n "
                + "la matrice, contenga come prima riga la definizione del suo alfabeto:\n"
                + "ALPHABET = <CHAR> , <CHAR>, <CHAR>\n"
                + "dove per <CHAR> si intende un elemento dell'alfabeto";
    }
}
