package o.coproglotte.tmessenger

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.kitteh.vanish.VanishManager
import org.kitteh.vanish.VanishPlugin

class TMessenger : JavaPlugin() {

    lateinit private var commandExecutor: TCommandExecutor
    lateinit var vanishManager: VanishManager
        private set

    val replyMap = HashMap<Player, Player>()
    val blockList = ArrayList<Player>()

    private val COMMANDS = arrayOf("msg", "reply", "msgblock", "tmessengerreload")

    override fun onEnable() {
        commandExecutor = TCommandExecutor(this)
        COMMANDS.forEach { command ->
            getCommand(command).executor = commandExecutor
        }

        vanishManager = (server.pluginManager.getPlugin("VanishNoPacket") as VanishPlugin).manager

        server.pluginManager.registerEvents(PlayerQuitListener(replyMap), this)

        saveDefaultConfig()
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this)
    }
}