package net.mcarolan.craftinginterpreters.ast.expression;

import net.mcarolan.craftinginterpreters.scanner.Token;

public record Unary(Token operator, Expression right, int line) implements Expression {}
