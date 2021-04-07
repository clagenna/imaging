package prova.leggiexif;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProvaLeggere {
  private static final Logger s_log = LogManager.getLogger(ProvaLeggere.class);

  public ProvaLeggere() {
    //
  }

  public void printAllExifData(final File imageFile) throws ImageReadException, IOException {
    final ImageMetadata metadata = Imaging.getMetadata(imageFile);

    TiffImageMetadata tiffImageMetadata = null;
    if (metadata instanceof JpegImageMetadata) {
      tiffImageMetadata = ((JpegImageMetadata) metadata).getExif();
    } else if (metadata instanceof TiffImageMetadata) {
      tiffImageMetadata = (TiffImageMetadata) metadata;
    } else {
      return;
    }

    @SuppressWarnings("unchecked")
    List<TiffImageMetadata.TiffMetadataItem> items = (List<TiffImageMetadata.TiffMetadataItem>) tiffImageMetadata.getItems();

    for (TiffImageMetadata.TiffMetadataItem item : items) {
      ProvaLeggere.s_log.info(item.getTiffField().toString());
    }

  }

  public static void main(String[] args) throws ImageReadException, IOException {
    ProvaLeggere app = new ProvaLeggere();
    File fiImg = new File("C:/temp/photo/seconda.jpg");
    s_log.info(fiImg.getAbsolutePath());
    app.printAllExifData(fiImg);
  }

}
