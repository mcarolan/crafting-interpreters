package net.mcarolan.craftinginterpreters.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import net.mcarolan.craftinginterpreters.ast.Binary;
import net.mcarolan.craftinginterpreters.ast.Literal;
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
                new Literal(1.0), new Token(TokenType.PLUS, "+", null, 1, 1), new Literal(1.0)),
            new Token(TokenType.EQUAL_EQUAL, "==", null, 1, 1),
            new Literal(2.0));
    var tokens =
        List.of(
            new Token(TokenType.NUMBER, "1", 1.0, 1, 1),
            new Token(TokenType.PLUS, "+", null, 1, 1),
            new Token(TokenType.NUMBER, "1", 1.0, 1, 1),
            new Token(TokenType.EQUAL_EQUAL, "==", null, 1, 1),
            new Token(TokenType.NUMBER, "2", 2.0, 1, 1),
            eof(1));
    var parser = new Parser(tokens);
    assertEquals(expected, parser.parse());
  }
}
