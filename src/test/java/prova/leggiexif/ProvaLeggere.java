package prova.leggiexif;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata.GPSInfo;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sm.claudio.imaging.sys.Utility;

public class ProvaLeggere {
  private static final Logger s_log = LogManager.getLogger(ProvaLeggere.class);

  public ProvaLeggere() {
    //
  }

  public void printAllExifData(final File imageFile) throws ImageReadException, IOException {
    final ImageMetadata metadata = Imaging.getMetadata(imageFile);

    TiffImageMetadata exif = null;
    if (metadata instanceof JpegImageMetadata) {
      exif = ((JpegImageMetadata) metadata).getExif();
    } else if (metadata instanceof TiffImageMetadata) {
      exif = (TiffImageMetadata) metadata;
    } else {
      return;
    }
    @SuppressWarnings("unused") String szModel = "";
    String szDt = null;
    Double dLon = null;
    Double dLat = null;

    String[] sz1 = exif.getFieldValue(Utility.EXIF_TAG_MAKE);
    if (sz1 != null && sz1.length > 0)
      szModel += sz1[0];
    String[] sz2 = exif.getFieldValue(Utility.EXIF_TAG_MODEL);
    if (sz2 != null && sz2.length > 0)
      szModel += " " + sz2[0];

    String[] arr = exif.getFieldValue(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
    if (arr != null && arr.length > 0)
      szDt = arr[0];
    GPSInfo gpsi = exif.getGPS();
    if (gpsi != null) {
      dLon = gpsi.getLongitudeAsDegreesEast();
      dLat = gpsi.getLatitudeAsDegreesNorth();
    }
    if (szDt != null)
      s_log.info("Data acquisizione = {}", szDt);
    if (dLon != null) // 49.43990728827985, 1.0990532511898845
      s_log.info("GPS = {} {}", String.format(Locale.US, "%.12f", dLat), String.format(Locale.US, "%.12f", dLon));

    @SuppressWarnings("unchecked") List<TiffImageMetadata.TiffMetadataItem> items = (List<TiffImageMetadata.TiffMetadataItem>) exif
        .getItems();

    for (TiffImageMetadata.TiffMetadataItem item : items) {
      ProvaLeggere.s_log.info(item.getTiffField().toString());
    }
  }

  public static void main(String[] args) throws ImageReadException, IOException {
    ProvaLeggere app = new ProvaLeggere();
    File fiImg = new File("\\\\nascasa\\photo\\2023\\2023-07-06 Normandia Bretagna\\f20230712_111617.jpg");
    s_log.info(fiImg.getAbsolutePath());
    app.printAllExifData(fiImg);
  }

}
