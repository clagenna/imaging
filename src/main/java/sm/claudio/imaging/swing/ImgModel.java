package sm.claudio.imaging.swing;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;
import lombok.Setter;
import sm.claudio.imaging.fsvisit.FSDir;
import sm.claudio.imaging.fsvisit.FSFileFactory;
import sm.claudio.imaging.fsvisit.FileSystemVisitatore;
import sm.claudio.imaging.main.EExifPriority;
import sm.claudio.imaging.sys.AppProperties;
import sm.claudio.imaging.sys.ISwingLogger;

public class ImgModel {
  private static final Logger s_log = LogManager.getLogger(ImgModel.class);
  @Setter
  @Getter
  private String              directory;
  @Setter
  @Getter
  private EExifPriority       priority;
  @Setter
  @Getter
  private boolean             recursive;

  public ImgModel() {
    //
  }

  public void esegui() {
    String sz = recursive ? "Recurse" : "No Recusion";
    sz += ";" + priority.desc();
    sz += ";" + directory;
    System.out.println("ImgModel.esegui:" + sz);
    String szSrc = getDirectory();
    Path fi = Paths.get(szSrc);
    ImgModel.s_log.info("Inizio scansione di {}", szSrc);
    if (FSFileFactory.getInst() == null)
      new FSFileFactory();
    FSDir fsDir = null;
    try {
      fsDir = new FSDir(fi);
    } catch (FileNotFoundException e) {
      ImgModel.s_log.error("Errore open direttorio " + szSrc, e);
      return;
    }
    FileSystemVisitatore fsv = new FileSystemVisitatore();
    fsDir.accept(fsv);
    ImgModel.s_log.info("Fine scansione di {}", szSrc);

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

}
