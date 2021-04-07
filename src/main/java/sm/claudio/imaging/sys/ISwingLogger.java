package sm.claudio.imaging.sys;

import java.nio.file.Path;
import java.util.Date;

public interface ISwingLogger {
  void sparaMess(String p_msg);

  void addRow(String att, String nuo, Path loc, Date dt);
}
