package ru.smartro.worknote.network.workNote.responseDTO

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class WaybillBodyDTO(
    val data: WayBillWrapper,
    val success: Boolean,
    @Json(name = "organisation_id") val organisationId: Int
) {

    @JsonClass(generateAdapter = true)
    data class WayBillWrapper(
        @Json(name = "waybill") val waybill: WaybillBodyDTOData
    )

    @JsonClass(generateAdapter = true)
    data class WaybillBodyDTOData(
        @Json(name = "srp_id") val srpId: Int,
        @Json(name = "srp_vehicle_id") val srpVehicleId: Int,
        @Json(name = "srp_driver_id") val srpDriverId: Int,
        @Json(name = "datetime") val date: String,
        @Json(name = "workorder") val workOrders: List<WorkOrder>
    )

    @JsonClass(generateAdapter = true)
    data class WorkOrder(
        @Json(name = "srp_id") val srpId: Int,
        @Json(name = "platform_items") val platformItems: List<Platform>
    )

    @JsonClass(generateAdapter = true)
    data class Platform(
        @Json(name = "srp_id") val srpId: Int,

        val address: String,
        @Json(name = "kgo_norma") val kgoNorma: Int?,
        val name: String,
        val latitude: Double,
        val longitude: Double,
        val containers: List<Container>
    )

    @JsonClass(generateAdapter = true)
    data class Container(
        @Json(name = "srp_point_details_id") val srpDetailsId: Int,
        @Json(name = "inv_number") val invNumber: String?,
        @Json(name = "srp_type_id") val srpTypeId: Int,
        @Json(name = "type") val srpTypeName: String,
        @Json(name = "is_active") val isActive: Boolean
    )
}
