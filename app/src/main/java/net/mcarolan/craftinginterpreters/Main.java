package net.mcarolan.craftinginterpreters;

import com.google.common.base.Charsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import net.mcarolan.craftinginterpreters.lox.DefaultEnvironmentAdapter;
import net.mcarolan.craftinginterpreters.lox.DefaultIOPort;
import net.mcarolan.craftinginterpreters.lox.Interpreter;
import net.mcarolan.craftinginterpreters.lox.LoxException;
import net.mcarolan.craftinginterpreters.parser.Parser;
import net.mcarolan.craftinginterpreters.scanner.Scanner;

public class Main {
  public static void main(String[] args) throws IOException {
    switch (args.length) {
      case 1 -> runFile(args[0]);
      case 0 -> runPrompt();
      default -> {
        System.out.println("Usage: jlox [script]");
        System.exit(ExitCode.BAD_CMDLINE_ARGUMENTS.getCode());
      }
    }
  }

  static void run(String source) {
    final var scanner = new Scanner(source);
    final var tokens = scanner.scanTokens();
    final var parser = new Parser(tokens);
    final var statements = parser.parse();
    final var interpreter = new Interpreter(new DefaultEnvironmentAdapter(), new DefaultIOPort());
    interpreter.interpret(statements);
  }

  static void runFile(String path) throws IOException {
    final var bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charsets.UTF_8));
  }

  static void runPrompt() throws IOException {
    final var input = new InputStreamReader(System.in);
    try (final var reader = new BufferedReader(input)) {
      while (true) {
        System.out.print("lox> ");
        final var line = reader.readLine();

        if (line == null) {
          break;
        }

        try {
          run(line);
        } catch (LoxException e) {
          System.err.println(e.getMessage());
        }
      }
    }
  }
}
