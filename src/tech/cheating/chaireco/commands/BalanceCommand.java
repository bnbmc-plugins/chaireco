package tech.cheating.chaireco.commands;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.cheating.chaireco.Economy;
import tech.cheating.chaireco.EconomyAPI;

import java.sql.SQLException;

public class BalanceCommand implements CommandExecutor {

    private Economy plugin;

    public BalanceCommand(Economy eco) {
        plugin = eco;
    }

    private void sendPlayerBalance(CommandSender commandSender, OfflinePlayer player) {
        try {
            commandSender.sendMessage(ChatColor.GREEN + "Your balance: " + EconomyAPI.getDollarValue(plugin.api.getBalance(player)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            if (commandSender instanceof Player) {
                sendPlayerBalance(commandSender, (OfflinePlayer) commandSender);
            } else {
                commandSender.sendMessage("Your balance is unavailable");
            }
        } else {
            OfflinePlayer player = plugin.getServer().getPlayer(strings[0]);
            if (player != null) {
                sendPlayerBalance(commandSender, player);
            } else {
                commandSender.sendMessage("Could not find that player");
            }
        }
        return true;
    }
}
