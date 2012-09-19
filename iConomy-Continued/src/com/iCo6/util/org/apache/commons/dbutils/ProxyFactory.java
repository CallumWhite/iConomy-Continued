package com.iCo6.util.org.apache.commons.dbutils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class ProxyFactory
{
  private static final ProxyFactory instance = new ProxyFactory();

  public static ProxyFactory instance()
  {
    return instance;
  }

  public <T> T newProxyInstance(Class<T> type, InvocationHandler handler)
  {
    return type.cast(Proxy.newProxyInstance(handler.getClass().getClassLoader(), new Class[] { type }, handler));
  }

  public CallableStatement createCallableStatement(InvocationHandler handler)
  {
    return (CallableStatement)newProxyInstance(CallableStatement.class, handler);
  }

  public Connection createConnection(InvocationHandler handler)
  {
    return (Connection)newProxyInstance(Connection.class, handler);
  }

  public Driver createDriver(InvocationHandler handler)
  {
    return (Driver)newProxyInstance(Driver.class, handler);
  }

  public PreparedStatement createPreparedStatement(InvocationHandler handler)
  {
    return (PreparedStatement)newProxyInstance(PreparedStatement.class, handler);
  }

  public ResultSet createResultSet(InvocationHandler handler)
  {
    return (ResultSet)newProxyInstance(ResultSet.class, handler);
  }

  public ResultSetMetaData createResultSetMetaData(InvocationHandler handler)
  {
    return (ResultSetMetaData)newProxyInstance(ResultSetMetaData.class, handler);
  }

  public Statement createStatement(InvocationHandler handler)
  {
    return (Statement)newProxyInstance(Statement.class, handler);
  }
}