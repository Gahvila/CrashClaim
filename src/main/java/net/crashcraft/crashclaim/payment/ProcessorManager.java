package net.crashcraft.crashclaim.payment;

import net.crashcraft.crashclaim.payment.providers.ClaimBlockProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ProcessorManager {
    private PaymentProcessor processor;
    private ClaimBlockProvider provider;

    public ProcessorManager(JavaPlugin plugin, String providerOverride) throws ProviderInitializationException {
        Logger logger = plugin.getLogger();

        if (providerOverride == null) {
            providerOverride = "";
        }
        provider = new ClaimBlockProvider();
        processor = new PaymentProcessor(provider, plugin);
        Bukkit.getPluginManager().registerEvents(provider, plugin);
    }

    public PaymentProcessor getProcessor() {
        return processor;
    }
}
