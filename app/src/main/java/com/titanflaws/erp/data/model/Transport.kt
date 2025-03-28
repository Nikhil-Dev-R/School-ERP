package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a transport vehicle in the school
 * @property vehicleId Unique identifier for the vehicle
 * @property registrationNumber Vehicle registration number
 * @property type Vehicle type (BUS, VAN, etc.)
 * @property model Vehicle model
 * @property capacity Seating capacity
 * @property driver Driver name
 * @property driverContactNumber Driver contact number
 * @property assistant Assistant/conductor name
 * @property assistantContactNumber Assistant contact number
 * @property routeId ID of the route this vehicle is assigned to
 * @property status Vehicle status (OPERATIONAL, MAINTENANCE, INACTIVE)
 * @property gpsDeviceId GPS tracking device ID
 * @property insuranceNumber Insurance policy number
 * @property insuranceValidUntil Insurance validity end date
 * @property fitnessValidUntil Fitness certificate validity end date
 * @property purchaseDate Date when the vehicle was purchased
 * @property lastMaintenanceDate Last maintenance date
 * @property nextMaintenanceDate Next scheduled maintenance date
 * @property isActive Whether this vehicle is currently active
 * @property createdAt Creation timestamp
 * @property updatedAt Last update timestamp
 */
@Entity(
    tableName = "transport_vehicles",
    indices = [
        androidx.room.Index("registrationNumber", unique = true)
    ]
)
data class Transport(
    @PrimaryKey
    val vehicleId: String,
    val registrationNumber: String,
    val type: String,
    val model: String?,
    val capacity: Int,
    val driver: String,
    val driverContactNumber: String,
    val assistant: String?,
    val assistantContactNumber: String?,
    val routeId: String?,
    val status: String,
    val gpsDeviceId: String?,
    val insuranceNumber: String?,
    val insuranceValidUntil: Date?,
    val fitnessValidUntil: Date?,
    val purchaseDate: Date?,
    val lastMaintenanceDate: Date?,
    val nextMaintenanceDate: Date?,
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) 