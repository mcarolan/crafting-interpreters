package net.mcarolan.craftinginterpreters.ast;

import static org.junit.jupiter.api.Assertions.*;

import net.mcarolan.craftinginterpreters.ast.expression.Binary;
import net.mcarolan.craftinginterpreters.ast.expression.Grouping;
import net.mcarolan.craftinginterpreters.ast.expression.Literal;
import net.mcarolan.craftinginterpreters.ast.expression.Unary;
import net.mcarolan.craftinginterpreters.lox.value.NumberValue;
import net.mcarolan.craftinginterpreters.scanner.Token;
import net.mcarolan.craftinginterpreters.scanner.TokenType;
import org.junit.jupiter.api.Test;

class AstPrinterTest {

  @Test
  void printAnExpression() {
    var expression =
        new Binary(
            new Unary(
                new Token(TokenType.MINUS, "-", null, 1, 1),
                new Literal(new NumberValue(123), 1),
                1),
            new Token(TokenType.STAR, "*", null, 1, 1),
            new Grouping(new Literal(new NumberValue(45.67), 1), 1),
            1);
    var expected = "(* (- 123) (grouping 45.67))";

    assertEquals(expected, AstPrinter.print(expression));
  }
}
