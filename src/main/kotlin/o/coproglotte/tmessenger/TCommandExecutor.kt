package o.coproglotte.tmessenger

import o.coproglotte.tmessenger.TCommandExecutor.PMType.MSG
import o.coproglotte.tmessenger.TCommandExecutor.PMType.REPLY
import o.coproglotte.tmessenger.utils.*
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TCommandExecutor(private val tMessenger: TMessenger) : CommandExecutor {

    private enum class PMType {
        MSG,
        REPLY
    }

    override fun onCommand(commandSender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        val sender = commandSender as? Player ?: return false

        when (command.name.toLowerCase()) {

            "msg" -> {
                if (sender.hasPermission(NODE_CHAT)) {
                    if (args != null && args.size >= 2) {
                        val receiver = tMessenger.server.getPlayer(args[0])
                        if (receiver != null) {
                            if (sender.canSendTo(receiver, MSG, args[0])) {
                                if (!sender.isVanished()) {
                                    tMessenger.replyMap.put(receiver, sender)
                                }
                                sendPrivateMessage(sender, receiver, args.joinToString(" "))
                                return true
                            }
                        } else {
                            sendFormattedMessage(sender, "help.offlinePlayer", Pair(DISPLAYNAME, args[0]))
                        }
                    } else {
                        sendFormattedMessage(sender, "help.syntax", Pair(LABEL, label))
                    }
                } else {
                    sendFormattedMessage(sender, "help.permissionMissing", Pair(LABEL, label))
                }
            }

            "reply" -> {
                if (sender.hasPermission(NODE_CHAT)) {
                    if (args != null && args.isNotEmpty()) {
                        val receiver = tMessenger.replyMap[sender]
                        if (receiver != null) {
                            if (sender.canSendTo(receiver, REPLY, label)) {
                                if (!sender.isVanished()) {
                                    tMessenger.replyMap.put(receiver, sender)
                                }
                                sendPrivateMessage(sender, receiver, args.joinToString(" "))
                                return true
                            }
                        } else {
                            sendFormattedMessage(sender, "help.replyToNobody", Pair(LABEL, label))
                        }
                    } else {
                        sendFormattedMessage(sender, "help.replySyntax", Pair(LABEL, label))
                    }
                } else {
                    sendFormattedMessage(sender, "help.permissionMissing", Pair(LABEL, label))
                }
            }

            "msgblock" -> {
                if (sender.hasPermission(NODE_BLOCKMSG)) {
                    val blockList = tMessenger.blockList
                    if (blockList.contains(sender)) {
                        blockList.remove(sender)
                        sendFormattedMessage(sender, "msgblock.deactivated")
                        return true
                    } else {
                        blockList.add(sender)
                        sendFormattedMessage(sender, "msgblock.activated", Pair(LABEL, label))
                        return true
                    }
                } else {
                    sendFormattedMessage(sender, "help.permissionMissing", Pair(LABEL, label))
                }
            }

            "tmessengerreload" -> {
                if (sender.hasPermission(NODE_RELOAD)) {
                    tMessenger.reloadConfig()
                    return true
                } else {
                    sendFormattedMessage(sender, "help.permissionMissing", Pair(LABEL, label))
                }
            }
        }

        return false
    }


    private fun Player.canSendTo(other: Player, pmType: PMType, replaceString: String) : Boolean {
        if (this.hasPermission(NODE_BLOCKMSG_EXEMPT) || this.isOp) {
            return true
        }

        if (this.isBlockingPM()) {
            sendFormattedMessage(this, "help.messageBlocked", Pair(DISPLAYNAME, other.displayName))
            return false
        }

        if (other.isBlockingPM()) {
            sendFormattedMessage(this, "help.msgblockEnabled")
            return false
        }

        if (other == this) {
            sendFormattedMessage(this, "help.selfMsg")
            return false
        }

        if (other.isVanished()) {
            val replyMap = tMessenger.replyMap.clone() as HashMap<Player, Player>
            replyMap.forEach {
                if (it.value == other.player) {
                    tMessenger.replyMap.remove(it.key)
                }
            }
            when (pmType) {
                MSG -> sendFormattedMessage(this, "help.offlinePlayer", Pair(DISPLAYNAME, replaceString))
                REPLY -> sendFormattedMessage(this, "help.replyToNobody", Pair(LABEL, replaceString))
            }
            return false
        }

        return true
    }

    private fun Player.isVanished() : Boolean {
        return tMessenger.vanishManager.isVanished(player)
    }

    private fun Player.isBlockingPM() : Boolean {
        return tMessenger.blockList.contains(this)
    }

    private fun sendPrivateMessage(sender: Player, receiver: Player, message: String) {
        sendFormattedMessage(sender, "format.outgoing",
                Pair(DISPLAYNAME, receiver.displayName), Pair(MESSAGE, message))
        sendFormattedMessage(receiver, "format.incoming",
                Pair(DISPLAYNAME, sender.displayName), Pair(MESSAGE, message))
    }

    private fun sendFormattedMessage(player: Player, format: String, vararg replacePatterns: Pair<String, String>) {
        player.sendMessage(formatMessage(format, *replacePatterns))
    }

    private fun formatMessage(format: String, vararg replacePatterns: Pair<String, String>) : String {
        var formatted = ChatColor.translateAlternateColorCodes('&', tMessenger.config.getString(format))

        replacePatterns.forEach {
            formatted = formatted.replace(it.first, it.second)
        }

        return formatted
    }
}