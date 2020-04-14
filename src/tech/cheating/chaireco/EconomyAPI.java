package tech.cheating.chaireco;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import tech.cheating.chaireco.exceptions.EconomyBalanceTooLowException;
import tech.cheating.chaireco.exceptions.EconomyInvalidTxAmount;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EconomyAPI implements IEconomy {

    private Database db;

    public EconomyAPI(Economy eco, Database dbs) {
        db = dbs;
    }

    public static String getDollarValue(int cents) {
        return IEconomy.getDollarValue(cents);
    }

    public static int getCentValue(String dollarValue) throws NumberFormatException {
        return IEconomy.getCentValue(dollarValue);
    }

    public int getBalance(OfflinePlayer player) throws SQLException {
        return getBalance(player.getUniqueId().toString());
    }

    public int getBalance(String player) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("SELECT balance FROM balances WHERE player=?");
        statement.setString(1, player);
        ResultSet results = statement.executeQuery();

        if (!results.next()) return 0;
        return results.getInt(1);
    }

    private void addTransactionRecord(OfflinePlayer player, int amount, String reason) throws SQLException {
        addTransactionRecord(player.getUniqueId().toString(), amount, reason);
    }

    private void addTransactionRecord(String player, int amount, String reason) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("INSERT INTO history(player, value, reason) VALUES(?, ?, ?)");
        statement.setString(1, player);
        statement.setInt(2, amount);
        statement.setString(3, reason);
        statement.executeUpdate();
    }


    public void withdraw(OfflinePlayer player, int amount, String reason) throws EconomyBalanceTooLowException, SQLException, EconomyInvalidTxAmount {
        withdraw(player.getUniqueId().toString(), amount, reason);
    }

    public void withdraw(String player, int amount, String reason) throws EconomyBalanceTooLowException, SQLException, EconomyInvalidTxAmount {
        if (amount <= 0) throw new EconomyInvalidTxAmount(amount + " needs to be greater than zero");

        int balance = getBalance(player);
        if (balance < amount) throw new EconomyBalanceTooLowException("Balance for " + player + " is too low");

        int newBalance = balance - amount;
        PreparedStatement statement = db.getConnection().prepareStatement("UPDATE balances SET balance=? WHERE player=?");
        statement.setInt(1, newBalance);
        statement.setString(2, player);
        statement.executeUpdate();

        addTransactionRecord(player, -amount, reason);
    }

    public void deposit(OfflinePlayer player, int amount, String reason) throws EconomyInvalidTxAmount, SQLException {
        deposit(player.getUniqueId().toString(), amount, reason);
    }

    public void deposit(String player, int amount, String reason) throws EconomyInvalidTxAmount, SQLException {
        if (amount <= 0) throw new EconomyInvalidTxAmount(amount + " needs to be greater than zero");

        int balance = getBalance(player);
        int newBalance = balance + amount;
        PreparedStatement statement = db.getConnection().prepareStatement("UPDATE balances SET balance=? WHERE player=?");
        statement.setInt(1, newBalance);
        statement.setString(2, player);
        statement.executeUpdate();

        addTransactionRecord(player, amount, reason);
    }

    public void transfer(OfflinePlayer from, OfflinePlayer to, int amount, String reason) throws EconomyInvalidTxAmount, SQLException, EconomyBalanceTooLowException {
        transfer(from.getUniqueId().toString(), from.getName(), to.getUniqueId().toString(), to.getName(), amount, reason);
    }

    public void transfer(String from, String fromName, String to, String toName, int amount, String reason) throws SQLException, EconomyBalanceTooLowException {
        try {
            db.getConnection().createStatement().execute("SAVEPOINT transfer");
            this.withdraw(from, amount, "Transfer to " + toName + ": " + reason);
            this.deposit(to, amount, "Transfer from " + fromName + ": " + reason);
            db.getConnection().createStatement().execute("RELEASE SAVEPOINT transfer");
        } catch (Exception e) {
            db.getConnection().createStatement().execute("ROLLBACK TO SAVEPOINT transfer");
            db.getConnection().createStatement().execute("RELEASE SAVEPOINT transfer");
            throw e;
        }
    }
}
