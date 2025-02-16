package net.mcarolan.craftinginterpreters.ast;

import net.mcarolan.craftinginterpreters.lox.value.LoxValue;
import net.mcarolan.craftinginterpreters.scanner.Token;

public class AstPrinter {

  public static String print(Expression expression) {
    return switch (expression) {
      case Grouping(Expression inner, int lineStart, int lineEnd) ->
          parenthesise("grouping", inner);
      case Binary(Expression left, Token operator, Expression right, int lineStart, int lineEnd) ->
          parenthesise(operator.lexeme(), left, right);
      case Literal(LoxValue loxValue, int lineStart, int lineEnd) -> loxValue.stringify();
      case Unary(Token operator, Expression right, int lineStart, int lineEnd) ->
          parenthesise(operator.lexeme(), right);
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
