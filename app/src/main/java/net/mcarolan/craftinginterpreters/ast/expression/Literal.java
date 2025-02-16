package net.mcarolan.craftinginterpreters.ast.expression;

import net.mcarolan.craftinginterpreters.lox.value.LoxValue;

public record Literal(LoxValue value, int line) implements Expression {}
