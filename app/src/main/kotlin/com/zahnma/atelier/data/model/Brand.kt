package com.zahnma.atelier.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BrandsData(
    @SerialName("schemaVersion") val schemaVersion: Int,
    @SerialName("dataVersion") val dataVersion: String,
    @SerialName("brands") val brands: List<Brand>,
)

@Serializable
data class Brand(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("country") val country: String,
    @SerialName("founded") val founded: Int,
    @SerialName("category") val category: String,
    @SerialName("description") val description: String? = null,
    @SerialName("logoUrl") val logoUrl: String? = null,
    @SerialName("directors") val directors: List<CreativeDirector>,
)

@Serializable
data class CreativeDirector(
    @SerialName("personId") val personId: String,
    @SerialName("name") val name: String,
    @SerialName("role") val role: String,
    @SerialName("startDate") val startDate: String,
    @SerialName("endDate") val endDate: String? = null,
    @SerialName("isCurrent") val isCurrent: Boolean? = null,
    @SerialName("bio") val bio: String? = null,
) {
    val currentlyServing: Boolean
        get() = endDate == null
}
