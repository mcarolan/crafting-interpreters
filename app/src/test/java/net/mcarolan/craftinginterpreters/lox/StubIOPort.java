package net.mcarolan.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

public class StubIOPort implements IOPort {

  private final List<String> stdout = new ArrayList<>();

  @Override
  public void print(String string) {
    stdout.add(string);
  }

  List<String> getStdout() {
    return stdout;
  }
}
