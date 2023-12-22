package prova.takeout;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.Test;

import lombok.Getter;

public class ProvaParseJsonConSplit {
  private static final String CSZ_JSON_TAKEOUT = "F:\\temp\\gpx\\Takeout\\Cronologia delle posizioni\\Records-2023.json";
  @Getter
  private int                 riga;
  private static String       CSZ_JSON         = "[{\r\n"                                                                //
      + "    \"latitudeE7\": 496378241,\r\n"                                                                             //
      + "    \"longitudeE7\": 2218728,\r\n"                                                                              //
      + "    \"accuracy\": 2200,\r\n"                                                                                    //
      + "    \"altitude\": 178,\r\n"                                                                                     //
      + "    \"verticalAccuracy\": 4,\r\n"                                                                               //
      + "    \"activity\": [{\r\n"                                                                                       //
      + "      \"activity\": [{\r\n"                                                                                     //
      + "        \"type\": \"IN_VEHICLE\",\r\n"                                                                          //
      + "        \"confidence\": 93,\r\n"                                                                                //
      + "        \"extra\": {\r\n"                                                                                       //
      + "          \"type\": \"VALUE\",\r\n"                                                                             //
      + "          \"name\": \"vehicle_personal_confidence\",\r\n"                                                       //
      + "          \"intVal\": 100\r\n"                                                                                  //
      + "        }\r\n"                                                                                                  //
      + "      }, {\r\n"                                                                                                 //
      + "        \"type\": \"IN_ROAD_VEHICLE\",\r\n"                                                                     //
      + "        \"confidence\": 93\r\n"                                                                                 //
      + "      }, {\r\n"                                                                                                 //
      + "        \"type\": \"IN_FOUR_WHEELER_VEHICLE\",\r\n"                                                             //
      + "        \"confidence\": 92\r\n"                                                                                 //
      + "      }, {\r\n"                                                                                                 //
      + "        \"type\": \"IN_CAR\",\r\n"                                                                              //
      + "        \"confidence\": 92\r\n"                                                                                 //
      + "      }, {\r\n"                                                                                                 //
      + "        \"type\": \"IN_RAIL_VEHICLE\",\r\n"                                                                     //
      + "        \"confidence\": 6\r\n"                                                                                  //
      + "      }, {\r\n"                                                                                                 //
      + "        \"type\": \"UNKNOWN\",\r\n"                                                                             //
      + "        \"confidence\": 1\r\n"                                                                                  //
      + "      }],\r\n"                                                                                                  //
      + "      \"timestamp\": \"2023-07-08T13:08:29.671Z\"\r\n"                                                          //
      + "    }],\r\n"                                                                                                    //
      + "    \"source\": \"CELL\",\r\n"                                                                                  //
      + "    \"deviceTag\": 1081131441,\r\n"                                                                             //
      + "    \"platformType\": \"ANDROID\",\r\n"                                                                         //
      + "    \"osLevel\": 33,\r\n"                                                                                       //
      + "    \"serverTimestamp\": \"2023-07-08T13:24:17.551Z\",\r\n"                                                     //
      + "    \"deviceTimestamp\": \"2023-07-08T13:24:17.179Z\",\r\n"                                                     //
      + "    \"batteryCharging\": false,\r\n"                                                                            //
      + "    \"formFactor\": \"PHONE\",\r\n"                                                                             //
      + "    \"timestamp\": \"2023-07-08T13:09:15.241Z\"\r\n"                                                            //
      + "  }, {" + "    \"serverTimestamp\": \"2023-07-08T13:24:17.551Z\",\r\n"                                          //
      + "    \"deviceTimestamp\": \"2023-07-08T13:24:17.179Z\",\r\n"                                                     //
      + "    \"batteryCharging\": false,\r\n"                                                                            //
      + " }]";

  public ProvaParseJsonConSplit() {
    //
  }

  @Test
  public void provaLex() {

    String text = "Hello@World@This@Is@A@Java@Program";
    String textMixed = "@HelloWorld@This:Is@A#Java#Program";

    String splitter = "((?=@))";
    String[] splits = text.split(splitter);
    stampa(splits, splitter);

    splitter = "((?<=@))";
    splits = text.split(splitter);
    stampa(splits, splitter);

    splitter = "((?=@)|(?<=@))";
    splits = text.split(splitter);
    stampa(splits, splitter);

    splitter = "((?=:|#|@)|(?<=:|#|@))";
    splits = textMixed.split(splitter);
    stampa(splits, splitter);

    splitter = "((?=\\[|\\]|\\{|\\}|,|:)|(?<=\\[|\\]|\\{|\\}|,|:))";
    splitter = "((?=\\[|\\]|\\{|\\}|,|:|\")|(?<=\\[|\\]|\\{|\\}|,|:|\"))";
    splits = CSZ_JSON.split(splitter);
    stampa(splits, splitter);

    riga = 0;
    try (Stream<String> stre = Files.lines(Paths.get(CSZ_JSON_TAKEOUT))) {
      stre.forEach(s -> analizzaRiga(s));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void stampa(String[] p_splits, String p_string) {
    System.out.println("---------------------\n\t" + p_string);
    for (String sz : p_splits)
      System.out.println(sz);
  }

  private Object analizzaRiga(String p_s) {
    riga++;

    return null;
  }
}
