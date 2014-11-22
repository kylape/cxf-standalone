package com.redhat.gss.ws;

public class WrapperHack<T> {
  private final T item;

  public WrapperHack(T item) {
    this.item = item;
  }

  public T getItem() {
    return item;
  }
}
