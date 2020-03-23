package tech.cheating.chaireco;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Economy extends JavaPlugin {

    public Database db = new Database(this);

    @Override
    public void onDisable() {
        getLogger().info("Stopping money printers...");
        db.disconnect();
    }

    @Override
    public void onEnable() {
        getLogger().info("Causing hyper-inflation... ");
        try {
            db.connect("chaireco");
        } catch (SQLException e) {
            getLogger().severe("failed to connect to database: " + e.getMessage());
        }
    }
}
