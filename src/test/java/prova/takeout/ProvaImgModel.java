package prova.takeout;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.Test;

import sm.claudio.imaging.gpx.GeoCoord;
import sm.claudio.imaging.gpx.RicercaDicotomica;
import sm.claudio.imaging.main.EExifPriority;
import sm.claudio.imaging.swing.ImgModel;
import sm.claudio.imaging.sys.AppProperties;
import sm.claudio.imaging.sys.Utility;

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
    m_mod.clear();
    m_mod.analizza();
    // questa innesca la parseTracks
    m_mod.setFileGPX(CSZ_GPX_FILE);
    // questa innesca per JSON
    m_mod.setFileGPX(CSZ_JSON_TAKEOUT);

    String szDt = "2023-07-08T10:44:37Z";
    LocalDateTime dt = Utility.parseUTC(szDt);
    stampaPeriodo(dt);
    trovaPosizione(dt);

  }

  private void stampaPeriodo(LocalDateTime dt) {
    RicercaDicotomica<GeoCoord> liDic = m_mod.getListDicot();
    LocalDateTime dtMin = dt.minus(Duration.ofHours(4));
    LocalDateTime dtMax = dt.plus(Duration.ofHours(4));

    liDic.stream() //
        .filter(s -> s.getDatetime().isAfter(dtMin)) //
        .filter(s -> s.getDatetime().isBefore(dtMax)) //
        .forEach(s -> System.out.println(s.toCsv()));
  }

  private void trovaPosizione(LocalDateTime dt) {
    GeoCoord coo = new GeoCoord();
    coo.setTstamp(dt);
    coo = m_mod.cercaGPS(coo);
    System.out.printf("Data %s trovato %s\n", Utility.s_dtfmt.format(dt), coo.toString());
  }
}
