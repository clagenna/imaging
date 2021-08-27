package sm.claudio.imaging.fsvisit;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sm.claudio.imaging.swing.ImgModel;
import sm.claudio.imaging.sys.AppProperties;

public class FSFile implements IFSVisitable {

  private Logger   m_log;
  private Path     m_file;
  // private File   m_fiBackup;
  private Path     m_parent;
  private ImgModel m_model;

  public FSFile() {
    //
  }

  /**
   * Questo viene richiamato sui tipi di files non riconosciuti come foto ma
   * come files generici
   *
   * @param p_fi
   * @throws FileNotFoundException
   */
  public FSFile(Path p_fi) throws FileNotFoundException {
    setPath(p_fi);
  }

  @Override
  public void accept(IFSVisitatore p_fsv) {
    try {
      p_fsv.visit(this);
    } catch (FileNotFoundException e) {
      getLogger().error("visita", e);
    }
  }

  public ImgModel getModel() {
    if (m_model != null)
      return m_model;
    m_model = AppProperties.getInst().getModel();
    return m_model;
  }

  @Override
  public void setPath(Path p_fi) throws FileNotFoundException {
    if ( !Files.exists(p_fi, LinkOption.NOFOLLOW_LINKS))
      throw new FileNotFoundException(p_fi.toString());
    m_file = p_fi;
    Path fiParent = m_file.getParent();
    setParent(fiParent);
    String clsNam = getClass().getSimpleName();
    switch (clsNam) {
      case "FSFile":
      case "FSFoto":
      case "FSJpeg":
      case "FSTiff":
        getModel().add(this);
        break;
      case "FSNef":
      case "FSCr2":
        getModel().add(this);
        break;
      default:
        break;
    }
  }

  @Override
  public Path getPath() {
    return m_file;
  }

  @Override
  public void setParent(Path p_parent) {
    m_parent = p_parent;
  }

  @Override
  public Path getParent() {
    return m_parent;
  }

  public Logger getLogger() {
    if (m_log == null)
      m_log = LogManager.getLogger(getClass());
    return m_log;
  }

  @Override
  public boolean equals(Object obj) {
    if ( ! (obj instanceof FSFile))
      return false;
    FSFile fsAltro = (FSFile) obj;
    Path pthAltro = fsAltro.getPath();
    if (pthAltro == null)
      return false;
    if (getPath() == null)
      return false;
    return m_file.equals(pthAltro);
  }

}
