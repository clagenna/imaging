package prova.takeout;

import org.junit.Test;

import sm.claudio.imaging.main.EExifPriority;
import sm.claudio.imaging.swing.ImgModel;
import sm.claudio.imaging.sys.AppProperties;

public class ProvaImgModel {

  private static final String CSZ_FOTO_DIR     = "F:\\My Foto\\2023\\Test-gpx";
  private static final String CSZ_GPX_FILE     = "F:\\My Foto\\2023\\Test-gpx\\2023-07-06 Bretagna Normandia_TRK.gpx";
  private static final String CSZ_JSON_TAKEOUT = "F:\\temp\\gpx\\Takeout\\Cronologia delle posizioni\\Records-2023.json";

  private ImgModel            m_mod;

  public ProvaImgModel() {
    //
  }

  @Test
  public void doIt() {
    AppProperties m_prop = new AppProperties();
    m_prop.openProperties();

    m_mod = new ImgModel();
    m_mod.setPriority(EExifPriority.ExifFileDir);
    m_mod.setDirectory(CSZ_FOTO_DIR);
    m_mod.analizza();
    // questa innesca la parseTracks
    m_mod.setFileGPX(CSZ_GPX_FILE);
    // questa innesca per JSON
    m_mod.setFileGPX(CSZ_JSON_TAKEOUT);

  }
}
