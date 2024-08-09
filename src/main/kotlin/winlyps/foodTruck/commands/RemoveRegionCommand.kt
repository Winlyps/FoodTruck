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

        val playerId = sender.uniqueId
        if (storage.getCorners(playerId).isEmpty()) {
            sender.sendMessage("You have no food region set.")
            return true
        }

        storage.removeRegion(playerId)
        sender.sendMessage("Your food region has been removed.")
        return true
    }
}