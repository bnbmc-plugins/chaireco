package tech.cheating.chaireco.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import tech.cheating.chaireco.Database;
import tech.cheating.chaireco.IEconomy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class BalTopCommand implements CommandExecutor {
    Database db;
    public BalTopCommand(Database db) {
        this.db = db;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        int page = 0;
        if (strings.length > 0) {
            try {
                page = Integer.parseInt(strings[0]) - 1;
                if (page <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED + "Sorry, enter a valid page number.");
                return true;
            }
        }

        try {
            PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM balances ORDER BY balance DESC LIMIT 10 OFFSET ?");
            statement.setInt(1, page * 10);

            boolean first = true;
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                if (first) {
                    commandSender.sendMessage(ChatColor.GOLD + "--- Balances (Page " + (page + 1) + ") ---");
                    first = false;
                }

                String player;
                player = Bukkit.getOfflinePlayer(UUID.fromString(results.getString("player"))).getName();
                commandSender.sendMessage(ChatColor.GOLD + IEconomy.getDollarValue(results.getInt("balance")) + " " + ChatColor.GREEN + player);
            }

            if (first) {
                commandSender.sendMessage(ChatColor.RED + "Sorry, enter a valid page number.");
            }
        } catch (SQLException e) {
            commandSender.sendMessage(ChatColor.RED + "Sorry, a database error occurred. Contact an admin immediately!");
            e.printStackTrace();
        }

        return true;
    }
}
