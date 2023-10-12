package org.hotal.actionbarmoney;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import net.milkbowl.vault.economy.Economy;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBarMoney extends JavaPlugin implements Listener {

    private Economy econ = null;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Vaultの経済プラグインが見つかりません! 無効化しています...", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.getServer().getPluginManager().registerEvents(this, this);

        this.getServer().getScheduler().runTaskTimer(this, this::updateAllPlayerMoneyDisplay, 0L, 20L * 3600L);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        displayMoney(player);
    }

    private void displayMoney(Player player) {
        double balance = econ.getBalance(player);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§e現在の所持金" + balance + "" + econ.currencyNamePlural()));
    }

    public void updateAllPlayerMoneyDisplay() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            displayMoney(player);
        }
    }
}