package sm.claudio.imaging.gpx;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import sm.claudio.imaging.swing.ImgModel;
import sm.claudio.imaging.sys.TimerMeter;

public class JsonParser {
  private static final Logger         s_log = LogManager.getLogger(JsonParser.class);
  private String                      m_jsonFile;
  private ImgModel                    m_mod;
  private RicercaDicotomica<GeoCoord> m_li;
  private LocalDateTime               m_tsStart;
  private LocalDateTime               m_tsEnd;

  public JsonParser(String p_gpx, ImgModel p_mod) {
    m_jsonFile = p_gpx;
    m_mod = p_mod;
    m_tsStart = m_mod.getMinFotoDate();
    m_tsEnd = m_mod.getMaxFotoDate();
  }

  public RicercaDicotomica<GeoCoord> parse() {
    m_li = new RicercaDicotomica<>();
    TimerMeter tim = new TimerMeter("Json parse");
    s_log.debug("Inizio JSON parse {}", m_jsonFile);
    try (InputStream inputStream = Files.newInputStream(Path.of(m_jsonFile));
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));) {
      reader.beginArray();
      while (reader.hasNext()) {
        GooglePos pp = new Gson().fromJson(reader, GooglePos.class);
        if (pp.getTimeTs().isBefore(m_tsStart) || pp.getTimeTs().isAfter(m_tsEnd))
          continue;
        GeoCoord geo = GeoCoord.fromGooglePos(pp);
        if (geo != null)
          m_li.add(geo);
        // System.out.println(pp);
      }
      reader.endArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
    s_log.debug("JsonParser.parse({})", tim.stop());
    return m_li;
  }
}
