package net.mcarolan.craftinginterpreters.ast.statement;

import net.mcarolan.craftinginterpreters.ast.expression.Expression;
import net.mcarolan.craftinginterpreters.scanner.Token;

public record Var(Token name, Expression initialiser) implements Statement {}
