package com.iCo6.util.org.apache.commons.dbutils.handlers;

import com.iCo6.util.org.apache.commons.dbutils.ResultSetHandler;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractKeyedHandler<K, V>
  implements ResultSetHandler<Map<K, V>>
{
  public Map<K, V> handle(ResultSet rs)
    throws SQLException
  {
    Map result = createMap();
    while (rs.next()) {
      result.put(createKey(rs), createRow(rs));
    }
    return result;
  }

  protected Map<K, V> createMap()
  {
    return new HashMap();
  }

  protected abstract K createKey(ResultSet paramResultSet)
    throws SQLException;

  protected abstract V createRow(ResultSet paramResultSet)
    throws SQLException;
}