package prova.observer.ctre;

public class MainPCLNews {

  public MainPCLNews() {
    //
  }

  public static void main(String[] args) {
    MainPCLNews app = new MainPCLNews();
    app.doTheJob();
  }

  private void doTheJob() {
    PCLAgenziaNews anews = new PCLAgenziaNews();
    RaiUno uno = new RaiUno();
    anews.addPropertyChangeListener(uno);
    RaiDue due = new RaiDue();
    RaiTre tre = new RaiTre();
    // ----------------------------
    String not = "Prima notizia";
    anews.setNews(not);
    not = "Seconda notizia";
    anews.setNews(not);
    // ----------------------------
    anews.addPropertyChangeListener(due);
    not = "Terza notizia";
    anews.setNews(not);
    anews.addPropertyChangeListener(tre);
    not = "Quarta notizia";
    anews.setNews(not);

    System.out.println("Fine del broadcast");

  }

}
