package prova.observer.auno;

public class MainNews {

  public MainNews() {
    //
  }

  public static void main(String[] args) {
    MainNews app = new MainNews();
    app.doTheJob();
  }

  private void doTheJob() {
    AgenziaNews anews = new AgenziaNews();
    RaiUno uno = new RaiUno();
    anews.addCanale(uno);
    RaiDue due = new RaiDue();
    RaiTre tre = new RaiTre();
    // ----------------------------
    String not = "Prima notizia";
    anews.setNews(not);
    not = "Seconda notizia";
    anews.setNews(not);
    // ----------------------------
    anews.addCanale(due);
    not = "Terza notizia";
    anews.setNews(not);
    anews.addCanale(tre);
    not = "Quarta notizia";
    anews.setNews(not);

    System.out.println("Fine del broadcast");
  }

}
