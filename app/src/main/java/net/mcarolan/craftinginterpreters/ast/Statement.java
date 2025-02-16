package net.mcarolan.craftinginterpreters.ast;

public sealed interface Statement permits ExpressionStatement, Print {}
