package xyz.lilyflower.solaris.internal.illumos;

import java.util.Arrays;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import xyz.lilyflower.solaris.api.LoadStage;
import xyz.lilyflower.solaris.api.SolarisIntegrationModule;
import xyz.lilyflower.solaris.init.Solaris;

public class PlanetSetup implements SolarisIntegrationModule {

    @Override
    public void run() {
    }

    private static ResourceLocation ICON(String body) {
        return new ResourceLocation("galacticraftcore", "textures/gui/celestialbodies/" + body + ".png");
    }

    @Override
    public List<String> requiredMods() {
        return Arrays.asList("GalacticraftCore", "lotr");
    }

    @Override
    public boolean valid() {
        return Solaris.STATE == LoadStage.RUNNING;
    }
}
