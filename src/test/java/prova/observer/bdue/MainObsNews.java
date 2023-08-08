package prova.observer.bdue;

import java.util.Observable;
import java.util.Observer;

/**
 * L' interfaccia {@link Observer} non è perfetta ed è stata <b>deprecata</b>
 * sin dalla versione di Java 9.<br/>
 * Uno degli svantaggi è che {@link Observable} non è un'<u>interfaccia</u>, ma
 * una <b>classe</b>! <br/>
 * Per questo le sottoclassi <b>non</b> possono essere utilizzate come
 * {@link Observable}!<br/>
 * Inoltre, uno sviluppatore potrebbe sovrascrivere alcuni dei metodi
 * sincronizzati di {@link Observable} e interrompere la sicurezza dei
 * thread.<br/>
 *
 * Ora diamo un'occhiata all'interfaccia ProperyChangeListener , consigliata
 * rispetto a Observer .
 *
 * @author claudio
 *
 */
@SuppressWarnings("deprecation")
public class MainObsNews {

  public MainObsNews() {
    //
  }

  public static void main(String[] args) {
    MainObsNews app = new MainObsNews();
    app.doTheJob();
  }

  private void doTheJob() {
    ObsAgenziaNews anews = new ObsAgenziaNews();
    RaiUno uno = new RaiUno();
    anews.addObserver(uno);
    RaiDue due = new RaiDue();
    RaiTre tre = new RaiTre();
    // ----------------------------
    String not = "Prima notizia";
    anews.setNews(not);
    not = "Seconda notizia";
    anews.setNews(not);
    // ----------------------------
    anews.addObserver(due);
    not = "Terza notizia";
    anews.setNews(not);
    anews.addObserver(tre);
    not = "Quarta notizia";
    anews.setNews(not);

    System.out.println("Fine del broadcast");

  }

}
