package com.iCo6.util.org.apache.commons.dbutils.handlers;

import com.iCo6.util.org.apache.commons.dbutils.ResultSetHandler;
import com.iCo6.util.org.apache.commons.dbutils.RowProcessor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class MapHandler
  implements ResultSetHandler<Map<String, Object>>
{
  private final RowProcessor convert;

  public MapHandler()
  {
    this(ArrayHandler.ROW_PROCESSOR);
  }

  public MapHandler(RowProcessor convert)
  {
    this.convert = convert;
  }

  public Map<String, Object> handle(ResultSet rs)
    throws SQLException
  {
    return rs.next() ? this.convert.toMap(rs) : null;
  }
}