package prova.finale;

@FunctionalInterface
public interface InterfacciaFunzionale {
  void testSeEffitivoFinal();

  default void testValore() {
    int realeFinale = 10;
    @SuppressWarnings("unused")
    InterfacciaFunzionale intfFunzionale = () -> System.out.println("Il valore eff di realeFinale:" + realeFinale);
  }
}
