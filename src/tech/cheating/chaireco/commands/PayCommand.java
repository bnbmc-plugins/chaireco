package tech.cheating.chaireco.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import tech.cheating.chaireco.Economy;
import tech.cheating.chaireco.exceptions.EconomyBalanceTooLowException;

import java.sql.SQLException;

public class PayCommand implements CommandExecutor {
    private Economy plugin;

    public PayCommand(Economy eco) {
        plugin = eco;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage("Sorry, use /eco deposit to print money.");
            return false;
        }

        Player player = (Player)commandSender;
        if (strings.length > 1) {
            String reason = strings.length > 2 ? strings[2] : "No reason provided";
            int amount = Integer.parseInt(strings[1]);
            Player otherPlayer = plugin.getServer().getPlayer(strings[0]);
            if (otherPlayer != null && amount > 0) {
                try {
                    plugin.api.transfer(player, otherPlayer, amount*100, reason);
                    commandSender.sendMessage("You have successfully wired $" + amount + " to " + otherPlayer.getDisplayName());
                    otherPlayer.sendMessage(player.getDisplayName() + " has wired you $" + amount);
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (EconomyBalanceTooLowException e) {
                    player.sendMessage("You do not have enough money.");
                }
            }

            // /pay RedChair 200 Hello
        }

        return false;
    }
}
