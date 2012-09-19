package com.iCo6.util;

import com.iCo6.iConomy;
import java.util.LinkedHashMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messaging
{
  private static CommandSender sender = null;

  public static String argument(String original, LinkedHashMap<String, Object> arguments)
  {
    for (String item : arguments.keySet()) {
      if (item.contains(","))
        for (String i : item.split(","))
          original = original.replace("+" + i, String.valueOf(arguments.get(item)));
      else {
        original = original.replace("+" + item, String.valueOf(arguments.get(item)));
      }
    }

    return original;
  }

  public static String argument(String original, Object[] arguments, Object[] points)
  {
    if (arguments.length < 1) {
      return original;
    }
    for (int i = 0; i < arguments.length; i++) {
      if (String.valueOf(arguments[i]).contains(",")) {
        for (String arg : String.valueOf(arguments[i]).split(","))
          original = original.replace(arg, String.valueOf(points[i]));
      }
      else {
        original = original.replace(String.valueOf(arguments[i]), String.valueOf(points[i]));
      }
    }

    return original;
  }

  public static String parse(String original)
  {
    original = colorize(original);
    return original.replaceAll("(&([a-z0-9]))", "§$2").replace("&&", "&");
  }

  public static String colorize(String string)
  {
    if ((iConomy.TerminalSupport) && 
      (!(sender instanceof Player))) {
      string = string.replace("`r", "\033[1;31m").replace("`R", "\033[0;31m").replace("`y", "\033[1;33m").replace("`Y", "\033[0;33m").replace("`g", "\033[1;32m").replace("`G", "\033[0;32m").replace("`a", "\033[1;36m").replace("`A", "\033[0;36m").replace("`b", "\033[1;34m").replace("`B", "\033[0;34m").replace("`p", "\033[1;35m").replace("`P", "\033[0;35m").replace("`k", "\033[0;0m").replace("`s", "\033[0;37m").replace("`S", "\033[1;30m").replace("`w", "\033[1;37m").replace("<r>", "\033[0m").replace("`e", "\033[0m").replace("<silver>", "\033[0;37m").replace("<gray>", "\033[1;30m").replace("<rose>", "\033[1;31m").replace("<lime>", "\033[1;32m").replace("<aqua>", "\033[1;36m").replace("<pink>", "\033[1;35m").replace("<yellow>", "\033[1;33m").replace("<blue>", "\033[1;34m").replace("<black>", "\033[0;0m").replace("<red>", "\033[0;31m").replace("<green>", "\033[0;32m").replace("<teal>", "\033[0;36m").replace("<navy>", "\033[0;34m").replace("<purple>", "\033[0;35m").replace("<gold>", "\033[0;33m").replace("<white>", "\033[1;37m") + "\033[0m";
    }

    string = string.replace("`e", "").replace("`r", ChatColor.RED.toString()).replace("`R", ChatColor.DARK_RED.toString()).replace("`y", ChatColor.YELLOW.toString()).replace("`Y", ChatColor.GOLD.toString()).replace("`g", ChatColor.GREEN.toString()).replace("`G", ChatColor.DARK_GREEN.toString()).replace("`a", ChatColor.AQUA.toString()).replace("`A", ChatColor.DARK_AQUA.toString()).replace("`b", ChatColor.BLUE.toString()).replace("`B", ChatColor.DARK_BLUE.toString()).replace("`p", ChatColor.LIGHT_PURPLE.toString()).replace("`P", ChatColor.DARK_PURPLE.toString()).replace("`k", ChatColor.BLACK.toString()).replace("`s", ChatColor.GRAY.toString()).replace("`S", ChatColor.DARK_GRAY.toString()).replace("`w", ChatColor.WHITE.toString());

    string = string.replace("<r>", "").replace("<black>", "§0").replace("<navy>", "§1").replace("<green>", "§2").replace("<teal>", "§3").replace("<red>", "§4").replace("<purple>", "§5").replace("<gold>", "§6").replace("<silver>", "§7").replace("<gray>", "§8").replace("<blue>", "§9").replace("<lime>", "§a").replace("<aqua>", "§b").replace("<rose>", "§c").replace("<pink>", "§d").replace("<yellow>", "§e").replace("<white>", "§f");

    return string;
  }

  public static String bracketize(String message)
  {
    return "[" + message + "]";
  }

  public static void save(Player player)
  {
    sender = player;
  }

  public static void save(CommandSender sender)
  {
    sender = sender;
  }

  public static void send(Player player, String message)
  {
    player.sendMessage(parse(message));
  }

  public static void send(CommandSender sender, String message)
  {
    sender.sendMessage(parse(message));
  }

  public static void send(String message)
  {
    if (sender != null)
      sender.sendMessage(parse(message));
  }
}