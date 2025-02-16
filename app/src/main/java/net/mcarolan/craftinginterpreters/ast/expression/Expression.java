package net.mcarolan.craftinginterpreters.ast.expression;

public sealed interface Expression permits Assign, Binary, Grouping, Literal, Unary, Variable {
  int line();
}
