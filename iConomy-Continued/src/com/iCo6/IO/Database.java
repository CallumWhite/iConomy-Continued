package com.iCo6.IO;

import com.iCo6.Constants.Nodes;
import com.iCo6.IO.exceptions.MissingDriver;
import com.iCo6.IO.mini.Mini;
import com.iCo6.iConomy;
import com.iCo6.util.Common;
import com.iCo6.util.org.apache.commons.dbutils.DbUtils;
import com.iCo6.util.org.apache.commons.dbutils.QueryRunner;
import com.iCo6.util.org.apache.commons.dbutils.ResultSetHandler;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database
{
  private String type;
  private String driver;
  private String url;
  private String username;
  private String password;
  ResultSetHandler<Boolean> returnBoolean = new ResultSetHandler() {
    public Boolean handle(ResultSet rs) {
      try {
        rs.next();
      } catch (SQLException ex) {
        return Boolean.valueOf(false);
      }

      return Boolean.valueOf(true);
    }
  };

  public Database(String type, String url, String username, String password)
    throws MissingDriver
  {
    this.type = type;
    this.url = ("jdbc:" + url);
    this.username = username;
    this.password = password;

    if (Common.matches(type, new String[] { "itemdb", "item", "items", "inventory", "invdb", "inventorydb", "flatfile", "ff", "mini", "minidb", "flat", "orbdb", "orb", "exp", "xp", "xpdb", "expdb" }))
    {
      return;
    }
    if (Common.matches(type, new String[] { "mysql", "mysqldb" })) {
      this.driver = "com.mysql.jdbc.Driver";
    }
    if (Common.matches(type, new String[] { "h2", "h2db", "h2sql" })) {
      this.driver = "org.h2.Driver";
    }
    if (Common.matches(type, new String[] { "postgresql", "postgre", "postgredb" })) {
      this.driver = "org.postgresql.Driver";
    }
    if (Common.matches(type, new String[] { "sqlite", "sqlite2", "sqlite3", "sqlitedb" })) {
      this.driver = "org.sqlite.JDBC";
    }
    if (this.driver == null) {
      return;
    }
    if (!DbUtils.loadDriver(this.driver))
      throw new MissingDriver("Please make sure the " + type + " driver library jar exists.");
  }

  public Connection getConnection() throws SQLException {
    return (this.username.isEmpty()) && (this.password.isEmpty()) ? DriverManager.getConnection(this.url) : DriverManager.getConnection(this.url, this.username, this.password);
  }

  public boolean tableExists(String table)
  {
    boolean exists = false;
    try
    {
      Connection conn = getConnection();
      QueryRunner run = new QueryRunner();
      try
      {
        String t = Constants.Nodes.DatabaseTable.toString();
        exists = ((Boolean)run.query(conn, "SELECT id FROM " + table, this.returnBoolean)).booleanValue();
      } finally {
        DbUtils.close(conn);
      }
    } catch (SQLException e) {
      exists = false;
    }

    return exists;
  }

  public Type getType() {
    return getType(this.type);
  }

  public static Type getType(String type) {
    if (Common.matches(type, new String[] { "flatfile", "ff", "mini", "minidb", "flat" })) {
      return Type.MiniDB;
    }
    if (Common.matches(type, new String[] { "itemdb", "item", "items", "inv", "inventory", "invdb", "inventorydb" })) {
      return Type.InventoryDB;
    }
    if (Common.matches(type, new String[] { "orbdb", "orb", "exp", "xp", "xpdb", "expdb" })) {
      return Type.OrbDB;
    }
    if (Common.matches(type, new String[] { "mysql", "mysqldb" })) {
      return Type.MySQL;
    }
    if (Common.matches(type, new String[] { "h2", "h2db", "h2sql" })) {
      return Type.H2DB;
    }
    if (Common.matches(type, new String[] { "postgresql", "postgre", "postgredb" })) {
      return Type.Postgre;
    }
    if (Common.matches(type, new String[] { "sqlite", "sqlite2", "sqlite3", "sqlitedb" })) {
      return Type.SQLite;
    }
    return Type.MiniDB;
  }

  public boolean isSQL()
  {
    return !Common.matches(this.type, new String[] { "itemdb", "item", "items", "inventory", "invdb", "inventorydb", "flatfile", "ff", "mini", "minidb", "flat", "orbdb", "orb", "exp", "xp", "xpdb", "expdb" });
  }

  public Mini getDatabase()
  {
    if (!Common.matches(this.type, new String[] { "itemdb", "item", "items", "inventory", "invdb", "inventorydb", "flatfile", "ff", "mini", "minidb", "flat", "orbdb", "orb", "exp", "xp", "xpdb", "expdb" }))
    {
      return null;
    }
    return new Mini(iConomy.directory.getPath(), "accounts.mini");
  }

  public Mini getTransactionDatabase() {
    if (!Common.matches(this.type, new String[] { "itemdb", "item", "items", "inventory", "invdb", "inventorydb", "flatfile", "ff", "mini", "minidb", "flat", "orbdb", "orb", "exp", "xp", "xpdb", "expdb" }))
    {
      return null;
    }
    return new Mini(iConomy.directory.getPath(), "transactions.mini");
  }

  public InventoryDB getInventoryDatabase() {
    if (!Common.matches(this.type, new String[] { "itemdb", "item", "items", "inv", "inventory", "invdb", "inventorydb" }))
    {
      return null;
    }
    return new InventoryDB();
  }

  public static enum Type
  {
    OrbDB, MiniDB, InventoryDB, MySQL, SQLite, Postgre, H2DB;
  }
}