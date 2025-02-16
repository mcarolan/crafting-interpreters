package net.mcarolan.craftinginterpreters.ast;

public record Grouping(Expression expression, int lineStart, int lineEnd) implements Expression {}
