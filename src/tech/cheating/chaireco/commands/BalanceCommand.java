package tech.cheating.chaireco.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.cheating.chaireco.Economy;

import java.sql.SQLException;

public class BalanceCommand implements CommandExecutor {

    private Economy plugin;

    public BalanceCommand(Economy eco) {
        plugin = eco;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            if (commandSender instanceof Player) {
                Player player = (Player)commandSender;
                try {
                    commandSender.sendMessage("Your balance is: $" + plugin.api.getBalance(player)/100);
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                commandSender.sendMessage("Your balance is unavailable");
                return false;
            }
        } else {
            OfflinePlayer player = plugin.getServer().getPlayer(strings[0]);
            if (player != null) {
                try {
                    commandSender.sendMessage("Your balance is: $" + plugin.api.getBalance(player)/100);
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                commandSender.sendMessage("Could not find that player");
                return false;
            }
        }
    }
}
