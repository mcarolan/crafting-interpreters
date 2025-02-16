package net.mcarolan.craftinginterpreters.lox;

import net.mcarolan.craftinginterpreters.lox.value.LoxValue;

public interface EnvironmentPort {
  void define(String name, LoxValue value);

  void assign(String name, LoxValue value) throws IllegalArgumentException;

  LoxValue get(String name) throws IllegalArgumentException;
}
