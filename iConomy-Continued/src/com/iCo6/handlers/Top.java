package com.iCo6.handlers;

import com.iCo6.command.Handler;
import com.iCo6.command.Parser.Argument;
import com.iCo6.command.exceptions.InvalidUsage;
import com.iCo6.iConomy;
import com.iCo6.system.Account;
import com.iCo6.system.Accounts;
import com.iCo6.system.Holdings;
import com.iCo6.util.Messaging;
import com.iCo6.util.Template;
import com.iCo6.util.Template.Node;
import java.util.LinkedHashMap;
import java.util.List;
import org.bukkit.command.CommandSender;

public class Top extends Handler
{
  private Accounts Accounts = new Accounts();

  public Top(iConomy plugin) {
    super(plugin, iConomy.Template);
  }

  public boolean perform(CommandSender sender, LinkedHashMap<String, Parser.Argument> arguments) throws InvalidUsage
  {
    if (!hasPermissions(sender, "top")) {
      throw new InvalidUsage("You do not have permission to do that.");
    }
    this.template.set(Template.Node.TOP_OPENING);
    Messaging.send(sender, this.template.parse());

    this.template.set(Template.Node.TOP_ITEM);
    List top = this.Accounts.getTopAccounts(5);
    for (int i = 0; i < top.size(); i++) {
      Account account = (Account)top.get(i);
      this.template.add("i", Integer.valueOf(i + 1));
      this.template.add("name", account.name);
      this.template.add("amount", account.getHoldings().toString());
      Messaging.send(sender, this.template.parse());
    }

    return false;
  }
}