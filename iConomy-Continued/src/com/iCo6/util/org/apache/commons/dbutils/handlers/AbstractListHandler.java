package com.iCo6.util.org.apache.commons.dbutils.handlers;

import com.iCo6.util.org.apache.commons.dbutils.ResultSetHandler;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListHandler<T>
  implements ResultSetHandler<List<T>>
{
  public List<T> handle(ResultSet rs)
    throws SQLException
  {
    List rows = new ArrayList();
    while (rs.next()) {
      rows.add(handleRow(rs));
    }
    return rows;
  }

  protected abstract T handleRow(ResultSet paramResultSet)
    throws SQLException;
}