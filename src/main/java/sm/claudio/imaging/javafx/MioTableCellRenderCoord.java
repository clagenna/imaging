package sm.claudio.imaging.javafx;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import sm.claudio.imaging.fsvisit.FSFile;
import sm.claudio.imaging.gpx.ESrcGeoCoord;
import sm.claudio.imaging.gpx.GeoCoord;
import sm.claudio.imaging.sys.AppProperties;

public class MioTableCellRenderCoord<T, F> extends TableCell<FSFile, Double> {

  private String m_colName;

  public MioTableCellRenderCoord(String p_sz) {
    m_colName = p_sz;
  }

  @Override
  protected void updateItem(Double p_item, boolean p_empty) {
    super.updateItem(p_item, p_empty);
    setText(null);
    setStyle("");
    AppProperties prop = AppProperties.getInst();
    boolean showGMS = prop.isShowGMS();
    if (p_item == null || p_empty || getTableRow() == null || (p_item == 0))
      return;
    if (getTableRow() == null)
      return;
    FSFile fil = getTableRow().getItem();
    if (fil.isInterpolato()) {
      setTextFill(Color.BLACK);
      setStyle("-fx-background-color: #FFA500");
    }
    int latlon = GeoCoord.LATITUDE;
    if (m_colName.startsWith("lon"))
      latlon = GeoCoord.LONGITUDINE;
    String sz = "";
    GeoCoord coo = new GeoCoord(ESrcGeoCoord.foto);
    switch (latlon) {
      case GeoCoord.LATITUDE:
        if (showGMS) {
          coo.setLat(p_item);
          coo.setLon(0);
          sz = coo.getLatGMS().toString();
        } else {
          sz = String.format("%.10f", p_item);
        }
        break;
      case GeoCoord.LONGITUDINE:
        if (showGMS) {
          coo.setLat(0);
          coo.setLon(p_item);
          sz = coo.getLonGMS().toString();
        } else {
          sz = String.format("%.10f", p_item);
        }
        break;
    }
    setText(sz);
  }

}
