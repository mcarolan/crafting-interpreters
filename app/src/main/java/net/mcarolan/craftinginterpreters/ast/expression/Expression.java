package net.mcarolan.craftinginterpreters.ast.expression;

public sealed interface Expression
    permits Assign, Binary, Grouping, Literal, Logical, Unary, Variable {
  int line();
}
