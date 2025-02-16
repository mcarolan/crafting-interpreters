package net.mcarolan.craftinginterpreters.ast.statement;

public record ExpressionStatement(
    net.mcarolan.craftinginterpreters.ast.expression.Expression expression) implements Statement {}
