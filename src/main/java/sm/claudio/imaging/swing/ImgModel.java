package sm.claudio.imaging.swing;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.Getter;
import lombok.Setter;
import sm.claudio.imaging.main.EExifPriority;
import sm.claudio.imaging.sys.AppProperties;
import sm.claudio.imaging.sys.ISwingLogger;

public class ImgModel {
  @Setter @Getter private String        directory;
  @Setter @Getter private EExifPriority priority;
  @Setter @Getter private boolean       recursive;

  public ImgModel() {
    //
  }

  public void esegui() {
    String sz = recursive ? "Recurse" : "No Recusion";
    sz += ";" + priority.desc();
    sz += ";" + directory;
    System.out.println("ImgModel.esegui:" + sz);
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
