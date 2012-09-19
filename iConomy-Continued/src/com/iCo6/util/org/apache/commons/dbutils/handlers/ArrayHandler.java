package com.iCo6.util.org.apache.commons.dbutils.handlers;

import com.iCo6.util.org.apache.commons.dbutils.BasicRowProcessor;
import com.iCo6.util.org.apache.commons.dbutils.ResultSetHandler;
import com.iCo6.util.org.apache.commons.dbutils.RowProcessor;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArrayHandler
  implements ResultSetHandler<Object[]>
{
  static final RowProcessor ROW_PROCESSOR = new BasicRowProcessor();
  private final RowProcessor convert;

  public ArrayHandler()
  {
    this(ROW_PROCESSOR);
  }

  public ArrayHandler(RowProcessor convert)
  {
    this.convert = convert;
  }

  public Object[] handle(ResultSet rs)
    throws SQLException
  {
    return rs.next() ? this.convert.toArray(rs) : null;
  }
}