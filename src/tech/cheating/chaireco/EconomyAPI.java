package tech.cheating.chaireco;

import org.bukkit.OfflinePlayer;

public class EconomyAPI {

    private Economy plugin;
    private Database db;

    public EconomyAPI(Economy eco, Database dbs) {
        plugin = eco;
        db = dbs;
    }

    public int getBalance(OfflinePlayer player) {
        
    }

    public boolean withdraw(OfflinePlayer player, int amount, String reason) {

    }

    public boolean deposit(OfflinePlayer player, int amount, String reason) {

    }

    public boolean transfer(OfflinePlayer from, OfflinePlayer to, int amount, String reason) {

    }
}
