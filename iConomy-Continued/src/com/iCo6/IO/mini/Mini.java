package com.iCo6.IO.mini;

import com.iCo6.IO.mini.file.Manager;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Mini
{
  private String folder;
  private String database;
  private String source;
  private boolean changed = false;
  private boolean caseSensitive = false;
  private Manager Database;
  private LinkedHashMap<String, Arguments> Indexes;
  private LinkedHashMap<String, Arguments> pushedIndexes;

  public Mini(String folder, String database)
  {
    this.database = database;
    this.folder = folder;
    this.Database = new Manager(this.folder, this.database, true);
    read();
  }

  public Mini(String folder, String database, boolean caseSensitive)
  {
    this.caseSensitive = caseSensitive;
    this.database = database;
    this.folder = folder;
    this.Database = new Manager(this.folder, this.database, true);
    read();
  }

  public Mini(File data)
  {
    this.database = data.getName();
    this.folder = data.getPath();
    this.Database = new Manager(this.folder, this.database, true);
    read();
  }

  public Mini(File data, boolean caseSensitive)
  {
    this.caseSensitive = caseSensitive;
    this.database = data.getName();
    this.folder = data.getPath();
    this.Database = new Manager(this.folder, this.database, true);
    read();
  }

  public static void main(String[] args) {
    Mini mini = new Mini(".", "mini");

    Double amount = mini.getArguments("Nijikokun").getDouble("money");
    mini.setArgument("Nijikokun", "money", Double.valueOf(amount.doubleValue() + 2.0D), true);

    System.out.println(mini.getArguments("Nijikokun").getDouble("money"));
    System.out.println(mini.getIndices().toString());
  }

  private String[] trim(String[] values) {
    int i = 0; for (int length = values.length; i < length; i++) {
      if (values[i] != null)
        values[i] = values[i].trim();
    }
    return values;
  }

  private void read() {
    read(true);
  }

  private void read(boolean pushed) {
    this.Database = new Manager(this.folder, this.database, true);
    this.Database.removeDuplicates();
    this.Database.read();

    this.Indexes = new LinkedHashMap();

    if (pushed) {
      this.pushedIndexes = new LinkedHashMap();
    }
    for (String line : this.Database.getLines()) {
      if (line.trim().isEmpty()) {
        continue;
      }
      String[] parsed = trim(line.trim().split(" "));

      if ((parsed[0].contains(":")) || (parsed[0].isEmpty())) {
        continue;
      }
      Arguments entry = new Arguments(parseIndice(parsed[0]));
      for (String item : parsed) {
        if (item.contains(":")) {
          String[] map = trim(item.split(":", 2));
          String key = map[0]; String value = map[1];

          if (key == null) {
            continue;
          }
          entry.setValue(key, value);
        }
      }
      this.Indexes.put(parseIndice(parsed[0]), entry);
    }
  }

  public boolean hasIndex(Object index)
  {
    return this.Indexes.containsKey(parseIndice(index));
  }

  public LinkedHashMap<String, Arguments> getIndices()
  {
    return this.Indexes;
  }

  public void addIndex(Arguments entry)
  {
    addIndex(entry.getKey(), entry);
  }

  public void addIndex(Object index, Arguments entry)
  {
    this.pushedIndexes.put(parseIndice(index), entry);
    this.changed = true;
  }

  public boolean alterIndex(Object original, String updated)
  {
    return alterIndex(original, updated, true);
  }

  public boolean alterIndex(Object original, String updated, boolean update)
  {
    if ((!hasIndex(original)) || (hasIndex(updated))) return false;

    Arguments data = (Arguments)this.Indexes.get(parseIndice(original));
    removeIndex(original);
    addIndex(updated, data);

    if (update) update();

    return true;
  }

  public void removeIndex(Object key)
  {
    this.Database.remove(((Arguments)this.Indexes.get(parseIndice(key))).toString());
    read(false);
  }

  public Arguments getArguments(Object key)
  {
    return (Arguments)this.Indexes.get(parseIndice(key));
  }

  public void setArgument(String index, Object key, Object value)
  {
    setArgument(index, key, String.valueOf(value), false);
  }

  public void setArgument(Object index, Object key, String value, boolean save)
  {
    if (!hasIndex(index)) return;
    this.changed = true;

    Arguments original = ((Arguments)this.Indexes.get(parseIndice(index))).copy();
    original.setValue(parseIndice(key), value);

    this.pushedIndexes.put(parseIndice(index), original);
    if (save) update(); 
  }

  public void setArgument(Object index, Object key, Object value, boolean save)
  {
    String formatted = "";
    Iterator i$;
    if ((value instanceof int[])) {
      for (int v : (int[])(int[])value)
        formatted = v + ",";
    } else if ((value instanceof String[])) {
      for (String v : (String[])(String[])value)
        formatted = v + ",";
    } else if ((value instanceof Double[])) {
      for (Double v : (Double[])(Double[])value)
        formatted = v + ",";
    } else if ((value instanceof Boolean[])) {
      for (Boolean v : (Boolean[])(Boolean[])value)
        formatted = v + ",";
    } else if ((value instanceof Long[])) {
      for (Long v : (Long[])(Long[])value)
        formatted = v + ",";
    } else if ((value instanceof Float[])) {
      for (Float v : (Float[])(Float[])value)
        formatted = v + ",";
    } else if ((value instanceof Byte[])) {
      for (Byte v : (Byte[])(Byte[])value)
        formatted = v + ",";
    } else if ((value instanceof char[])) {
      for (char v : (char[])(char[])value)
        formatted = v + ",";
    } else if ((value instanceof ArrayList)) {
      ArrayList data = (ArrayList)value;
      for (i$ = data.iterator(); i$.hasNext(); ) { Object v = i$.next();
        formatted = v + ",";
      }
    }
    if (formatted.length() > 1)
      formatted.substring(0, formatted.length() - 2);
    else {
      formatted = String.valueOf(value);
    }

    setArgument(parseIndice(index), parseIndice(key), formatted, save);
  }

  public void update()
  {
    if (!this.changed) {
      return;
    }
    LinkedList lines = new LinkedList();

    for (String key : this.pushedIndexes.keySet()) {
      if ((this.Indexes.containsKey(key)) && 
        (!((Arguments)this.Indexes.get(key)).toString().equals(((Arguments)this.pushedIndexes.get(key)).toString()))) {
        this.Database.remove(((Arguments)this.Indexes.get(key)).toString());
      }

    }

    read(false);

    for (String key : this.pushedIndexes.keySet()) {
      if (this.Indexes.containsKey(key)) {
        if (!((Arguments)this.Indexes.get(key)).toString().equals(((Arguments)this.pushedIndexes.get(key)).toString())) {
          this.Indexes.put(key, this.pushedIndexes.get(key));
          this.Database.append(((Arguments)this.Indexes.get(key)).toString());
        }
      }
      else this.Database.append(((Arguments)this.pushedIndexes.get(key)).toString());

    }

    this.pushedIndexes.clear();
    read();
  }

  private String parseIndice(Object key) {
    if (this.caseSensitive) {
      return String.valueOf(key);
    }
    return String.valueOf(key).toLowerCase();
  }
}