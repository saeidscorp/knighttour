package one.scorp.knighttour;

public class Pair<T, E> {
    public T first;
    public E second;

    public Pair(T f, E s) {
        first = f;
        second = s;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Pair)) return false;
        Pair p = (Pair) other;
        return first.equals(p.first) && second.equals(p.second);
    }
}
