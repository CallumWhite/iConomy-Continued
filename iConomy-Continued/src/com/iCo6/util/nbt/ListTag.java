package com.iCo6.util.nbt;

import java.util.Collections;
import java.util.List;

public final class ListTag<T extends Tag> extends Tag
{
  private final Class<T> type;
  private final List<T> value;

  public ListTag(String name, Class<T> type, List<T> value)
  {
    super(name);
    this.type = type;
    this.value = Collections.unmodifiableList(value);
  }

  public Class<T> getType()
  {
    return this.type;
  }

  public List<T> getValue()
  {
    return this.value;
  }

  public String toString()
  {
    String name = getName();
    String append = "";
    if ((name != null) && (!name.equals(""))) {
      append = "(\"" + getName() + "\")";
    }

    StringBuilder bldr = new StringBuilder();
    bldr.append("TAG_List" + append + ": " + this.value.size() + " entries of type " + NBTUtils.getTypeName(this.type) + "\r\n{\r\n");
    for (Tag t : this.value) {
      bldr.append("   " + t.toString().replaceAll("\r\n", "\r\n   ") + "\r\n");
    }
    bldr.append("}");
    return bldr.toString();
  }
}