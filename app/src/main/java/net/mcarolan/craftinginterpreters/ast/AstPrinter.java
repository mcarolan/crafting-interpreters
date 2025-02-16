package net.mcarolan.craftinginterpreters.ast;

import net.mcarolan.craftinginterpreters.ast.expression.*;
import net.mcarolan.craftinginterpreters.lox.value.LoxValue;
import net.mcarolan.craftinginterpreters.lox.value.StringValue;
import net.mcarolan.craftinginterpreters.scanner.Token;

public class AstPrinter {

  public static String print(Expression expression) {
    return switch (expression) {
      case Grouping(Expression inner, int ignored) -> parenthesise("grouping", inner);
      case Binary(Expression left, Token operator, Expression right, int ignored) ->
          parenthesise(operator.lexeme(), left, right);
      case Literal(LoxValue loxValue, int ignored) -> loxValue.stringify();
      case Unary(Token operator, Expression right, int ignored) ->
          parenthesise(operator.lexeme(), right);
      case Variable variable -> parenthesise("var", variable);
      case Assign assign ->
          parenthesise(
              "assign",
              new Literal(new StringValue(assign.name().lexeme()), assign.line()),
              assign.value());
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
