package xyz.lilyflower.solaris.util;

import java.util.ArrayList;

public class InvertedList<T> extends ArrayList<T> {
    @Override
    public boolean contains(Object o) {
        return !super.contains(o);
    }
}
