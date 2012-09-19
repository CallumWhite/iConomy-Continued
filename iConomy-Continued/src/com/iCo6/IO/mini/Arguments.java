package com.iCo6.IO.mini;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Arguments
{
  private String key;
  private LinkedHashMap<String, String> values;
  private boolean caseSensitive;

  public Arguments(Object key)
  {
    this.key = parseKey(key);
    this.values = new LinkedHashMap();
    this.caseSensitive = false;
  }

  public Arguments(Object key, boolean caseSensitive) {
    this.key = parseKey(key);
    this.values = new LinkedHashMap();
    this.caseSensitive = caseSensitive;
  }

  public String getKey() {
    return parseKey(this.key);
  }

  private String encode(String data) {
    return data.trim().replace(" ", "}+{");
  }

  private String decode(String data) {
    return data.replace("}+{", " ").trim();
  }

  public boolean hasKey(Object key) {
    return this.values.containsKey(parseKey(key));
  }

  public void setValue(String key, Object value) {
    this.values.put(encode(parseKey(key)), encode(String.valueOf(value)));
  }

  public String getValue(String key) {
    return decode((String)this.values.get(parseKey(key)));
  }

  public Integer getInteger(String key) throws NumberFormatException {
    return Integer.valueOf(getValue(key));
  }

  public Double getDouble(String key) throws NumberFormatException {
    return Double.valueOf(getValue(key));
  }

  public Long getLong(String key) throws NumberFormatException {
    return Long.valueOf(getValue(key));
  }

  public Float getFloat(String key) throws NumberFormatException {
    return Float.valueOf(getValue(key));
  }

  public Short getShort(String key) throws NumberFormatException {
    return Short.valueOf(getValue(key));
  }

  public Boolean getBoolean(String key) {
    return Boolean.valueOf(getValue(key));
  }

  public String[] getArray(String key) {
    String value = getValue(key);

    if ((value == null) || (!value.contains(","))) return null;
    if (value.split(",") == null) return null;

    return trim(value.split(","));
  }

  private String[] trim(String[] values) {
    int i = 0; for (int length = values.length; i < length; i++) {
      if (values[i] != null)
        values[i] = values[i].trim();
    }
    return values;
  }

  private String parseKey(Object key) {
    if (this.caseSensitive) {
      return String.valueOf(key);
    }
    return String.valueOf(key).toLowerCase();
  }

  private List trim(List values) {
    List trimmed = new ArrayList();

    int i = 0; for (int length = values.size(); i < length; i++) {
      String v = (String)values.get(i);

      if (v != null) v = v.trim();

      trimmed.add(v);
    }

    return trimmed;
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(this.key).append(" ");

    for (String k : this.values.keySet()) {
      sb.append(k).append(":").append((String)this.values.get(k)).append(" ");
    }
    return sb.toString().trim();
  }

  public Arguments copy() {
    Arguments copy = new Arguments(this.key);

    for (String k : this.values.keySet()) {
      copy.values.put(k, this.values.get(k));
    }
    return copy;
  }
}