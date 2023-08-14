package prova.takeout;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import sm.claudio.imaging.gpx.GooglePos;

public class ParseGooTakeout {
  private static final String CSZ_FILE = "F:\\temp\\gpx\\Takeout\\Cronologia delle posizioni\\Records_ARR.json";

  public ParseGooTakeout() {
    // 
  }

  @Test
  public void doIt() {
    try (InputStream inputStream = Files.newInputStream(Path.of(CSZ_FILE));
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));) {
      reader.beginArray();
      while (reader.hasNext()) {
        GooglePos pp = new Gson().fromJson(reader, GooglePos.class);
        System.out.println(pp);
      }
      reader.endArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
