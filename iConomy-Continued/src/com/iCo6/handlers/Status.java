package com.iCo6.handlers;

import com.iCo6.command.Handler;
import com.iCo6.command.Parser.Argument;
import com.iCo6.command.exceptions.InvalidUsage;
import com.iCo6.iConomy;
import com.iCo6.system.Account;
import com.iCo6.system.Accounts;
import com.iCo6.util.Messaging;
import com.iCo6.util.Template;
import com.iCo6.util.Template.Node;
import java.util.LinkedHashMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Status extends Handler
{
  private Accounts Accounts = new Accounts();

  public Status(iConomy plugin) {
    super(plugin, iConomy.Template);
  }

  public boolean perform(CommandSender sender, LinkedHashMap<String, Parser.Argument> arguments) throws InvalidUsage
  {
    if (!hasPermissions(sender, "status")) {
      throw new InvalidUsage("You do not have permission to do that.");
    }
    String name = ((Parser.Argument)arguments.get("name")).getStringValue();
    String tag = this.template.color(Template.Node.TAG_MONEY);
    boolean self = false;

    if ((!isConsole(sender)) && 
      (((Player)sender).getName().equalsIgnoreCase(name))) {
      self = true;
    }
    if (name.equals("0")) {
      throw new InvalidUsage("Missing <white>name<rose>: /money status <name> (new status)");
    }
    if (!this.Accounts.exists(name)) {
      this.template.set(Template.Node.ERROR_ACCOUNT);
      this.template.add("name", name);

      Messaging.send(sender, tag + this.template.parse());
      return false;
    }

    Account account = new Account(name);

    if (((Parser.Argument)arguments.get("status")).getStringValue().equalsIgnoreCase("empty")) {
      int current = account.getStatus().intValue();

      if (self) {
        this.template.set(Template.Node.PERSONAL_STATUS);
      } else {
        this.template.set(Template.Node.PLAYER_STATUS);
        this.template.add("name", name);
      }

      this.template.add("status", Integer.valueOf(current));
      Messaging.send(sender, tag + this.template.parse());
    }
    else {
      if (!hasPermissions(sender, "status+")) {
        throw new InvalidUsage("You do not have permission to do that.");
      }
      int status = ((Parser.Argument)arguments.get("status")).getIntegerValue().intValue();
      account.setStatus(status);

      this.template.set(Template.Node.ACCOUNTS_STATUS);
      this.template.add("status", Integer.valueOf(status));
      Messaging.send(sender, tag + this.template.parse());
    }

    return false;
  }
}