package net.mcarolan.craftinginterpreters.scanner;

import net.mcarolan.craftinginterpreters.lox.value.LoxValue;

public record Token(TokenType type, String lexeme, LoxValue literal, int lineStart, int lineEnd) {}
