package scriptservice.ports.utils;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.icon.ItemStackIcon;
import com.lunarclient.apollo.mods.impl.ModCooldowns;
import com.lunarclient.apollo.module.ApolloModuleManager;
import com.lunarclient.apollo.module.cooldown.Cooldown;
import com.lunarclient.apollo.module.cooldown.CooldownModule;
import com.lunarclient.apollo.module.modsetting.ModSettingModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import com.lunarclient.apollo.player.ApolloPlayerManager;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import scriptservice.ports.Main;

import java.time.Duration;

public class apolloUtils extends utilManager {
    public apolloUtils(Main plugin) {
        super(plugin);
    }

    // init stuff
    private final ApolloPlayerManager apolloPlayerManager = Apollo.getPlayerManager();
    private final ApolloModuleManager apolloModuleManager = Apollo.getModuleManager();
    private final CooldownModule cooldownModule = apolloModuleManager.getModule(CooldownModule.class);
    private final ModSettingModule modSettingModule = apolloModuleManager.getModule(ModSettingModule.class);

    // overrides
    @Override
    public void init() {
        modSettingModule.getOptions().set(ModCooldowns.ENABLED, true);
    }

    //--// primary
    public final boolean isUsingLC(Player player) {
        return apolloPlayerManager.hasSupport(player.getUniqueId());
    }

    public final ApolloPlayer getApolloPlayer(Player player) {
         return apolloPlayerManager.getPlayer(player.getUniqueId()).orElse(null);
    }

    //--// privs
    private void showCooldown(Player player, String cooldownName, int itemId, long cooldownTime) {
        ApolloPlayer apolloPlayer = getApolloPlayer(player);
        if (apolloPlayer == null) {return;}

        cooldownModule.displayCooldown(apolloPlayer, Cooldown.builder()
                .name(cooldownName)
                .duration(Duration.ofSeconds(cooldownTime))
                .icon(ItemStackIcon.builder()
                        .itemId(itemId)
                        .build()
                ).build()
        );
    }

    private void removeCooldown(Player player, String cooldownName) {
        ApolloPlayer apolloPlayer = getApolloPlayer(player);
        if (apolloPlayer == null) {return;}

        cooldownModule.removeCooldown(apolloPlayer, cooldownName);
    }

    //--// publics
    // show specific cooldowns
    public final void showWindChargeCooldown(Player player) {
        //noinspection deprecation
        showCooldown(player, "windcharge-cooldown", Material.SNOW_BALL.getId(), 2); // ports:windcharge.png
    }

    public final void showNoFallDuration(Player player) {
        //noinspection deprecation
        showCooldown(player, "nofall-cooldown", Material.CHAINMAIL_BOOTS.getId(), 4); // ports:nofall.png
    }

    public final void showFallDamageReduction(Player player) {
        //noinspection deprecation
        showCooldown(player, "fallreduction-cooldown", Material.STRING.getId(), 4); // ports:windburst.png
    }

    // remove specific cooldowns
    public final void removeNoFallDuration(Player player) {
        removeCooldown(player, "nofall-cooldown");
    }

    public final void removeFallDamageReduction(Player player) {
        removeCooldown(player, "fallreduction-cooldown");
    }
}
