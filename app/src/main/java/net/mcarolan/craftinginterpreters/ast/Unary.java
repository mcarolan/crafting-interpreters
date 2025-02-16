package net.mcarolan.craftinginterpreters.ast;

import net.mcarolan.craftinginterpreters.scanner.Token;

public record Unary(Token operator, Expression right, int lineStart, int lineEnd)
    implements Expression {}
