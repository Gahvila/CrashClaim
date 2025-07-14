package net.crashcraft.crashclaim.commands;

import co.aikar.commands.PaperCommandManager;
import net.crashcraft.crashclaim.CrashClaim;
import net.crashcraft.crashclaim.commands.claiming.ClaimCommand;
import net.crashcraft.crashclaim.commands.claiming.UnClaimCommand;
import net.crashcraft.crashclaim.data.ClaimDataManager;
import net.crashcraft.crashclaim.payment.PaymentProcessor;
import net.crashcraft.crashclaim.payment.PaymentProvider;
import net.crashcraft.crashclaim.permissions.BypassManager;
import net.crashcraft.crashclaim.permissions.PermissionHelper;
import net.crashcraft.crashclaim.visualize.VisualizationManager;

import java.util.ArrayList;

public class CommandManager {
    private final PaperCommandManager commandManager;
    private final CrashClaim plugin;
    private final PaymentProcessor paymentProcessor;

    public CommandManager(CrashClaim plugin, PaymentProcessor paymentProcessor) {
        this.plugin = plugin;
        this.paymentProcessor = paymentProcessor;
        this.commandManager = new PaperCommandManager(plugin);

        loadCommands();
    }

    private void loadCommands(){
        ClaimDataManager manager = plugin.getDataManager();
        BypassManager bypassManager = PermissionHelper.getPermissionHelper().getBypassManager();
        VisualizationManager visualizationManager = plugin.getVisualizationManager();

        commandManager.registerCommand(new ShowClaimsCommand(visualizationManager, manager));
        commandManager.registerCommand(new HideClaimsCommand(visualizationManager));

        commandManager.registerCommand(new ClaimCommand(manager, visualizationManager));
        commandManager.registerCommand(new UnClaimCommand(manager, visualizationManager));


        commandManager.registerCommand(new MenuCommand(manager, visualizationManager));
        commandManager.registerCommand(new BypassCommand(bypassManager));
        commandManager.registerCommand(new ClaimInfoCommand(manager));
        commandManager.registerCommand(new EjectCommand(manager));
        commandManager.registerCommand(new AdminCommand(plugin));
        commandManager.registerCommand(new EconomyCommand(paymentProcessor));
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }
}
