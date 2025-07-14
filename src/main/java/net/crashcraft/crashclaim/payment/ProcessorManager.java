package net.crashcraft.crashclaim.payment;

import net.crashcraft.crashclaim.payment.providers.ClaimBlockProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ProcessorManager {
    private PaymentProcessor processor;

    public ProcessorManager(JavaPlugin plugin, String providerOverride) throws ProviderInitializationException{
        Logger logger = plugin.getLogger();

        if (providerOverride == null){
            providerOverride = "";
        }

        processor = new PaymentProcessor(new ClaimBlockProvider(), plugin);
    }

    public PaymentProcessor getProcessor(){
        return processor;
    }
}
