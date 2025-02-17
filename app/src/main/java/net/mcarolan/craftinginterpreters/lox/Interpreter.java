package net.mcarolan.craftinginterpreters.lox;

import static net.mcarolan.craftinginterpreters.scanner.TokenType.OR;

import java.util.List;
import net.mcarolan.craftinginterpreters.ast.expression.*;
import net.mcarolan.craftinginterpreters.ast.statement.*;
import net.mcarolan.craftinginterpreters.lox.value.*;
import net.mcarolan.craftinginterpreters.scanner.TokenType;

public class Interpreter {

  private EnvironmentPort environment;
  private final IOPort io;

  public Interpreter(EnvironmentPort environment, IOPort io) {
    this.environment = environment;
    this.io = io;
  }

  LoxValue evaluateExpression(Expression expression) {
    return switch (expression) {
      case Binary binary -> evaluateBinary(binary);
      case Grouping grouping -> evaluateGrouping(grouping);
      case Literal literal -> evaluateLiteral(literal);
      case Unary unary -> evaluateUnary(unary);
      case Variable variable -> evaluateVariable(variable);
      case Assign assign -> evaluateAssignment(assign);
      case Logical logical -> evaluateLogical(logical);
    };
  }

  private LoxValue evaluateLogical(Logical logical) {
    final var left = evaluateExpression(logical.left());

    if (logical.operator().type() == OR) {
      if (isTruthy(left)) {
        return left;
      }
    } else {
      if (!isTruthy(left)) {
        return left;
      }
    }

    return evaluateExpression(logical.right());
  }

  private LoxValue evaluateAssignment(Assign assign) {
    final var value = evaluateExpression(assign.value());
    environment.assign(assign.name().lexeme(), value);
    return value;
  }

  private LoxValue evaluateVariable(Variable variable) {
    try {
      return environment.get(variable.name().lexeme());
    } catch (IllegalArgumentException e) {
      throw new InterpreterException("Failed to evaluate variable", variable.line(), e);
    }
  }

  public void interpret(List<Statement> statements) {
    statements.forEach(this::evaluateStatement);
  }

  private void evaluateStatement(Statement statement) {
    switch (statement) {
      case ExpressionStatement expression -> {
        evaluateExpression(expression.expression());
      }
      case Print print -> {
        final var stringValue = evaluateExpression(print.expression()).stringify();
        io.print(stringValue);
      }
      case Var var -> {
        final var initialiser = evaluateExpression(var.initialiser());
        environment.define(var.name().lexeme(), initialiser);
      }
      case Block block -> {
        final var previous = environment;
        environment = new DefaultEnvironmentAdapter(previous);
        block.statementList().forEach(this::evaluateStatement);
        environment = previous;
      }
      case If anIf -> {
        if (isTruthy(evaluateExpression(anIf.condition()))) {
          evaluateStatement(anIf.thenBranch());
        } else {
          anIf.elseBranch().ifPresent(this::evaluateStatement);
        }
      }
      case While aWhile -> {
        while (isTruthy(evaluateExpression(aWhile.condition()))) {
          evaluateStatement(aWhile.body());
        }
      }
    }
  }

  private LoxValue evaluateGrouping(Grouping grouping) {
    return evaluateExpression(grouping.expression());
  }

  private LoxValue evaluateBinary(Binary binary) {
    final var left = evaluateExpression(binary.left());
    final var right = evaluateExpression(binary.right());

    final var line = binary.operator().lineStart();
    final var operatorType = binary.operator().type();
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
    if (left instanceof NumberValue(final var leftValue)
        && right instanceof NumberValue(final var rightValue)) {
      final var result =
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
    if (left instanceof NumberValue(final var leftValue)
        && right instanceof NumberValue(final var rightValue)) {
      final var result =
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
    if (left instanceof NumberValue(final var leftValue)
        && right instanceof NumberValue(final var rightValue)) {
      return new NumberValue(leftValue + rightValue);
    }
    if (left instanceof StringValue(final var leftValue)
        && right instanceof StringValue(final var rightValue)) {
      return new StringValue(leftValue + rightValue);
    }
    throw new InterpreterException(
        "Addition only supported if both operands are Number or both operands are String", line);
  }

  private static LoxValue evaluateLiteral(Literal literal) {
    return literal.value();
  }

  private LoxValue evaluateUnary(Unary unary) {
    final var right = evaluateExpression(unary.right());

    return switch (unary.operator().type()) {
      case MINUS -> interpretMinus(right, unary.operator().lineStart());
      case BANG -> BooleanValue.of(!isTruthy(right));
      default ->
          throw new InterpreterException(
              String.format("Unexpected unary with %s operator", unary.operator().type()),
              unary.operator().lineStart());
    };
  }

  private static boolean isTruthy(LoxValue value) {
    return switch (value) {
      case BooleanValue booleanValue -> booleanValue.value();
      case NullValue ignored -> false;
      case NumberValue ignored -> true;
      case StringValue ignored -> true;
    };
  }

  private static LoxValue interpretMinus(LoxValue right, int line) {
    return switch (right) {
      case NumberValue numberValue -> new NumberValue(-numberValue.value());
      default -> throw new InterpreterException("Minus only supported for number values", line);
    };
  }
}
