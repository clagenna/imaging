package sm.claudio.imaging.sys.ex;

public class ImagingLog4jRowException extends ImagingException {

  /** long */
  private static final long serialVersionUID = 2271943661219302651L;

  public ImagingLog4jRowException() {
    //
  }

  public ImagingLog4jRowException(String p_msg) {
    super(p_msg);
  }

  public ImagingLog4jRowException(String p_msg, Throwable p_ex) {
    super(p_msg, p_ex);
  }

}
