package net.mcarolan.craftinginterpreters.ast.statement;

public sealed interface Statement permits Block, ExpressionStatement, If, Print, Var, While {}
