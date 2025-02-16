package net.mcarolan.craftinginterpreters.parser;

import static net.mcarolan.craftinginterpreters.scanner.TokenType.*;

import java.util.ArrayList;
import java.util.List;
import net.mcarolan.craftinginterpreters.ast.expression.*;
import net.mcarolan.craftinginterpreters.ast.statement.*;
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

  private Statement declaration() {
    if (match(VAR)) {
      return varDeclaration();
    }

    return statement();
  }

  private Statement varDeclaration() {
    var name = consume(IDENTIFIER, "Expect variable name");

    Expression initialiser = new Literal(NullValue.VALUE, previous().lineEnd());

    if (match(EQUAL)) {
      initialiser = expression();
    }

    expectSemicolon();
    return new Var(name, initialiser);
  }

  private Statement statement() {
    if (match(PRINT)) {
      return printStatement();
    }
    if (match(LEFT_BRACE)) {
      return new Block(block());
    }
    return declaration();
  }

  private List<Statement> block() {
    var statements = new ArrayList<Statement>();

    while (!check(RIGHT_BRACE) && !isAtEnd()) {
      statements.add(declaration());
    }

    consume(RIGHT_BRACE, "Expected } after blcok");
    return statements;
  }

  private Statement printStatement() {
    var expression = expression();
    expectSemicolon();
    return new Print(expression);
  }

  private void expectSemicolon() {
    consume(SEMICOLON, "Expected ;");
  }

  private Expression expression() {
    return assignment();
  }

  private Expression assignment() {
    var expression = equality();

    if (match(EQUAL)) {
      var equals = previous();
      var value = assignment();

      if (expression instanceof Variable var) {
        return new Assign(var.name(), value, value.line());
      } else {
        throw new ParserException("Invalid assignment target", equals.lineEnd());
      }
    }

    return expression;
  }

  private Expression equality() {
    var expression = comparison();

    while (match(BANG_EQUAL, EQUAL_EQUAL)) {
      var operator = previous();
      var right = comparison();

      expression = new Binary(expression, operator, right, right.line());
    }

    return expression;
  }

  private Expression comparison() {
    var expression = term();

    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      var operator = previous();
      var right = term();
      expression = new Binary(expression, operator, right, right.line());
    }

    return expression;
  }

  private Expression term() {
    var expression = factor();

    while (match(MINUS, PLUS)) {
      var operator = previous();
      var right = factor();
      expression = new Binary(expression, operator, right, right.line());
    }

    return expression;
  }

  private Expression factor() {
    var expression = unary();

    while (match(SLASH, STAR)) {
      var operator = previous();
      var right = unary();
      expression = new Binary(expression, operator, right, right.line());
    }

    return expression;
  }

  private Expression unary() {
    if (match(BANG, MINUS)) {
      var operator = previous();
      var right = unary();
      return new Unary(operator, right, right.line());
    }

    return primary();
  }

  private Expression primary() {
    if (match(FALSE)) {
      return new Literal(BooleanValue.FALSE, previous().lineEnd());
    }

    if (match(TRUE)) {
      return new Literal(BooleanValue.TRUE, previous().lineEnd());
    }

    if (match(NIL)) {
      return new Literal(NullValue.VALUE, previous().lineEnd());
    }

    if (match(NUMBER, STRING)) {
      return new Literal(previous().literal(), previous().lineEnd());
    }

    if (match(IDENTIFIER)) {
      return new Variable(previous(), previous().lineEnd());
    }

    if (match(LEFT_PAREN)) {
      var expression = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new Grouping(expression, expression.line());
    }

    throw new ParserException("Expect expression.", peek().lineEnd());
  }

  private Token consume(TokenType type, String errorMessage) {
    if (!match(type)) {
      throw new ParserException(errorMessage, peek().lineEnd());
    }
    return previous();
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
