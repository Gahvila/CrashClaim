package net.crashcraft.crashclaim.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import net.crashcraft.crashclaim.CrashClaim;
import net.crashcraft.crashclaim.localization.Localization;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;

@CommandAlias("crashclaim")
public class AdminCommand extends BaseCommand {
    private final CrashClaim crashClaim;

    public AdminCommand(CrashClaim crashClaim){
        this.crashClaim = crashClaim;
    }

    @Subcommand("reload")
    @CommandPermission("crashclaim.admin.reload")
    public void reload(CommandSender sender){
        sender.sendMessage(Localization.RELOAD__RELOADING.getMessage(null));

        crashClaim.loadConfigs(); // Reload configs and their values.
        Localization.rebuildCachedMessages(); // Reload localization config, will load with new language value if changed in config.

        sender.sendMessage(Localization.RELOAD__RELOADED.getMessage(null));
    }

    @Subcommand("version")
    @CommandPermission("crashclaim.admin.version")
    public void version(CommandSender sender){
        // This message should not be configurable by end user as it is used for debug

        sender.sendMessage(ChatColor.GREEN + crashClaim.getDescription().getName() + ": " + ChatColor.YELLOW + crashClaim.getDescription().getVersion()
                + ChatColor.GREEN + "\nMinecraft Version: " + ChatColor.YELLOW + Bukkit.getMinecraftVersion()
                + ChatColor.GREEN + "\nServer Version: " + ChatColor.YELLOW + Bukkit.getVersion()
                + ChatColor.GOLD + "\nHard Dependencies:");

        for (String name : crashClaim.getDescription().getDepend()){
            Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
            String version = plugin == null ? ChatColor.RED + "Not Available" : plugin.getDescription().getVersion();
            sender.sendMessage(ChatColor.GOLD + " - " + ChatColor.GREEN + name + ": " + ChatColor.YELLOW + version);
        }

        sender.sendMessage(ChatColor.GOLD + "Soft Dependencies:");

        for (String name : crashClaim.getDescription().getSoftDepend()){
            Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
            String version = plugin == null ? "Not Available" : plugin.getDescription().getVersion();
            sender.sendMessage(ChatColor.GOLD + " - " + ChatColor.GREEN + name + ": " + ChatColor.YELLOW + version);
        }
    }
}
