package net.mcarolan.craftinginterpreters.ast.expression;

import net.mcarolan.craftinginterpreters.scanner.Token;

public record Variable(Token name, int line) implements Expression {}
