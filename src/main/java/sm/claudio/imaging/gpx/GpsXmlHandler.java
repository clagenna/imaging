package sm.claudio.imaging.gpx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GpsXmlHandler extends DefaultHandler {
  private LinkedList<String>          m_stack;
  private String                      m_txChars;
  private GeoCoord                    m_coo;
  private RicercaDicotomica<GeoCoord> m_liCoo;

  public GpsXmlHandler() {
    //
  }

  @Override
  public void startDocument() throws SAXException {
    m_stack = new LinkedList<>();
    m_liCoo = new RicercaDicotomica<>();
  }

  @Override
  public void endDocument() throws SAXException {
    // m_liCoo.stream().forEach(s -> System.out.println(s.toCsv()));
    // System.out.println("Elems = " + m_liCoo.size());
  }

  /**
   * Proviamo ad interpretare la seguente sequenza:
   *
   * <pre>
   * &lt;trk&gt;
    &lt;name&gt;2023-07-06 Courmayeur&lt;/name&gt;
    &lt;extensions&gt;
      &lt;gpxx:TrackExtension&gt;
        &lt;gpxx:DisplayColor&gt;DarkGray&lt;/gpxx:DisplayColor&gt;
      &lt;/gpxx:TrackExtension&gt;
    &lt;/extensions&gt;
    &lt;trkseg&gt;
      &lt;trkpt lat=&quot;45.129960989579558&quot; lon=&quot;9.658012976869941&quot;&gt;
        &lt;ele&gt;59.740000000000002&lt;/ele&gt;
        &lt;time&gt;2023-07-06T08:27:44Z&lt;/time&gt;
      &lt;/trkpt&gt;
      &lt;trkpt lat=&quot;45.132209016010165&quot; lon=&quot;9.653621027246118&quot;&gt;
        &lt;ele&gt;62.619999999999997&lt;/ele&gt;
        &lt;time&gt;2023-07-06T08:27:57Z&lt;/time&gt;
      &lt;/trkpt&gt;
   * </pre>
   *
   * trk - trkseg - trkpt : ele/time
   *
   */
  @Override
  public void startElement(String p_uri, String p_localName, String p_qName, Attributes p_attributes) throws SAXException {
    m_txChars = null;
    m_stack.push(p_qName);
    String szXpath = getXpath();
    switch (szXpath) {
      case "gpx/trk/trkseg/trkpt":
        m_coo = new GeoCoord(ESrcGeoCoord.track);
        Map<String, String> mp = parseAttributes(p_attributes);
        m_coo.setLatitude(mp.get("lat"));
        m_coo.setLongitude(mp.get("lon"));
        // System.out.println(mp);
        break;
    }
  }

  @Override
  public void characters(char[] p_ch, int p_start, int p_length) throws SAXException {
    m_txChars = new String(p_ch, p_start, p_length);
    m_txChars = m_txChars.replaceAll("\\n", "");
    m_txChars = m_txChars.replaceAll("\\r", "");
    m_txChars = m_txChars.trim();
    if (m_txChars.length() == 0)
      m_txChars = null;
  }

  @Override
  public void endElement(String p_uri, String p_localName, String p_qName) throws SAXException {
    String szXpath = getXpath();
    switch (szXpath) {
      case "gpx/trk/trkseg/trkpt/ele":
        m_coo.setAltitude(m_txChars);
        break;
      case "gpx/trk/trkseg/trkpt/time":
        m_coo.setTstamp(m_txChars);
        m_coo.setSourceCoord(ESrcGeoCoord.track);
        m_liCoo.add(m_coo);
        m_coo = null;
        break;
    }
    m_stack.pop();
  }

  private String getXpath() {
    Iterator<String> iter = m_stack.descendingIterator();
    String sz = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iter, 0), false) //
        .collect(Collectors.joining("/"));
    return sz;
  }

  private Map<String, String> parseAttributes(Attributes attributes) {
    int k = 0;
    if (attributes != null)
      k = attributes.getLength();
    Map<String, String> map = new HashMap<>();
    if (k == 0)
      return map;
    for (int i = 0; i < k; i++) {
      String ob = attributes.getLocalName(i);
      String vv = attributes.getValue(i);
      map.put(ob, vv);
    }
    return map;
  }

  public RicercaDicotomica<GeoCoord> list() {
    return m_liCoo;
  }
}
