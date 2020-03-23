package tech.cheating.chaireco;

import org.bukkit.event.player.PlayerJoinEvent;

public class EventHandler {

    private Database db;

    public EventHandler(Database database) {
        db = database;
    }

    @org.bukkit.event.EventHandler
    public void onJoin(PlayerJoinEvent e) {
        
    }
}
