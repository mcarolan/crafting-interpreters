package net.mcarolan.craftinginterpreters.ast;

public sealed interface Expression permits Binary, Grouping, Literal, Unary {}
