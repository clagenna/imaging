package sm.claudio.imaging.fsvisit;

import java.io.FileNotFoundException;
import java.nio.file.Path;

public interface IFSVisitable {
  void accept(IFSVisitatore fsv);

  Path getPath();

  void setPath(Path p_fi) throws FileNotFoundException;

  void setParent(Path p_parent);

  Path getParent();
}
