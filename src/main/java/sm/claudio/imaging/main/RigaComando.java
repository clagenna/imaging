package sm.claudio.imaging.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RigaComando {

  private static final Logger s_log              = LogManager.getLogger(RigaComando.class);

  public static final String  CSZ_OPT_DSTDIR     = "dst";
  public static final String  CSZ_OPT_SRCDIR     = "src";
  public static final String  CSZ_OPT_SETDATE    = "setdate";
  public static final String  CSZ_OPT_FILTROFILE = "files";
  public static final String  CSZ_OPT_OVDATE     = "dateover";
  public static final String  CSZ_OPT_RENAME     = "rename";
  public static final String  CSZ_OPT_SUBDIRS    = "recurse";

  private Options             s_options;
  private CommandLine         s_cmdLine;

  private Path                pthSrc;
  private Path                pthDst;

  public RigaComando() {
    //
  }

  public void creaOptions() {
    s_options = new Options();
    final boolean WITH_ARGS = true;
    final boolean NO_ARGS = false;

    Option op = new Option(CSZ_OPT_SRCDIR, WITH_ARGS, "il direttorio sorgente per la scansione");
    op.setRequired(true);
    s_options.addOption(op);

    op = new Option(CSZ_OPT_DSTDIR, WITH_ARGS, "il direttorio destinazione dei files convertiti");
    op.setRequired(true);
    s_options.addOption(op);

    op = new Option(CSZ_OPT_SETDATE, WITH_ARGS,
        "Imposta la data ai JPeg, lo desume dal nome direttorio (arg min di incremento, def: 5min)");
    op.setRequired(false);
    op.setOptionalArg(true);
    s_options.addOption(op);

    op = new Option(CSZ_OPT_FILTROFILE, WITH_ARGS, "Seleziona solo i file specificati (es: img*.jpg)");
    op.setRequired(false);
    op.setOptionalArg(true);
    s_options.addOption(op);

    op = new Option(CSZ_OPT_OVDATE, NO_ARGS, "Se trova gia' una data nel info exif la sovrascrive");
    op.setRequired(false);
    s_options.addOption(op);

    op = new Option(CSZ_OPT_RENAME, NO_ARGS, "Rinomina il file con la sua data di scatto");
    op.setRequired(false);
    s_options.addOption(op);

    s_options.addOption(CSZ_OPT_SUBDIRS, NO_ARGS, "Se la scansione deve essere ricorsiva");

  }

  public boolean parseOptions(String[] args) {
    boolean bRet = false;
    CommandLineParser prs = new DefaultParser();
    if (args.length == 0) {
      help();
      return bRet;
    }

    try {
      s_cmdLine = prs.parse(s_options, args);
      controllaOptions();
    } catch (ParseException e) {
      s_log.error(e);
      help();
      return bRet;
    }
    s_log.debug("MainApp.main() src=" + s_cmdLine.getOptionValue(CSZ_OPT_SRCDIR));
    s_log.debug("MainApp.main() dst=" + s_cmdLine.getOptionValue(CSZ_OPT_DSTDIR));

    return bRet;
  }

  public void controllaOptions() throws ParseException {
    if ( !s_cmdLine.hasOption(CSZ_OPT_SRCDIR))
      throw new ParseException("Non hai specificato il dir sorgente");
    String sz = s_cmdLine.getOptionValue(CSZ_OPT_SRCDIR);
    pthSrc = Paths.get(sz);

    try {
      if (s_cmdLine.hasOption(CSZ_OPT_DSTDIR)) {
        sz = s_cmdLine.getOptionValue(CSZ_OPT_DSTDIR);
        pthDst = Paths.get(sz);
        if (Files.isSameFile(pthSrc, pthDst)) {
          pthDst = null;
        }
      }
    } catch (IOException e) {
      throw new ParseException("Errore determinazione dei direttori:" + e.getMessage());
    }
  }

  public CommandLine getCommandLine() {
    return s_cmdLine;
  }

  public boolean isOption(String p_sz) {
    return getCommandLine().hasOption(p_sz);
  }

  public String getOption(String p_sz) {
    return getCommandLine().getOptionValue(p_sz);
  }

  public Integer getOptionInt(String p_sz) {
    String sz = getCommandLine().getOptionValue(p_sz);
    Integer ii = null;
    if (sz != null)
      ii = Integer.valueOf(sz);
    return ii;
  }

  public void help() {
    HelpFormatter hlp = new HelpFormatter();
    hlp.printHelp("ScanPictures", s_options, true);
  }

}
