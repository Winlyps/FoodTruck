//2.File: FoodTruckCommand.kt
package winlyps.foodTruck.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import winlyps.foodTruck.storage.RegionStorage

class FoodTruckCommand(private val storage: RegionStorage) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be run by a player.")
            return true
        }

        val goldenAxe = ItemStack(org.bukkit.Material.GOLDEN_AXE)
        sender.inventory.addItem(goldenAxe)
        sender.sendMessage("You received a special golden axe to set the food truck area.")

        return true
    }
}