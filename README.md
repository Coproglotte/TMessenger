# TMessenger
A [Spigot](https://www.spigotmc.org/) plugin to improve private messaging.

Code inspired from the [ChatMessenger](https://github.com/Zeryther/ChatMessenger/) plugin.

## Features
- Send private messages to other players with `/msg`, `/tell` or `/pm`
- Reply to private messages with `/reply`
- Block incoming and outgoing private messages with `/blockmsg` or `/blockpm`
- Customize plugin strings and private messages syntax
- Normal players can't PM vanished players

## Permissions
- **`tmessenger.pm`** Allows to send and receive private messages
- **`tmessenger.blockMsg`** Allows to block private messages
- **`tmessenger.blockMsg.exempt`** Allows to send private messages to players who enabled the blocking option
- **`tmessenger.reload`** Allows to run `/tmreload` to reload the plugin config file

## Dependencies
This plugin depends on [VanishNoPacket](http://dev.bukkit.org/bukkit-plugins/vanish/)

## Building
Create a `libs/` directory in the root folder and copy VanishNoPacket jar inside.

Run `gradle shadowJar` to compile the jar file.

A `buildAndStartServer` (`bASS`) Gradle task allows you to build and copy the plugin to a Spigot server's directory and start the server.