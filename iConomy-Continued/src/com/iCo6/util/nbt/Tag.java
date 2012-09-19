package com.iCo6.util.nbt;

public abstract class Tag
{
  private final String name;

  public Tag()
  {
    this("");
  }

  public Tag(String name)
  {
    this.name = name;
  }

  public final String getName()
  {
    return this.name;
  }

  public abstract Object getValue();
}