package sm.claudio.imaging.fsvisit;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FSFile implements IFSVisitable {

  private Logger m_log;
  private Path   m_file;
  // private File   m_fiBackup;
  private Path   m_parent;

  public FSFile() {
    //
  }

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

  @Override
  public void setPath(Path p_fi) throws FileNotFoundException {
    if ( !Files.exists(p_fi, LinkOption.NOFOLLOW_LINKS))
      throw new FileNotFoundException(p_fi.toString());
    m_file = p_fi;
    Path fiParent = m_file.getParent();
    setParent(fiParent);
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

}
