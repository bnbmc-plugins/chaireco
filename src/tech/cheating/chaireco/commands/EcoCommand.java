package tech.cheating.chaireco.commands;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.cheating.chaireco.Economy;
import tech.cheating.chaireco.EconomyAPI;
import tech.cheating.chaireco.exceptions.EconomyBalanceTooLowException;

import java.sql.SQLException;
import java.util.Arrays;

public class EcoCommand implements CommandExecutor {
    private Economy plugin;

    public EcoCommand(Economy eco) {
        plugin = eco;
    }

    private void performDeposit(CommandSender commandSender, Player player, int amount, String reason, boolean printReason) throws SQLException {
        plugin.api.deposit(player, amount, reason);

        if (commandSender != player) {
            commandSender.sendMessage(ChatColor.GREEN + "----- TRANSFER OUT -----");
            commandSender.sendMessage(ChatColor.GREEN + EconomyAPI.getDollarValue(amount) + ChatColor.RESET + " » " + ChatColor.GOLD + player.getDisplayName());
            if (printReason) commandSender.sendMessage(ChatColor.AQUA + "\"" + reason + "\"");
            commandSender.sendMessage(ChatColor.GREEN + "Balance for Reserve Bank of Minecraft: ∞");
        }

        player.sendMessage(ChatColor.GREEN + "----- TRANSFER IN -----");
        player.sendMessage(ChatColor.GOLD + "Reserve Bank of Minecraft" + ChatColor.RESET + " » " + ChatColor.GREEN + EconomyAPI.getDollarValue(amount));
        if (printReason) player.sendMessage(ChatColor.AQUA + "\"" + reason + "\"");
        player.sendMessage(ChatColor.GREEN + "Your balance: " + EconomyAPI.getDollarValue(plugin.api.getBalance(player)));
    }

    private void performWithdrawal(CommandSender commandSender, Player player, int amount, String reason, boolean printReason) throws SQLException {
        try {
            plugin.api.withdraw(player, amount, reason);

            if (commandSender != player) {
                commandSender.sendMessage(ChatColor.GREEN + "----- TRANSFER IN -----");
                commandSender.sendMessage(ChatColor.GOLD + player.getDisplayName() + ChatColor.RESET + " » " + ChatColor.GREEN + EconomyAPI.getDollarValue(amount));
                if (printReason) commandSender.sendMessage(ChatColor.AQUA + "\"" + reason + "\"");
                commandSender.sendMessage(ChatColor.GREEN + "Balance for Reserve Bank of Minecraft: ∞");
            }

            player.sendMessage(ChatColor.GREEN + "----- TRANSFER OUT -----");
            player.sendMessage(ChatColor.GREEN + EconomyAPI.getDollarValue(amount) + ChatColor.RESET + " » " + ChatColor.GOLD + "Reserve Bank of Minecraft");
            if (printReason) player.sendMessage(ChatColor.AQUA + "\"" + reason + "\"");
            player.sendMessage(ChatColor.GREEN + "Your balance: " + EconomyAPI.getDollarValue(plugin.api.getBalance(player)));
        } catch (EconomyBalanceTooLowException e) {
            commandSender.sendMessage(ChatColor.GREEN + "----- TRANSFER OUT -----");
            commandSender.sendMessage(ChatColor.RED + EconomyAPI.getDollarValue(amount) + ChatColor.RESET + " » " + ChatColor.GOLD + "Reserve Bank of Minecraft" + ChatColor.RED + " (FAILED)");
            commandSender.sendMessage(ChatColor.RED + "Insufficient funds in the target's account.");
            commandSender.sendMessage(ChatColor.GREEN + "Balance for Reserve Bank of Minecraft: ∞");
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 3) return false;
        if (!commandSender.hasPermission("chaireco.eco")) {
            commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "NO PERMISSION! " + ChatColor.RED + "You do not have permission to print money!");
            return true;
        }

        Player target = plugin.getServer().getPlayer(strings[1]);
        if (target == null) {
            commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR! " + ChatColor.RED + "Could not find that player");
            return true;
        }

        int amount;
        try {
            amount = EconomyAPI.getCentValue(strings[2]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR! " + ChatColor.RED + "\"" + strings[2] + "\" is not a valid dollar value.");
            return true;
        }

        String reason = strings.length > 3 ? String.join(" ", Arrays.asList(strings).subList(3, strings.length)) : "No reason provided";

        try {
            switch (strings[0].toLowerCase()) {
                case "deposit":
                case "give":
                    this.performDeposit(commandSender, target, amount, reason, strings.length > 3);
                    break;
                case "withdraw":
                case "take":
                    this.performWithdrawal(commandSender, target, amount, reason, strings.length > 3);
                    break;
                case "set":
                    int bal = plugin.api.getBalance(target);
                    int difference = bal - amount;
                    if (difference == 0) {
                        commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR! " + ChatColor.RED + "That dollar value matches the player's balance.");
                    } else if (difference < 0) {
                        this.performDeposit(commandSender, target, -difference, "Fix balance to " + EconomyAPI.getDollarValue(amount) + ": " + reason, strings.length > 3);
                    } else {
                        this.performWithdrawal(commandSender, target, difference, "Fix balance to " + EconomyAPI.getDollarValue(amount) + ": " + reason, strings.length > 3);
                    }
                    break;
                default:
                    commandSender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR! " + ChatColor.RED + "\"" + strings[0] + "\" is not a valid verb");
                    return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }
}
