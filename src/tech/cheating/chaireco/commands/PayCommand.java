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
import tech.cheating.chaireco.exceptions.EconomyInvalidTxAmount;

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
            commandSender.sendMessage(ChatColor.RED + "Sorry, use /eco deposit to print money.");
            return true;
        }

        Player player = (Player) commandSender;
        if (strings.length > 1) {
            String reason = strings.length > 2 ? String.join(" ", Arrays.asList(strings).subList(2, strings.length)) : "No reason provided";
            Player otherPlayer = plugin.getServer().getPlayer(strings[0]);
            if (otherPlayer == null) {
                commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR! " + ChatColor.RED + "Could not find that player");
                return true;
            }

            int amount;
            try {
                amount = EconomyAPI.getCentValue(strings[1]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR! " + ChatColor.RED + "\"" + strings[1] + "\" is not a valid dollar value.");
                return true;
            }

            try {
                plugin.api.transfer(player, otherPlayer, amount, reason);

                commandSender.sendMessage(ChatColor.GREEN + "----- TRANSFER OUT -----");
                commandSender.sendMessage(ChatColor.GREEN + EconomyAPI.getDollarValue(amount) + ChatColor.RESET + " » " + ChatColor.GOLD + otherPlayer.getDisplayName());
                if (strings.length > 2) commandSender.sendMessage(ChatColor.AQUA + "\"" + reason + "\"");
                commandSender.sendMessage(ChatColor.GREEN + "Your balance: " + EconomyAPI.getDollarValue(plugin.api.getBalance(player)));

                otherPlayer.sendMessage(ChatColor.GREEN + "----- TRANSFER IN -----");
                otherPlayer.sendMessage(ChatColor.GOLD + player.getDisplayName() + ChatColor.RESET + " » " + ChatColor.GREEN + EconomyAPI.getDollarValue(amount));
                if (strings.length > 2) otherPlayer.sendMessage(ChatColor.AQUA + "\"" + reason + "\"");
                otherPlayer.sendMessage(ChatColor.GREEN + "Your balance: " + EconomyAPI.getDollarValue(plugin.api.getBalance(otherPlayer)));
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (EconomyBalanceTooLowException e) {
                try {
                    commandSender.sendMessage(ChatColor.GREEN + "----- TRANSFER OUT -----");
                    commandSender.sendMessage(ChatColor.RED + EconomyAPI.getDollarValue(amount) + ChatColor.RESET + " » " + ChatColor.GOLD + otherPlayer.getDisplayName() + ChatColor.RED + " (FAILED)");
                    commandSender.sendMessage(ChatColor.RED + "Insufficient funds in your account.");
                    commandSender.sendMessage(ChatColor.GREEN + "Your balance: " + EconomyAPI.getDollarValue(plugin.api.getBalance(player)));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } catch (EconomyInvalidTxAmount e) {
                try {
                    commandSender.sendMessage(ChatColor.GREEN + "----- TRANSFER OUT -----");
                    commandSender.sendMessage(ChatColor.RED + EconomyAPI.getDollarValue(amount) + ChatColor.RESET + " » " + ChatColor.GOLD + otherPlayer.getDisplayName() + ChatColor.RED + " (FAILED)");
                    commandSender.sendMessage(ChatColor.RED + "Invalid amount to transfer.");
                    commandSender.sendMessage(ChatColor.GREEN + "Your balance: " + EconomyAPI.getDollarValue(plugin.api.getBalance(player)));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return true;
    }
}
