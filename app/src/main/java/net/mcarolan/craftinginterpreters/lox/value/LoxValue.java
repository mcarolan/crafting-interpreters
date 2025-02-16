package net.mcarolan.craftinginterpreters.lox.value;

public sealed interface LoxValue permits BooleanValue, NullValue, NumberValue, StringValue {
  String stringify();
}
