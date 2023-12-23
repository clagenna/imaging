package sm.claudio.imaging.sys;

import org.apache.logging.log4j.Level;

import lombok.Getter;
import lombok.Setter;
import sm.claudio.imaging.sys.ex.ImagingLog4jRowException;

public class Log4jRow {

  @Getter @Setter
  private String logClass;
  @Getter @Setter
  private String szTime;
  @Getter @Setter
  private Level  level;
  @Getter @Setter
  private String message;

  public Log4jRow() {
    //
  }

  public Log4jRow(String[] arr) throws ImagingLog4jRowException {
    if (arr == null)
      throw new ImagingLog4jRowException("Null array");
    if (arr.length < 4)
      throw new ImagingLog4jRowException("Log4jRow.Log4jRow(), pochi campi:" + arr.length);
    setLogClass(arr[0]);
    setSzTime(arr[1]);
    setLevel(Level.valueOf(arr[2]));
    setMessage(arr[3]);
  }

  @Override
  public String toString() {
    String sz = String.format("%s %6s %s", szTime, level.toString(), message);
    return sz;
  }

  public String getTime() {
    String sz = szTime.substring(10).trim();
    int n = sz.indexOf(",");
    if (n >= 0)
      sz = sz.substring(0, n);
    return sz;
  }
}
