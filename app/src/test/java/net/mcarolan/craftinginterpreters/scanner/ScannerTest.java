package net.mcarolan.craftinginterpreters.scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;
import net.mcarolan.craftinginterpreters.lox.value.NumberValue;
import net.mcarolan.craftinginterpreters.lox.value.StringValue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ScannerTest extends ScannerFixtures {

  record TokenTestCase(String source, List<Token> expectedTokens) {}

  static Stream<TokenTestCase> provideTokenTestCases() {
    return Stream.of(
        new TokenTestCase("(", List.of(new Token(TokenType.LEFT_PAREN, "(", null, 1, 1), eof(1))),
        new TokenTestCase(")", List.of(new Token(TokenType.RIGHT_PAREN, ")", null, 1, 1), eof(1))),
        new TokenTestCase("{", List.of(new Token(TokenType.LEFT_BRACE, "{", null, 1, 1), eof(1))),
        new TokenTestCase("}", List.of(new Token(TokenType.RIGHT_BRACE, "}", null, 1, 1), eof(1))),
        new TokenTestCase(",", List.of(new Token(TokenType.COMMA, ",", null, 1, 1), eof(1))),
        new TokenTestCase(".", List.of(new Token(TokenType.DOT, ".", null, 1, 1), eof(1))),
        new TokenTestCase("-", List.of(new Token(TokenType.MINUS, "-", null, 1, 1), eof(1))),
        new TokenTestCase("+", List.of(new Token(TokenType.PLUS, "+", null, 1, 1), eof(1))),
        new TokenTestCase(";", List.of(new Token(TokenType.SEMICOLON, ";", null, 1, 1), eof(1))),
        new TokenTestCase("*", List.of(new Token(TokenType.STAR, "*", null, 1, 1), eof(1))),
        new TokenTestCase("!", List.of(new Token(TokenType.BANG, "!", null, 1, 1), eof(1))),
        new TokenTestCase("=", List.of(new Token(TokenType.EQUAL, "=", null, 1, 1), eof(1))),
        new TokenTestCase("<", List.of(new Token(TokenType.LESS, "<", null, 1, 1), eof(1))),
        new TokenTestCase(">", List.of(new Token(TokenType.GREATER, ">", null, 1, 1), eof(1))),
        new TokenTestCase("!=", List.of(new Token(TokenType.BANG_EQUAL, "!=", null, 1, 1), eof(1))),
        new TokenTestCase(
            "==", List.of(new Token(TokenType.EQUAL_EQUAL, "==", null, 1, 1), eof(1))),
        new TokenTestCase("<=", List.of(new Token(TokenType.LESS_EQUAL, "<=", null, 1, 1), eof(1))),
        new TokenTestCase(
            ">=", List.of(new Token(TokenType.GREATER_EQUAL, ">=", null, 1, 1), eof(1))),

        // two operators same line
        new TokenTestCase(
            "()",
            List.of(
                new Token(TokenType.LEFT_PAREN, "(", null, 1, 1),
                new Token(TokenType.RIGHT_PAREN, ")", null, 1, 1),
                eof(1))),

        // two operators different lines
        new TokenTestCase(
            "(\n)",
            List.of(
                new Token(TokenType.LEFT_PAREN, "(", null, 1, 1),
                new Token(TokenType.RIGHT_PAREN, ")", null, 2, 2),
                eof(2))),

        // slash support
        new TokenTestCase(
            "(/)",
            List.of(
                new Token(TokenType.LEFT_PAREN, "(", null, 1, 1),
                new Token(TokenType.SLASH, "/", null, 1, 1),
                new Token(TokenType.RIGHT_PAREN, ")", null, 1, 1),
                eof(1))),
        // comment support
        new TokenTestCase(
            "(//)\n)",
            List.of(
                new Token(TokenType.LEFT_PAREN, "(", null, 1, 1),
                new Token(TokenType.RIGHT_PAREN, ")", null, 2, 2),
                eof(2))),

        // whitespace ignore
        new TokenTestCase("\r!", List.of(new Token(TokenType.BANG, "!", null, 1, 1), eof(1))),
        new TokenTestCase("\t!", List.of(new Token(TokenType.BANG, "!", null, 1, 1), eof(1))),
        new TokenTestCase(" !", List.of(new Token(TokenType.BANG, "!", null, 1, 1), eof(1))),

        // string
        new TokenTestCase(
            "\"hello, world!\"",
            List.of(
                new Token(
                    TokenType.STRING, "\"hello, world!\"", new StringValue("hello, world!"), 1, 1),
                eof(1))),
        new TokenTestCase(
            "\"hello\nworld!\"!",
            List.of(
                new Token(
                    TokenType.STRING, "\"hello\nworld!\"", new StringValue("hello\nworld!"), 1, 2),
                new Token(TokenType.BANG, "!", null, 2, 2),
                eof(2))),

        // number
        new TokenTestCase(
            "123",
            List.of(new Token(TokenType.NUMBER, "123", new NumberValue(123.0), 1, 1), eof(1))),
        new TokenTestCase(
            "123.45",
            List.of(new Token(TokenType.NUMBER, "123.45", new NumberValue(123.45), 1, 1), eof(1))),
        new TokenTestCase(
            "-123.45",
            List.of(
                new Token(TokenType.MINUS, "-", null, 1, 1),
                new Token(TokenType.NUMBER, "123.45", new NumberValue(123.45), 1, 1),
                eof(1))),

        // identifier
        new TokenTestCase(
            "orchid", List.of(new Token(TokenType.IDENTIFIER, "orchid", null, 1, 1), eof(1))),

        // keywords
        new TokenTestCase("AND", List.of(new Token(TokenType.AND, "AND", null, 1, 1), eof(1))),
        new TokenTestCase(
            "CLASS", List.of(new Token(TokenType.CLASS, "CLASS", null, 1, 1), eof(1))),
        new TokenTestCase("ELSE", List.of(new Token(TokenType.ELSE, "ELSE", null, 1, 1), eof(1))),
        new TokenTestCase(
            "FALSE", List.of(new Token(TokenType.FALSE, "FALSE", null, 1, 1), eof(1))),
        new TokenTestCase("FOR", List.of(new Token(TokenType.FOR, "FOR", null, 1, 1), eof(1))),
        new TokenTestCase("FUN", List.of(new Token(TokenType.FUN, "FUN", null, 1, 1), eof(1))),
        new TokenTestCase("IF", List.of(new Token(TokenType.IF, "IF", null, 1, 1), eof(1))),
        new TokenTestCase("NIL", List.of(new Token(TokenType.NIL, "NIL", null, 1, 1), eof(1))),
        new TokenTestCase("OR", List.of(new Token(TokenType.OR, "OR", null, 1, 1), eof(1))),
        new TokenTestCase(
            "PRINT", List.of(new Token(TokenType.PRINT, "PRINT", null, 1, 1), eof(1))),
        new TokenTestCase(
            "RETURN", List.of(new Token(TokenType.RETURN, "RETURN", null, 1, 1), eof(1))),
        new TokenTestCase(
            "SUPER", List.of(new Token(TokenType.SUPER, "SUPER", null, 1, 1), eof(1))),
        new TokenTestCase("THIS", List.of(new Token(TokenType.THIS, "THIS", null, 1, 1), eof(1))),
        new TokenTestCase("TRUE", List.of(new Token(TokenType.TRUE, "TRUE", null, 1, 1), eof(1))),
        new TokenTestCase("VAR", List.of(new Token(TokenType.VAR, "VAR", null, 1, 1), eof(1))),
        new TokenTestCase(
            "WHILE", List.of(new Token(TokenType.WHILE, "WHILE", null, 1, 1), eof(1))));
  }

  @ParameterizedTest
  @MethodSource("provideTokenTestCases")
  void tokenTestCases(TokenTestCase testCase) {
    final var scanner = new Scanner(testCase.source);
    assertEquals(testCase.expectedTokens, scanner.scanTokens());
  }
}
