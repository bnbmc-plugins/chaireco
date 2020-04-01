package tech.cheating.chaireco;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import tech.cheating.chaireco.commands.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Economy extends JavaPlugin {

    public Database db = new Database(this);
    public EconomyAPI api = null;


    public void initPlayer(OfflinePlayer player) {
        int starterMoney = getConfig().getInt("starter-money");
        try {
            PreparedStatement s = db.getConnection().prepareStatement("INSERT INTO balances (player, balance) VALUES (?,0)");
            s.setString(1, player.getUniqueId().toString());
            int resultSet = s.executeUpdate();
            if (starterMoney > 0) {
                if (api != null) {
                    api.deposit(player, starterMoney*100, "Initial Deposit");
                }
            }
        } catch (SQLException e) {
            //Fail silently
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Stopping money printers...");
        db.disconnect();
    }

    @Override
    public void onEnable() {
        getConfig().addDefault("starter-money", 2000);
        getConfig().options().copyDefaults(true);
        getLogger().info("Causing hyper-inflation... ");
        getServer().getPluginManager().registerEvents(new EventHandler(this, db), this);
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("eco").setExecutor(new EcoCommand(this));
        getCommand("transactionhistory").setExecutor(new TransactionHistoryCommand(db));
        getCommand("baltop").setExecutor(new BalTopCommand(db));

        try {
            db.connect("chaireco");
            db.setup();
            api = new EconomyAPI(this, db);
            getServer().getServicesManager().register(IEconomy.class, api, this, ServicePriority.Normal);
        } catch (SQLException e) {
            getLogger().severe("failed to connect to database: " + e.getMessage());
        }
    }
}
