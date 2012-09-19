package com.iCo6.util.org.apache.commons.dbutils.wrappers;

import com.iCo6.util.org.apache.commons.dbutils.ProxyFactory;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class SqlNullCheckedResultSet
  implements InvocationHandler
{
  private static final Map<String, Method> nullMethods = new HashMap();
  private static final ProxyFactory factory;
  private InputStream nullAsciiStream = null;
  private BigDecimal nullBigDecimal = null;
  private InputStream nullBinaryStream = null;
  private Blob nullBlob = null;
  private boolean nullBoolean = false;
  private byte nullByte = 0;
  private byte[] nullBytes = null;
  private Reader nullCharacterStream = null;
  private Clob nullClob = null;
  private Date nullDate = null;
  private double nullDouble = 0.0D;
  private float nullFloat = 0.0F;
  private int nullInt = 0;
  private long nullLong = 0L;
  private Object nullObject = null;
  private Ref nullRef = null;
  private short nullShort = 0;
  private String nullString = null;
  private Time nullTime = null;
  private Timestamp nullTimestamp = null;
  private URL nullURL = null;
  private final ResultSet rs;

  public static ResultSet wrap(ResultSet rs)
  {
    return factory.createResultSet(new SqlNullCheckedResultSet(rs));
  }

  public SqlNullCheckedResultSet(ResultSet rs)
  {
    this.rs = rs;
  }

  public InputStream getNullAsciiStream()
  {
    return this.nullAsciiStream;
  }

  public BigDecimal getNullBigDecimal()
  {
    return this.nullBigDecimal;
  }

  public InputStream getNullBinaryStream()
  {
    return this.nullBinaryStream;
  }

  public Blob getNullBlob()
  {
    return this.nullBlob;
  }

  public boolean getNullBoolean()
  {
    return this.nullBoolean;
  }

  public byte getNullByte()
  {
    return this.nullByte;
  }

  public byte[] getNullBytes()
  {
    if (this.nullBytes == null) return null;
    byte[] copy = new byte[this.nullBytes.length];
    System.arraycopy(this.nullBytes, 0, copy, 0, this.nullBytes.length);
    return copy;
  }

  public Reader getNullCharacterStream()
  {
    return this.nullCharacterStream;
  }

  public Clob getNullClob()
  {
    return this.nullClob;
  }

  public Date getNullDate()
  {
    return this.nullDate;
  }

  public double getNullDouble()
  {
    return this.nullDouble;
  }

  public float getNullFloat()
  {
    return this.nullFloat;
  }

  public int getNullInt()
  {
    return this.nullInt;
  }

  public long getNullLong()
  {
    return this.nullLong;
  }

  public Object getNullObject()
  {
    return this.nullObject;
  }

  public Ref getNullRef()
  {
    return this.nullRef;
  }

  public short getNullShort()
  {
    return this.nullShort;
  }

  public String getNullString()
  {
    return this.nullString;
  }

  public Time getNullTime()
  {
    return this.nullTime;
  }

  public Timestamp getNullTimestamp()
  {
    return this.nullTimestamp;
  }

  public URL getNullURL()
  {
    return this.nullURL;
  }

  public Object invoke(Object proxy, Method method, Object[] args)
    throws Throwable
  {
    Object result = method.invoke(this.rs, args);

    Method nullMethod = (Method)nullMethods.get(method.getName());

    return (nullMethod != null) && (this.rs.wasNull()) ? nullMethod.invoke(this, (Object[])null) : result;
  }

  public void setNullAsciiStream(InputStream nullAsciiStream)
  {
    this.nullAsciiStream = nullAsciiStream;
  }

  public void setNullBigDecimal(BigDecimal nullBigDecimal)
  {
    this.nullBigDecimal = nullBigDecimal;
  }

  public void setNullBinaryStream(InputStream nullBinaryStream)
  {
    this.nullBinaryStream = nullBinaryStream;
  }

  public void setNullBlob(Blob nullBlob)
  {
    this.nullBlob = nullBlob;
  }

  public void setNullBoolean(boolean nullBoolean)
  {
    this.nullBoolean = nullBoolean;
  }

  public void setNullByte(byte nullByte)
  {
    this.nullByte = nullByte;
  }

  public void setNullBytes(byte[] nullBytes)
  {
    byte[] copy = new byte[nullBytes.length];
    System.arraycopy(nullBytes, 0, copy, 0, nullBytes.length);
    this.nullBytes = copy;
  }

  public void setNullCharacterStream(Reader nullCharacterStream)
  {
    this.nullCharacterStream = nullCharacterStream;
  }

  public void setNullClob(Clob nullClob)
  {
    this.nullClob = nullClob;
  }

  public void setNullDate(Date nullDate)
  {
    this.nullDate = nullDate;
  }

  public void setNullDouble(double nullDouble)
  {
    this.nullDouble = nullDouble;
  }

  public void setNullFloat(float nullFloat)
  {
    this.nullFloat = nullFloat;
  }

  public void setNullInt(int nullInt)
  {
    this.nullInt = nullInt;
  }

  public void setNullLong(long nullLong)
  {
    this.nullLong = nullLong;
  }

  public void setNullObject(Object nullObject)
  {
    this.nullObject = nullObject;
  }

  public void setNullRef(Ref nullRef)
  {
    this.nullRef = nullRef;
  }

  public void setNullShort(short nullShort)
  {
    this.nullShort = nullShort;
  }

  public void setNullString(String nullString)
  {
    this.nullString = nullString;
  }

  public void setNullTime(Time nullTime)
  {
    this.nullTime = nullTime;
  }

  public void setNullTimestamp(Timestamp nullTimestamp)
  {
    this.nullTimestamp = nullTimestamp;
  }

  public void setNullURL(URL nullURL)
  {
    this.nullURL = nullURL;
  }

  static
  {
    Method[] methods = SqlNullCheckedResultSet.class.getMethods();
    for (int i = 0; i < methods.length; i++) {
      String methodName = methods[i].getName();

      if (methodName.startsWith("getNull")) {
        String normalName = "get" + methodName.substring(7);
        nullMethods.put(normalName, methods[i]);
      }

    }

    factory = ProxyFactory.instance();
  }
}