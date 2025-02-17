package net.mcarolan.craftinginterpreters.lox;

public sealed class LoxException extends RuntimeException
    permits InterpreterException, ParserException, ScannerException {

  public LoxException(String message, int line, Throwable cause) {
    super(String.format("Line %d: %s", line, message), cause);
  }

  public LoxException(String message, int line) {
    this(message, line, null);
  }
}
