package net.mcarolan.craftinginterpreters.lox;

public final class InterpreterException extends LoxException {
  public InterpreterException(String message, int line) {
    super(message, line);
  }

  public InterpreterException(String message, int line, Throwable cause) {
    super(message, line, cause);
  }
}
