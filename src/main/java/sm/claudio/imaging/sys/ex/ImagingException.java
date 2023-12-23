package sm.claudio.imaging.sys.ex;

public class ImagingException extends Exception {

  /** serialVersionUID long */
  private static final long serialVersionUID = 7534038539443135465L;

  public ImagingException() {
    //
  }

  public ImagingException(String p_szMsg) {
    super(p_szMsg);
  }

  public ImagingException(String p_msg, Throwable p_ex) {
    super(p_msg, p_ex);
  }

}
