package net.mcarolan.craftinginterpreters.lox;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;
import net.mcarolan.craftinginterpreters.lox.value.*;
import net.mcarolan.craftinginterpreters.parser.Parser;
import net.mcarolan.craftinginterpreters.scanner.Scanner;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class InterpreterTest {

  record ExpressionTestCase(String input, LoxValue expectedValue) {}

  record ProgramTestCase(String input, List<String> expectedStandardOutput) {}

  private static Stream<ExpressionTestCase> provideExpressionTestCases() {
    return Stream.of(
        new ExpressionTestCase("42", new NumberValue(42)),
        new ExpressionTestCase("\"42\"", new StringValue("42")),
        new ExpressionTestCase("true", BooleanValue.TRUE),
        new ExpressionTestCase("false", BooleanValue.FALSE),
        new ExpressionTestCase("nil", NullValue.VALUE),
        new ExpressionTestCase("(42)", new NumberValue(42)),
        new ExpressionTestCase("-42", new NumberValue(-42)),
        new ExpressionTestCase("!nil", BooleanValue.TRUE),
        new ExpressionTestCase("!0", BooleanValue.FALSE),
        new ExpressionTestCase("!\"hello\"", BooleanValue.FALSE),
        new ExpressionTestCase("!true", BooleanValue.FALSE),
        new ExpressionTestCase("!false", BooleanValue.TRUE),
        new ExpressionTestCase("3 - 2", new NumberValue(1)),
        new ExpressionTestCase("6 / 2", new NumberValue(3)),
        new ExpressionTestCase("6 * 2", new NumberValue(12)),
        new ExpressionTestCase("6 + 2", new NumberValue(8)),
        new ExpressionTestCase("\"6\" + \"2\"", new StringValue("62")),
        new ExpressionTestCase("2 > 2", BooleanValue.FALSE),
        new ExpressionTestCase("3 > 2", BooleanValue.TRUE),
        new ExpressionTestCase("2 >= 2", BooleanValue.TRUE),
        new ExpressionTestCase("3 >= 2", BooleanValue.TRUE),
        new ExpressionTestCase("2 < 2", BooleanValue.FALSE),
        new ExpressionTestCase("1 < 2", BooleanValue.TRUE),
        new ExpressionTestCase("2 <= 2", BooleanValue.TRUE),
        new ExpressionTestCase("1 <= 2", BooleanValue.TRUE),
        new ExpressionTestCase("1 == 1", BooleanValue.TRUE),
        new ExpressionTestCase("1 == \"1\"", BooleanValue.FALSE),
        new ExpressionTestCase("1 == true", BooleanValue.FALSE),
        new ExpressionTestCase("1 == nil", BooleanValue.FALSE),
        new ExpressionTestCase("\"1\" == \"1\"", BooleanValue.TRUE),
        new ExpressionTestCase("true == true", BooleanValue.TRUE),
        new ExpressionTestCase("nil == nil", BooleanValue.TRUE),
        new ExpressionTestCase("1 != \"1\"", BooleanValue.TRUE),
        new ExpressionTestCase("1 != true", BooleanValue.TRUE),
        new ExpressionTestCase("1 != nil", BooleanValue.TRUE),
        new ExpressionTestCase("\"1\" != \"1\"", BooleanValue.FALSE),
        new ExpressionTestCase("true != true", BooleanValue.FALSE),
        new ExpressionTestCase("nil != nil", BooleanValue.FALSE));
  }

  @ParameterizedTest
  @MethodSource("provideExpressionTestCases")
  void expressionTestCases(ExpressionTestCase testCase) {
    var scanner = new Scanner(testCase.input);
    var parser = new Parser(scanner.scanTokens());
    var expression = parser.parseExpression();
    var result = Interpreter.evaluateExpression(expression);

    assertEquals(testCase.expectedValue, result);
  }

  private static Stream<ProgramTestCase> provideProgramTestCases() {
    return Stream.of(
        new ProgramTestCase("print \"one\";", List.of("one")),
        new ProgramTestCase("print true;", List.of("true")),
        new ProgramTestCase("print 1 + 1;", List.of("2")),
        new ProgramTestCase(
            """
                    print "hello";
                    print "world";
                    """,
            List.of("hello", "world")));
  }

  @ParameterizedTest
  @MethodSource("provideProgramTestCases")
  void programTestCases(ProgramTestCase testCase) {
    var scanner = new Scanner(testCase.input);
    var parser = new Parser(scanner.scanTokens());
    var statements = parser.parse();
    var environment = new StubEnvironmentAdapter();
    var interpreter = new Interpreter(environment);
    interpreter.interpret(statements);
    assertEquals(testCase.expectedStandardOutput(), environment.getStandardOutput());
  }
}
