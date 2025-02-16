package net.mcarolan.craftinginterpreters.lox;

public class DefaultIOPort implements IOPort {
  @Override
  public void print(String string) {
    System.out.println(string);
  }
}
