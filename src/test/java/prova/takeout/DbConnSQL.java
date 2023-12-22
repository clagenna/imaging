package prova.takeout;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.Getter;
import lombok.Setter;

public class DbConnSQL implements Closeable {

  private static DbConnSQL    s_inst;

  public static final String  CSZ_DRIVER = "com.mysql.cj.jdbc.Driver";
  private static final String CSZ_URL    = "jdbc:sqlserver://%s:%d;"   //
      + "database=%s;"                                                 //
      + "user=%s;"                                                     //
      + "password=%s;"                                                 //
      + "encrypt=true;"                                                //
      + "trustServerCertificate=false;"                                //
      + "loginTimeout=10;";
  private static final int    CN_SERVICE = 1433;

  @Getter @Setter
  private String              host;
  @Getter @Setter
  private int                 service;
  @Getter @Setter
  private String              dbname;
  @Getter @Setter
  private String              user;
  @Getter @Setter
  private String              passwd;
  @Getter
  private Connection          conn;

  public DbConnSQL() {
    if (s_inst != null)
      throw new UnsupportedOperationException("DBConn gia istanziata");
    s_inst = this;
  }

  public Connection doConn() {
    String szUrl = String.format(CSZ_URL, "localhost", //
        CN_SERVICE, //
        "banca", //
        "sqlgianni", //
        "sicuelserver");
    try {
      conn = DriverManager.getConnection(szUrl);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return conn;
  }

  @Override
  public void close() throws IOException {
    try {
      if (conn != null)
        conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    conn = null;
  }

}
