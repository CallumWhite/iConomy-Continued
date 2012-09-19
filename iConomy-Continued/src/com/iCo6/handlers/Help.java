package com.iCo6.handlers;

import com.iCo6.Constants.Nodes;
import com.iCo6.command.Handler;
import com.iCo6.command.Parser;
import com.iCo6.command.Parser.Argument;
import com.iCo6.command.exceptions.InvalidUsage;
import com.iCo6.iConomy;
import com.iCo6.util.Messaging;
import java.util.LinkedHashMap;
import org.bukkit.command.CommandSender;

public class Help extends Handler
{
  public Help(iConomy plugin)
  {
    super(plugin, iConomy.Template);
  }

  public boolean perform(CommandSender sender, LinkedHashMap<String, Parser.Argument> arguments) throws InvalidUsage
  {
    if (!hasPermissions(sender, "help")) {
      throw new InvalidUsage("You do not have permission to do that.");
    }

    Messaging.send(sender, "`w ");
    Messaging.send(sender, "`w iConomy (`y" + Constants.Nodes.CodeName.toString() + "`w)");
    Messaging.send(sender, "`w ");
    Messaging.send(sender, "`S [] `wRequired, `S() `sOptional");
    Messaging.send(sender, "`w ");

    for (String action : this.plugin.Commands.getHelp().keySet()) {
      if (!hasPermissions(sender, action)) {
        continue;
      }
      String description = this.plugin.Commands.getHelp(action)[1];
      String command = "";

      if ((action.equalsIgnoreCase("money")) || (action.equalsIgnoreCase("money+")))
        command = "/money `w" + this.plugin.Commands.getHelp(action)[0] + "`s";
      else {
        command = "/money `w" + action + this.plugin.Commands.getHelp(action)[0] + "`s";
      }
      command = command.replace("[", "`S[`s").replace("]", "`S]").replace("(", "`S(");
      Messaging.send(sender, String.format("   %1$s `Y-`y %2$s", new Object[] { command, description }));
    }

    return false;
  }
}