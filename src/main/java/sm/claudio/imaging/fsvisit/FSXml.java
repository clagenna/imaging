package sm.claudio.imaging.fsvisit;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FSXml extends FSFile {

  private static Logger s_log = LogManager.getLogger(FSXml.class);
  
  public FSXml() {
    // 
  }

  public FSXml(Path p_fi) throws FileNotFoundException {
    super(p_fi);
  }

  @Override
  public Logger getLogger() {
    return s_log;
  }

}
