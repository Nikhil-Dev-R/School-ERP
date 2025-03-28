package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a stop on a transport route
 * @property stopId Unique identifier for the stop
 * @property routeId ID of the route this stop belongs to
 * @property stopName Name of the stop
 * @property stopAddress Address of the stop
 * @property stopNumber Sequence number of the stop in the route
 * @property pickupTime Morning pickup time (minutes from midnight)
 * @property dropTime Afternoon drop time (minutes from midnight)
 * @property latitude Geographical latitude
 * @property longitude Geographical longitude
 * @property studentIds List of student IDs using this stop
 * @property isActive Whether this stop is currently active
 * @property createdAt Creation timestamp
 * @property updatedAt Last update timestamp
 */
@Entity(
    tableName = "route_stops",
    foreignKeys = [
        ForeignKey(
            entity = Transport::class,
            parentColumns = ["vehicleId"],
            childColumns = ["routeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("routeId"),
        Index(value = ["routeId", "stopNumber"], unique = true)
    ]
)
data class RouteStop(
    @PrimaryKey
    val stopId: String,
    val routeId: String,
    val stopName: String,
    val stopAddress: String,
    val stopNumber: Int,
    val pickupTime: Int,
    val dropTime: Int,
    val latitude: Double?,
    val longitude: Double?,
    val studentIds: List<String>?,
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) 