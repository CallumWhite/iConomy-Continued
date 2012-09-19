package com.iCo6.system;

import com.iCo6.Constants.Nodes;
import com.iCo6.iConomy;
import com.iCo6.util.Template;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimerTask;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class Interest extends TimerTask
{
  Template Template = null;

  public Interest(String directory) {
    this.Template = new Template(directory, "Messages.yml");
  }

  public void run()
  {
    Accounts Accounts = new Accounts();
    DecimalFormat DecimalFormat = new DecimalFormat("#.##");
    List players = new ArrayList();
    LinkedHashMap queries = new LinkedHashMap();

    if (Constants.Nodes.InterestOnline.getBoolean().booleanValue()) {
      Player[] player = iConomy.Server.getOnlinePlayers();

      for (Player p : player)
        players.add(p.getName());
    }
    else {
      players.addAll(Queried.accountList());
    }

    double cutoff = Constants.Nodes.InterestCutoff.getDouble().doubleValue();
    double percentage = Constants.Nodes.InterestPercentage.getDouble().doubleValue();
    double min = Constants.Nodes.InterestMin.getDouble().doubleValue();
    double max = Constants.Nodes.InterestMax.getDouble().doubleValue();
    double amount = 0.0D;

    String table = Constants.Nodes.DatabaseTable.toString();
    String query = "UPDATE " + table + " SET balance = ? WHERE username = ?";

    if (percentage == 0.0D) {
      try {
        if (min != max)
          amount = Double.valueOf(DecimalFormat.format(Math.random() * (max - min) + min)).doubleValue();
        else
          amount = max;
      }
      catch (NumberFormatException e) {
        amount = max;
      }
    }

    for (String name : players) {
      if (!Accounts.exists(name)) {
        continue;
      }
      Account account = new Account(name);
      Double balance = account.getHoldings().getBalance();

      if ((cutoff > 0.0D) && (
        (balance.doubleValue() >= cutoff) || (
        (cutoff < 0.0D) && 
        (balance.doubleValue() <= cutoff)))) {
        continue;
      }
      LinkedHashMap data = new LinkedHashMap();

      if (percentage != 0.0D) {
        amount = Double.valueOf(DecimalFormat.format(percentage * balance.doubleValue() / 100.0D)).doubleValue();
      }
      data.put("original", balance);
      data.put("balance", Double.valueOf(balance.doubleValue() + amount));
      queries.put(name, data);
    }

    if (queries.isEmpty()) {
      return;
    }
    Queried.doInterest(query, queries);
  }
}