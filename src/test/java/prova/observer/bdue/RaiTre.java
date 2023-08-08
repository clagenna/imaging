package prova.observer.bdue;

import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class RaiTre implements Observer {

  public RaiTre() {
    var cln = getClass().getSimpleName();
    System.out.println("Nata la Tv :" + cln);
  }

  @Override
  public void update(Observable p_o, Object p_news) {
    var cln = getClass().getSimpleName();
    System.out.printf("%s.aggiorna(%s)\n", cln, p_news);
  }

}
