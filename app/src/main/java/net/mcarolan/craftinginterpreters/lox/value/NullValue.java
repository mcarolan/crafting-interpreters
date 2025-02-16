package net.mcarolan.craftinginterpreters.lox.value;

public record NullValue() implements LoxValue {
  public static NullValue VALUE = new NullValue();

  @Override
  public String stringify() {
    return "nil";
  }
}
