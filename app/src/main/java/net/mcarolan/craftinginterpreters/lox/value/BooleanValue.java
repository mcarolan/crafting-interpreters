package net.mcarolan.craftinginterpreters.lox.value;

public record BooleanValue(boolean value) implements LoxValue {
  public static BooleanValue TRUE = new BooleanValue(true);
  public static BooleanValue FALSE = new BooleanValue(false);

  public static BooleanValue of(boolean value) {
    return value ? BooleanValue.TRUE : BooleanValue.FALSE;
  }

  public BooleanValue not() {
    return this.value() ? BooleanValue.FALSE : BooleanValue.TRUE;
  }

  @Override
  public String stringify() {
    return Boolean.toString(value);
  }
}
