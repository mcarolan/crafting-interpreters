package net.mcarolan.craftinginterpreters.ast.statement;

import net.mcarolan.craftinginterpreters.ast.expression.Expression;

public record Print(Expression expression) implements Statement {}
