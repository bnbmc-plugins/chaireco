package tech.cheating.chaireco.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import tech.cheating.chaireco.Database;
import tech.cheating.chaireco.Economy;
import tech.cheating.chaireco.EconomyAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionHistoryCommand implements CommandExecutor {
    private Database db;

    public TransactionHistoryCommand(Database db) {
        this.db = db;
    }

    void printTxLine(Player player, int value, int multiplier, String reason) {
        String valueString;
        if (value < 0) {
            valueString = ChatColor.GOLD + EconomyAPI.getDollarValue(value * multiplier) + " ";
        } else {
            valueString = ChatColor.YELLOW + "+" + EconomyAPI.getDollarValue(value * multiplier) + " ";
        }

        String multiplierString = " ";
        if (multiplier > 1) {
            multiplierString = "x" + multiplier + " @ " + EconomyAPI.getDollarValue(value);
        }
        player.sendMessage(valueString + ChatColor.GREEN + reason + " " + ChatColor.RED + multiplierString);
    }

    void printHeader(Player player, int page) {
            player.sendMessage(ChatColor.GOLD + "----- Transaction History -----");
            player.sendMessage(ChatColor.AQUA + "Page " + (page + 1) + ChatColor.RESET + " - Ordered from newest to oldest");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage(ChatColor.RED + "Sorry, you need to be a player to view transaction history.");
            return true;
        }

        int page = 1;
        if (strings.length == 1) {
            try {
                page = Integer.parseInt(strings[0]);
                if (page < 1) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED + "Sorry, that's not a valid page number.");
                return true;
            }
        }

        page--;

        try {
            Player player = (Player) commandSender;
            Connection connection = db.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT id, value, reason FROM history WHERE player=? ORDER BY id DESC");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet results = statement.executeQuery();

            int currentPageNumber = 0;
            int itemsOnPage = 0;
            int previousValue = 0;
            String previousReason = null;
            int previousRepeatCount = 0;
            boolean printedHeader = false;
            while (results.next()) {
                int value = results.getInt("value");
                String reason = results.getString("reason");

                if (value == previousValue && reason.equals(previousReason)) {
                    //Collapse these transactions into one
                    previousRepeatCount++;
                } else {
                    //Print the previous transaction
                    if (currentPageNumber == page && previousReason != null) {
                        if (!printedHeader) {
                            printHeader(player, page);
                            printedHeader = true;
                        }

                        printTxLine(player, previousValue, previousRepeatCount, previousReason);
                    }

                    itemsOnPage++;
                    previousValue = value;
                    previousReason = reason;
                    previousRepeatCount = 1;

                    if (itemsOnPage == 10) {
                        itemsOnPage = 0;
                        currentPageNumber++;

                        if (currentPageNumber > page) break; //Break early if we've reached the target
                    }
                }
            }

            //Print the final transaction
            if (currentPageNumber == page) {
                if (!printedHeader) {
                    printHeader(player, page);
                }

                printTxLine(player, previousValue, previousRepeatCount, previousReason);
            }

            if (currentPageNumber < page) {
                commandSender.sendMessage(ChatColor.RED + "You don't have that many transactions.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
