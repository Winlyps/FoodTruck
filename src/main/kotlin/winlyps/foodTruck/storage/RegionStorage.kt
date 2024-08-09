//5.File: RegionStorage.kt
package winlyps.foodTruck.storage

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.reflect.Type
import java.util.UUID

class RegionStorage(private val plugin: JavaPlugin) {

    private val gson = Gson()
    private val regionsFile = File(plugin.dataFolder, "regions.json")
    private val regions = mutableMapOf<UUID, List<Location>>()

    init {
        if (!regionsFile.exists()) {
            regionsFile.parentFile.mkdirs()
            regionsFile.createNewFile()
        } else {
            loadRegions()
        }
    }

    fun addCorner(playerId: UUID, location: Location) {
        val corners = regions.getOrDefault(playerId, mutableListOf()) as MutableList<Location>
        if (corners.size < 4) {
            corners.add(location)
            regions[playerId] = corners
        }
    }

    fun getCorners(playerId: UUID): List<Location> {
        return regions[playerId] ?: emptyList()
    }

    fun saveRegion(playerId: UUID) {
        val corners = regions[playerId] ?: return
        saveRegions(regions)
    }

    fun removeRegion(playerId: UUID) {
        regions.remove(playerId)
        saveRegions(regions)
    }

    fun isInRegion(location: Location): Boolean {
        for ((_, corners) in regions) {
            if (isLocationInRegion(location, corners)) {
                return true
            }
        }
        return false
    }

    private fun isLocationInRegion(location: Location, corners: List<Location>): Boolean {
        if (corners.size != 4) return false

        val minX = corners.minOf { it.blockX }
        val maxX = corners.maxOf { it.blockX }
        val minZ = corners.minOf { it.blockZ }
        val maxZ = corners.maxOf { it.blockZ }

        return location.blockX in minX..maxX && location.blockZ in minZ..maxZ
    }

    private fun loadRegions() {
        if (!regionsFile.exists()) return

        val type: Type = object : TypeToken<MutableMap<UUID, List<SerializedLocation>>>() {}.type
        val serializedRegions = gson.fromJson<MutableMap<UUID, List<SerializedLocation>>>(FileReader(regionsFile), type) ?: mutableMapOf()

        for ((playerId, serializedLocations) in serializedRegions) {
            regions[playerId] = serializedLocations.map { it.toLocation(plugin) }
        }
    }

    private fun saveRegions(regions: MutableMap<UUID, List<Location>>) {
        val serializedRegions = mutableMapOf<UUID, List<SerializedLocation>>()
        for ((playerId, locations) in regions) {
            serializedRegions[playerId] = locations.map { SerializedLocation.fromLocation(it) }
        }

        FileWriter(regionsFile).use { writer ->
            gson.toJson(serializedRegions, writer)
        }
    }

    private data class SerializedLocation(
            val world: String,
            val x: Double,
            val y: Double,
            val z: Double,
            val yaw: Float,
            val pitch: Float
    ) {
        fun toLocation(plugin: JavaPlugin): Location {
            return Location(plugin.server.getWorld(world), x, y, z, yaw, pitch)
        }

        companion object {
            fun fromLocation(location: Location): SerializedLocation {
                return SerializedLocation(
                        location.world?.name ?: "world",
                        location.x,
                        location.y,
                        location.z,
                        location.yaw,
                        location.pitch
                )
            }
        }
    }
}