package com.example.composeapptask.feature.dao

import com.google.gson.annotations.SerializedName

data class UserMedicationResponse(
    val problems: List<Problem>?
)

data class Problem(
    val asthma: List<Asthma>?,
    @SerializedName("Diabetes") val diabetes: List<Diabete>?
)

class Asthma

data class Diabete(
    val labs: List<Lab>?,
    val medications: List<Medication>?
)

data class Lab(
    val missingField: String?
)

data class Medication(
    val medicationsClasses: List<MedicationsClasse>?
)

data class AssociatedDrug(
    val dose: String?,
    val name: String?,
    val strength: String?
)

data class ClassName(
    @SerializedName("associatedDrug") val associatedDrug: List<AssociatedDrug>?,
    @SerializedName("associatedDrug#2") val associatedDrug2: List<AssociatedDrug>?
)

data class MedicationsClasse(
    val className: List<ClassName>?,
    val className2: List<ClassName>?
)

fun UserMedicationResponse.getAllAssociatedDrugs(): List<AssociatedDrug> {
    return problems
        ?.flatMap { problem ->
            problem.diabetes?.flatMap { diabetes ->
                diabetes.medications?.flatMap { medication ->
                    medication.medicationsClasses?.flatMap { medicationsClass ->
                        val class1Drugs = medicationsClass.className?.flatMap { className ->
                            className.associatedDrug.orEmpty()
                        }.orEmpty()

                        val class2Drugs = medicationsClass.className2?.flatMap { className2 ->
                            className2.associatedDrug2.orEmpty()
                        }.orEmpty()

                        class1Drugs + class2Drugs
                    }.orEmpty()
                }.orEmpty()
            }.orEmpty()
        }.orEmpty()
}
