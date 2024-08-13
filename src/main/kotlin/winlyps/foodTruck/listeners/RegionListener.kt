//4.File: RegionListener.kt
package winlyps.foodTruck.listeners

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.scheduler.BukkitRunnable
import winlyps.foodTruck.FoodTruck
import winlyps.foodTruck.storage.RegionStorage
import java.util.UUID

class RegionListener(private val plugin: FoodTruck, private val storage: RegionStorage) : Listener {

    private val foodTaskMap = mutableMapOf<UUID, Int>()

    init {
        startFoodReplenishmentTask()
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK || event.hand != EquipmentSlot.HAND) return
        if (event.item?.type != org.bukkit.Material.GOLDEN_AXE) return

        val player = event.player
        val location = event.clickedBlock?.location ?: return

        storage.addCorner(player.uniqueId, location)
        player.sendMessage("Corner set at ${location.blockX}, ${location.blockY}, ${location.blockZ}")

        if (storage.getCorners(player.uniqueId).size == 4) {
            storage.saveRegion(player.uniqueId)
            player.sendMessage("Food region set and saved.")
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        startFoodTask(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        stopFoodTask(event.player)
    }

    private fun startFoodReplenishmentTask() {
        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (storage.isInRegion(player.location)) {
                        player.foodLevel = (player.foodLevel + 2).coerceAtMost(20)
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 60) // Run every 3 seconds (60 ticks)
    }

    private fun startFoodTask(player: Player) {
        val taskId = object : BukkitRunnable() {
            override fun run() {
                if (storage.isInRegion(player.location)) {
                    player.foodLevel = (player.foodLevel + 2).coerceAtMost(20)
                }
            }
        }.runTaskTimer(plugin, 0, 60).taskId // Run every 3 seconds (60 ticks)
        foodTaskMap[player.uniqueId] = taskId
    }

    private fun stopFoodTask(player: Player) {
        val taskId = foodTaskMap[player.uniqueId] ?: return
        Bukkit.getScheduler().cancelTask(taskId)
        foodTaskMap.remove(player.uniqueId)
    }
}
