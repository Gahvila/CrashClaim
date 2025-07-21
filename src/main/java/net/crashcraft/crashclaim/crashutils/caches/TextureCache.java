package net.crashcraft.crashclaim.crashutils.caches;

import com.destroystokyo.paper.event.profile.FillProfileEvent;
import com.destroystokyo.paper.event.profile.PreFillProfileEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TextureCache implements Listener {

    private final Cache<UUID, ProfileProperty> cache;

    public TextureCache() {
        cache = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.DAYS)
                .maximumSize(10_000)
                .build();
    }

    @EventHandler
    public void onPreFillProfileEvent(PreFillProfileEvent e) {
        if (!e.getPlayerProfile().hasTextures()) {
            ProfileProperty profileProperty = cache.getIfPresent(e.getPlayerProfile().getId());
            if (profileProperty != null) {
                e.getPlayerProfile().setProperty(profileProperty);
            }
        }
    }

    @EventHandler
    public void onFillProfileEvent(FillProfileEvent e) {
        final PlayerProfile profile = e.getPlayerProfile();
        if (profile.getId() != null && profile.hasTextures()) {
            for (ProfileProperty profileProperty : profile.getProperties()) {
                if ("textures".equals(profileProperty.getName())) {
                    cache.put(profile.getId(), profileProperty);
                    break;
                }
            }
        }
    }
}
