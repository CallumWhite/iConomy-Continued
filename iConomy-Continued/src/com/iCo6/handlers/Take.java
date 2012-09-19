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
import org.bukkit.command.CommandSender;

public class Take extends Handler
{
  private Accounts Accounts = new Accounts();

  public Take(iConomy plugin) {
    super(plugin, iConomy.Template);
  }

  public boolean perform(CommandSender sender, LinkedHashMap<String, Parser.Argument> arguments) throws InvalidUsage
  {
    if (!hasPermissions(sender, "take")) {
      throw new InvalidUsage("You do not have permission to do that.");
    }
    String name = ((Parser.Argument)arguments.get("name")).getStringValue();
    String tag = this.template.color(Template.Node.TAG_MONEY);

    if (name.equals("0")) {
      throw new InvalidUsage("Missing name parameter: /money take <name> <amount>");
    }
    if (((Parser.Argument)arguments.get("amount")).getStringValue().equals("empty"))
      throw new InvalidUsage("Missing amount parameter: /money take <name> <amount>"); Double amount;
    try {
      amount = ((Parser.Argument)arguments.get("amount")).getDoubleValue();
    } catch (NumberFormatException e) {
      throw new InvalidUsage("Invalid amount parameter, must be double.");
    }

    if ((Double.isInfinite(amount.doubleValue())) || (Double.isNaN(amount.doubleValue()))) {
      throw new InvalidUsage("Invalid amount parameter, must be double.");
    }
    if (!this.Accounts.exists(name)) {
      this.template.set(Template.Node.ERROR_ACCOUNT);
      this.template.add("name", name);

      Messaging.send(sender, tag + this.template.parse());
      return false;
    }

    Account account = new Account(name);
    account.getHoldings().subtract(amount.doubleValue());

    this.template.set(Template.Node.PLAYER_DEBIT);
    this.template.add("name", name);
    this.template.add("amount", iConomy.format(amount.doubleValue()));

    Messaging.send(sender, tag + this.template.parse());
    return false;
  }
}