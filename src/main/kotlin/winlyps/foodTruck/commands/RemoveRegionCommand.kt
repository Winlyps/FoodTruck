//3.File: RemoveRegionCommand.kt
package winlyps.foodTruck.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import winlyps.foodTruck.storage.RegionStorage

class RemoveRegionCommand(private val storage: RegionStorage) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be run by a player.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("Usage: /removeregion <regionName>")
            return true
        }

        val regionName = args[0]
        if (!storage.getRegionNames().contains(regionName)) {
            sender.sendMessage("Region '$regionName' does not exist.")
            return true
        }

        storage.removeRegion(regionName)
        sender.sendMessage("Region '$regionName' has been removed.")
        return true
    }
}