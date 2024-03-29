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
 *
 */
module imaging {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  requires javafx.base;
  requires javafx.web;
  requires javafx.media;
  requires org.controlsfx.controls;
  requires java.sql;
  requires com.jfoenix;
  requires java.desktop;
  requires commons.cli;
  requires org.apache.logging.log4j;
  requires org.apache.commons.imaging;
  requires lombok;
  requires metadata.extractor;

  // opens prova.javafx to javafx.graphics, javafx.fxml, org.controlsfx.controls;
  opens sm.claudio.imaging.javafx to javafx.graphics, javafx.fxml, javafx.media, org.controlsfx.controls;
  opens sm.claudio.imaging.fsvisit to javafx.base, javafx.graphics, javafx.fxml, org.controlsfx.controls;
  opens prova.javafx.tree       to javafx.base, javafx.graphics, javafx.fxml, org.controlsfx.controls;
  
}