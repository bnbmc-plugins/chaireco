package tech.cheating.chaireco;

import org.bukkit.OfflinePlayer;
import tech.cheating.chaireco.exceptions.EconomyBalanceTooLowException;
import tech.cheating.chaireco.exceptions.EconomyInvalidTxAmount;

import java.sql.SQLException;

public interface IEconomy {
    static String getDollarValue(int cents) {
        return (cents < 0 ? "-" : "") + "$" + Math.abs(cents / 100) + "." + String.format("%02d", Math.abs(cents % 100));
    }

    static int getCentValue(String dollarValue) throws NumberFormatException {
        return (int) (Float.parseFloat(dollarValue) * 100);
    }

    void transfer(OfflinePlayer from, OfflinePlayer to, int amount, String reason) throws EconomyInvalidTxAmount, SQLException, EconomyBalanceTooLowException;
    void transfer(String from, String fromName, String to, String toName, int amount, String reason) throws SQLException, EconomyBalanceTooLowException;

    int getBalance(OfflinePlayer player) throws SQLException;
    int getBalance(String player) throws SQLException;

    void withdraw(OfflinePlayer player, int amount, String reason) throws EconomyBalanceTooLowException, SQLException, EconomyInvalidTxAmount;
    void withdraw(String player, int amount, String reason) throws EconomyBalanceTooLowException, SQLException, EconomyInvalidTxAmount;

    void deposit(OfflinePlayer player, int amount, String reason) throws EconomyInvalidTxAmount, SQLException;
    void deposit(String player, int amount, String reason) throws EconomyInvalidTxAmount, SQLException;
}
