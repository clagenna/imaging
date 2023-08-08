package prova.javafx.tree.car;

       

public class ProvaPropReference {
  
  public ProvaPropReference() {
    // 
  }

  public static void main(String[] args) {
    var app = new ProvaPropReference();
    app.doTheJob();
  }

  @SuppressWarnings("unused")
  private void doTheJob() {
    var au = new Automobile("Mercedes", "SL500", 2022);
    // PropertyReference<Automobile> propertyRef = new PropertyReference<Automobile>(au.getClass(), getProperty());
  }

}
