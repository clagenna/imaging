package sm.claudio.imaging.fsvisit;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FSJpeg extends FSFoto {
  private static Logger s_log = LogManager.getLogger(FSJpeg.class);

  LocalDateTime         m_dtFromFile;
  LocalDateTime         m_dtFromExif;

  public FSJpeg() {
    //
  }

  public FSJpeg(Path p_fi) throws FileNotFoundException {
    super(p_fi);
  }

  @Override
  public Logger getLogger() {
    return s_log;
  }

  @Override
  public void accept(IFSVisitatore p_fsv) {
    try {
      p_fsv.visit(this);
    } catch (FileNotFoundException e) {
      getLogger().error("visita", e);
    }
  }

  public void analizzaJpeg() {
    if (isFileInError()) {
      getLogger().error("Il file \"{}\" e' in errore, non lo tratto", getPath().toString());
      return;
    }
    if ( !isDaAggiornare()) {
      getLogger().debug("Nulla da fare con {}", getPath().toString());
      return;
    }
    // System.out.println(toString());
    Set<CosaFare> df = getCosaFare();
    // il cambio nome ha priorita perche imposta anche     dtAssunta
    if (df.contains(CosaFare.setNomeFile)) {
      cambiaNomeFile();
      df.remove(CosaFare.setNomeFile);
    }
    for (CosaFare che : df) {
      switch (che) {
        case setDtAcquisizione:
          cambiaDtAcquisizione();
          break;
        case setDtCreazione:
          cambiaDtCreazione();
          cambiaDtUltModif();
          break;
        case setNomeFile:
          cambiaNomeFile();
          break;
        case setUltModif:
          cambiaDtUltModif();
          break;
      }
    }

  }

  @Override
  public String getFileExtention() {
    return "jpg";
  }
}
