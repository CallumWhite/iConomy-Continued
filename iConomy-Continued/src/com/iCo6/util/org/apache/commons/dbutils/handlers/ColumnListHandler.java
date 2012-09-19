package com.iCo6.util.org.apache.commons.dbutils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ColumnListHandler extends AbstractListHandler<Object>
{
  private final int columnIndex;
  private final String columnName;

  public ColumnListHandler()
  {
    this(1, null);
  }

  public ColumnListHandler(int columnIndex)
  {
    this(columnIndex, null);
  }

  public ColumnListHandler(String columnName)
  {
    this(1, columnName);
  }

  private ColumnListHandler(int columnIndex, String columnName)
  {
    this.columnIndex = columnIndex;
    this.columnName = columnName;
  }

  protected Object handleRow(ResultSet rs)
    throws SQLException
  {
    if (this.columnName == null) {
      return rs.getObject(this.columnIndex);
    }
    return rs.getObject(this.columnName);
  }
}