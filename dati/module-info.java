/**
 * A Module is
 * <ul>
 * <li>a group of closely related packages and resources</li>
 * <li>along with a new module descriptor file.</li>
 * </ul>
 * In other words, it's a “package of Java Packages” abstraction that allows us
 * to make our code even more reusable.
 *
 * @see <a href="https://www.baeldung.com/java-9-modularity">il sito
 *      Baeldung</a>
 * @author claudio
 */
module imaging {
  //  requires com.google.gson;
  //  requires com.jfoenix;
  //  requires transitive commons.cli;
  
  requires java.desktop;
  requires java.sql;
  requires javafx.base;
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  requires javafx.media;
  requires javafx.web;
  requires lombok;
  requires metadata.extractor;
  requires org.apache.commons.imaging;
  requires org.apache.logging.log4j;
  requires org.apache.logging.log4j.core;
  requires org.controlsfx.controls;
  requires com.jfoenix;
  requires com.google.gson;
  requires transitive commons.cli;
  //    requires org.controlsfx.controls;

  opens sm.claudio.imaging to javafx.graphics, javafx.fxml, javafx.media, org.controlsfx.controls;
  opens sm.claudio.imaging.gpx to com.google.gson;
  // opens prova.javafx to javafx.graphics, javafx.fxml, javafx.media, org.controlsfx.controls;
  //  opens sm.claudio.imaging.javafx to javafx.graphics, javafx.fxml, javafx.media, org.controlsfx.controls;
  //  opens sm.claudio.imaging.fsvisit to javafx.base, javafx.graphics, javafx.fxml, org.controlsfx.controls;
  //  opens sm.claudio.imaging.gpx to com.google.gson;
  // opens prova.javafx to javafx.graphics, javafx.fxml, org.controlsfx.controls;
  // opens prova.javafx.tree to javafx.base, javafx.graphics, javafx.fxml, org.controlsfx.controls;
}
