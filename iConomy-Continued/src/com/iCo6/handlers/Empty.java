package com.iCo6.handlers;

import com.iCo6.command.Handler;
import com.iCo6.command.Parser.Argument;
import com.iCo6.command.exceptions.InvalidUsage;
import com.iCo6.iConomy;
import com.iCo6.system.Accounts;
import com.iCo6.util.Messaging;
import com.iCo6.util.Template;
import com.iCo6.util.Template.Node;
import java.util.LinkedHashMap;
import org.bukkit.command.CommandSender;

public class Empty extends Handler
{
  private Accounts Accounts = new Accounts();

  public Empty(iConomy plugin) {
    super(plugin, iConomy.Template);
  }

  public boolean perform(CommandSender sender, LinkedHashMap<String, Parser.Argument> arguments) throws InvalidUsage
  {
    if (!hasPermissions(sender, "empty")) {
      throw new InvalidUsage("You do not have permission to do that.");
    }
    this.Accounts.empty();

    String tag = this.template.color(Template.Node.TAG_MONEY);
    this.template.set(Template.Node.ACCOUNTS_EMPTY);
    Messaging.send(sender, tag + this.template.parse());

    return false;
  }
}