package com.iCo6.util.org.apache.commons.dbutils.handlers;

import com.iCo6.util.org.apache.commons.dbutils.RowProcessor;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArrayListHandler extends AbstractListHandler<Object[]>
{
  private final RowProcessor convert;

  public ArrayListHandler()
  {
    this(ArrayHandler.ROW_PROCESSOR);
  }

  public ArrayListHandler(RowProcessor convert)
  {
    this.convert = convert;
  }

  protected Object[] handleRow(ResultSet rs)
    throws SQLException
  {
    return this.convert.toArray(rs);
  }
}