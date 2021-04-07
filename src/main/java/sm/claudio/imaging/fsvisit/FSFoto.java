package sm.claudio.imaging.fsvisit;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputField;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import sm.claudio.imaging.sys.ParseData;

public abstract class FSFoto extends FSFile {

  private static final String                  CSZ_FOTOFILEPATTERN = "'f'yyyyMMdd_HHmmss";
  private static final List<DateTimeFormatter> s_arrFmt;

  static {
    s_arrFmt = new ArrayList<DateTimeFormatter>();
    s_arrFmt.add(DateTimeFormatter.ofPattern("'f'yyyyMMdd'_'HHmmss"));
    s_arrFmt.add(DateTimeFormatter.ofPattern("'WhatsApp Image 'yyyy-MM-dd' at 'HH.mm.ss"));
    s_arrFmt.add(DateTimeFormatter.ofPattern("yyyy-MM-dd' at 'HH.mm.ss"));
  }

  /** da EXIF_TAG_DATE_TIME_ORIGINAL */
  private boolean       m_bFileInError;
  private LocalDateTime dtAssunta = null;
  private LocalDateTime dtNomeFile;
  private LocalDateTime dtCreazione;
  private LocalDateTime dtUltModif;
  private LocalDateTime dtAcquisizione;
  private LocalDateTime dtParentDir;

  enum CosaFare {
    setNomeFile, //
    setDtCreazione, //
    setUltModif, //
    setDtAcquisizione
  };

  private Set<CosaFare> m_daFare;

  public FSFoto() {
    //
  }

  public FSFoto(Path p_fi) throws FileNotFoundException {
    super(p_fi);
  }

  @Override
  public void setPath(Path p_fi) throws FileNotFoundException {
    super.setPath(p_fi);
    m_bFileInError = false;
    leggiFilesAttributes();
    interpretaDateTimeDaNomefile();
    leggiExifDtOriginal();
    leggiDtParentDir();
  }

  private void leggiExifDtOriginal() {
    ImageMetadata metadata = null;
    File fi = getPath().toFile();
    try {
      metadata = Imaging.getMetadata(fi);
    } catch (ImageReadException | IOException e) {
      setFileInError(true);
      getLogger().error("Errore Lettura metadata:" + fi.getAbsolutePath(), e);
      return;
    }
    // getLogger().info("-------->" + getPath().getFileName());
    TiffImageMetadata exif = null;
    if (metadata instanceof JpegImageMetadata) {
      exif = ((JpegImageMetadata) metadata).getExif();
    } else if (metadata instanceof TiffImageMetadata) {
      exif = (TiffImageMetadata) metadata;
    } else {
      getLogger().info("Mancano completamente le info EXIF!");
      // return;
    }
    if (exif == null)
      return;
    String szDt = null;
    try {
      String[] arr = exif.getFieldValue(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
      if (arr != null && arr.length > 0) {
        szDt = arr[0];
      }
      if (szDt != null)
        dtAcquisizione = LocalDateTime.from(ParseData.s_fmtDtExif.parse(szDt));
    } catch (ImageReadException | DateTimeParseException e) {
      setFileInError(true);
      getLogger().error("Errore leggi Dt ORIGINAL", e);
    }

  }

  public String toStringExif() {
    StringBuilder sb = new StringBuilder();
    ImageMetadata metadata = null;
    try {
      metadata = Imaging.getMetadata(getPath().toFile());
    } catch (ImageReadException | IOException e) {
      getLogger().error("Lettura metadata", e);
      return null;
    }
    getLogger().info("-------->" + getPath().getFileName());
    TiffImageMetadata exif = null;
    if (metadata instanceof JpegImageMetadata) {
      exif = ((JpegImageMetadata) metadata).getExif();
    } else if (metadata instanceof TiffImageMetadata) {
      exif = (TiffImageMetadata) metadata;
    } else {
      getLogger().info("Mancano completamente le info EXIF!");
      // return;
    }

    @SuppressWarnings("unchecked")
    List<TiffImageMetadata.TiffMetadataItem> items = (List<TiffImageMetadata.TiffMetadataItem>) exif.getItems();
    /**
     * <pre>
     *
     * -------->f20031030_174032.jpg
    271   (0x10f:   Make):  'PENTAX Corporation' (19 ASCII)
    272   (0x110:   Model):   'PENTAX Optio 550' (17 ASCII)
    282   (0x11a:   XResolution):   72 (1 Rational)
    283   (0x11b:   YResolution):   72 (1 Rational)
    296   (0x128:   ResolutionUnit):  2 (1 Short)
    305   (0x131:   Software):  '1.00' (5 ASCII)
    306   (0x132:   DateTime):  '2003:10:30 17:40:31' (20 ASCII)
    531   (0x213:   YCbCrPositioning):  2 (1 Short)
    34665   (0x8769:  ExifOffset):  502 (1 Long)
    50341   (0xc4a5:  PrintIM):   80, 114, 105, 110, 116, 73, 77, 0, 48, 50, 53, 48, 0, 0, 12, 0, 1, 0, 22, 0, 22, 0, 2, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0, 10, 0, 0, 0, 0... (128) (128 Undefined)
    33434   (0x829a:  ExposureTime):  10/2050 (0,005) (1 Rational)
    33437   (0x829d:  FNumber):   3 (1 Rational)
    36864   (0x9000:  ExifVersion):   48, 50, 50, 48 (4 Undefined)
    36867   (0x9003:  DateTimeOriginal):  '2003:10:30 17:40:31' (20 ASCII)
    36868   (0x9004:  DateTimeDigitized):   '2003:10:30 17:40:31' (20 ASCII)
    37121   (0x9101:  ComponentsConfiguration):   1, 2, 3, 0 (4 Undefined)
    37122   (0x9102:  CompressedBitsPerPixel):  5 (1 Rational)
    37380   (0x9204:  ExposureCompensation):  0 (1 SRational)
    37381   (0x9205:  MaxApertureValue):  3 (1 Rational)
    37383   (0x9207:  MeteringMode):  2 (1 Short)
    37385   (0x9209:  Flash):   9 (1 Short)
    37386   (0x920a:  FocalLength):   78/10 (7,8) (1 Rational)
    37500   (0x927c:  MakerNote):   65, 79, 67, 0, 0, 0, 41, 0, 1, 0, 3, 0, 1, 0, 0, 0, 2, 0, 0, 0, 2, 0, 3, 0, 1, 0, 0, 0, -128, 2, -32, 1, 3, 0, 4, 0, 1, 0, 0, 0, 122, -97, 0, 0, 4, 0, 4, 0, 1, 0, 0... (1238) (1238 Undefined)
    40960   (0xa000:  FlashpixVersion):   48, 49, 48, 48 (4 Undefined)
    40961   (0xa001:  ColorSpace):  1 (1 Short)
    40962   (0xa002:  ExifImageWidth):  2592 (1 Long)
    40963   (0xa003:  ExifImageLength):   1944 (1 Long)
    40965   (0xa005:  InteropOffset):   922 (1 Long)
    41985   (0xa401:  CustomRendered):  0 (1 Short)
    41986   (0xa402:  ExposureMode):  0 (1 Short)
    41987   (0xa403:  WhiteBalance):  0 (1 Short)
    41988   (0xa404:  DigitalZoomRatio):  0 (1 Rational)
    41989   (0xa405:  FocalLengthIn35mmFormat):   37 (1 Short)
    41990   (0xa406:  SceneCaptureType):  0 (1 Short)
    41992   (0xa408:  Contrast):  0 (1 Short)
    41993   (0xa409:  Saturation):  0 (1 Short)
    41994   (0xa40a:  Sharpness):   0 (1 Short)
    41996   (0xa40c:  SubjectDistanceRange):  3 (1 Short)
    1   (0x1:   InteroperabilityIndex):   'R98' (4 ASCII)
    2   (0x2:   InteroperabilityVersion):   48, 49, 48, 48 (4 Undefined)
    259   (0x103:   Compression):   6 (1 Short)
    282   (0x11a:   XResolution):   72 (1 Rational)
    283   (0x11b:   YResolution):   72 (1 Rational)
    296   (0x128:   ResolutionUnit):  2 (1 Short)
    513   (0x201:   JpgFromRawStart):   4084 (1 Long)
    514   (0x202:   JpgFromRawLength):  7811 (1 Long)
     *
     * </pre>
     */
    for (TiffImageMetadata.TiffMetadataItem item : items) {
      sb.append(item.getTiffField().toString()) //
          .append("\\n");
    }
    return sb.toString();
  }

  private void leggiFilesAttributes() {
    BasicFileAttributes attr = null;
    try {
      attr = Files.readAttributes(getPath(), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
    } catch (IOException e) {
      getLogger().error("Errore lettura attr files", e);
      return;
    }
    FileTime ux = attr.lastModifiedTime();
    setDtUltModif(LocalDateTime.ofInstant(ux.toInstant(), ZoneId.systemDefault()));
    ux = attr.creationTime();
    setDtCreazione(LocalDateTime.ofInstant(ux.toInstant(), ZoneId.systemDefault()));
  }

  private void interpretaDateTimeDaNomefile() {
    dtNomeFile = null;
    Path pth = getPath();
    String sz = pth.getName(pth.getNameCount() - 1).toString();
    int n = sz.lastIndexOf(".");
    if (n > 0)
      sz = sz.substring(0, n);
    ParseData prs = new ParseData();
    setDtNomeFile(prs.parseData(sz));
    if (dtNomeFile == null)
      getLogger().debug("File name no e' DateTime :" + sz);

  }

  private void leggiDtParentDir() {
    Path pth = getParent();
    if (pth == null) {
      getLogger().debug("Il dir padre non ha data interpretabile:*NULL*");
      return;
    }
    String sz = pth.getName(pth.getNameCount() - 1).toString();
    ParseData prs = new ParseData();
    dtParentDir = prs.parseData(sz);
    if (dtParentDir == null)
      getLogger().debug("No e' DateTime Parent Dir name :" + sz);

  }

  public LocalDateTime getDtAcquisizione() {
    return dtAcquisizione;
  }

  public void setDtAcquisizione(LocalDateTime p_dtAcquisizione) {
    dtAcquisizione = p_dtAcquisizione;
  }

  public LocalDateTime getDtCreazione() {
    return dtCreazione;
  }

  public void setDtCreazione(LocalDateTime p_dtCreazione) {
    dtCreazione = p_dtCreazione;
  }

  public LocalDateTime getDtUltModif() {
    return dtUltModif;
  }

  public void setDtUltModif(LocalDateTime p_dtUltModif) {
    dtUltModif = p_dtUltModif;
  }

  public LocalDateTime getDtNomeFile() {
    return dtNomeFile;
  }

  public void setDtNomeFile(LocalDateTime p_dtNomeFile) {
    dtNomeFile = p_dtNomeFile;
  }

  public LocalDateTime getDtAssunta() {
    return dtAssunta;
  }

  public void setDtAssunta(LocalDateTime p_dtAssunta) {
    dtAssunta = p_dtAssunta;
  }

  public boolean isDaAggiornare() {
    getLogger().debug("Analizzo {}", getPath().toString());
    m_daFare = new HashSet<>();
    if (dtNomeFile == null) {
      dtNomeFile = LocalDateTime.MAX;
      m_daFare.add(CosaFare.setNomeFile);
      m_daFare.add(CosaFare.setDtCreazione);
      m_daFare.add(CosaFare.setUltModif);
    }
    if (dtCreazione == null)
      m_daFare.add(CosaFare.setDtCreazione);
    if (dtUltModif == null)
      m_daFare.add(CosaFare.setUltModif);
    if (dtAcquisizione == null)
      m_daFare.add(CosaFare.setDtAcquisizione);

    if (dtAcquisizione != null) {
      if (dtNomeFile.isAfter(dtAcquisizione))
        m_daFare.add(CosaFare.setNomeFile);
      if (dtAcquisizione.isAfter(dtNomeFile))
        m_daFare.add(CosaFare.setDtAcquisizione);
    }

    if (dtCreazione != null) {
      if (dtNomeFile.isAfter(dtCreazione))
        m_daFare.add(CosaFare.setNomeFile);
      if (dtCreazione.isAfter(dtNomeFile))
        m_daFare.add(CosaFare.setDtCreazione);
    }

    if (dtUltModif != null) {
      if (dtNomeFile.isAfter(dtUltModif))
        m_daFare.add(CosaFare.setNomeFile);
      if (dtUltModif.isAfter(dtNomeFile))
        m_daFare.add(CosaFare.setUltModif);
    }

    if ( !m_daFare.contains(CosaFare.setNomeFile)) {
      String szNam = creaNomeFile();
      if ( !getPath().endsWith(szNam))
        m_daFare.add(CosaFare.setNomeFile);
    }
    return m_daFare.size() != 0;
  }

  public Set<CosaFare> getCosaFare() {
    return m_daFare;
  }

  public LocalDateTime getPiuVecchiaData() {
    LocalDateTime dt = LocalDateTime.MAX;
    if (dtAssunta != null)
      return dtAssunta;
    // verifico le altre ...
    if (dtNomeFile != null && dt.isAfter(dtNomeFile))
      dt = dtNomeFile;
    if (dtCreazione != null && dt.isAfter(dtCreazione))
      dt = dtCreazione;
    if (dtUltModif != null && dt.isAfter(dtUltModif))
      dt = dtUltModif;

    if (dtAcquisizione != null) {
      if (dt.isAfter(dtAcquisizione))
        dt = dtAcquisizione;
    } else {
      // se non ha dtAcq allora prendo il parent Dt
      if (dtParentDir != null)
        if (dtNomeFile == null)
          dt = dtParentDir;
    }
    return dt;
  }

  public void cambiaNomeFile() {
    LocalDateTime dt = getPiuVecchiaData();
    String fnam = creaNomeFile(dt);
    Path pthFrom = getPath();
    Path pthTo = Paths.get(getParent().toString(), fnam);
    int k = 1; // loop

    while (Files.exists(pthTo, LinkOption.NOFOLLOW_LINKS)) {
      //      String sz = fnam.replace(".", String.format("_%d.", k++));
      //      pthTo = Paths.get(getParent().toString(), sz);
      if (k++ > 1000)
        throw new UnsupportedOperationException("Troppi loop sul nome file:" + pthFrom.toString());
      dt = dt.plusMinutes(1);
      fnam = creaNomeFile(dt);
      pthTo = Paths.get(getParent().toString(), fnam);
    }
    setDtAssunta(dt);
    try {
      // CopyOption[] opt = { StandardCopyOption.COPY_ATTRIBUTES };
      // Files.move(pthFrom, pthTo, opt);
      getLogger().info("Cambio nome da \"{}\" a \"{}\"", //
          pthFrom.getName(pthFrom.getNameCount() - 1), //
          pthTo.getName(pthTo.getNameCount() - 1) //
      );
      Files.move(pthFrom, pthTo, new CopyOption[0]);
      setPath(pthTo);
    } catch (IOException e) {
      getLogger().error("Errore rename per {}", getPath().toString(), e);
    }

  }

  private String creaNomeFile() {
    return creaNomeFile(null);
  }

  private String creaNomeFile(LocalDateTime p_dt) {
    if (p_dt == null)
      p_dt = getPiuVecchiaData();
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern(CSZ_FOTOFILEPATTERN);
    String fnam = String.format("%s.%s", fmt.format(p_dt), getFileExtention());
    return fnam;
  }

  public void cambiaDtCreazione() {
    LocalDateTime dt = getPiuVecchiaData();
    Instant inst = dt.toInstant(OffsetDateTime.now().getOffset());
    FileTime tm = FileTime.fromMillis(inst.toEpochMilli());

    try {
      // Files.setAttribute(getPath(), "creationTime", tm, LinkOption.NOFOLLOW_LINKS);
      Files.setAttribute(getPath(), "creationTime", tm);
    } catch (IOException e) {
      getLogger().error("Errore di set CreatedTime per {}", getPath().toString(), e);
    }
  }

  public void cambiaDtUltModif() {
    LocalDateTime dt = getPiuVecchiaData();
    Instant inst = dt.toInstant(OffsetDateTime.now().getOffset());
    FileTime tm = FileTime.from(inst);

    try {
      Files.setAttribute(getPath(), "lastModifiedTime", tm, LinkOption.NOFOLLOW_LINKS);
    } catch (IOException e) {
      getLogger().error("Errore di set CreatedTime per {}", getPath().toString(), e);
    }
  }

  public void cambiaDtAcquisizione() {

    if (this instanceof FSTiff) {
      getLogger().error("Non cambio EXIF per {}", getPath().toString());
      return;
    }

    Path pthCopy = Paths.get(getParent().toString(), UUID.randomUUID().toString() + "." + getFileExtention());
    boolean bOk = true;
    try {
      Files.copy(getPath(), pthCopy, StandardCopyOption.COPY_ATTRIBUTES);
    } catch (IOException e) {
      getLogger().error("Errore set EXIF dtAcq per {}", getPath().toString(), e);
      return;
    }
    File jpegImageFile = pthCopy.toFile();
    File dst = getPath().toFile();
    try (FileOutputStream fos = new FileOutputStream(dst); OutputStream os = new BufferedOutputStream(fos)) {
      TiffOutputSet outputSet = null;
      JpegImageMetadata jpegMetadata = (JpegImageMetadata) Imaging.getMetadata(jpegImageFile);
      TiffImageMetadata exif = null;
      if (jpegMetadata != null)
        exif = jpegMetadata.getExif();
      // TiffOutputSet class sono i dati EXIF che devo andare a scrivere
      if (exif != null)
        outputSet = exif.getOutputSet();

      // Se il file non contiene metadata EXIF, ne creiamo uno vuoto
      if (null == outputSet)
        outputSet = new TiffOutputSet();

      TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();

      // sovrascrivo la dtAcq con la data ottenuta perche adesso deve diventare quella!
      dtAcquisizione = getPiuVecchiaData();
      String szDt = ParseData.s_fmtDtExif.format(dtAcquisizione);

      TiffOutputField dd = new TiffOutputField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, FieldType.ASCII, szDt.length(),
          szDt.getBytes());
      exifDirectory.removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
      exifDirectory.add(dd);
      // printTagValue(jpegMetadata, TiffConstants.TIFF_TAG_DATE_TIME);

      new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os, outputSet);
    } catch (FileNotFoundException e) {
      bOk = false;
      getLogger().error("Errore sul file {}", pthCopy.toString(), e);
    } catch (IOException e) {
      bOk = false;
      getLogger().error("Errore I/O sul file {}", pthCopy.toString(), e);
    } catch (ImageReadException | ImageWriteException e) {
      bOk = false;
      getLogger().error("Errore lettura EXIF sul file {}", pthCopy.toString(), e);
    }

    try {
      if (bOk)
        Files.delete(pthCopy);
      else
        Files.move(pthCopy, getPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      getLogger().error("Errore di cancellazione di {}", pthCopy.toString(), e);
    }

  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getPath().toString()) //
        .append("\n\t");
    sb.append("\tOldest Data:\t" + getPiuVecchiaData().toString()) //
        .append("\n\t");
    /*
     * dtNomeFile; dtCreazione; dtUltModif; dtAcquisizione;
     */
    sb.append("dtNomeFile:\t" + (dtNomeFile != null ? dtNomeFile.toString() : "*NULL*")) //
        .append("\n\t");
    sb.append("dtCreazione:\t" + (dtCreazione != null ? dtCreazione.toString() : "*NULL*")) //
        .append("\n\t");
    sb.append("dtUltModif:\t" + (dtUltModif != null ? dtUltModif.toString() : "*NULL*")) //
        .append("\n\t");
    sb.append("dtAcquisizione:\t" + (dtAcquisizione != null ? dtAcquisizione.toString() : "*NULL*")) //
        .append("\n\t");

    return sb.toString();
  }

  protected void setFileInError(boolean bv) {
    m_bFileInError = bv;
  }

  public boolean isFileInError() {
    return m_bFileInError;
  }

  public abstract String getFileExtention();

}
