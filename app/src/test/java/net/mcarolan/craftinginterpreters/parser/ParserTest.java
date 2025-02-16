package net.mcarolan.craftinginterpreters.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import net.mcarolan.craftinginterpreters.ast.Binary;
import net.mcarolan.craftinginterpreters.ast.Literal;
import net.mcarolan.craftinginterpreters.lox.value.NumberValue;
import net.mcarolan.craftinginterpreters.scanner.ScannerFixtures;
import net.mcarolan.craftinginterpreters.scanner.Token;
import net.mcarolan.craftinginterpreters.scanner.TokenType;
import org.junit.jupiter.api.Test;

class ParserTest extends ScannerFixtures {

  @Test
  void parseSimpleExpression() {
    var expected =
        new Binary(
            new Binary(
                new Literal(new NumberValue(1.0), 1, 1),
                new Token(TokenType.PLUS, "+", null, 1, 1),
                new Literal(new NumberValue(1.0), 1, 1),
                1,
                1),
            new Token(TokenType.EQUAL_EQUAL, "==", null, 1, 1),
            new Literal(new NumberValue(2.0), 1, 1),
            1,
            1);
    var tokens =
        List.of(
            new Token(TokenType.NUMBER, "1", new NumberValue(1.0), 1, 1),
            new Token(TokenType.PLUS, "+", null, 1, 1),
            new Token(TokenType.NUMBER, "1", new NumberValue(1.0), 1, 1),
            new Token(TokenType.EQUAL_EQUAL, "==", null, 1, 1),
            new Token(TokenType.NUMBER, "2", new NumberValue(2.0), 1, 1),
            eof(1));
    var parser = new Parser(tokens);
    assertEquals(expected, parser.parseExpression());
  }
}
