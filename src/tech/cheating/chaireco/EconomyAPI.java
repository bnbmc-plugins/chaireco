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
        if (amount < 0) return false;
    }

    public boolean deposit(OfflinePlayer player, int amount, String reason) {
        if (amount < 0) return false;
    }

    public boolean transfer(OfflinePlayer from, OfflinePlayer to, int amount, String reason) {
        if (this.withdraw(from, amount, reason)) {
            this.deposit(to, amount, reason);
        }
    }
}
