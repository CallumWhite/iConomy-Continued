package com.iCo6.command;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Parser
{
  private LinkedHashMap<String, String[]> commands;
  private LinkedHashMap<String, Handler> handlers;
  private LinkedHashMap<String, Object> arguments;
  private LinkedHashMap<String, String[]> help;
  private LinkedHashMap<String, String> permissions;
  private String message;
  private String[] message_split;
  private ArrayList<String> command_list;
  private final String BASE = "/";
  private final String ENDVARIABLE = "~";
  private final String VARIABLE = "+";
  private final String COMMAND = "-";
  private final String FCSPLIT = "|";
  private final String CSPLIT = "\\|";
  private final String SPLIT = " ";
  private final String DVARIABLE = "\\:";
  private Object[] mapping;
  public String found;

  public Parser()
  {
    this.commands = new LinkedHashMap();
    this.handlers = new LinkedHashMap();
    this.arguments = new LinkedHashMap();
    this.help = new LinkedHashMap();
    this.permissions = new LinkedHashMap();
    this.command_list = new ArrayList();
    this.mapping = new Object[0];
  }

  public String[] getCommands() {
    return (String[])this.command_list.toArray(new String[0]);
  }

  public void add(String Command, Handler Handler) {
    String base = Command.split(" ")[0];
    this.commands.put(Command, Command.split(" "));

    if (!this.handlers.containsKey(base.substring(1, base.length()))) {
      this.handlers.put(base.substring(1, base.length()), Handler);
    }
    this.handlers.put(Command, Handler);
    this.command_list.add(base.substring(1, base.length()).toLowerCase());
  }

  public void setHelp(String command, String[] help) {
    this.help.put(command, help);
  }

  public boolean hasHelp(String command) {
    return this.help.containsKey(command);
  }

  public LinkedHashMap<String, String[]> getHelp() {
    return this.help;
  }

  public String[] getHelp(String command) {
    return (String[])this.help.get(command);
  }

  public void setPermission(String Command, String level) {
    this.permissions.put(Command, level);
  }

  public Handler getHandler() {
    if (this.found.isEmpty()) return null;

    return (Handler)this.handlers.get(this.found);
  }

  public Handler getHandler(String Command) {
    return (Handler)this.handlers.get(Command.toLowerCase());
  }

  public boolean hasPermission(String Command) {
    return this.permissions.containsKey(Command);
  }

  public String getPermission(String Command) {
    return (String)this.permissions.get(Command);
  }

  public void save(String message) {
    this.arguments = new LinkedHashMap();
    this.mapping = new Object[0];

    this.message = message;
    this.message_split = message.split(" ");
  }

  public String base() {
    if (this.message_split.length < 0) return null;
    for (String command : this.commands.keySet()) {
      ArrayList container = new ArrayList();
      Object[] objects = new Object[0];
      String[] command_split = (String[])this.commands.get(command);
      int location = 0;

      if (command_split.length >= 0) {
        for (String section : command_split) {
          String symbol = section.substring(0, 1);
          String variable = section.substring(1, section.length());
          boolean split = variable.contains("|");

          if (section.startsWith("/")) {
            if (split) {
              for (String against : variable.split("\\|")) {
                if ((symbol + against).equalsIgnoreCase(this.message_split[location])) return against;
              }
              break;
            }
            if (!section.equalsIgnoreCase(this.message_split[location])) break; return variable;
          }
        }
      }

    }

    return null;
  }

  public String command() {
    if (this.message_split.length < 0) return null;
    for (String command : this.commands.keySet()) {
      ArrayList container = new ArrayList();
      Object[] objects = new Object[0];
      String[] command_split = (String[])this.commands.get(command);
      int location = 0;

      if (command_split.length >= 0) {
        for (String section : command_split) {
          String symbol = section.substring(0, 1);
          String variable = section.substring(1, section.length());
          boolean split = variable.contains("|");

          if (symbol.equals("-")) {
            if (this.message_split.length <= location) break;
            if (split) {
              for (String against : variable.split("\\|")) {
                if ((against.equalsIgnoreCase(this.message_split[location])) || ((symbol + against).equalsIgnoreCase(this.message_split[location])))
                  return against;
              }
              break;
            }
            if ((!variable.equalsIgnoreCase(this.message_split[location])) && (!section.equalsIgnoreCase(this.message_split[location]))) break;
            return variable;
          }

          location++;
        }
      }
    }
    return null;
  }

  public ArrayList<Object> parse() {
    this.found = "";

    if (this.message_split.length < 0) {
      return new ArrayList();
    }

    for (String command : this.commands.keySet()) {
      ArrayList container = new ArrayList();
      String[] command_split = (String[])this.commands.get(command);
      boolean foundCommand = false;
      int location = 0;

      if (command_split.length >= 0) {
        for (String section : command_split) {
          String[] variables = new String[0];
          String symbol = section.substring(0, 1);
          String variable = section.substring(1, section.length());
          boolean split = variable.contains("|");
          boolean found = false;

          if ((section.startsWith("/")) || (section.startsWith("-"))) {
            if (this.message_split.length <= location)
              break;
            if (split) {
              for (String against : variable.split("\\|")) {
                if (!(symbol + against).equalsIgnoreCase(this.message_split[location])) if (!(symbol + against).equalsIgnoreCase(this.message_split[location]))
                    continue;
                found = true;

                if (!section.startsWith("-")) break;
                this.found = command;
                foundCommand = true; break;
              }

            }
            else if (!(section.startsWith("-") ? variable : section).equalsIgnoreCase(this.message_split[location])) { if (!(section.startsWith("-") ? section : section).equalsIgnoreCase(this.message_split[location])); } else {
              found = true;

              if (section.startsWith("-")) {
                this.found = command;
                foundCommand = true;
              }

            }

            if (!found)
              break;
          }
          if (section.startsWith("+")) {
            if (this.message_split.length <= location) {
              if (variable.contains(":")) {
                variables = variable.split("\\:");

                if (variables.length > 0)
                  this.arguments.put(variables[0], variables[1]);
                else
                  this.arguments.put(variable, Integer.valueOf(0));
              }
              else {
                this.arguments.put(variable, Integer.valueOf(0));
              }
            }
            else if (variable.contains(":")) {
              variables = variable.split("\\:");

              if (variables.length > 0)
                this.arguments.put(variables[0], this.message_split[location]);
              else
                this.arguments.put(variable, this.message_split[location]);
            }
            else {
              this.arguments.put(variable, this.message_split[location]);
            }

          }

          if (section.startsWith("~")) {
            if (this.message_split.length <= location) {
              if (variable.contains(":")) {
                variables = variable.split("\\:");

                if (variables.length > 0)
                  this.arguments.put(variables[0], variables[1]);
                else
                  this.arguments.put(variable, Integer.valueOf(0));
              }
              else {
                this.arguments.put(variable, Integer.valueOf(0));
              }
            }
            else if (variable.contains(":")) {
              variables = variable.split("\\:");

              if (variables.length > 0)
                this.arguments.put(variables[0], combine(location, this.message_split, " "));
              else
                this.arguments.put(variable, combine(location, this.message_split, " "));
            }
            else {
              this.arguments.put(variable, combine(location, this.message_split, " "));
            }

          }

          location++;
        }

        if (container.size() > 0) return null;
        if (foundCommand) break;
      }
    }
    return new ArrayList(this.arguments.values());
  }

  public LinkedHashMap<String, Argument> getArguments() {
    if (this.arguments == null) return null;

    LinkedHashMap Arguments = new LinkedHashMap();
    for (String key : this.arguments.keySet()) {
      Arguments.put(key, new Argument(key, this.arguments.get(key)));
    }
    return Arguments;
  }

  public Object getValue(String argument) {
    if (this.arguments == null) return null;
    return this.arguments.containsKey(argument) ? this.arguments.get(argument) : null;
  }

  public String getString(String argument) {
    if (this.arguments == null) return null;
    return this.arguments.containsKey(argument) ? String.valueOf(this.arguments.get(argument)) : null;
  }

  public int getInteger(String argument) {
    if (this.arguments == null) return 0;
    int value = 0;
    try
    {
      value = this.arguments.containsKey(argument) ? Integer.valueOf(String.valueOf(this.arguments.get(argument))).intValue() : 0;
    } catch (NumberFormatException ex) {
    }
    return value;
  }

  public boolean getBoolean(String argument) {
    if (this.arguments == null) return false;
    return this.arguments.containsKey(argument) ? Boolean.parseBoolean(String.valueOf(this.arguments.get(argument))) : false;
  }

  private String combine(int startIndex, String[] string, String seperator)
  {
    StringBuilder builder = new StringBuilder();

    for (int i = startIndex; i < string.length; i++) {
      builder.append(string[i]);
      builder.append(seperator);
    }

    builder.deleteCharAt(builder.length() - seperator.length());
    return builder.toString();
  }
  public class Argument {
    private String name;
    private Object value;

    public Argument(String name, Object value) { this.name = name;
      this.value = value; }

    public String getName()
    {
      return this.name;
    }

    public Object getValue() {
      return this.value;
    }

    public String getStringValue() {
      return String.valueOf(this.value);
    }

    public Boolean getBooleanValue() {
      return Boolean.valueOf(Boolean.parseBoolean(String.valueOf(this.value)));
    }

    public Double getDoubleValue() throws NumberFormatException {
      return Double.valueOf(String.valueOf(this.value));
    }

    public Integer getIntegerValue() throws NumberFormatException {
      return Integer.valueOf(String.valueOf(this.value));
    }
  }

  public class InvalidSyntaxException extends Exception
  {
    public InvalidSyntaxException(String message)
    {
      super();
    }
  }
}