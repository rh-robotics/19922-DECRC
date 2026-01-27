package org.firstinspires.ftc.teamcode.helpers;

/**
 * Simple immutable tuple for passing around pairs of related data
 * Uses Generics
 */
public class Tuple<A,B> {
    private A first;
    private B second;
    //constructor of javatuples class

    /**
     * Constructor for the new tuple make sure to assign the correct data type
     * @param first the first of the pair values
     * @param second the second of the pair values
     */
    public Tuple(A first, B second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first value of the tuple
     * @return the first value
     */
    public A getFirst() {
            return first;
        }

    /**
     * Returns the second value of the tuple
     * @return the second value
     */
    public B getSecond() {
            return second;
        }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

}
