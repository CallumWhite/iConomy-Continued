package com.iCo6.util.org.apache.commons.dbutils.handlers;

import com.iCo6.util.org.apache.commons.dbutils.ResultSetHandler;
import com.iCo6.util.org.apache.commons.dbutils.RowProcessor;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BeanHandler<T>
  implements ResultSetHandler<T>
{
  private final Class<T> type;
  private final RowProcessor convert;

  public BeanHandler(Class<T> type)
  {
    this(type, ArrayHandler.ROW_PROCESSOR);
  }

  public BeanHandler(Class<T> type, RowProcessor convert)
  {
    this.type = type;
    this.convert = convert;
  }

  public T handle(ResultSet rs)
    throws SQLException
  {
    return rs.next() ? this.convert.toBean(rs, this.type) : null;
  }
}