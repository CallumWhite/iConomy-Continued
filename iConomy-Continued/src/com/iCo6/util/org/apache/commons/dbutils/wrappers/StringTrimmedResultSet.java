package com.iCo6.util.org.apache.commons.dbutils.wrappers;

import com.iCo6.util.org.apache.commons.dbutils.ProxyFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSet;

public class StringTrimmedResultSet
  implements InvocationHandler
{
  private static final ProxyFactory factory = ProxyFactory.instance();
  private final ResultSet rs;

  public static ResultSet wrap(ResultSet rs)
  {
    return factory.createResultSet(new StringTrimmedResultSet(rs));
  }

  public StringTrimmedResultSet(ResultSet rs)
  {
    this.rs = rs;
  }

  public Object invoke(Object proxy, Method method, Object[] args)
    throws Throwable
  {
    Object result = method.invoke(this.rs, args);

    if ((method.getName().equals("getObject")) || (method.getName().equals("getString")))
    {
      if ((result instanceof String)) {
        result = ((String)result).trim();
      }
    }

    return result;
  }
}