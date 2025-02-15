package net.mcarolan.craftinginterpreters;

public final class ScannerException extends LoxException {
  public ScannerException(String message, int line) {
    super(message, line);
  }
}
