package net.mcarolan.craftinginterpreters.lox.value;

public record NumberValue(double value) implements LoxValue {
  @Override
  public String stringify() {
    var string = Double.toString(value);
    if (string.endsWith(".0")) {
      return string.substring(0, string.length() - 2);
    } else {
      return string;
    }
  }
}
