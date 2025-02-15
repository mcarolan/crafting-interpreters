package net.mcarolan.craftinginterpreters.scanning;

public record Token(TokenType type, String lexeme, Object literal, int lineStart, int lineEnd) {}
