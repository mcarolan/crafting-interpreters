package net.mcarolan.craftinginterpreters.ast;

public record Literal(Object value) implements Expression {}
