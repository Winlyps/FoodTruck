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
    private val regions = mutableMapOf<String, List<Location>>()
    private val availableRegionNames = mutableSetOf<String>()
    private var regionCounter = 1

    init {
        if (!regionsFile.exists()) {
            regionsFile.parentFile.mkdirs()
            regionsFile.createNewFile()
        } else {
            loadRegions()
        }
    }

    fun addCorner(playerId: UUID, location: Location) {
        val corners = regions.getOrDefault(playerId.toString(), mutableListOf()) as MutableList<Location>
        if (corners.size < 4) {
            corners.add(location)
            regions[playerId.toString()] = corners
        }
    }

    fun getCorners(playerId: UUID): List<Location> {
        return regions[playerId.toString()] ?: emptyList()
    }

    fun saveRegion(playerId: UUID) {
        val corners = regions[playerId.toString()] ?: return
        val regionName = getNextAvailableRegionName()
        regions[regionName] = corners
        regions.remove(playerId.toString())
        saveRegions(regions)
    }

    fun removeRegion(regionName: String) {
        regions.remove(regionName)
        availableRegionNames.add(regionName)
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

    fun getRegionNames(): List<String> {
        return regions.keys.toList()
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

        val type: Type = object : TypeToken<MutableMap<String, List<SerializedLocation>>>() {}.type
        val serializedRegions = gson.fromJson<MutableMap<String, List<SerializedLocation>>>(FileReader(regionsFile), type) ?: mutableMapOf()

        for ((regionName, serializedLocations) in serializedRegions) {
            regions[regionName] = serializedLocations.map { it.toLocation(plugin) }
        }

        regionCounter = regions.keys.filter { it.startsWith("FoodRegion") }.map { it.substring(10).toInt() }.maxOrNull()?.plus(1) ?: 1
    }

    private fun saveRegions(regions: MutableMap<String, List<Location>>) {
        val serializedRegions = mutableMapOf<String, List<SerializedLocation>>()
        for ((regionName, locations) in regions) {
            serializedRegions[regionName] = locations.map { SerializedLocation.fromLocation(it) }
        }

        FileWriter(regionsFile).use { writer ->
            gson.toJson(serializedRegions, writer)
        }
    }

    private fun getNextAvailableRegionName(): String {
        if (availableRegionNames.isEmpty()) {
            return "FoodRegion${regionCounter++}"
        }
        return availableRegionNames.minByOrNull { it.substring(10).toInt() } ?: "FoodRegion${regionCounter++}"
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