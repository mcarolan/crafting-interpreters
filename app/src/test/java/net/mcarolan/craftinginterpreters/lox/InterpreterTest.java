package net.mcarolan.craftinginterpreters.lox;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.mcarolan.craftinginterpreters.lox.value.*;
import net.mcarolan.craftinginterpreters.parser.Parser;
import net.mcarolan.craftinginterpreters.scanner.Scanner;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class InterpreterTest {

  record ExpressionTestCase(String input, LoxValue expectedValue) {}

  record ProgramTestCase(
      String input, List<String> expectedStandardOutput, Map<String, LoxValue> expectedVariables) {}

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
    final var scanner = new Scanner(testCase.input);
    final var parser = new Parser(scanner.scanTokens());
    final var expression = parser.parseExpression();
    final var interpreter = new Interpreter(new DefaultEnvironmentAdapter(), new StubIOPort());
    final var result = interpreter.evaluateExpression(expression);

    assertEquals(testCase.expectedValue, result);
  }

  private static Stream<ProgramTestCase> provideProgramTestCases() {
    return Stream.of(
        new ProgramTestCase("print \"one\";", List.of("one"), Map.of()),
        new ProgramTestCase("print true;", List.of("true"), Map.of()),
        new ProgramTestCase("print 1 + 1;", List.of("2"), Map.of()),
        new ProgramTestCase(
            """
                                print "hello";
                                print "world";
                                """,
            List.of("hello", "world"),
            Map.of()),
        new ProgramTestCase(
            """
                                var a = 1;
                                var b = 2;
                                print a + b;
                                """,
            List.of("3"),
            Map.of("a", new NumberValue(1), "b", new NumberValue(2))),
        new ProgramTestCase(
            """
                                var a;
                                print a;
                                """,
            List.of("nil"),
            Map.of("a", NullValue.VALUE)),
        new ProgramTestCase(
            """
                                var a = 1;
                                print a = 2;
                                """,
            List.of("2"),
            Map.of("a", new NumberValue(2))),
        new ProgramTestCase(
            """
                                var a = "global a";
                                var b = "global b";
                                var c = "global c";
                                {
                                    var a = "outer a";
                                    var b = "outer b";
                                    {
                                        var a = "inner a";
                                        print a;
                                        print b;
                                        print c;
                                    }
                                    print a;
                                    print b;
                                    print c;
                                }
                                print a;
                                print b;
                                print c;
                                """,
            List.of(
                "inner a",
                "outer b",
                "global c",
                "outer a",
                "outer b",
                "global c",
                "global a",
                "global b",
                "global c"),
            Map.of(
                "a",
                new StringValue("global a"),
                "b",
                new StringValue("global b"),
                "c",
                new StringValue("global c"))),
        new ProgramTestCase(
            """
                        var a = 2;
                        if (a == 2) print "a is 2"; else print "foo";
                        """,
            List.of("a is 2"),
            Map.of("a", new NumberValue(2))),
        new ProgramTestCase(
            """
                        print "hi" or 2;
                        print nil or "yes";
                        """,
            List.of("hi", "yes"),
            Map.of()),
        new ProgramTestCase(
            """
                        var i = 0;
                        while (i < 3) {
                          print i;
                          i = i + 1;
                        }
                        """,
            List.of("0", "1", "2"),
            Map.of("i", new NumberValue(3))),
        new ProgramTestCase(
            """
                        for (var i = 0; i < 3; i = i + 1) {
                            print i;
                        }
                        """,
            List.of("0", "1", ""),
            Map.of()));
  }

  @ParameterizedTest
  @MethodSource("provideProgramTestCases")
  void programTestCases(ProgramTestCase testCase) {
    final var scanner = new Scanner(testCase.input);
    final var parser = new Parser(scanner.scanTokens());
    final var statements = parser.parse();
    final var environment = new DefaultEnvironmentAdapter();
    final var io = new StubIOPort();
    final var interpreter = new Interpreter(environment, io);
    interpreter.interpret(statements);
    assertEquals(testCase.expectedStandardOutput(), io.getStdout());
    assertEquals(testCase.expectedVariables, environment.values);
  }
}
