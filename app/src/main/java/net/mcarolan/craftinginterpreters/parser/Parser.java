package net.mcarolan.craftinginterpreters.parser;

import java.util.List;
import net.mcarolan.craftinginterpreters.ParserException;
import net.mcarolan.craftinginterpreters.ast.*;
import net.mcarolan.craftinginterpreters.scanner.Token;
import net.mcarolan.craftinginterpreters.scanner.TokenType;

public class Parser {

  private final List<Token> tokens;
  private int current = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public Expression parse() {
    return expression();
  }

  private Expression expression() {
    return equality();
  }

  private Expression equality() {
    var expression = comparison();

    while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
      var operator = previous();
      var right = comparison();

      expression = new Binary(expression, operator, right);
    }

    return expression;
  }

  private Expression comparison() {
    var expression = term();

    while (match(
        TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
      var operator = previous();
      var right = term();
      expression = new Binary(expression, operator, right);
    }

    return expression;
  }

  private Expression term() {
    var expression = factor();

    while (match(TokenType.MINUS, TokenType.PLUS)) {
      var operator = previous();
      var right = factor();
      expression = new Binary(expression, operator, right);
    }

    return expression;
  }

  private Expression factor() {
    var expression = unary();

    while (match(TokenType.SLASH, TokenType.STAR)) {
      var operator = previous();
      var right = unary();
      expression = new Binary(expression, operator, right);
    }

    return expression;
  }

  private Expression unary() {
    if (match(TokenType.BANG, TokenType.MINUS)) {
      var operator = previous();
      var right = unary();
      return new Unary(operator, right);
    }

    return primary();
  }

  private Expression primary() {
    if (match(TokenType.FALSE)) {
      return new Literal(false);
    }

    if (match(TokenType.TRUE)) {
      return new Literal(true);
    }

    if (match(TokenType.NIL)) {
      return new Literal(null);
    }

    if (match(TokenType.NUMBER, TokenType.STRING)) {
      return new Literal(previous().literal());
    }

    if (match(TokenType.LEFT_PAREN)) {
      var line = previous().lineEnd();
      var expression = expression();
      consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.", line);
      return new Grouping(expression);
    }

    throw new ParserException("Expect expression.", peek().lineEnd());
  }

  private void consume(TokenType type, String errorMessage, int line) {
    if (!match(type)) {
      throw new ParserException(errorMessage, line);
    }
  }

  private boolean match(TokenType... types) {
    for (var type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }
    return false;
  }

  private boolean check(TokenType type) {
    if (isAtEnd()) {
      return false;
    }
    return peek().type() == type;
  }

  private Token advance() {
    if (!isAtEnd()) {
      current++;
    }
    return previous();
  }

  private boolean isAtEnd() {
    return peek().type() == TokenType.EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }
}
