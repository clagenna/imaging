package prova.takeout;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.Test;

import sm.claudio.imaging.gpx.ESrcGeoCoord;
import sm.claudio.imaging.gpx.GeoCoord;
import sm.claudio.imaging.gpx.RicercaDicotomica;
import sm.claudio.imaging.sys.Utility;

public class ReadGoogleTakeout {
  private static final String         CSZ_FILE = "F:\\temp\\gpx\\Takeout\\Cronologia delle posizioni\\Records.json";

  private static final String         CSZ_LAT  = "latitudeE7";
  private static final String         CSZ_LON  = "longitudeE7";
  private static final String         CSZ_ALT  = "altitude";
  private static final String         CSZ_TST  = "deviceTimestamp";
  private static final String         CSZ_FACT = "formFactor";
  private static final long           N_PRIMO  = 15083;

  private int                         added;
  private long                        riga;
  private static NumberFormat         intFmt   = NumberFormat.getNumberInstance(Locale.getDefault());
  private GeoCoord                    m_geo;
  private RicercaDicotomica<GeoCoord> m_li;
  private LocalDateTime               m_tsStart;
  private LocalDateTime               m_tsEnd;

  public ReadGoogleTakeout() {
    //
  }

  @Test
  public void doTheJob() {
    m_tsStart = Utility.parseUTC("2023-07-06T06:00:00.000Z");
    m_tsEnd = Utility.parseUTC("2023-07-20T23:00:00.000Z");
    m_li = new RicercaDicotomica<>();
    added = 0;
    riga = 0;
    try (Stream<String> stre = Files.lines(Paths.get(CSZ_FILE))) {
      stre.forEach(s -> analizzaRiga(s));
      m_li.sort();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.printf("Lette %s righe\n", intFmt.format(riga));
    System.out.printf("Coord=%s\n", intFmt.format(m_li.size()));
    //    try (PrintWriter bos = new PrintWriter(new FileWriter("TakeOut.csv"))) {
    //      m_li.stream().forEach(s -> bos.println(s.toCsv()));
    //    } catch (FileNotFoundException e) {
    //      e.printStackTrace();
    //    } catch (IOException e) {
    //      e.printStackTrace();
    //    }
    LocalDateTime curr = m_tsStart;
    while (curr.isBefore(m_tsEnd)) {
      ConvToTrk cnv = new ConvToTrk();
      cnv.setMinTime(curr);
      curr = curr.plusDays(1);
      cnv.setMaxTime(curr);
      cnv.convToGPX(m_li);
    }
  }

  /**
   * Analizzo la struttura JSON:
   *
   * <pre>
   *       "formFactor": "PHONE",
   *       "timestamp": "2023-07-18T08:57:01.995Z"
   *    }, {
   *       "latitudeE7": 480805516,
   *       "longitudeE7": 26039380,
   *       "accuracy": 3200,
   *       "altitude": 135,
   *       "verticalAccuracy": 6,
   *       "source": "CELL",
   *       "deviceTag": 2008223542,
   *       "platformType": "ANDROID",
   *       "osLevel": 33,
   *       "serverTimestamp": "2023-07-18T09:08:51.780Z",
   *       "deviceTimestamp": "2023-07-18T09:08:51.444Z",
   *       "batteryCharging": false,
   *       "deviceDesignation": "UNKNOWN",
   *       "formFactor": "TABLET",
   *       "timestamp": "2023-07-18T08:57:47.361Z"
   *     }, {
   *       "latitudeE7": 480797083,
   *       "longitudeE7": 26154834,
   * </pre>
   *
   * @param p_s
   * @return
   */

  private Object analizzaRiga(String p_s) {

    if ( ( (riga++) % N_PRIMO) == 0)
      System.out.printf("Riga=%s\n", intFmt.format(riga));
    if (p_s == null || !p_s.contains(":"))
      return null;
    double dd;
    String[] arr = p_s.split(":");
    String lKey = arr[0].trim().replaceAll("\"", "");
    String lVal = arr[1].trim().replaceAll("\"", "").replace(",", "");
    for (int i = 2; i < arr.length; i++)
      lVal += ":" + arr[i].trim().replaceAll("\"", "").replace(",", "");

    switch (lKey) {
      case CSZ_LAT:
        m_geo = new GeoCoord(ESrcGeoCoord.google);
        dd = Double.parseDouble(lVal) / 10_000_000f;
        m_geo.setLat(dd);
        added = 1;
        break;
      case CSZ_LON:
        dd = Double.parseDouble(lVal) / 10_000_000f;
        m_geo.setLon(dd);
        added++;
        break;
      case CSZ_ALT:
        dd = Double.parseDouble(lVal);
        m_geo.setAlt(dd);
        added++;
        break;
      case CSZ_TST:
        m_geo.setTstamp(lVal);
        added++;
        break;
      case CSZ_FACT:
        if ( !lVal.equals("PHONE"))
          break;
        if (m_geo.getDatetime().isAfter(m_tsStart) && m_geo.getDatetime().isBefore(m_tsEnd))
          if (added >= 3)
            m_li.add(m_geo);
          else
            System.out.printf("analizzaRiga incompleto, riga=%d\n", riga);
        break;
    }
    return null;
  }

}
