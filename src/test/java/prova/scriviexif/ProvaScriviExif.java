package prova.scriviexif;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputField;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.commons.io.FileUtils;

public class ProvaScriviExif {

  public void removeExifMetadata(File jpegImageFile, File dst) throws IOException, ImageReadException, ImageWriteException {
    try (FileOutputStream fos = new FileOutputStream(dst); OutputStream os = new BufferedOutputStream(fos)) {
      new ExifRewriter().removeExifMetadata(jpegImageFile, os);
    }
  }

  /**
   * This example illustrates how to add/update EXIF metadata in a JPEG file.
   *
   * @param jpegImageFile
   *          A source image file.
   * @param dst
   *          The output file.
   * @throws IOException
   * @throws ImageReadException
   * @throws ImageWriteException
   */
  public void changeExifMetadata(File jpegImageFile, File dst) throws IOException, ImageReadException, ImageWriteException {

    try (FileOutputStream fos = new FileOutputStream(dst); OutputStream os = new BufferedOutputStream(fos)) {
      TiffOutputSet outputSet = null;
      // note that metadata might be null if no metadata is found.
      ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
      JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
      // note that exif might be null if no Exif metadata is found.
      if (null == jpegMetadata)
        return;
      TiffImageMetadata exif = jpegMetadata.getExif();
      if (null == exif)
        return;
      // TiffImageMetadata class is immutable (read-only).  TiffOutputSet class represents the Exif data to write.
      // Usually, we want to update existing Exif metadata by  changing  the values of a few fields, or adding a field.
      // In these cases, it is easiest to use getOutputSet() to  start with a "copy" of the fields read from the image.
      outputSet = exif.getOutputSet();

      // if file does not contain any exif metadata, we create an empty set of exif metadata. Otherwise, we keep all of the other existing tags.
      if (null == outputSet)
        outputSet = new TiffOutputSet();

      // Example of how to add a field/tag to the output set.
      //
      // Note that you should first remove the field/tag if it already
      // exists in this directory, or you may end up with duplicate tags. See above.
      //
      // Certain fields/tags are expected in certain Exif directories;
      // Others can occur in more than one directory (and often have a different meaning in different directories).
      //
      // TagInfo constants often contain a description of what directories are associated with a given tag.

      TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
      // make sure to remove old value if present (this method will not fail if the tag does not exist).
      exifDirectory.removeField(ExifTagConstants.EXIF_TAG_APERTURE_VALUE);
      exifDirectory.add(ExifTagConstants.EXIF_TAG_APERTURE_VALUE, new RationalNumber(3, 10));

      // Example of how to add/update GPS info to output set.

      // New York City
      double longitude = -74.0; // 74 degrees W (in Degrees East)
      double latitude = 40 + 43 / 60.0; // 40 degrees N (in Degrees North)

      outputSet.setGPSInDegrees(longitude, latitude);

      // printTagValue(jpegMetadata, TiffConstants.TIFF_TAG_DATE_TIME);

      new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os, outputSet);
    }
  }

  /**
   * This example illustrates how to remove a tag (if present) from EXIF
   * metadata in a JPEG file.
   *
   * In this case, we remove the "aperture" tag from the EXIF metadata if
   * present.
   *
   * @param jpegImageFile
   *          A source image file.
   * @param dst
   *          The output file.
   * @throws IOException
   * @throws ImageReadException
   * @throws ImageWriteException
   */
  public void removeExifTag(File jpegImageFile, File dst) throws IOException, ImageReadException, ImageWriteException {
    try (FileOutputStream fos = new FileOutputStream(dst); OutputStream os = new BufferedOutputStream(fos)) {
      TiffOutputSet outputSet = null;

      // note that metadata might be null if no metadata is found.
      ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
      JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
      if (null != jpegMetadata) {
        // note that exif might be null if no Exif metadata is found.
        TiffImageMetadata exif = jpegMetadata.getExif();

        if (null != exif) {
          // TiffImageMetadata class is immutable (read-only).
          // TiffOutputSet class represents the Exif data to write.
          //
          // Usually, we want to update existing Exif metadata by
          // changing
          // the values of a few fields, or adding a field.
          // In these cases, it is easiest to use getOutputSet() to
          // start with a "copy" of the fields read from the image.
          outputSet = exif.getOutputSet();
        }
      }

      if (null == outputSet) {
        // file does not contain any exif metadata. We don't need to
        // update the file; just copy it.
        FileUtils.copyFile(jpegImageFile, dst);
        return;
      }

      {
        // Example of how to remove a single tag/field.
        // There are two ways to do this.

        // Option 1: brute force
        // Note that this approach is crude: Exif data is organized in
        // directories. The same tag/field may appear in more than one
        // directory, and have different meanings in each.
        outputSet.removeField(ExifTagConstants.EXIF_TAG_APERTURE_VALUE);

        // Option 2: precision
        // We know the exact directory the tag should appear in, in this
        // case the "exif" directory.
        // One complicating factor is that in some cases, manufacturers
        // will place the same tag in different directories.
        // To learn which directory a tag appears in, either refer to
        // the constants in ExifTagConstants.java or go to Phil Harvey's
        // EXIF website.
        TiffOutputDirectory exifDirectory = outputSet.getExifDirectory();
        if (null != exifDirectory) {
          exifDirectory.removeField(ExifTagConstants.EXIF_TAG_APERTURE_VALUE);
        }
      }

      new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os, outputSet);
    }
  }

  /**
   * This example illustrates how to set the GPS values in JPEG EXIF metadata.
   *
   * @param jpegImageFile
   *          A source image file.
   * @param dst
   *          The output file.
   * @throws IOException
   * @throws ImageReadException
   * @throws ImageWriteException
   */
  public void setExifGPSTag(File jpegImageFile, File dst) throws IOException, ImageReadException, ImageWriteException {

    try (OutputStream bufos = new BufferedOutputStream(new FileOutputStream(dst))) {
      TiffOutputSet outputSet = null;
      // note that metadata might be null if no metadata is found.
      ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
      JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
      if (null != jpegMetadata) {
        // note that exif might be null if no Exif metadata is found.
        TiffImageMetadata exif = jpegMetadata.getExif();
        if (null != exif) {
          // TiffImageMetadata class is immutable (read-only).
          // TiffOutputSet class represents the Exif data to write.
          // Usually, we want to update existing Exif metadata by  changing
          // the values of a few fields, or adding a field.
          // In these cases, it is easiest to use getOutputSet() to  start with a "copy" of the fields read from the image.
          outputSet = exif.getOutputSet();
        }
      }

      // if file does not contain any exif metadata, we create an empty
      // set of exif metadata. Otherwise, we keep all of the other
      // existing tags.
      if (null == outputSet)
        outputSet = new TiffOutputSet();
      TiffOutputDirectory exifDir = outputSet.getOrCreateExifDirectory();
      String szFile = jpegImageFile.getName();
      int n = szFile.lastIndexOf(".");
      if (n >= 0)
        szFile = szFile.substring(0, n);

      DateTimeFormatter fmtrin = DateTimeFormatter.ofPattern("'f'yyyyMMdd_HHmmss");
      LocalDateTime dt = LocalDateTime.from(fmtrin.parse(szFile));
      DateTimeFormatter fmtrout = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
      String szDt = fmtrout.format(dt);

      TiffOutputField dd = new TiffOutputField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, FieldType.ASCII, szDt.length(),
          szDt.getBytes());
      exifDir.removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
      exifDir.add(dd);

      // Kislovodsk: 43.92113540910801, 42.714829661239285
      // New York City
      double longitude = -74.0; // 74 degrees W (in Degrees East)
      double latitude = 40 + 43 / 60.0; // 40 degrees N (in Degrees North)
      longitude = 42.714829661239285;
      latitude = 43.92113540910801;
      outputSet.removeField(ExifTagConstants.EXIF_TAG_GPSINFO);
      outputSet.setGPSInDegrees(longitude, latitude);

      new ExifRewriter().updateExifMetadataLossless(jpegImageFile, bufos, outputSet);
    }
  }

  public static void main(String[] args) throws ImageReadException, ImageWriteException, IOException {
    ProvaScriviExif app = new ProvaScriviExif();
    File fiIn = new File("F:/temp/photo/pippo/f19590703_101122.jpg");
    File fiOut = new File("F:/temp/photo/pippo/f19590703_101122_COPIA.jpg");
    app.setExifGPSTag(fiIn, fiOut);
  }
}
