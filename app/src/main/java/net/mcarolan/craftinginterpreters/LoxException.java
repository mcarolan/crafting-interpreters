package net.mcarolan.craftinginterpreters;

public sealed class LoxException extends RuntimeException permits ScannerException {
  private int line;

  public LoxException(String message, int line) {
    super(message);
    this.line = line;
  }
}
