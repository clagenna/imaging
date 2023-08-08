package sm.claudio.imaging.javafx;

import javafx.scene.control.TableCell;
import sm.claudio.imaging.fsvisit.FSFile;
import sm.claudio.imaging.gpx.ESrcGeoCoord;
import sm.claudio.imaging.gpx.GeoCoord;

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
    if (p_item == null || p_empty || getTableRow() == null || (p_item == 0))
      return;
    int latlon = GeoCoord.LATITUDE;
    if (m_colName.startsWith("lon"))
      latlon = GeoCoord.LONGITUDINE;
    String sz = "";
    GeoCoord coo = new GeoCoord(ESrcGeoCoord.foto);
    switch (latlon) {
      case GeoCoord.LATITUDE:
        coo.setLat(p_item);
        coo.setLon(0);
        sz = coo.getLatGMS().toString();
        break;
      case GeoCoord.LONGITUDINE:
        coo.setLat(0);
        coo.setLon(p_item);
        sz = coo.getLonGMS().toString();
        break;
    }
    setText(sz);
  }

}
