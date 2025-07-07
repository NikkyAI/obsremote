package moe.nikky.vrcobs.graph

import kotlinx.serialization.Serializable

@Serializable
class ViewersResponse(
    val viewers: List<Region>
) {
    @Serializable
    data class Region(
        val region: String,
        val total: Int,
    )
}
