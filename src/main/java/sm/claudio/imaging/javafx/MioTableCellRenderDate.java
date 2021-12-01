package sm.claudio.imaging.javafx;

import java.time.LocalDateTime;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;

import sm.claudio.imaging.fsvisit.FSFile;
import sm.claudio.imaging.fsvisit.FSFoto;

public class MioTableCellRenderDate<T, F> extends TableCell<FSFile, LocalDateTime> {

  private String m_colName;

  public MioTableCellRenderDate(String p_colNam) {
    m_colName = p_colNam;
  }

  @Override
  protected void updateItem(LocalDateTime item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null || empty) {
      setText(null);
      setStyle("");
      return;
    }
    if (getTableRow() == null)
      return;
    FSFile fil = getTableRow().getItem();
    if ( ! (fil instanceof FSFoto)) {
      setText(null);
      setStyle("");
      return;
    }
    FSFoto fot = (FSFoto) fil;
    // Format date.
    LocalDateTime dt = fot.getDtAssunta();
    if (dt == null)
      dt = fot.getPiuVecchiaData();
    String sz = fil.formatDt(item);
    setText(sz);
    // Style all dates in March with a different color.
    int v = dt.compareTo(item);
    v = v < 0 ? -1 : v;
    v = v > 0 ? 1 : v;
//    System.out.printf("MioTableCellRenderDate.updateItem(%s %s %d %s)\n", //
//        m_colName, sz, v, fil.formatDt(dt));
    switch (v) {
      case -1:
        setTextFill(Color.BLACK);
        setStyle("-fx-background-color: lightcoral");
        break;
      case 0:
        setTextFill(Color.BLACK);
        setStyle("");
        break;
      case 1:
        setTextFill(Color.BLACK);
        setStyle("-fx-background-color: darkturquoise; -fx-font-weight: bolder;");
        break;
    }
  }

}
