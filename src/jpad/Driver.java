package jpad;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 * jpad stands for Java Production Auditing Driver
 * 
 * It is a very small lightweight proxy JDBC driver
 * that sits between your application and the real JDBC driver.
 * 
 * It intercepts method calls for certain key classes
 * and asynchronously passes each method call and its arguments
 * to another event handling layer that can interpret, analyze, store, log, etc.
 * interesting things about how your application is accessing the database.
 * 
 * @author arthur
 */
public class Driver implements java.sql.Driver
{
  private static final String prefix = "jpad:";
  private static final int prefixLen = prefix.length();
  private static final Thread eventHandlerThread = new Thread(new EventHandler());

  private Driver(){}

  static
  {
    try
    {
      // TODO: need to manage life cycle of this thread very carefully
      // probably need a shutdown hook for it too...
      eventHandlerThread.start();
      DriverManager.registerDriver(new Driver());
    }
    catch (SQLException s)
    {
      // this exception should never be thrown, JDBC just defines it
      // for completeness
      throw (RuntimeException) new RuntimeException
        ("could not register jpad driver!").initCause(s);
    }
    
    // testing
    // TODO: jpad driver should be loaded automatically via JDBC 4 mechanisms so that
    // the jpad driver itself never need be instantiated via Class.forName !
    try {
      Class.forName("net.sourceforge.jtds.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public Connection connect(String url, Properties info) throws SQLException {
    if (url != null && url.startsWith(prefix))
    {
      url = url.substring(prefixLen);
      Connection conn = DriverManager.getConnection(url, info);
      if (conn != null)
      {
        return (Connection) (new Handler(null, conn, Connection.class)).src;
      }
    }
    return null;
  }

  public boolean acceptsURL(String url) throws SQLException {
    return url != null && url.startsWith(prefix);
  }

  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
      throws SQLException {
    return null;
  }

  public int getMajorVersion() {
    return 0;
  }

  public int getMinorVersion() {
    return 1;
  }

  /**
   * We assume that we are jdbc compliant, although this is really just an optimistic guess.
   */
  public boolean jdbcCompliant() {
    return true;
  }
}
