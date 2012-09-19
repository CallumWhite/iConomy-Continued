package com.iCo6.system;

import com.iCo6.Constants.Nodes;
import com.iCo6.IO.Database;
import com.iCo6.IO.Database.Type;
import com.iCo6.IO.InventoryDB;
import com.iCo6.IO.mini.Arguments;
import com.iCo6.IO.mini.Mini;
import com.iCo6.iConomy;
import com.iCo6.util.Thrun;
import com.iCo6.util.org.apache.commons.dbutils.DbUtils;
import com.iCo6.util.org.apache.commons.dbutils.QueryRunner;
import com.iCo6.util.org.apache.commons.dbutils.ResultSetHandler;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class Queried
{
  static Mini database;
  static InventoryDB inventory;
  static ResultSetHandler<String> returnName = new ResultSetHandler() {
    public String handle(ResultSet rs) throws SQLException {
      if (rs.next()) {
        return rs.getString("name");
      }
      return null;
    }
  };

  static ResultSetHandler<List<String>> returnList = new ResultSetHandler() { private List<String> accounts;

    public List<String> handle(ResultSet rs) throws SQLException { this.accounts = new ArrayList();

      while (rs.next()) {
        this.accounts.add(rs.getString("username"));
      }
      return this.accounts;
    }
  };

  static ResultSetHandler<Boolean> returnBoolean = new ResultSetHandler() {
    public Boolean handle(ResultSet rs) throws SQLException {
      return Boolean.valueOf(rs.next());
    }
  };

  static ResultSetHandler<Double> returnBalance = new ResultSetHandler() {
    public Double handle(ResultSet rs) throws SQLException {
      if (rs.next()) return Double.valueOf(rs.getDouble("balance"));
      return null;
    }
  };

  static ResultSetHandler<Integer> returnStatus = new ResultSetHandler() {
    public Integer handle(ResultSet rs) throws SQLException {
      if (rs.next()) return Integer.valueOf(rs.getInt("status"));
      return null;
    }
  };

  static boolean useOrbDB()
  {
    if (!iConomy.Database.getType().toString().equalsIgnoreCase("orbdb")) {
      return false;
    }
    if (database == null) {
      database = iConomy.Database.getDatabase();
    }
    return true;
  }

  static boolean useMiniDB() {
    if (!iConomy.Database.getType().toString().equalsIgnoreCase("minidb")) {
      return false;
    }
    if (database == null) {
      database = iConomy.Database.getDatabase();
    }
    return true;
  }

  static boolean useInventoryDB() {
    if (!iConomy.Database.getType().toString().equalsIgnoreCase("inventorydb")) {
      return false;
    }
    if (inventory == null) {
      inventory = iConomy.Database.getInventoryDatabase();
    }
    if (database == null) {
      database = iConomy.Database.getDatabase();
    }
    return true;
  }

  static List<String> accountList() {
    List accounts = new ArrayList();

    if ((useMiniDB()) || (useInventoryDB()) || (useOrbDB())) {
      if (useInventoryDB()) {
        accounts.addAll(inventory.getAllPlayers());
      }
      if (useOrbDB()) {
        for (Player p : iConomy.Server.getOnlinePlayers())
          accounts.add(p.getName());
      }
      accounts.addAll(database.getIndices().keySet());

      return accounts;
    }
    try
    {
      QueryRunner run = new QueryRunner();
      Connection c = iConomy.Database.getConnection();
      try
      {
        String t = Constants.Nodes.DatabaseTable.toString();
        accounts = (List)run.query(c, "SELECT username FROM " + t, returnList);
      } catch (SQLException ex) {
        System.out.println("[iConomy] Error issueing SQL query: " + ex);
      } finally {
        DbUtils.close(c);
      }
    } catch (SQLException ex) {
      System.out.println("[iConomy] Database Error: " + ex);
    }

    return accounts;
  }

  static List<Account> topAccounts(int amount) {
    Accounts Accounts = new Accounts();
    List accounts = new ArrayList();
    List finals = new ArrayList();
    List total = new ArrayList();

    if ((useMiniDB()) || (useInventoryDB()) || (useOrbDB())) {
      if (useInventoryDB()) {
        total.addAll(inventory.getAllPlayers());
      }
      if (useOrbDB()) {
        for (Player p : iConomy.Server.getOnlinePlayers())
          total.add(p.getName());
      }
      total.addAll(database.getIndices().keySet());
    } else {
      try {
        QueryRunner run = new QueryRunner();
        Connection c = iConomy.Database.getConnection();
        try
        {
          String t = Constants.Nodes.DatabaseTable.toString();
          total = (List)run.query(c, "SELECT username FROM " + t + " WHERE status <> 1 ORDER BY balance DESC LIMIT " + amount, returnList);
        } catch (SQLException ex) {
          System.out.println("[iConomy] Error issueing SQL query: " + ex);
        } finally {
          DbUtils.close(c);
        }
      } catch (SQLException ex) {
        System.out.println("[iConomy] Database Error: " + ex);
      }
    }

    for (Iterator it = total.iterator(); it.hasNext(); ) {
      String player = (String)it.next();
      if ((useMiniDB()) || (useInventoryDB()) || (useOrbDB()))
        accounts.add(Accounts.get(player));
      else {
        finals.add(new Account(player));
      }
    }

    if ((useMiniDB()) || (useInventoryDB()) || (useOrbDB())) {
      Collections.sort(accounts, new MoneyComparator());

      if (amount > accounts.size()) {
        amount = accounts.size();
      }
      for (int i = 0; i < amount; i++) {
        if (((Account)accounts.get(i)).getStatus().intValue() == 1) {
          i--;
        }
        else {
          finals.add(accounts.get(i));
        }
      }
    }
    return finals;
  }

  static boolean createAccount(String name, Double balance, Integer status) {
    Boolean created = Boolean.valueOf(false);

    if ((useMiniDB()) || (useInventoryDB()) || (useOrbDB())) {
      if (hasAccount(name)) {
        return false;
      }
      if ((useOrbDB()) && 
        (iConomy.Server.getPlayer(name) != null)) {
        return false;
      }
      if ((useInventoryDB()) && 
        (inventory.dataExists(name))) {
        return false;
      }
      Arguments Row = new Arguments(name);
      Row.setValue("balance", balance);
      Row.setValue("status", status);

      database.addIndex(Row.getKey(), Row);
      database.update();

      return true;
    }
    try
    {
      QueryRunner run = new QueryRunner();
      Connection c = iConomy.Database.getConnection();
      try
      {
        String t = Constants.Nodes.DatabaseTable.toString();
        Integer amount = Integer.valueOf(run.update(c, "INSERT INTO " + t + "(username, balance, status) values (?, ?, ?)", new Object[] { name.toLowerCase(), balance, status }));

        if (amount.intValue() > 0)
          created = Boolean.valueOf(true);
      } catch (SQLException ex) {
        System.out.println("[iConomy] Error issueing SQL query: " + ex);
      } finally {
        DbUtils.close(c);
      }
    } catch (SQLException ex) {
      System.out.println("[iConomy] Database Error: " + ex);
    }

    return false;
  }

  static boolean removeAccount(String name) {
    Boolean removed = Boolean.valueOf(false);

    if ((useMiniDB()) || (useInventoryDB()) || (useOrbDB())) {
      if (database.hasIndex(name)) {
        database.removeIndex(name);
        database.update();

        return true;
      }

      return false;
    }
    try
    {
      QueryRunner run = new QueryRunner();
      Connection c = iConomy.Database.getConnection();
      try
      {
        String t = Constants.Nodes.DatabaseTable.toString();
        Integer amount = Integer.valueOf(run.update(c, "DELETE FROM " + t + " WHERE username=?", name.toLowerCase()));

        if (amount.intValue() > 0)
          removed = Boolean.valueOf(true);
      } catch (SQLException ex) {
        System.out.println("[iConomy] Error issueing SQL query: " + ex);
      } finally {
        DbUtils.close(c);
      }
    } catch (SQLException ex) {
      System.out.println("[iConomy] Database Error: " + ex);
    }

    return removed.booleanValue();
  }

  static boolean hasAccount(String name) {
    Boolean exists = Boolean.valueOf(false);

    if ((useMiniDB()) || (useInventoryDB()) || (useOrbDB())) {
      if ((useInventoryDB()) && 
        (inventory.dataExists(name))) {
        return true;
      }
      if ((useOrbDB()) && 
        (iConomy.Server.getPlayer(name) != null)) {
        return true;
      }
      return database.hasIndex(name);
    }
    try
    {
      QueryRunner run = new QueryRunner();
      Connection c = iConomy.Database.getConnection();
      try
      {
        String t = Constants.Nodes.DatabaseTable.toString();
        exists = (Boolean)run.query(c, "SELECT id FROM " + t + " WHERE username=?", returnBoolean, new Object[] { name.toLowerCase() });
      } catch (SQLException ex) {
        System.out.println("[iConomy] Error issueing SQL query: " + ex);
      } finally {
        DbUtils.close(c);
      }
    } catch (SQLException ex) {
      System.out.println("[iConomy] Database Error: " + ex);
    }

    return exists.booleanValue();
  }

  static double getBalance(String name) {
    Double balance = Constants.Nodes.Balance.getDouble();

    if (!hasAccount(name)) {
      return balance.doubleValue();
    }
    if ((useMiniDB()) || (useInventoryDB()) || (useOrbDB())) {
      if ((useInventoryDB()) && 
        (inventory.dataExists(name))) {
        return inventory.getBalance(name);
      }
      if ((useOrbDB()) && 
        (iConomy.Server.getPlayer(name) != null)) {
        return iConomy.Server.getPlayer(name).getTotalExperience();
      }
      if (database.hasIndex(name)) {
        return database.getArguments(name).getDouble("balance").doubleValue();
      }
      return balance.doubleValue();
    }
    try
    {
      QueryRunner run = new QueryRunner();
      Connection c = iConomy.Database.getConnection();
      try
      {
        String t = Constants.Nodes.DatabaseTable.toString();
        balance = (Double)run.query(c, "SELECT balance FROM " + t + " WHERE username=?", returnBalance, new Object[] { name.toLowerCase() });
      } catch (SQLException ex) {
        System.out.println("[iConomy] Error issueing SQL query: " + ex);
      } finally {
        DbUtils.close(c);
      }
    } catch (SQLException ex) {
      System.out.println("[iConomy] Database Error: " + ex);
    }

    return balance.doubleValue();
  }

  static void setBalance(String name, double balance) {
    double original = 0.0D; double gain = 0.0D; double loss = 0.0D;

    if (Constants.Nodes.Logging.getBoolean().booleanValue()) {
      original = getBalance(name);
      gain = balance - original;
      loss = original - balance;
    }

    if (!hasAccount(name)) {
      createAccount(name, Double.valueOf(balance), Integer.valueOf(0));

      if (Constants.Nodes.Logging.getBoolean().booleanValue()) {
        if (gain < 0.0D) gain = 0.0D;
        if (loss < 0.0D) loss = 0.0D;

        Transactions.insert(new Transaction("setBalance", "System", name).setFromBalance(Double.valueOf(original)).setToBalance(Double.valueOf(balance)).setGain(Double.valueOf(gain)).setLoss(Double.valueOf(loss)).setSet(Double.valueOf(balance)));
      }

      return;
    }

    if ((useMiniDB()) || (useInventoryDB()) || (useOrbDB())) {
      if ((useInventoryDB()) && 
        (inventory.dataExists(name))) {
        inventory.setBalance(name, balance); return;
      }

      if (useOrbDB()) {
        Player gainer = iConomy.Server.getPlayer(name);

        if (gainer != null) {
          if (balance < gainer.getTotalExperience()) {
            int amount = (int)(gainer.getTotalExperience() - balance);
            for (int i = 0; i < amount; i++)
              if (gainer.getExp() > 0.0F) {
                gainer.setExp(gainer.getExp() - 1.0F); } else {
                if (gainer.getTotalExperience() <= 0) break;
                gainer.setTotalExperience(gainer.getTotalExperience() - 1);
              }
          }
          else
          {
            int amount = (int)(balance - gainer.getTotalExperience());

            for (int i = 0; i < amount; i++)
              gainer.setExp(gainer.getExp() + 1.0F);
          }
        }
        return;
      }

      if (database.hasIndex(name)) {
        database.setArgument(name, "balance", Double.valueOf(balance));
        database.update();
      }

      return;
    }
    try
    {
      QueryRunner run = new QueryRunner();
      Connection c = iConomy.Database.getConnection();
      try
      {
        String t = Constants.Nodes.DatabaseTable.toString();
        update = run.update(c, "UPDATE " + t + " SET balance=? WHERE username=?", new Object[] { Double.valueOf(balance), name.toLowerCase() });
      }
      catch (SQLException ex)
      {
        int update;
        System.out.println("[iConomy] Error issueing SQL query: " + ex);
      } finally {
        DbUtils.close(c);
      }
    } catch (SQLException ex) {
      System.out.println("[iConomy] Database Error: " + ex);
    }
  }

  static void doInterest(String query, LinkedHashMap<String, HashMap<String, Object>> queries) {
    Object[][] parameters = new Object[queries.size()][2];

    int i = 0;
    for (String name : queries.keySet()) {
      double balance = ((Double)((HashMap)queries.get(name)).get("balance")).doubleValue();
      double original = 0.0D; double gain = 0.0D; double loss = 0.0D;

      if (Constants.Nodes.Logging.getBoolean().booleanValue()) {
        original = getBalance(name);
        gain = balance - original;
        loss = original - balance;
      }

      if ((!useInventoryDB()) && (!useMiniDB()) && (!useOrbDB())) {
        parameters[i][0] = Double.valueOf(balance);
        parameters[i][1] = name;

        i++;
      } else if (useMiniDB()) {
        if (!hasAccount(name)) {
          continue;
        }
        database.setArgument(name, "balance", Double.valueOf(balance));
        database.update();
      } else if (useInventoryDB()) {
        if (inventory.dataExists(name)) {
          inventory.setBalance(name, balance);
        } else if (database.hasIndex(name)) {
          database.setArgument(name, "balance", Double.valueOf(balance));
          database.update();
        }
      } else if (useOrbDB()) {
        if (!hasAccount(name)) {
          continue;
        }
        Player gainer = iConomy.Server.getPlayer(name);

        if (gainer != null) {
          setBalance(name, balance);
        }
      }
      if (Constants.Nodes.Logging.getBoolean().booleanValue()) {
        if (gain < 0.0D) gain = 0.0D;
        if (loss < 0.0D) loss = 0.0D;

        Transactions.insert(new Transaction("Interest", "System", name).setFromBalance(Double.valueOf(original)).setToBalance(Double.valueOf(balance)).setGain(Double.valueOf(gain)).setLoss(Double.valueOf(loss)).setSet(Double.valueOf(balance)));
      }

    }

    if ((!useInventoryDB()) && (!useMiniDB()) && (!useOrbDB()))
      Thrun.init(new Runnable(query, parameters) {
        public void run() {
          try {
            QueryRunner run = new QueryRunner();
            Connection c = iConomy.Database.getConnection();
            try
            {
              run.batch(c, this.val$query, this.val$parameters);
            } catch (SQLException ex) {
              System.out.println("[iConomy] Error with batching: " + ex);
            } finally {
              DbUtils.close(c);
            }
          } catch (SQLException ex) {
            System.out.println("[iConomy] Database Error: " + ex);
          }
        } } );
  }

  public static void purgeDatabase() {
    if ((useMiniDB()) || (useInventoryDB()) || (useOrbDB())) {
      for (String index : database.getIndices().keySet()) {
        if (database.getArguments(index).getDouble("balance") == Constants.Nodes.Balance.getDouble())
          database.removeIndex(index);
      }
      database.update();

      if (useInventoryDB()) {
        for (Player p : iConomy.Server.getOnlinePlayers())
          if ((inventory.dataExists(p.getName())) && (inventory.getBalance(p.getName()) == Constants.Nodes.Balance.getDouble().doubleValue()))
            inventory.setBalance(p.getName(), 0.0D);
      }
      if (useOrbDB()) {
        for (Player p : iConomy.Server.getOnlinePlayers())
          if (p.getExp() == Constants.Nodes.Balance.getDouble().doubleValue())
            p.setExp(0.0F);
      }
      return;
    }

    Thrun.init(new Runnable() {
      public void run() {
        try {
          QueryRunner run = new QueryRunner();
          Connection c = iConomy.Database.getConnection();
          try
          {
            String t = Constants.Nodes.DatabaseTable.toString();
            amount = Integer.valueOf(run.update(c, "DELETE FROM " + t + " WHERE balance=?", Constants.Nodes.Balance.getDouble()));
          }
          catch (SQLException ex)
          {
            Integer amount;
            System.out.println("[iConomy] Error issueing SQL query: " + ex);
          } finally {
            DbUtils.close(c);
          }
        } catch (SQLException ex) {
          System.out.println("[iConomy] Database Error: " + ex);
        }
      } } );
  }

  static void emptyDatabase() {
    if ((useMiniDB()) || (useInventoryDB()) || (useOrbDB())) {
      for (String index : database.getIndices().keySet()) {
        database.removeIndex(index);
      }
      database.update();

      if (useInventoryDB()) {
        for (Player p : iConomy.Server.getOnlinePlayers())
          if (inventory.dataExists(p.getName()))
            inventory.setBalance(p.getName(), 0.0D);
      }
      if (useOrbDB()) {
        for (Player p : iConomy.Server.getOnlinePlayers())
          p.setExp(0.0F);
      }
      return;
    }

    Thrun.init(new Runnable() {
      public void run() {
        try {
          QueryRunner run = new QueryRunner();
          Connection c = iConomy.Database.getConnection();
          try
          {
            String t = Constants.Nodes.DatabaseTable.toString();
            amount = Integer.valueOf(run.update(c, "TRUNCATE TABLE " + t));
          }
          catch (SQLException ex)
          {
            Integer amount;
            System.out.println("[iConomy] Error issueing SQL query: " + ex);
          } finally {
            DbUtils.close(c);
          }
        } catch (SQLException ex) {
          System.out.println("[iConomy] Database Error: " + ex);
        }
      } } );
  }

  static Integer getStatus(String name) {
    int status = 0;

    if (!hasAccount(name)) {
      return Integer.valueOf(-1);
    }
    if (useMiniDB()) {
      return database.getArguments(name).getInteger("status");
    }
    if (useInventoryDB()) {
      return Integer.valueOf(database.hasIndex(name) ? database.getArguments(name).getInteger("status").intValue() : inventory.dataExists(name) ? 1 : 0);
    }
    if (useOrbDB())
      return Integer.valueOf(database.hasIndex(name) ? database.getArguments(name).getInteger("status").intValue() : iConomy.Server.getPlayer(name) != null ? 1 : 0);
    try
    {
      QueryRunner run = new QueryRunner();
      Connection c = iConomy.Database.getConnection();
      try
      {
        String t = Constants.Nodes.DatabaseTable.toString();
        status = ((Integer)run.query(c, "SELECT status FROM " + t + " WHERE username=?", returnStatus, new Object[] { name.toLowerCase() })).intValue();
      } catch (SQLException ex) {
        System.out.println("[iConomy] Error issueing SQL query: " + ex);
      } finally {
        DbUtils.close(c);
      }
    } catch (SQLException ex) {
      System.out.println("[iConomy] Database Error: " + ex);
    }

    return Integer.valueOf(status);
  }

  static void setStatus(String name, int status) {
    if (!hasAccount(name)) {
      return;
    }
    if (useMiniDB()) {
      database.setArgument(name, "status", Integer.valueOf(status));
      database.update();

      return;
    }

    if ((useInventoryDB()) || (useOrbDB())) {
      if (database.hasIndex(name)) {
        database.setArgument(name, "status", Integer.valueOf(status));
        database.update();
      }

      return;
    }
    try
    {
      QueryRunner run = new QueryRunner();
      Connection c = iConomy.Database.getConnection();
      try
      {
        String t = Constants.Nodes.DatabaseTable.toString();
        update = run.update(c, "UPDATE " + t + " SET status=? WHERE username=?", new Object[] { Integer.valueOf(status), name.toLowerCase() });
      }
      catch (SQLException ex)
      {
        int update;
        System.out.println("[iConomy] Error issueing SQL query: " + ex);
      } finally {
        DbUtils.close(c);
      }
    } catch (SQLException ex) {
      System.out.println("[iConomy] Database Error: " + ex);
    }
  }
}