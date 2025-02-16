package net.mcarolan.craftinginterpreters.lox.value;

public record StringValue(String value) implements LoxValue {
  @Override
  public String stringify() {
    return value;
  }
}
