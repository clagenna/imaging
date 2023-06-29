package sm.claudio.imaging.sys;

import java.nio.file.Path;

import javafx.beans.property.SimpleStringProperty;

public class FotoPath {
  private Path                 pth;
  private SimpleStringProperty name;
  private SimpleStringProperty fullPath;

  public FotoPath(Path p_pth) {
    pth = p_pth;
    name = new SimpleStringProperty(this, "name", p_pth.getFileName().toString());
    fullPath = new SimpleStringProperty(this, "fullPath", p_pth.getParent().toString());
  }

  public SimpleStringProperty nameProperty() {
    return name;
  }

  public SimpleStringProperty fullPathProperty() {
    return fullPath;
  }

  public Path getPath() {
    return pth;
  }

  @Override
  public String toString() {
    String sz = String.format("%12s %s", name.get(), fullPath.get());
    return sz;
  }

}
