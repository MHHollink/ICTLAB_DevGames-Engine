package nl.devgames.utils;

/**
 * This class is a representation of a 2-Tuple. It can be used when a method needs to return 2 parameters at once.
 *
 * @param <K> The class type of the first parameter
 * @param <V> The class type of the second parameter
 */
public class Tuple<K,V> {

    private K k;
    private V v;

    public Tuple(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public Tuple() {
    }

    public K getK() {
        return k;
    }
    public V getV() {
        return v;
    }

    public void setK(K k) {
        this.k = k;
    }
    public void setV(V v) {
        this.v = v;
    }
}
