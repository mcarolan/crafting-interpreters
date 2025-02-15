package net.mcarolan.craftinginterpreters.ast;

import net.mcarolan.craftinginterpreters.scanner.Token;

public class AstPrinter {

  public static String print(Expression expression) {
    return switch (expression) {
      case Grouping(Expression inner) -> parenthesise("grouping", inner);
      case Binary(Expression left, Token operator, Expression right) ->
          parenthesise(operator.lexeme(), left, right);
      case Literal(Object value) -> value == null ? "nil" : value.toString();
      case Unary(Token operator, Expression right) -> parenthesise(operator.lexeme(), right);
    };
  }

  private static String parenthesise(String name, Expression... expressions) {
    var builder = new StringBuilder();

    builder.append("(").append(name);

    for (var expression : expressions) {
      builder.append(" ");
      builder.append(print(expression));
    }
    builder.append(")");

    return builder.toString();
  }
}
