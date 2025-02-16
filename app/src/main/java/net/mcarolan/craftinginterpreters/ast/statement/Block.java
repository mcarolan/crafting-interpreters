package net.mcarolan.craftinginterpreters.ast.statement;

import java.util.List;

public record Block(List<Statement> statementList) implements Statement {}
