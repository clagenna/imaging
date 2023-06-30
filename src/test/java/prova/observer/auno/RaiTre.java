package prova.observer.auno;

public class RaiTre implements ICanaliTV {

  public RaiTre() {
    var cln = getClass().getSimpleName();
    System.out.println("Nata la Tv :" + cln);
  }

  @Override
  public void aggiorna(String p_news) {
    var cln = getClass().getSimpleName();
    System.out.printf("%s.aggiorna(%s)\n", cln, p_news);
  }

}
