package net.mcarolan.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

public class StubEnvironmentAdapter implements EnvironmentPort {

  private final List<String> standardOutput = new ArrayList<>();

  @Override
  public void print(String string) {
    standardOutput.add(string);
  }

  List<String> getStandardOutput() {
    return standardOutput;
  }
}
