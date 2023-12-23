package sm.claudio.imaging.sys;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

class StreamGobbler implements Runnable {
  private InputStream      inputStream;
  private Consumer<String> consumer;

  public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
    this.inputStream = inputStream;
    this.consumer = consumer;
  }

  @Override
  public void run() {
    new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
  }
}

/**
 * Per l'articolo vedi
 * {@linkplain https://www.baeldung.com/run-shell-command-in-java}
 */
public class ProcessLauncher {
  private boolean isWindows;

  public ProcessLauncher() {
    isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
  }

  /**
   * per l'executorService vedi
   * {@linkplain https://www.baeldung.com/java-executor-service-tutorial}
   *
   * @param p_cmd
   */
  @SuppressWarnings("unused")
  public void esegui(String p_cmd) {
    ProcessBuilder builder = new ProcessBuilder();
    if (isWindows) {
      builder.command("cmd.exe", "/c", "start", p_cmd);
    } else {
      builder.command("sh", "-c", "ls");
    }
    builder.directory(new File(System.getProperty("user.home")));
    Process process;
    StreamGobbler streamGobbler;
    try {
      process = builder.start();
      streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
      // ExecutorService executorService = new ExecutorService();
      // Future<?> future = executorService.submit(streamGobbler);
      int exitCode = process.waitFor();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // assertDoesNotThrow(() -> future.get(10, TimeUnit.SECONDS));
    // Assert.assertEquals(0, exitCode);
  }
}
