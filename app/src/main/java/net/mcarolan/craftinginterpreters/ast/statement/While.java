package net.mcarolan.craftinginterpreters.ast.statement;

import net.mcarolan.craftinginterpreters.ast.expression.Expression;

public record While(Expression condition, Statement body) implements Statement {}
