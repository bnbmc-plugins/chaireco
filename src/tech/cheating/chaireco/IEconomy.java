package tech.cheating.chaireco;

import org.bukkit.OfflinePlayer;
import tech.cheating.chaireco.exceptions.EconomyBalanceTooLowException;
import tech.cheating.chaireco.exceptions.EconomyInvalidTxAmount;

import java.sql.SQLException;

public interface IEconomy {
    public void transfer(OfflinePlayer from, OfflinePlayer to, int amount, String reason) throws EconomyInvalidTxAmount, SQLException, EconomyBalanceTooLowException;
    public int getBalance(OfflinePlayer player) throws SQLException;
    public void withdraw(OfflinePlayer player, int amount, String reason) throws EconomyBalanceTooLowException, SQLException, EconomyInvalidTxAmount;
    public void deposit(OfflinePlayer player, int amount, String reason) throws EconomyInvalidTxAmount, SQLException;
}
