package sm.claudio.imaging.swing;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;
import lombok.Setter;
import sm.claudio.imaging.fsvisit.FSDir;
import sm.claudio.imaging.fsvisit.FSFile;
import sm.claudio.imaging.fsvisit.FSFileFactory;
import sm.claudio.imaging.fsvisit.FileSystemVisitatore;
import sm.claudio.imaging.main.EExifPriority;
import sm.claudio.imaging.sys.AppProperties;
import sm.claudio.imaging.sys.ISwingLogger;

public class ImgModel {
  private static final Logger           s_log = LogManager.getLogger(ImgModel.class);
  @Setter @Getter private String        directory;
  @Setter @Getter private EExifPriority priority;
  @Setter @Getter private boolean       recursive;

  private List<FSFile>                  m_liFoto;

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
  }

  public void analizza() {
    ISwingLogger swingl = AppProperties.getInst().getSwingLogger();
    System.out.println("ImgModel.esegui:" + toString());
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

  public void esegui() {
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

  @Override
  public String toString() {
    String sz = String.format("path=\"%s\", prio=%s, recurse=%s", //
        directory, priority.desc(), (recursive ? "recursive" : "currDir only"));
    return sz;
  }

}
