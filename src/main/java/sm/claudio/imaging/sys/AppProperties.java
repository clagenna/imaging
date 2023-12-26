package sm.claudio.imaging.sys;

import java.beans.Beans;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;
import lombok.Setter;
import sm.claudio.imaging.swing.ImgModel;

public class AppProperties {

  private static final Logger  s_log                = LogManager.getLogger(AppProperties.class);
  private static final String  CSZ_PROP_FILE        = "imaging.properties";
  public static final String   CSZ_PROP_LASTDIR     = "last.dir";
  public static final String   CSZ_PROP_LASTFIL     = "last.fil";
  public static final String   CSZ_PROP_DIMFRAME_X  = "frame.dimx";
  public static final String   CSZ_PROP_DIMFRAME_Y  = "frame.dimy";
  public static final String   CSZ_PROP_POSFRAME_X  = "frame.posx";
  public static final String   CSZ_PROP_POSFRAME_Y  = "frame.posy";
  public static final String   CSZ_PROP_TBCOL       = "tbview.col.%s";

  private static final String  CSZ_TIPO_CAMBIO_NOME = "tipoCambioNome";
  private static final String  CSZ_PROP_LASTGPX     = "gps.last";
  private static final String  CSZ_PROP_SHOW_GMS    = "gps.show_gms";

  private static AppProperties s_inst;
  private ETipoCambioNome      tipoCambioNome;
  @Getter @Setter
  private Properties           properties;
  @Getter @Setter
  private File                 propertyFile;
  @Getter @Setter
  private ImgModel             model;

  public AppProperties() {
    if (AppProperties.s_inst != null && !Beans.isDesignTime())
      throw new UnsupportedOperationException("AppProperties gia' istanziato");
    AppProperties.s_inst = this;
  }

  public void openProperties() {
    if (getPropertyFile() == null)
      setPropertyFile(new File(AppProperties.CSZ_PROP_FILE));

    AppProperties.s_log.info("Apro il file properties {}", getPropertyFile().getAbsolutePath());
    setProperties(new Properties());
    if ( !getPropertyFile().exists()) {
      AppProperties.s_log.error("Il file di properties {} non esiste", AppProperties.CSZ_PROP_FILE);
      return;
    }
    try (InputStream is = new FileInputStream(getPropertyFile())) {
      setProperties(new Properties());
      getProperties().load(is);
      setPropertyFile(getPropertyFile());
    } catch (IOException e) {
      // e.printStackTrace();
      AppProperties.s_log.error("Errore apertura property file: {}", getPropertyFile().getAbsolutePath(), e);
    }
  }

  public void saveProperties() {
    try (OutputStream output = new FileOutputStream(getPropertyFile())) {
      getProperties().store(output, null);
      AppProperties.s_log.info("Salvo property file {}", getPropertyFile().getAbsolutePath());
    } catch (IOException e) {
      // e.printStackTrace();
      AppProperties.s_log.error("Errore scrittura property file: {}", getPropertyFile().getAbsolutePath(), e);
    }
  }

  public static AppProperties getInst() {
    return AppProperties.s_inst;
  }

  public String getLastDir() {
    String szRet = null;
    if (getProperties() != null)
      szRet = getProperties().getProperty(AppProperties.CSZ_PROP_LASTDIR);
    return szRet;
  }

  public void setLastDir(String p_lastDir) {
    if (getProperties() != null)
      if (p_lastDir != null)
        getProperties().setProperty(AppProperties.CSZ_PROP_LASTDIR, p_lastDir);
  }

  public String getLastFile() {
    String szRet = null;
    if (getProperties() != null)
      szRet = getProperties().getProperty(AppProperties.CSZ_PROP_LASTFIL);
    return szRet;
  }

  public void setLastFile(String p_lastFile) {
    if (getProperties() != null)
      if (p_lastFile != null)
        getProperties().setProperty(AppProperties.CSZ_PROP_LASTFIL, p_lastFile);
  }

  public String getLastGPX() {
    String szRet = null;
    if (getProperties() != null)
      szRet = getProperties().getProperty(AppProperties.CSZ_PROP_LASTGPX);
    return szRet;
  }

  public void setLastGPX(String p_lastGPX) {
    if (getProperties() != null)
      if (p_lastGPX != null)
        getProperties().setProperty(AppProperties.CSZ_PROP_LASTGPX, p_lastGPX);
  }

  public boolean isShowGMS() {
    return getBooleanPropVal(CSZ_PROP_SHOW_GMS);
  }

  public void setShowGMS(boolean pv) {
    getProperties().setProperty(CSZ_PROP_SHOW_GMS, String.valueOf(pv));
  }

  public ETipoCambioNome getTipoCambioNome() {
    if (tipoCambioNome == null) {
      if (getProperties().containsKey(CSZ_TIPO_CAMBIO_NOME)) {
        String sz = getPropVal(CSZ_TIPO_CAMBIO_NOME);
        try {
          tipoCambioNome = ETipoCambioNome.valueOf(sz);
        } catch (Exception e) {
          s_log.error("Il valore {}={} non e' interpreabile", CSZ_TIPO_CAMBIO_NOME, sz);
        }
      } else
        tipoCambioNome = ETipoCambioNome.piu1Secondo;
      getProperties().setProperty(CSZ_TIPO_CAMBIO_NOME, tipoCambioNome.toString());
    }
    return tipoCambioNome;
  }

  public void setTipoCambioNome(ETipoCambioNome p_t) {
    tipoCambioNome = p_t;
    getProperties().setProperty(CSZ_TIPO_CAMBIO_NOME, tipoCambioNome.toString());
  }

  public String getPropVal(String p_key) {
    String szRet = null;
    if (getProperties() != null)
      szRet = getProperties().getProperty(p_key);
    return szRet;
  }

  public void setPropVal(String p_key, String p_val) {
    if (getProperties() != null)
      if (p_val != null)
        getProperties().setProperty(p_key, p_val);
  }

  public void setPropVal(String p_key, int p_val) {
    setPropVal(p_key, String.valueOf(p_val));
  }

  public void setDoublePropVal(String p_key, double p_val) {
    String sz = String.format(Locale.US, "%.3f", p_val);
    setPropVal(p_key, sz);
  }

  public double getDoublePropVal(String p_key) {
    String sz = getPropVal(p_key);
    if (sz == null || sz.length() == 0)
      return 0f;
    return Double.parseDouble(sz);
  }

  public int getPropIntVal(String p_key) {
    Integer ii = Integer.valueOf(0);
    String sz = getPropVal(p_key);
    try {
      if (sz != null)
        ii = Integer.decode(sz);
    } catch (NumberFormatException e) {
      // e.printStackTrace();
    }
    return ii.intValue();
  }

  public boolean getBooleanPropVal(String p_key) {
    boolean bRet = false;
    String sz = getPropVal(p_key);
    if (sz == null)
      return bRet;
    sz = sz.toLowerCase();
    switch (sz) {
      case "vero":
      case "true":
      case "yes":
      case "y":
      case "t":
      case "1":
        bRet = true;
        break;
    }
    return bRet;
  }

  public void setBooleanPropVal(String p_key, boolean bVal) {
    setPropVal(p_key, Boolean.valueOf(bVal).toString());
  }
  //
  //  public ISwingLogger getSwingLogger() {
  //    return swingLogger;
  //  }
  //
  //  public void setSwingLogger(ISwingLogger swingLogger) {
  //    this.swingLogger = swingLogger;
  //  }
  //
  //  public Properties getProperties() {
  //    return properties;
  //  }
  //
  //  public void setProperties(Properties properties) {
  //    this.properties = properties;
  //  }
  //
  //  public File getPropertyFile() {
  //    return propertyFile;
  //  }
  //
  //  public void setPropertyFile(File propertyFile) {
  //    this.propertyFile = propertyFile;
  //  }
  //
  //  public ImgModel getModel() {
  //    return model;
  //  }
  //
  //  public void setModel(ImgModel model) {
  //    this.model = model;
  //  }
}
