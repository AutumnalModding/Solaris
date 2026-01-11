package xyz.lilyflower.solaris.api;

import java.util.ArrayList;
import xyz.lilyflower.solaris.util.SolarisExtensions;

@SuppressWarnings("unused")
public interface ContentRegistry<T> {
    ArrayList<SolarisExtensions.Pair<T, String>> contents();
    void register(SolarisExtensions.Pair<T, String> pair);
    boolean valid(String key);
    boolean runnable();
}
