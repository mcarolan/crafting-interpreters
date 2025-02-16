package net.mcarolan.craftinginterpreters.ast;

import net.mcarolan.craftinginterpreters.scanner.Token;

public record Binary(Expression left, Token operator, Expression right, int lineStart, int lineEnd)
    implements Expression {}
