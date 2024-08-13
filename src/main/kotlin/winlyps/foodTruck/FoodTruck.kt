//1.File: FoodTruck.kt
package winlyps.foodTruck

import org.bukkit.plugin.java.JavaPlugin
import winlyps.foodTruck.commands.FoodTruckCommand
import winlyps.foodTruck.commands.RemoveRegionCommand
import winlyps.foodTruck.commands.RemoveRegionTabCompleter
import winlyps.foodTruck.listeners.RegionListener
import winlyps.foodTruck.storage.RegionStorage

class FoodTruck : JavaPlugin() {

    override fun onEnable() {
        // Initialize storage
        val storage = RegionStorage(this)

        // Register commands
        getCommand("foodtruck")?.setExecutor(FoodTruckCommand(storage))
        getCommand("removeregion")?.let { cmd ->
            cmd.setExecutor(RemoveRegionCommand(storage))
            cmd.tabCompleter = RemoveRegionTabCompleter(storage)
        }

        // Register event listener
        server.pluginManager.registerEvents(RegionListener(this, storage), this)
    }

    override fun onDisable() {
        // Save data if necessary
    }
}
