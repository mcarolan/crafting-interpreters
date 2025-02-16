package net.mcarolan.craftinginterpreters.lox;

public final class ParserException extends LoxException {
  public ParserException(String message, int line) {
    super(message, line);
  }
}
