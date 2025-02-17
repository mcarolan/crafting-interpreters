package net.mcarolan.craftinginterpreters.ast.statement;

import java.util.Optional;
import net.mcarolan.craftinginterpreters.ast.expression.Expression;

public record If(Expression condition, Statement thenBranch, Optional<Statement> elseBranch)
    implements Statement {}
