package sm.claudio.imaging.main;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sm.claudio.imaging.fsvisit.FSDir;
import sm.claudio.imaging.fsvisit.FSFileFactory;
import sm.claudio.imaging.fsvisit.FileSystemVisitatore;

public class MainApp {
  private static final Logger s_log = LogManager.getLogger(MainApp.class);

  private static MainApp      s_app = null;

  private RigaComando         m_cmd;

  public static MainApp getApp() {
    return s_app;
  }

  public MainApp() {
    if (s_app != null)
      throw new UnsupportedOperationException("Main App gia istanziata !");
    s_app = this;
    m_cmd = new RigaComando();
    m_cmd.creaOptions();
  }

  public static void main(String[] args) {

    MainApp appl = new MainApp();
    appl.parseOptions(args);
    appl.vaiColTango();

  }

  public boolean parseOptions(String[] args) {
    return m_cmd.parseOptions(args);
  }

  private void vaiColTango() {
    String szSrc = m_cmd.getOption(RigaComando.CSZ_OPT_SRCDIR);
    Path fi = Paths.get(szSrc);
    s_log.info("Inizio scansione di {}", szSrc);
    new FSFileFactory();
    FSDir fsDir = null;
    try {
      fsDir = new FSDir(fi);
    } catch (FileNotFoundException e) {
      s_log.error("Errore open direttorio " + szSrc, e);
      return;
    }
    FileSystemVisitatore fsv = new FileSystemVisitatore();
    fsDir.accept(fsv);
    s_log.info("Fine scansione di {}", szSrc);
  }

  public boolean isScanSubDirs() {
    return m_cmd.isOption(RigaComando.CSZ_OPT_SUBDIRS);
  }

  public boolean isCopiaFile() {
    String szDst = m_cmd.getOption(RigaComando.CSZ_OPT_DSTDIR);
    return szDst != null;
  }

}
