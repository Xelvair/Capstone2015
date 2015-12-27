package capstone2015.util;

public class Pair<T, U> {

    private final T first;
    private final U second;

    /*public static <T, U> Pair<T, U> create(T first, U second) {
        return new Pair<T, U>(first, second);
    }*/

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

}
