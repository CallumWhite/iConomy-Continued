package com.iCo6.handlers;

import com.iCo6.command.Handler;
import com.iCo6.command.Parser.Argument;
import com.iCo6.command.exceptions.InvalidUsage;
import com.iCo6.iConomy;
import com.iCo6.system.Account;
import com.iCo6.system.Accounts;
import com.iCo6.system.Holdings;
import com.iCo6.util.Common;
import com.iCo6.util.Messaging;
import com.iCo6.util.Template;
import com.iCo6.util.Template.Node;
import java.util.LinkedHashMap;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Payment extends Handler
{
  private Accounts Accounts = new Accounts();

  public Payment(iConomy plugin) {
    super(plugin, iConomy.Template);
  }

  public boolean perform(CommandSender sender, LinkedHashMap<String, Parser.Argument> arguments) throws InvalidUsage
  {
    if (!hasPermissions(sender, "pay")) {
      return false;
    }
    if (isConsole(sender)) {
      Messaging.send(sender, "`rCannot remove money from a non-living organism.");
      return false;
    }

    Player from = (Player)sender;
    String name = ((Parser.Argument)arguments.get("name")).getStringValue();
    String tag = this.template.color(Template.Node.TAG_MONEY);

    if (name.equals("0")) {
      throw new InvalidUsage("Missing <white>name<rose>: /money pay <name> <amount>");
    }
    if (((Parser.Argument)arguments.get("amount")).getStringValue().equals("empty"))
      throw new InvalidUsage("Missing <white>amount<rose>: /money pay <name> <amount>"); Double amount;
    try {
      amount = ((Parser.Argument)arguments.get("amount")).getDoubleValue();
    } catch (NumberFormatException e) {
      throw new InvalidUsage("Invalid <white>amount<rose>, must be double.");
    }

    if ((Double.isInfinite(amount.doubleValue())) || (Double.isNaN(amount.doubleValue()))) {
      throw new InvalidUsage("Invalid <white>amount<rose>, must be double.");
    }
    if (amount.doubleValue() < 0.1D) {
      throw new InvalidUsage("Invalid <white>amount<rose>, cannot be less than 0.1");
    }
    if (Common.matches(from.getName(), new String[] { name })) {
      this.template.set(Template.Node.PAYMENT_SELF);
      Messaging.send(sender, this.template.parse());
      return false;
    }

    if (!this.Accounts.exists(name)) {
      this.template.set(Template.Node.ERROR_ACCOUNT);
      this.template.add("name", name);

      Messaging.send(sender, tag + this.template.parse());
      return false;
    }

    Account holder = new Account(from.getName());
    Holdings holdings = holder.getHoldings();

    if (holdings.getBalance().doubleValue() < amount.doubleValue()) {
      this.template.set(Template.Node.ERROR_FUNDS);
      Messaging.send(sender, tag + this.template.parse());
      return false;
    }

    Account account = new Account(name);
    holdings.subtract(amount.doubleValue());
    account.getHoldings().add(amount.doubleValue());

    this.template.set(Template.Node.PAYMENT_TO);
    this.template.add("name", name);
    this.template.add("amount", iConomy.format(amount.doubleValue()));
    Messaging.send(sender, tag + this.template.parse());

    Player to = iConomy.Server.getPlayer(name);

    if (to != null) {
      this.template.set(Template.Node.PAYMENT_FROM);
      this.template.add("name", from.getName());
      this.template.add("amount", iConomy.format(amount.doubleValue()));

      Messaging.send(to, tag + this.template.parse());
    }

    return false;
  }
}