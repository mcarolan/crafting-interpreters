package net.mcarolan.craftinginterpreters.parser;

import static net.mcarolan.craftinginterpreters.scanner.TokenType.*;

import java.util.ArrayList;
import java.util.List;
import net.mcarolan.craftinginterpreters.ast.*;
import net.mcarolan.craftinginterpreters.lox.ParserException;
import net.mcarolan.craftinginterpreters.lox.value.BooleanValue;
import net.mcarolan.craftinginterpreters.lox.value.NullValue;
import net.mcarolan.craftinginterpreters.scanner.Token;
import net.mcarolan.craftinginterpreters.scanner.TokenType;

public class Parser {

  private final List<Token> tokens;
  private int current = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public Expression parseExpression() {
    return expression();
  }

  public List<Statement> parse() {
    var statements = new ArrayList<Statement>();

    while (!isAtEnd()) {
      statements.add(statement());
    }
    return statements;
  }

  private Statement statement() {
    if (match(PRINT)) {
      return printStatement();
    }
    return expressionStatement();
  }

  private Statement expressionStatement() {
    var expression = expression();
    expectSemicolon(expression().lineEnd());
    return new ExpressionStatement(expression);
  }

  private Statement printStatement() {
    var expression = expression();
    expectSemicolon(expression.lineEnd());
    return new Print(expression);
  }

  private void expectSemicolon(int line) {
    consume(SEMICOLON, "Expected ;", line);
  }

  private Expression expression() {
    return equality();
  }

  private Expression equality() {
    var expression = comparison();

    while (match(BANG_EQUAL, EQUAL_EQUAL)) {
      var operator = previous();
      var right = comparison();

      expression = new Binary(expression, operator, right, operator.lineEnd(), right.lineEnd());
    }

    return expression;
  }

  private Expression comparison() {
    var expression = term();

    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      var operator = previous();
      var right = term();
      expression = new Binary(expression, operator, right, operator.lineEnd(), right.lineEnd());
    }

    return expression;
  }

  private Expression term() {
    var expression = factor();

    while (match(MINUS, PLUS)) {
      var operator = previous();
      var right = factor();
      expression = new Binary(expression, operator, right, operator.lineEnd(), right.lineEnd());
    }

    return expression;
  }

  private Expression factor() {
    var expression = unary();

    while (match(SLASH, STAR)) {
      var operator = previous();
      var right = unary();
      expression = new Binary(expression, operator, right, operator.lineEnd(), right.lineEnd());
    }

    return expression;
  }

  private Expression unary() {
    if (match(BANG, MINUS)) {
      var operator = previous();
      var right = unary();
      return new Unary(operator, right, operator.lineEnd(), right.lineEnd());
    }

    return primary();
  }

  private Expression primary() {
    if (match(FALSE)) {
      return new Literal(BooleanValue.FALSE, previous().lineEnd(), previous().lineEnd());
    }

    if (match(TRUE)) {
      return new Literal(BooleanValue.TRUE, previous().lineEnd(), previous().lineEnd());
    }

    if (match(NIL)) {
      return new Literal(NullValue.VALUE, previous().lineEnd(), previous().lineEnd());
    }

    if (match(NUMBER, STRING)) {
      return new Literal(previous().literal(), previous().lineEnd(), previous().lineEnd());
    }

    if (match(LEFT_PAREN)) {
      var line = previous().lineEnd();
      var expression = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.", line);
      return new Grouping(expression, line, expression.lineEnd());
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
    return peek().type() == EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }
}
