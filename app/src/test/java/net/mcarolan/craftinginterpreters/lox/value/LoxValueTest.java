package net.mcarolan.craftinginterpreters.lox.value;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LoxValueTest {

  @Test
  void stringifyTrue() {
    assertEquals("true", BooleanValue.TRUE.stringify());
  }

  @Test
  void stringifyFalse() {
    assertEquals("false", BooleanValue.FALSE.stringify());
  }

  @Test
  void stringifyString() {
    assertEquals("hello", new StringValue("hello").stringify());
  }

  @Test
  void stringifyNull() {
    assertEquals("nil", NullValue.VALUE.stringify());
  }

  @Test
  void stringifyNumberInteger() {
    assertEquals("1", new NumberValue(1).stringify());
  }

  @Test
  void stringifyNumberDecimal() {
    assertEquals("1.1", new NumberValue(1.1).stringify());
  }
}
