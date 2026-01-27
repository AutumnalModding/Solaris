package xyz.lilyflower.solaris.util.list;

import java.util.ArrayList;

public class ChainedArrayList<T> extends ArrayList<T> {
    public ChainedArrayList<T> chain(T element) {
        super.add(element);
        return this;
    }
    
    public ChainedArrayList<T> chain(int index, T element) {
        super.add(index, element);
        return this;
    }
}
