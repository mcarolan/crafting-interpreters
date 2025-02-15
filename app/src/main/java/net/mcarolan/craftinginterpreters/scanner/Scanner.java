package net.mcarolan.craftinginterpreters.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.mcarolan.craftinginterpreters.ScannerException;

public class Scanner {

  private static final Map<String, TokenType> KEYWORDS =
      Map.ofEntries(
          Map.entry("and", TokenType.AND),
          Map.entry("class", TokenType.CLASS),
          Map.entry("else", TokenType.ELSE),
          Map.entry("false", TokenType.FALSE),
          Map.entry("for", TokenType.FOR),
          Map.entry("fun", TokenType.FUN),
          Map.entry("if", TokenType.IF),
          Map.entry("nil", TokenType.NIL),
          Map.entry("or", TokenType.OR),
          Map.entry("print", TokenType.PRINT),
          Map.entry("return", TokenType.RETURN),
          Map.entry("super", TokenType.SUPER),
          Map.entry("this", TokenType.THIS),
          Map.entry("true", TokenType.TRUE),
          Map.entry("var", TokenType.VAR),
          Map.entry("while", TokenType.WHILE));

  private int current = 0;
  private int line = 1;
  private final String source;
  private final List<Token> tokens;

  public Scanner(String source) {
    this.source = source;
    this.tokens = new ArrayList<>();
  }

  public List<Token> scanTokens() {
    while (current < source.length()) {
      var ch = charAt(current);

      if (ch == '\n') {
        line++;
        current++;
        continue;
      }

      if (Character.isWhitespace(ch)) {
        current++;
        continue;
      }

      if (ch == '/' && charAt(current + 1) == '/') {
        skipLineComment();
        continue;
      }

      var token = scanToken();
      tokens.add(token);
    }

    tokens.add(new Token(TokenType.EOF, "", null, line, line));
    return tokens;
  }

  private void skipLineComment() {
    while (current < source.length() && source.charAt(current) != '\n') {
      current++;
    }
  }

  private Token string() {
    var start = current;
    var lineStart = line;

    current++;
    while (current < source.length() && source.charAt(current) != '"') {
      if (source.charAt(current) == '\n') {
        line++;
      }
      current++;
    }

    if (current >= source.length()) {
      throw new ScannerException("Unterminated string", line);
    }

    current++;
    var value = source.substring(start + 1, current - 1);
    var lexeme = source.substring(start, current);
    return new Token(TokenType.STRING, lexeme, value, lineStart, line);
  }

  private Token number() {
    var start = current;
    while (Character.isDigit(charAt(current))) {
      current++;
    }

    if (charAt(current) == '.' && Character.isDigit(charAt(current + 1))) {
      do {
        current++;
      } while (Character.isDigit(charAt(current)));
    }

    var lexeme = source.substring(start, current);
    var value = Double.parseDouble(lexeme);
    return new Token(TokenType.NUMBER, lexeme, value, line, line);
  }

  private Token identifierOrKeyword() {
    var start = current;

    do {
      ++current;
    } while (Character.isLetterOrDigit(charAt(current)));

    var lexeme = source.substring(start, current);
    var tokenType = KEYWORDS.getOrDefault(lexeme.toLowerCase(), TokenType.IDENTIFIER);
    var literal = tokenType == TokenType.IDENTIFIER ? lexeme : null;
    return new Token(tokenType, lexeme, literal, line, line);
  }

  private Token operatorToken(String lexeme, TokenType type) {
    current += lexeme.length();
    return new Token(type, lexeme, null, line, line);
  }

  private Token scanToken() {
    var c = charAt(current);

    return switch (c) {
      case '(' -> operatorToken(String.valueOf(c), TokenType.LEFT_PAREN);
      case ')' -> operatorToken(String.valueOf(c), TokenType.RIGHT_PAREN);
      case '{' -> operatorToken(String.valueOf(c), TokenType.LEFT_BRACE);
      case '}' -> operatorToken(String.valueOf(c), TokenType.RIGHT_BRACE);
      case ',' -> operatorToken(String.valueOf(c), TokenType.COMMA);
      case '.' -> operatorToken(String.valueOf(c), TokenType.DOT);
      case '-' -> operatorToken(String.valueOf(c), TokenType.MINUS);
      case '+' -> operatorToken(String.valueOf(c), TokenType.PLUS);
      case ';' -> operatorToken(String.valueOf(c), TokenType.SEMICOLON);
      case '*' -> operatorToken(String.valueOf(c), TokenType.STAR);
      case '/' -> operatorToken(String.valueOf(c), TokenType.SLASH);

      case '!' ->
          (charAt(current + 1) == '=')
              ? operatorToken("!=", TokenType.BANG_EQUAL)
              : operatorToken(String.valueOf(c), TokenType.BANG);

      case '=' ->
          (charAt(current + 1) == '=')
              ? operatorToken("==", TokenType.EQUAL_EQUAL)
              : operatorToken(String.valueOf(c), TokenType.EQUAL);

      case '<' ->
          (charAt(current + 1) == '=')
              ? operatorToken("<=", TokenType.LESS_EQUAL)
              : operatorToken(String.valueOf(c), TokenType.LESS);

      case '>' ->
          (charAt(current + 1) == '=')
              ? operatorToken(">=", TokenType.GREATER_EQUAL)
              : operatorToken(String.valueOf(c), TokenType.GREATER);

      case '"' -> string();

      default -> {
        if (Character.isDigit(c)) {
          yield number();
        } else if (Character.isLetter(c)) {
          yield identifierOrKeyword();
        } else {
          throw new ScannerException(String.format("%s (%d) is not supported", c, (int) c), line);
        }
      }
    };
  }

  private char charAt(int index) {
    return index < source.length() ? source.charAt(index) : '\0';
  }
}
