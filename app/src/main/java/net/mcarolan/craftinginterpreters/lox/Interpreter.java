package net.mcarolan.craftinginterpreters.lox;

import java.util.List;
import net.mcarolan.craftinginterpreters.ast.*;
import net.mcarolan.craftinginterpreters.lox.value.*;
import net.mcarolan.craftinginterpreters.scanner.TokenType;

public class Interpreter {

  private final EnvironmentPort environment;

  public Interpreter(EnvironmentPort environment) {
    this.environment = environment;
  }

  static LoxValue evaluateExpression(Expression expression) {
    return switch (expression) {
      case Binary binary -> evaluateBinary(binary);
      case Grouping grouping -> evaluateGrouping(grouping);
      case Literal literal -> evaluateLiteral(literal);
      case Unary unary -> evaluateUnary(unary);
    };
  }

  public void interpret(List<Statement> statements) {
    statements.forEach(this::evaluateStatement);
  }

  private void evaluateStatement(Statement statement) {
    switch (statement) {
      case ExpressionStatement expressionStatement -> {
        evaluateExpression(expressionStatement.expression());
      }
      case Print print -> {
        var stringValue = evaluateExpression(print.expression()).stringify();
        environment.print(stringValue);
      }
    }
    ;
  }

  private static LoxValue evaluateGrouping(Grouping grouping) {
    return evaluateExpression(grouping.expression());
  }

  private static LoxValue evaluateBinary(Binary binary) {
    var left = evaluateExpression(binary.left());
    var right = evaluateExpression(binary.right());

    var line = binary.operator().lineStart();
    var operatorType = binary.operator().type();
    return switch (operatorType) {
      case PLUS -> interpretAddition(left, right, line);
      case MINUS, SLASH, STAR -> interpretNumberExpression(left, right, operatorType, line);
      case GREATER, GREATER_EQUAL, LESS, LESS_EQUAL ->
          interpretNumberComparison(left, right, operatorType, line);
      case EQUAL_EQUAL -> interpretEquality(left, right);
      case BANG_EQUAL -> interpretEquality(left, right).not();
      default ->
          throw new InterpreterException(
              String.format("Unsupported binary with %s operator", operatorType), line);
    };
  }

  private static BooleanValue interpretEquality(LoxValue left, LoxValue right) {
    return BooleanValue.of(left.equals(right));
  }

  private static LoxValue interpretNumberExpression(
      LoxValue left, LoxValue right, TokenType operatorType, int line) {
    if (left instanceof NumberValue(var leftValue)
        && right instanceof NumberValue(var rightValue)) {
      var result =
          switch (operatorType) {
            case STAR -> leftValue * rightValue;
            case MINUS -> leftValue - rightValue;
            case SLASH -> {
              if (rightValue == 0.0) {
                throw new InterpreterException("Attempted to divide by 0", line);
              }
              yield leftValue / rightValue;
            }
            default ->
                throw new InterpreterException(
                    String.format("Unsupported number expression operator %s", operatorType), line);
          };
      return new NumberValue(result);
    }
    throw new InterpreterException(
        String.format("%s only supported if both operands are Number", operatorType), line);
  }

  private static LoxValue interpretNumberComparison(
      LoxValue left, LoxValue right, TokenType operatorType, int line) {
    if (left instanceof NumberValue(var leftValue)
        && right instanceof NumberValue(var rightValue)) {
      var result =
          switch (operatorType) {
            case GREATER -> leftValue > rightValue;
            case GREATER_EQUAL -> leftValue >= rightValue;
            case LESS -> leftValue < rightValue;
            case LESS_EQUAL -> leftValue <= rightValue;
            default ->
                throw new InterpreterException(
                    String.format("Unsupported number comparison operator %s", operatorType), line);
          };
      return BooleanValue.of(result);
    }
    throw new InterpreterException(
        String.format("%s only supported if both operands are Number", operatorType), line);
  }

  private static LoxValue interpretAddition(LoxValue left, LoxValue right, int line) {
    if (left instanceof NumberValue(var leftValue)
        && right instanceof NumberValue(var rightValue)) {
      return new NumberValue(leftValue + rightValue);
    }
    if (left instanceof StringValue(var leftValue)
        && right instanceof StringValue(var rightValue)) {
      return new StringValue(leftValue + rightValue);
    }
    throw new InterpreterException(
        "Addition only supported if both operands are Number or both operands are String", line);
  }

  private static LoxValue evaluateLiteral(Literal literal) {
    return literal.value();
  }

  private static LoxValue evaluateUnary(Unary unary) {
    var right = evaluateExpression(unary.right());

    return switch (unary.operator().type()) {
      case MINUS -> interpretMinus(right, unary.operator().lineStart());
      case BANG -> isTruthy(right).not();
      default ->
          throw new InterpreterException(
              String.format("Unexpected unary with %s operator", unary.operator().type()),
              unary.operator().lineStart());
    };
  }

  private static BooleanValue isTruthy(LoxValue value) {
    return switch (value) {
      case BooleanValue booleanValue -> booleanValue;
      case NullValue ignored -> BooleanValue.FALSE;
      case NumberValue ignored -> BooleanValue.TRUE;
      case StringValue ignored -> BooleanValue.TRUE;
    };
  }

  private static LoxValue interpretMinus(LoxValue right, int line) {
    return switch (right) {
      case NumberValue numberValue -> new NumberValue(-numberValue.value());
      default -> throw new InterpreterException("Minus only supported for number values", line);
    };
  }
}
