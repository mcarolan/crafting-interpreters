package net.mcarolan.craftinginterpreters.ast;

public record ExpressionStatement(Expression expression) implements Statement {}
