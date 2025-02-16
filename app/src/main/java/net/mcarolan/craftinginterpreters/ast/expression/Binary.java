package net.mcarolan.craftinginterpreters.ast.expression;

import net.mcarolan.craftinginterpreters.scanner.Token;

public record Binary(Expression left, Token operator, Expression right, int line)
    implements Expression {}
