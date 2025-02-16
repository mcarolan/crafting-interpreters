package net.mcarolan.craftinginterpreters.lox;

public class DefaultEnvironmentAdapter implements EnvironmentPort {
  @Override
  public void print(String string) {
    System.out.println(string);
  }
}
