package net.mcarolan.craftinginterpreters.lox;

public sealed class LoxException extends RuntimeException
    permits InterpreterException, ParserException, ScannerException {
  private int line;

  public LoxException(String message, int line) {
    super(message);
    this.line = line;
  }
}
