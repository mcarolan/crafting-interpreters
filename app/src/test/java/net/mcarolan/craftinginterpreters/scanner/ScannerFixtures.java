package net.mcarolan.craftinginterpreters.scanner;

public class ScannerFixtures {
  protected static Token eof(int line) {
    return new Token(TokenType.EOF, "", null, line, line);
  }
}
