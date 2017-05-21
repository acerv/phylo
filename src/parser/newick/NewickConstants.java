/* Generated By:JavaCC: Do not edit this line. NewickConstants.java */
package parser.newick;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface NewickConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int comment = 6;
  /** RegularExpression Id. */
  int digit = 12;
  /** RegularExpression Id. */
  int alpha = 13;
  /** RegularExpression Id. */
  int only_quote_char = 14;
  /** RegularExpression Id. */
  int single_quote = 15;
  /** RegularExpression Id. */
  int both_char = 16;
  /** RegularExpression Id. */
  int whitespace = 17;
  /** RegularExpression Id. */
  int unquoted_char = 18;
  /** RegularExpression Id. */
  int quoted_char = 19;
  /** RegularExpression Id. */
  int number = 20;
  /** RegularExpression Id. */
  int exponent = 21;
  /** RegularExpression Id. */
  int double_number = 22;
  /** RegularExpression Id. */
  int unquoted_string = 23;
  /** RegularExpression Id. */
  int quoted_string = 24;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\f\"",
    "<comment>",
    "\":\"",
    "\";\"",
    "\"(\"",
    "\",\"",
    "\")\"",
    "<digit>",
    "<alpha>",
    "<only_quote_char>",
    "\"\\\'\\\'\"",
    "<both_char>",
    "<whitespace>",
    "<unquoted_char>",
    "<quoted_char>",
    "<number>",
    "<exponent>",
    "<double_number>",
    "<unquoted_string>",
    "<quoted_string>",
  };

}
