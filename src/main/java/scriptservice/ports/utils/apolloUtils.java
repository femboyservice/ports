package scriptservice.ports.utils;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.icon.AdvancedResourceLocationIcon;
import com.lunarclient.apollo.common.icon.ItemStackIcon;
import com.lunarclient.apollo.common.icon.SimpleResourceLocationIcon;
import com.lunarclient.apollo.module.cooldown.Cooldown;
import com.lunarclient.apollo.module.cooldown.CooldownModule;

import org.bukkit.entity.Player;
import scriptservice.ports.Main;
import com.lunarclient.apollo.player.ApolloPlayer;

import java.time.Duration;
import java.util.Optional;

public class apolloUtils {
    private final Main main;
    public apolloUtils(Main main) {this.main = main;}

    // consts
    CooldownModule cooldownModule = Apollo.getModuleManager().getModule(CooldownModule.class);

    // fonctions
    // oui bon, j'avoue qu'il y a des fonctions sacrement inutile ici, mais c'est pour apprendre comment fonctionne apollo :)
    private void showSimpleResourcePackCooldown(Player player, String cooldownName, String pathName, long cooldown, int size) {
        boolean runningLunarClient = Apollo.getPlayerManager().hasSupport(player.getUniqueId());
        if (!runningLunarClient) {return;}

        Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(player.getUniqueId());

        apolloPlayerOpt.ifPresent(apolloPlayer -> {
            cooldownModule.displayCooldown(apolloPlayer, Cooldown.builder()
                    .name(cooldownName)
                    .duration(Duration.ofSeconds(cooldown))
                    .icon(SimpleResourceLocationIcon.builder()
                            .resourceLocation(pathName)
                            .size(size)
                            .build()
                    )
                    .build()
            );
        });
    }

    private void showAdvancedResourcePackCooldown(Player player, String cooldownName, String pathName, long cooldown, float iconHeight, float iconWidth, float minU, float maxU, float minV, float maxV) {
        boolean runningLunarClient = Apollo.getPlayerManager().hasSupport(player.getUniqueId());
        if (!runningLunarClient) {return;}

        Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(player.getUniqueId());

        AdvancedResourceLocationIcon icon =
                AdvancedResourceLocationIcon.builder()
                        .resourceLocation(pathName)
                        .width(iconWidth)
                        .height(iconHeight)
                        .minU(minU)
                        .maxU(maxU)
                        .minV(minV)
                        .maxV(maxV)
                        .build();

        apolloPlayerOpt.ifPresent(apolloPlayer -> {
            cooldownModule.displayCooldown(apolloPlayer, Cooldown.builder()
                    .name(cooldownName)
                    .duration(Duration.ofSeconds(cooldown))
                    .icon(icon)
                    .build()
            );
        });
    }

    private void removeCooldown(Player player, String cooldownName) {
        boolean runningLunarClient = Apollo.getPlayerManager().hasSupport(player.getUniqueId());
        if (!runningLunarClient) {return;}

        Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(player.getUniqueId());

        apolloPlayerOpt.ifPresent(apolloPlayer -> {
            cooldownModule.removeCooldown(apolloPlayer, cooldownName);
        });
    }

    public void showWindChargeCooldown(Player player) {
        showSimpleResourcePackCooldown(player, "windcharge-cooldown", "ports:windcharge.png", main.windchargeUtils.windchargeCooldown, 8);
    }

    public void showNoFallDuration(Player player) {
        showAdvancedResourcePackCooldown(player, "nofall-cooldown", "ports:nofall.png", main.windchargeUtils.activateFallDamageAfterSeconds, 16.0f, 16.0f, 0.0f, 1.0f, 0.0f, 1.0f);
    }

    public void showFallDamageReduction(Player player, int duration, String pathName) {
        showAdvancedResourcePackCooldown(player, "fallreduction-cooldown", pathName, duration, 16.0f, 16.0f, 0.0f, 1.0f, 0.0f, 1.0f);
    }

    public void removeNoFallDuration(Player player) {
        removeCooldown(player, "nofall-cooldown");
    }

    public void removeFallDamageReduction(Player player) {
        removeCooldown(player, "fallreduction-cooldown");
    }
}
