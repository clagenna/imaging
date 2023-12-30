package sm.claudio.imaging.swing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javafx.concurrent.Task;
import sm.claudio.imaging.fsvisit.FSDir;
import sm.claudio.imaging.fsvisit.FSFile;
import sm.claudio.imaging.fsvisit.FSFileFactory;
import sm.claudio.imaging.fsvisit.FSFoto;
import sm.claudio.imaging.fsvisit.FileSystemVisitatore;
import sm.claudio.imaging.gpx.GeoCoord;
import sm.claudio.imaging.gpx.GpsXmlHandler;
import sm.claudio.imaging.gpx.JsonParserStream;
import sm.claudio.imaging.gpx.RicercaDicotomica;
import sm.claudio.imaging.main.EExifPriority;
import sm.claudio.imaging.sys.AppProperties;
import sm.claudio.imaging.sys.TimerMeter;
import sm.claudio.imaging.sys.Utility;

public class ImgModel extends Task<String> {
  private static final Logger s_log = LogManager.getLogger(ImgModel.class);
  private String              directory;
  private String              fileGPX;
  private EExifPriority       priority;
  private boolean             recursive;

  private List<FSFile>                m_liFoto;
  private GpsXmlHandler               m_hand;
  private RicercaDicotomica<GeoCoord> m_liDicot;

  private LocalDateTime minFotoDate;
  private LocalDateTime maxFotoDate;

  public ImgModel() {
    AppProperties prop = AppProperties.getInst();
    prop.setModel(this);
  }

  public int add(FSFile p_f) {
    if (m_liFoto == null)
      m_liFoto = new ArrayList<>();
    if ( !m_liFoto.contains(p_f))
      m_liFoto.add(p_f);
    return m_liFoto.size();
  }

  public void clear() {
    s_log.debug("Pulizia del Model");
    if (m_liFoto != null)
      m_liFoto.clear();
    m_liFoto = null;
    if (m_liDicot != null)
      m_liDicot.clear();
    m_liDicot = null;
  }

  public void analizza() {
    s_log.debug("ImgModel.esegui: {}", toString());
    String szSrc = getDirectory();
    Path fi = Paths.get(szSrc);
    ImgModel.s_log.info("Inizio scansione di {}", szSrc);
    try {
      FSFileFactory.getInst();
    } catch (Exception e) {
      new FSFileFactory();
    }
    FSDir fsDir = null;
    try {
      fsDir = new FSDir(fi);
    } catch (FileNotFoundException e) {
      String szMsg = "Errore open direttorio " + szSrc;
      ImgModel.s_log.error(szMsg, e);
      return;
    }
    FileSystemVisitatore fsv = new FileSystemVisitatore();
    fsDir.accept(fsv);
    aggiungiGPSDalleFoto();
    if (minFotoDate != null && minFotoDate.isBefore(LocalDateTime.MAX)) {
      ImgModel.s_log.debug("Data min {} data Max {}", // 
          Utility.s_dtfmt_YMD_hms.format(minFotoDate), //
          Utility.s_dtfmt_YMD_hms.format(maxFotoDate));
    }
    ImgModel.s_log.info("Fine scansione di {}", szSrc);
  }

  private void aggiungiGPSDalleFoto() {
    minFotoDate = LocalDateTime.MAX;
    maxFotoDate = LocalDateTime.MIN;
    m_liFoto //
        .stream() //
        .forEach(s -> minMaxDate(s));

    if (m_liDicot == null || m_liFoto == null)
      return;
    List<GeoCoord> li = m_liFoto //
        .stream() //
        .filter(s -> s.isGPS()) //
        .map(s -> new GeoCoord(s)) //
        // .map(GeoCoord::fromFSFoto) // questa dà errore perchè non è una interfaccia funzionale
        .collect(Collectors.toList());
    m_liDicot.addAll(li);
  }

  private Object minMaxDate(FSFile p_s) {
    LocalDateTime vv = p_s.getAcquisizione();
    if (vv == null)
      return p_s;
    if (minFotoDate.isAfter(vv))
      minFotoDate = vv;
    if (maxFotoDate.isBefore(vv))
      maxFotoDate = vv;
    return p_s;
  }

  public void rinominaFiles() {
    List<FSFile> li = getListFiles();
    if (li == null || li.size() == 0)
      return;

    for (FSFile fsf : li) {
      if (fsf instanceof FSFoto)
        ((FSFoto) fsf).lavoraIlFile();
    }
  }

  public List<FSFile> getListFiles() {
    return m_liFoto;
  }

  public boolean isValoriOk() {
    String szLog = "";
    boolean bRet = priority != null;
    if ( !bRet)
      szLog = "Manca la priority";
    bRet &= directory != null;
    if (directory == null)
      szLog = "Manca il Direttorio di partenza";

    if (bRet) {
      Path pth = Paths.get(directory);
      bRet = Files.exists(pth, LinkOption.NOFOLLOW_LINKS);
      if ( !bRet)
        szLog = "Il direttorio non esiste";
    }
    if (szLog.length() > 0)
      s_log.warn(szLog);
    return bRet;
  }

  private boolean parseTracks(String p_gpx) {
    if (p_gpx.toLowerCase().endsWith(".gpx"))
      return parseGpxTracks(p_gpx);
    return parseJsonTracks(p_gpx);
  }

  private boolean parseJsonTracks(String p_gpx) {
    // JsonParser jsonParse = new JsonParser(p_gpx, this);
    JsonParserStream jsonParse = new JsonParserStream(p_gpx, this);
    RicercaDicotomica<GeoCoord> lLiDi = jsonParse.parse();
    m_liDicot.addAll(lLiDi);
    return true;
  }

  private boolean parseGpxTracks(String p_gpx) {
    s_log.debug("SAX parse di {}", p_gpx);
    TimerMeter tim = new TimerMeter("SAX parse");
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser saxParser;
    try {
      saxParser = factory.newSAXParser();
    } catch (ParserConfigurationException | SAXException e) {
      String szMsg = "Errore sul file GPX delle tracce: " + p_gpx + "; err=" + e.getMessage();
      ImgModel.s_log.error(szMsg, e);
      return false;
    }
    m_hand = new GpsXmlHandler();
    s_log.debug("Open SAX, time={}", tim.stop());
    tim = new TimerMeter("SAX parsing");
    try {
      saxParser.parse(p_gpx, m_hand);
      fileGPX = p_gpx;
    } catch (SAXException | IOException e) {
      String szMsg = "Errore parsing file GPX delle tracce: " + p_gpx + "; err=" + e.getMessage();
      ImgModel.s_log.error(szMsg, e);
      return false;
    }
    s_log.debug("Parse SAX, time={}", tim.stop());
    tim = new TimerMeter("SAX sorting");
    m_liDicot = m_hand.list();
    aggiungiGPSDalleFoto();
    s_log.debug("Sort SAX, time={}", tim.stop());
    return m_liDicot != null && m_liDicot.size() > 0;
  }

  public RicercaDicotomica<GeoCoord> getListDicot() {
    return m_liDicot;
  }

  public String getDirectory() {
    return directory;
  }

  public void setDirectory(String directory) {
    this.directory = directory;
  }

  public String getFileGPX() {
    return fileGPX;
  }

  public void setFileGPX(Path p_pthGPX) {
    if (p_pthGPX == null)
      return;
    if ( !parseTracks(p_pthGPX.toAbsolutePath().toString()))
      s_log.error("Non sono riuscito ad interpretare {}", p_pthGPX);
  }

  public EExifPriority getPriority() {
    return priority;
  }

  public void setPriority(EExifPriority priority) {
    this.priority = priority;
  }

  public boolean isRecursive() {
    return recursive;
  }

  public void setRecursive(boolean recursive) {
    this.recursive = recursive;
  }

  @Override
  public String toString() {
    String sz = String.format("path=\"%s\", prio=%s, recurse=%s", //
        directory, priority.desc(), recursive ? "recursive" : "currDir only");
    return sz;
  }

  public boolean isGPXFileOk() {
    return fileGPX != null && fileGPX.length() > 3 && m_liDicot != null && m_liDicot.size() > 0;
  }

  public void interpolaGPX() {
    if (m_liFoto == null || m_liFoto.size() == 0 || m_liDicot == null)
      return;
    s_log.info("Interpolazione delle coordinate GPS con il file di tracce");
    List<FSFile> liNoGps = m_liFoto //
        .stream() //
        .filter(s -> !s.isGPS()) //
        .collect(Collectors.toList());
    for (FSFile gp : liNoGps) {
      GeoCoord coo = new GeoCoord(gp);
      System.out.printf("ImgModel.interpolaGPX(%s)\n", coo.toCsv3());
    }
  }

  public LocalDateTime getMinFotoDate() {
    return minFotoDate;
  }

  public LocalDateTime getMaxFotoDate() {
    return maxFotoDate;
  }

  public GeoCoord cercaGPS(GeoCoord p_coo) {
    if (m_liDicot == null)
      return null;
    GeoCoord coo = m_liDicot.cercaDicot(p_coo);
    return coo;
  }

  @Override
  protected String call() throws Exception {
    //System.out.println("ImgModel Start BackGround Thread");
    s_log.info("Start BackGround Thread");
    rinominaFiles();
    s_log.info("Fine BackGround Thread");
    return "Done...";
  }

  public void setDirectory(Path p_pth) {
    if (p_pth != null)
      setDirectory(p_pth.toAbsolutePath().toString());
  }

}
