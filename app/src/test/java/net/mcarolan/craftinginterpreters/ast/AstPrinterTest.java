package net.mcarolan.craftinginterpreters.ast;

import static org.junit.jupiter.api.Assertions.*;

import net.mcarolan.craftinginterpreters.scanner.Token;
import net.mcarolan.craftinginterpreters.scanner.TokenType;
import org.junit.jupiter.api.Test;

class AstPrinterTest {

  @Test
  void printAnExpression() {
    var expression =
        new Binary(
            new Unary(new Token(TokenType.MINUS, "-", null, 1, 1), new Literal(123.0)),
            new Token(TokenType.STAR, "*", null, 1, 1),
            new Grouping(new Literal(45.67)));
    var expected = "(* (- 123.0) (grouping 45.67))";

    assertEquals(expected, AstPrinter.print(expression));
  }
}
