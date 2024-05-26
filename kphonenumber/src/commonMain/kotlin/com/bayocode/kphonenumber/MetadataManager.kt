package com.bayocode.kphonenumber

import com.bayocode.kphonenumber.generated.generatedMetadata

class MetadataManager {
    var territories: MutableList<MetadataTerritory> = mutableListOf()
        private set
    
    private var territoriesByCode: MutableMap<Int, MutableList<MetadataTerritory>> = mutableMapOf()
    private var mainTerritoryByCode: MutableMap<Int, MetadataTerritory> = mutableMapOf()
    private var territoriesByCountry: MutableMap<String, MetadataTerritory> = mutableMapOf()
    
    init {
        territories = populateTerritories()
        for (item in territories) {
            val currentTerritories: MutableList<MetadataTerritory> = territoriesByCode[item.countryCode] ?: mutableListOf()
            if (item.mainCountryForCode) {
                currentTerritories.add(0, item)
            } else {
                currentTerritories.add(item)
            }
            territoriesByCode[item.countryCode] = currentTerritories
            if (mainTerritoryByCode[item.countryCode] == null || item.mainCountryForCode) {
                mainTerritoryByCode[item.countryCode] = item
            }
            territoriesByCountry[item.codeID] = item
        }
    }
    
    fun populateTerritories(): MutableList<MetadataTerritory> {
        val territoryArray = mutableListOf<MetadataTerritory>()
        try {
            val metadata = generatedMetadata
            territoryArray.addAll(metadata.phoneNumberMetadata.territories.territory)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return territoryArray
    }
    
    fun filterTerritories(code: Int): List<MetadataTerritory>? {
        return territoriesByCode[code]
    }
    
    fun filterTerritories(country: String): MetadataTerritory? {
        return territoriesByCountry[country.uppercase()]
    }
    
    fun mainTerritory(code: Int): MetadataTerritory? {
        return mainTerritoryByCode[code]
    }
}
