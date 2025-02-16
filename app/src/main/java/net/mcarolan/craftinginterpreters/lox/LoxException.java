package net.mcarolan.craftinginterpreters.lox;

public sealed class LoxException extends RuntimeException
    permits InterpreterException, ParserException, ScannerException {
  private int line;

  public LoxException(String message, int line, Throwable cause) {
    super(message, cause);
    this.line = line;
  }

  public LoxException(String message, int line) {
    this(message, line, null);
  }
}
