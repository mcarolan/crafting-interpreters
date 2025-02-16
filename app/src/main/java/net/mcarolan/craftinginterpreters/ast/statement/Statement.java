package net.mcarolan.craftinginterpreters.ast.statement;

public sealed interface Statement permits Block, ExpressionStatement, Print, Var {}
