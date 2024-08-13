//2.File: FoodTruckCommand.kt
package winlyps.foodTruck.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import winlyps.foodTruck.storage.RegionStorage

class FoodTruckCommand(private val storage: RegionStorage) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be run by a player.")
            return true
        }

        if (!sender.hasPermission("foodtruck.use")) {
            sender.sendMessage("You do not have permission to use this command.")
            return true
        }

        val goldenAxe = ItemStack(org.bukkit.Material.GOLDEN_AXE)
        val meta = goldenAxe.itemMeta
        if (meta != null) {
            meta.setDisplayName("§6Food Truck Axe")
            goldenAxe.itemMeta = meta
        } else {
            sender.sendMessage("Failed to set item meta for the golden axe.")
            return true
        }

        sender.inventory.addItem(goldenAxe)
        sender.sendMessage("You received a special golden axe to set the food truck area.")

        return true
    }
}