package com.iCo6;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Constants
{
  public static void load(File configuration)
  {
    FileConfiguration config = YamlConfiguration.loadConfiguration(configuration);

    for (Nodes n : Nodes.values()) {
      if ((n.getNode().isEmpty()) || 
        (config.get(n.getNode()) == null)) continue;
      n.setValue(config.get(n.getNode()));
    }
  }

  public static enum Nodes
  {
    CodeName("", "April Fools"), 

    useHoldingsPermission("System.Permissions.Use.Holdings", Boolean.valueOf(false)), 

    Minor("System.Default.Currency.Minor", new ArrayList()), 
    Major("System.Default.Currency.Major", new ArrayList()), 

    MultiWorld("System.Default.Account.MultiWorld", Boolean.valueOf(false)), 
    Balance("System.Default.Account.Holdings", Double.valueOf(30.0D)), 

    AllowMinor("System.Formatting.Minor", Boolean.valueOf(false)), 
    isSplit("System.Formatting.Seperate", Boolean.valueOf(false)), 
    isSingle("System.Formatting.Single", Boolean.valueOf(false)), 

    Logging("System.Logging.Enabled", Boolean.valueOf(false)), 
    Purging("System.Purging.Enabled", Boolean.valueOf(true)), 

    Interest("System.Interest.Enabled", Boolean.valueOf(false)), 
    InterestOnline("System.Interest.Online", Boolean.valueOf(false)), 
    InterestTime("System.Interest.Interval.Seconds", Integer.valueOf(60)), 
    InterestPercentage("System.Interest.Amount.Percentage", Double.valueOf(0.0D)), 
    InterestCutoff("System.Interest.Amount.Cutoff", Double.valueOf(0.0D)), 
    InterestMin("System.Interest.Amount.Maximum", Double.valueOf(1.0D)), 
    InterestMax("System.Interest.Amount.Minimum", Double.valueOf(2.0D)), 

    DatabaseType("System.Database.Type", "MiniDB"), 
    DatabaseTable("System.Database.Table", "iConomy"), 
    DatabaseUrl("System.Database.URL", "mysql:\\\\localhost:3306\\iConomy"), 
    DatabaseUsername("System.Database.Username", "root"), 
    DatabasePassword("System.Database.Password", ""), 
    DatabaseMajorItem("System.Database.MajorItem", Integer.valueOf(266)), 
    DatabaseMinorItem("System.Database.MinorItem", Integer.valueOf(265)), 

    Convert("System.Database.Conversion.Enabled", Boolean.valueOf(false)), 
    ConvertFrom("System.Database.Conversion.Type", "H2DB"), 
    ConvertTable("System.Database.Conversion.Table", "iConomy"), 
    ConvertURL("System.Database.Conversion.URL", "mysql:\\\\localhost:3306\\iConomy"), 
    ConvertUsername("System.Database.Conversion.Username", "root"), 
    ConvertPassword("System.Database.Conversion.Password", ""), 
    ConvertAll("System.Database.Conversion.All", Boolean.valueOf(true));

    String node;
    Object value;

    private Nodes(String node, Object value) { this.node = node;
      this.value = value; }

    public String getNode()
    {
      return this.node;
    }

    public Object getValue() {
      return this.value;
    }

    public Boolean getBoolean() {
      return (Boolean)this.value;
    }

    public Integer getInteger() {
      if ((this.value instanceof Double)) {
        return Integer.valueOf(((Double)this.value).intValue());
      }
      return (Integer)this.value;
    }

    public Double getDouble() {
      if ((this.value instanceof Integer)) {
        return Double.valueOf(((Integer)this.value).intValue());
      }
      return (Double)this.value;
    }

    public Long getLong() {
      if ((this.value instanceof Integer)) {
        return Long.valueOf(((Integer)this.value).longValue());
      }
      return (Long)this.value;
    }

    public List<String> getStringList() {
      return (List)this.value;
    }

    public void setValue(Object value) {
      this.value = value;
    }

    public String toString()
    {
      return String.valueOf(this.value);
    }
  }

  public static enum Drivers
  {
    H2("http://mirror.nexua.org/Dependencies/h2.jar", "h2.jar"), 
    MySQL("http://mirror.nexua.org/Dependencies/mysql-connector-java-bin.jar", "mysql.jar"), 
    SQLite("http://mirror.nexua.org/Dependencies/sqlite-jdbc.jar", "sqlite.jar"), 
    Postgre("http://mirror.nexua.org/Dependencies/postgresql.jdbc4.jar", "postgresql.jar");

    String url;
    String filename;

    private Drivers(String url, String filename) { this.url = url;
      this.filename = filename; }

    public String getFilename()
    {
      return this.filename;
    }

    public String getUrl() {
      return this.url;
    }

    public String toString()
    {
      return this.url;
    }
  }
}