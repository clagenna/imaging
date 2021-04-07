package prova.dirs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListAllFilesJava8Nio {

  static String      szFiltro = "img*.jpg";
  static PathMatcher pthmFiltro;

  public static void main(String[] args) {
    Path pthStrt = Paths.get("\\\\nascasa", "photo");
    pthmFiltro = pthStrt.getFileSystem().getPathMatcher("glob:" + szFiltro);

    // Finding only files.
    System.out.println("File Names : ");
    ListAllFilesJava8Nio.printFileNames(pthStrt);

    // Finding only directories
    System.out.println("Folder Names : ");
    ListAllFilesJava8Nio.printFolderNames(pthStrt);

    // Filtering file names by a pattern.
    System.out.println("Filtering name by a pattern \"Line\": ");
    ListAllFilesJava8Nio.filterByPattern(pthStrt);

  }

  private static void printFileNames(Path directory) {

    // Reading the folder and getting Stream.
    try (Stream<Path> walk = Files.walk(directory)) {

      // Filtering the paths by a regualr file and adding into a list.
      List<String> fileNamesList = walk //
          .filter(s -> (Files.isRegularFile(s, LinkOption.NOFOLLOW_LINKS) && pthmFiltro.matches(s))) //
          .map(x -> x.toString()) //
          .collect(Collectors.toList());

      // printing the file nams
      fileNamesList.forEach(System.out::println);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void printFolderNames(Path directory) {

    // Reading the folder and getting Stream.
    try (Stream<Path> walk = Files.walk(directory)) {

      // Filtering the paths by a folder and adding into a list.
      List<String> folderNamesList = walk.filter(Files::isDirectory).map(x -> x.toString()).collect(Collectors.toList());

      // printing the folder names
      folderNamesList.forEach(System.out::println);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void filterByPattern(Path directory) {

    // Reading the folder and getting Stream.
    try (Stream<Path> walk = Files.walk(directory)) {

      // Filtering the paths by a folder and adding into a list.

      List<String> fileNamesList = walk.map(x -> x.toString()).filter(f -> f.contains("Line")).collect(Collectors.toList());

      // printing the folder names
      fileNamesList.forEach(System.out::println);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
