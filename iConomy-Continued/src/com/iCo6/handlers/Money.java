package com.iCo6.handlers;

import com.iCo6.Constants.Nodes;
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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Money extends Handler
{
  private Accounts Accounts = new Accounts();

  public Money(iConomy plugin) {
    super(plugin, iConomy.Template);
  }

  public boolean perform(CommandSender sender, LinkedHashMap<String, Parser.Argument> arguments) throws InvalidUsage
  {
    if ((Constants.Nodes.useHoldingsPermission.getBoolean().booleanValue()) && 
      (!hasPermissions(sender, "money"))) {
      throw new InvalidUsage("You do not have permission to do that.");
    }
    String name = ((Parser.Argument)arguments.get("name")).getStringValue();
    String tag = this.template.color(Template.Node.TAG_MONEY);

    if (name.equals("0")) {
      if (isConsole(sender)) {
        Messaging.send(sender, "`rCannot check money on non-living organism.");
        return false;
      }

      Player player = (Player)sender;

      if (player == null) {
        return false;
      }
      Account account = new Account(player.getName());
      account.getHoldings().showBalance(null);
      return false;
    }

    if (!hasPermissions(sender, "money+")) {
      throw new InvalidUsage("You do not have permission to do that.");
    }
    if (!this.Accounts.exists(name)) {
      this.template.set(Template.Node.ERROR_ACCOUNT);
      this.template.add("name", name);

      Messaging.send(sender, tag + this.template.parse());
      return false;
    }

    Account account = new Account(name);
    account.getHoldings().showBalance(sender);
    return false;
  }
}