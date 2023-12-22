package prova.takeout;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProvaParseConJackson {
  private static final String CSZ_JSON_TAKEOUT = "F:\\temp\\gpx\\Takeout\\Cronologia delle posizioni\\Records-2023_BIS.json";

  public ProvaParseConJackson() {
    //
  }

  @Test
  public void doTheJob() {
    int nesting = 0;
    ObjectMapper mapper = new ObjectMapper();
    // create instance of the File class
    File fileObj = new File(CSZ_JSON_TAKEOUT);
    try {
      // read JSON data from file using fileObj and map it using ObjectMapper and TypeReference classes
      Map<String, Object> userData = mapper.readValue(//
          fileObj, //
          new TypeReference<Map<String, Object>>() {
          } //
      );
      printMap(userData, nesting);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  private void printMap(Map<String, Object> p_map, int p_nest) {
    String p_tab = "  ".repeat(p_nest * 2);
    StringBuilder sb = new StringBuilder();
    String vir="";
    sb.append("VALUES (");
    System.out.printf("%s {\n", p_tab);
    for (String szKey : p_map.keySet()) {
      Object val = p_map.get(szKey);
      String szClass = val.getClass().getSimpleName();
      switch (szClass) {
        case "ArrayList":
          System.out.printf("%s%s {\n", p_tab, szKey);
          // stampo il 1^ livello solo
          if (p_nest <= 1)
            printArray((ArrayList<Object>) val, p_nest + 1);
          break;
        case "Map":
        case "LinkedHashMap":
          // System.out.printf("%s%s {", p_tab, szKey);
          printMap((Map<String, Object>) val, p_nest + 1);
          // System.out.printf("%s}", p_tab);
          break;
        default:
          System.out.printf("%s%s = (%s) %s\n", p_tab, szKey, szClass, val);
          switch (szKey) {
            case "longitudeE7":
            case "latitudeE7":
              sb.append(vir).append(String.format(Locale.US, "%.10f", Double.parseDouble(val.toString()) / 10_000_000F)).append(",\n\t");
              break;
            case "altitude":
            case "accuracy":
            case "verticalAccuracy":
            case "deviceTag":
            case "osLevel":
              sb.append(vir).append(val.toString()).append(",\n\t");
              break;
            case "source":
            case "platformType":
            case "deviceDesignation":
            case "formFactor":
              sb.append(vir).append(String.format("'%s',\n\t", val.toString()));
              break;
            case "serverTimestamp":
            case "deviceTimestamp":
            case "timestamp":
              sb.append(vir).append(String.format("'%s',\n\t", val.toString()));
              break;
            case "batteryCharging":
              sb.append(vir).append(Boolean.parseBoolean(val.toString()) ? 1 : 0).append(",\n\t");
              break;
          }
          break;
      }
      vir=",";
    }
    System.out.printf("%s}\n", p_tab);
    System.out.println(sb.toString());
  }

  @SuppressWarnings("unchecked")
  private void printArray(ArrayList<Object> p_map, int p_nest) {
    String p_tab = "  ".repeat(p_nest * 2);
    int qta = 0;
    for (Object val : p_map) {
      System.out.printf("%s(%d)[\n", p_tab, ++qta);
      String szClass = val.getClass().getSimpleName();
      switch (szClass) {
        case "ArrayList":
          printArray((ArrayList<Object>) val, p_nest + 1);
          break;
        case "Map":
        case "LinkedHashMap":
          // System.out.printf("%s {", p_tab);
          printMap((Map<String, Object>) val, p_nest + 1);
          // System.out.printf("%s}", p_tab);
          break;
        default:
          System.out.printf("%s(%d) = (%s) %s\n", p_tab, ++qta, szClass, val);
          break;
      }
      System.out.printf("%s(%d)]\n", p_tab, qta);
    }
  }
}
