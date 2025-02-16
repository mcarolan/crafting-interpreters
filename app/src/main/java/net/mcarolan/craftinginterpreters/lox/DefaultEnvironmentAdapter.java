package net.mcarolan.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.mcarolan.craftinginterpreters.lox.value.LoxValue;

public class DefaultEnvironmentAdapter implements EnvironmentPort {

  final Map<String, LoxValue> values = new HashMap<>();
  final Optional<EnvironmentPort> enclosing;

  public DefaultEnvironmentAdapter() {
    this.enclosing = Optional.empty();
  }

  public DefaultEnvironmentAdapter(EnvironmentPort enclosing) {
    this.enclosing = Optional.of(enclosing);
  }

  @Override
  public void define(String name, LoxValue value) {
    values.put(name, value);
  }

  @Override
  public LoxValue get(String name) throws IllegalArgumentException {
    if (values.containsKey(name)) {
      return values.get(name);
    }
    return enclosing
        .orElseThrow(
            () -> new IllegalArgumentException(String.format("Variable %s not defined", name)))
        .get(name);
  }

  @Override
  public void assign(String name, LoxValue value) throws IllegalArgumentException {
    if (values.containsKey(name)) {
      values.put(name, value);
    } else {
      enclosing
          .orElseThrow(
              () -> new IllegalArgumentException(String.format("Variable %s not defined", name)))
          .assign(name, value);
    }
  }
}
