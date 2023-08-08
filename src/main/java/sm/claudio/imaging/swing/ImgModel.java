package sm.claudio.imaging.swing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import sm.claudio.imaging.fsvisit.FSDir;
import sm.claudio.imaging.fsvisit.FSFile;
import sm.claudio.imaging.fsvisit.FSFileFactory;
import sm.claudio.imaging.fsvisit.FSFoto;
import sm.claudio.imaging.fsvisit.FileSystemVisitatore;
import sm.claudio.imaging.gpx.GeoCoord;
import sm.claudio.imaging.gpx.GpsXmlHandler;
import sm.claudio.imaging.gpx.RicercaDicotomica;
import sm.claudio.imaging.main.EExifPriority;
import sm.claudio.imaging.sys.AppProperties;
import sm.claudio.imaging.sys.ISwingLogger;

public class ImgModel {
  private static final Logger         s_log = LogManager.getLogger(ImgModel.class);
  private String                      directory;
  private String                      fileGPX;
  private EExifPriority               priority;
  private boolean                     recursive;

  private List<FSFile>                m_liFoto;
  private GpsXmlHandler               m_hand;
  private RicercaDicotomica<GeoCoord> m_liDicot;

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
  }

  public void analizza() {
    ISwingLogger swingl = AppProperties.getInst().getSwingLogger();
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
      swingl.sparaMess(szMsg);
      return;
    }
    FileSystemVisitatore fsv = new FileSystemVisitatore();
    fsDir.accept(fsv);
    ImgModel.s_log.info("Fine scansione di {}", szSrc);
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
    ISwingLogger swLog = AppProperties.getInst().getSwingLogger();
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
    swLog.sparaMess(szLog);
    // System.out.println("ImgModel.isValoriOk():" + bRet);
    return bRet;
  }

  private boolean parseGpxTrack(String p_gpx) {
    ISwingLogger swLog = AppProperties.getInst().getSwingLogger();
    m_liDicot = null;
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser saxParser;
    try {
      saxParser = factory.newSAXParser();
    } catch (ParserConfigurationException | SAXException e) {
      String szMsg = "Errore sul file GPX delle tracce: " + p_gpx + "; err=" + e.getMessage();
      ImgModel.s_log.error(szMsg, e);
      swLog.sparaMess(szMsg);
      return false;
    }
    m_hand = new GpsXmlHandler();
    try {
      saxParser.parse(p_gpx, m_hand);
      fileGPX = p_gpx;
    } catch (SAXException | IOException e) {
      String szMsg = "Errore parsing file GPX delle tracce: " + p_gpx + "; err=" + e.getMessage();
      ImgModel.s_log.error(szMsg, e);
      swLog.sparaMess(szMsg);
      return false;
    }
    m_liDicot = m_hand.list();
    return m_liDicot != null && m_liDicot.size() > 0;
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

  public void setFileGPX(String p_fileGPX) {
    if ( !parseGpxTrack(p_fileGPX))
      s_log.error("Non sono riuscito ad interpretare {}", p_fileGPX);
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
        directory, priority.desc(), (recursive ? "recursive" : "currDir only"));
    return sz;
  }

  public boolean isGPXFileOk() {
    return fileGPX != null && fileGPX.length() > 3 && m_liDicot != null && m_liDicot.size() > 0;
  }

  public void interpolaGPX() {
    try {
      s_log.info("Interpolazione delle coordinate GPS con il file di tracce");
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      // e.printStackTrace();
    }

  }

}
