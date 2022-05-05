package sm.claudio.imaging.sys;

import java.io.Serializable;

/**
 * Questa classe ha l'unico scopo di mantenere la versione degli standard
 * corrente. Bisogna mettere il branch corrente e la versione corrente.
 * 
 * 
 */
public class Versione implements Serializable {
  private static final long  serialVersionUID = 7926044879949766871L;

  /** Nome del branch a cui appartiene il progetto */
  public static final String BRANCH           = "HEAD";

  /** Nome Applicativo */
  public static final String NOME_APPL        = "Imaging";

  /** Nome Applicativo */
  public static final String DESC_APPL        = "Conversione nomi foto";

  /** logo grande, mi aspetto che sia sotto /newappl/images */
  public static final String LOGO_BIG_APPL    = "logo128.gif";
  /** logo piccolo, mi aspetto che sia sotto /newappl/images */
  public static final String LOGO_SMALL_APPL  = "logo16.gif";

  /** Major Version */
  public static final int    APP_MAX_VERSION  = 1;
  /** Minor Version */
  public static final int    APP_MIN_VERSION  = 0;
  /** Build Version */
  public static final int    APP_BUILD        = 6;

  // e oggi esteso ${dh:CSZ_DATEDEPLOY}
  public static final String CSZ_DATEDEPLOY   = "05/05/2022 18:22:38";

  /** il nome dell'elemento in cui racchiudere l'XML di questa classe */
  private static String      mainElem;

  static {
    mainElem = " ";
  }

  public static void main(String[] args) {
    System.out.println(DESC_APPL + " " + getVersion());
  }

  /**
   * Costruttore vuoto che inizializza le variabili interne per avere un XML
   * corretto.
   */
  public Versione() {
  }

  /**
   * Ritorna la versione corrente degli standard nella forma
   * 
   * <pre>
   * maxver.minver.build
   * </pre>
   * 
   * @return versione applicativo
   */
  public static String getVersion() {
    String szVer = String.format("%s %d.%d.%d", mainElem, toi(APP_MAX_VERSION), toi(APP_MIN_VERSION), toi(APP_BUILD));
    return szVer;
  }

  @Override
  public String toString() {
    return getVersion();
  }

  private static Integer toi(int i) {
    return Integer.valueOf(i);
  }

  public static String getVersionEx() {
    String sz = String.format("%s: %s ver. %s pubbl.il %s", NOME_APPL, DESC_APPL, getVersion(), CSZ_DATEDEPLOY);
    return sz;
  }
}
