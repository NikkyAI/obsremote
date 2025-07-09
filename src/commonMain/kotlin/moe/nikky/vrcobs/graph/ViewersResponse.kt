package moe.nikky.vrcobs.graph

import kotlinx.serialization.Serializable

@Serializable
data class ViewersResponse(
    val viewers: List<Region>
) {
    @Serializable
    data class Region(
        val region: String,
        val total: Int,
    )
}
