package o.coproglotte.tmessenger

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener(private val replyMap: HashMap<Player, Player>) : Listener {

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        replyMap.remove(event.player)
        (replyMap.clone() as HashMap<Player, Player>).forEach {
            if (it.value == event.player) {
                replyMap.remove(it.key)
            }
        }
    }
}