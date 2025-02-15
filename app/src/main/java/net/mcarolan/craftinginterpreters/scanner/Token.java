package net.mcarolan.craftinginterpreters.scanner;

public record Token(TokenType type, String lexeme, Object literal, int lineStart, int lineEnd) {}
