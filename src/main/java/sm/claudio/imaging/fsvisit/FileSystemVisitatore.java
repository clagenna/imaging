package sm.claudio.imaging.fsvisit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileSystemVisitatore implements IFSVisitatore {

  private static final Logger s_log = LogManager.getLogger(FileSystemVisitatore.class);

  @Override
  public void visit(FSDir p_vis) throws FileNotFoundException {
    FSFileFactory fact = FSFileFactory.getInst();
    try {
      Files.list(p_vis.getPath()) //
          .map(s -> fact.get(s)) //
          .forEach(s -> s.accept(this));
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void visit(FSFile p_vis) {
    s_log.warn("non analizzo FSFile " + p_vis.toString());
  }

  @Override
  public void visit(FSJpeg p_vis) {
    p_vis.analizzaJpeg();

  }

  @Override
  public void visit(FSTiff p_vis) {
    // s_log.warn("non analizzo FSTiff : " + p_vis.getPath().toString());
    p_vis.analizzaTIFF();
  }

  @Override
  public void visit(FSXml p_vis) {
    s_log.warn("non analizzo FSXml" + p_vis.toString());
  }

}
