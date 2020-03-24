package tech.cheating.chaireco.commands;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import tech.cheating.chaireco.Economy;
import tech.cheating.chaireco.EconomyAPI;
import tech.cheating.chaireco.exceptions.EconomyBalanceTooLowException;

import java.sql.SQLException;
import java.util.Arrays;

public class PayCommand implements CommandExecutor {
    private Economy plugin;

    public PayCommand(Economy eco) {
        plugin = eco;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage("Sorry, use /eco deposit to print money.");
            return true;
        }

        Player player = (Player) commandSender;
        if (strings.length > 1) {
            String reason = strings.length > 2 ? String.join(" ", Arrays.asList(strings).subList(2, strings.length)) : "No reason provided";
            Player otherPlayer = plugin.getServer().getPlayer(strings[0]);

            int amount;
            try {
                amount = Integer.parseInt(strings[1]) * 100;
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED + "\"" + strings[1] + "\" is not a valid dollar value.");
                return true;
            }

            try {
                if (otherPlayer != null && amount > 0) {
                    plugin.api.transfer(player, otherPlayer, amount, reason);

                    commandSender.sendMessage(ChatColor.GREEN + "----- TRANSFER OUT -----");
                    commandSender.sendMessage(ChatColor.GREEN + EconomyAPI.getDollarValue(amount) + ChatColor.RESET + " » " + ChatColor.GOLD + otherPlayer.getDisplayName());
                    if (strings.length > 2) commandSender.sendMessage(ChatColor.AQUA + "\"" + reason + "\"");
                    commandSender.sendMessage(ChatColor.GREEN + "Your balance: " + EconomyAPI.getDollarValue(plugin.api.getBalance(player)));

                    otherPlayer.sendMessage(ChatColor.GREEN + "----- TRANSFER IN -----");
                    otherPlayer.sendMessage(ChatColor.GREEN + player.getDisplayName() + ChatColor.RESET + " » " + ChatColor.GOLD + EconomyAPI.getDollarValue(amount));
                    if (strings.length > 2) otherPlayer.sendMessage(ChatColor.AQUA + "\"" + reason + "\"");
                    otherPlayer.sendMessage(ChatColor.GREEN + "Your balance: " + EconomyAPI.getDollarValue(plugin.api.getBalance(otherPlayer)));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (EconomyBalanceTooLowException e) {
                try {
                    otherPlayer.sendMessage(ChatColor.GREEN + "----- TRANSFER OUT -----");
                    otherPlayer.sendMessage(ChatColor.RED + EconomyAPI.getDollarValue(amount) + ChatColor.RESET + " » " + ChatColor.GOLD + otherPlayer.getDisplayName() + ChatColor.RED + " (FAILED)");
                    otherPlayer.sendMessage(ChatColor.RED + "Insufficient funds in your account.");
                    otherPlayer.sendMessage(ChatColor.GREEN + "Your balance: " + EconomyAPI.getDollarValue(plugin.api.getBalance(otherPlayer)));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return true;
    }
}
