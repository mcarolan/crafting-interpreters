package net.mcarolan.craftinginterpreters.ast.expression;

public record Grouping(Expression expression, int line) implements Expression {}
