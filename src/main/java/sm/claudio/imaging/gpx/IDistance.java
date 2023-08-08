package sm.claudio.imaging.gpx;

public interface IDistance<T> extends Comparable<T>{
  long distance(T from);
}
