package net.mcarolan.craftinginterpreters.ast.expression;

import net.mcarolan.craftinginterpreters.scanner.Token;

public record Assign(Token name, Expression value, int line) implements Expression {}
