package sm.claudio.imaging.fsvisit;

import java.io.FileNotFoundException;

public interface IFSVisitatore {

  void visit(FSDir vis) throws FileNotFoundException;

  void visit(FSFile vis) throws FileNotFoundException;

  void visit(FSFoto vis) throws FileNotFoundException;

  void visit(FSTiff vis) throws FileNotFoundException;

  void visit(FSXml vis) throws FileNotFoundException;
}
