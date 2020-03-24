package tech.cheating.chaireco;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventHandler implements Listener {

    private Database db;
    private Economy plugin;

    public EventHandler(Economy eco, Database database) {
        plugin = eco;
        db = database;
    }

    @org.bukkit.event.EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.initPlayer(e.getPlayer());
    }
}
