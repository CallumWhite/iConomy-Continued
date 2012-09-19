package com.iCo6.util.org.apache.commons.dbutils.handlers;

import com.iCo6.util.org.apache.commons.dbutils.RowProcessor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class KeyedHandler extends AbstractKeyedHandler<Object, Map<String, Object>>
{
  protected final RowProcessor convert;
  protected final int columnIndex;
  protected final String columnName;

  public KeyedHandler()
  {
    this(ArrayHandler.ROW_PROCESSOR, 1, null);
  }

  public KeyedHandler(RowProcessor convert)
  {
    this(convert, 1, null);
  }

  public KeyedHandler(int columnIndex)
  {
    this(ArrayHandler.ROW_PROCESSOR, columnIndex, null);
  }

  public KeyedHandler(String columnName)
  {
    this(ArrayHandler.ROW_PROCESSOR, 1, columnName);
  }

  private KeyedHandler(RowProcessor convert, int columnIndex, String columnName)
  {
    this.convert = convert;
    this.columnIndex = columnIndex;
    this.columnName = columnName;
  }

  protected Object createKey(ResultSet rs)
    throws SQLException
  {
    return this.columnName == null ? rs.getObject(this.columnIndex) : rs.getObject(this.columnName);
  }

  protected Map<String, Object> createRow(ResultSet rs)
    throws SQLException
  {
    return this.convert.toMap(rs);
  }
}