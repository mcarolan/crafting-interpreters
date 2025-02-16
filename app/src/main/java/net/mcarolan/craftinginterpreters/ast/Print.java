package net.mcarolan.craftinginterpreters.ast;

public record Print(Expression expression) implements Statement {}
