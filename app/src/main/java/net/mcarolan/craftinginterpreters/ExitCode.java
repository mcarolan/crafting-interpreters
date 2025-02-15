package net.mcarolan.craftinginterpreters;

public enum ExitCode {
  BAD_CMDLINE_ARGUMENTS(64),
  RUNTIME_ERROR(65);

  private final int code;

  ExitCode(int code) {
    this.code = code;
  }

  public int getCode() {
    return this.code;
  }
}
