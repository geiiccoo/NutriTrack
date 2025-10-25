package com.fit2081.nutritrack.data.patients

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a patient entity within the Room database.
 */
// Referenced from W5 Lab
@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey
    val userId: String,
    val phoneNumber: String,
    val sex: String,
    var name: String?,
    var password: String? = null, // Initially null until user sets it

    val HEIFAtotalscoreMale: Double,
    val HEIFAtotalscoreFemale: Double,
    val DiscretionaryHEIFAscoreMale: Double,
    val DiscretionaryHEIFAscoreFemale: Double,
    val Discretionaryservesize: Double,
    val VegetablesHEIFAscoreMale: Double,
    val VegetablesHEIFAscoreFemale: Double,
    val Vegetableswithlegumesallocatedservesize: Double,
    val LegumesallocatedVegetables: Double,
    val Vegetablesvariationsscore: Double,
    val VegetablesCruciferous: Double,
    val VegetablesTuberandbulb: Double,
    val VegetablesOther: Double,
    val Legumes: Double,
    val VegetablesGreen: Double,
    val VegetablesRedandorange: Double,
    val FruitHEIFAscoreMale: Double,
    val FruitHEIFAscoreFemale: Double,
    val Fruitservesize: Double,
    val Fruitvariationsscore: Double,
    val FruitPome: Double,
    val FruitTropicalandsubtropical: Double,
    val FruitBerry: Double,
    val FruitStone: Double,
    val FruitCitrus: Double,
    val FruitOther: Double,
    val GrainsandcerealsHEIFAscoreMale: Double,
    val GrainsandcerealsHEIFAscoreFemale: Double,
    val Grainsandcerealsservesize: Double,
    val GrainsandcerealsNonwholegrains: Double,
    val WholegrainsHEIFAscoreMale: Double,
    val WholegrainsHEIFAscoreFemale: Double,
    val Wholegrainsservesize: Double,
    val MeatandalternativesHEIFAscoreMale: Double,
    val MeatandalternativesHEIFAscoreFemale: Double,
    val Meatandalternativeswithlegumesallocatedservesize: Double,
    val LegumesallocatedMeatandalternatives: Double,
    val DairyandalternativesHEIFAscoreMale: Double,
    val DairyandalternativesHEIFAscoreFemale: Double,
    val Dairyandalternativesservesize: Double,
    val SodiumHEIFAscoreMale: Double,
    val SodiumHEIFAscoreFemale: Double,
    val Sodiummgmilligrams: Double,
    val AlcoholHEIFAscoreMale: Double,
    val AlcoholHEIFAscoreFemale: Double,
    val Alcoholstandarddrinks: Double,
    val WaterHEIFAscoreMale: Double,
    val WaterHEIFAscoreFemale: Double,
    val Water: Double,
    val WaterTotalmL: Double,
    val BeverageTotalmL: Double,
    val SugarHEIFAscoreMale: Double,
    val SugarHEIFAscoreFemale: Double,
    val Sugar: Double,
    val SaturatedFatHEIFAscoreMale: Double,
    val SaturatedFatHEIFAscoreFemale: Double,
    val SaturatedFat: Double,
    val UnsaturatedFatHEIFAscoreMale: Double,
    val UnsaturatedFatHEIFAscoreFemale: Double,
    val UnsaturatedFatservesize: Double
)