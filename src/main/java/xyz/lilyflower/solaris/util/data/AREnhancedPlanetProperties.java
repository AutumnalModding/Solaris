package xyz.lilyflower.solaris.util.data;

import zmaster587.advancedRocketry.api.IAtmosphere;

@SuppressWarnings("unused")
public interface AREnhancedPlanetProperties {
    IAtmosphere solaris$direct();
    String solaris$defined();

    void solaris$direct(IAtmosphere type);
    void solaris$defined(String name);
}
