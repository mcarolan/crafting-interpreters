package net.mcarolan.craftinginterpreters.ast;

import net.mcarolan.craftinginterpreters.lox.value.LoxValue;

public record Literal(LoxValue value, int lineStart, int lineEnd) implements Expression {}
