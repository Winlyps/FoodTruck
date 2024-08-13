//6.File: RemoveRegionTabCompleter.kt
package winlyps.foodTruck.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import winlyps.foodTruck.storage.RegionStorage

class RemoveRegionTabCompleter(private val storage: RegionStorage) : TabCompleter {

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        if (args.size == 1) {
            return storage.getRegionNames().filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return emptyList()
    }
}