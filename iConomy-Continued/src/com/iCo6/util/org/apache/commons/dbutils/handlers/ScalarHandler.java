package com.iCo6.util.org.apache.commons.dbutils.handlers;

import com.iCo6.util.org.apache.commons.dbutils.ResultSetHandler;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ScalarHandler
  implements ResultSetHandler<Object>
{
  private final int columnIndex;
  private final String columnName;

  public ScalarHandler()
  {
    this(1, null);
  }

  public ScalarHandler(int columnIndex)
  {
    this(columnIndex, null);
  }

  public ScalarHandler(String columnName)
  {
    this(1, columnName);
  }

  private ScalarHandler(int columnIndex, String columnName)
  {
    this.columnIndex = columnIndex;
    this.columnName = columnName;
  }

  public Object handle(ResultSet rs)
    throws SQLException
  {
    if (rs.next()) {
      if (this.columnName == null) {
        return rs.getObject(this.columnIndex);
      }
      return rs.getObject(this.columnName);
    }

    return null;
  }
}