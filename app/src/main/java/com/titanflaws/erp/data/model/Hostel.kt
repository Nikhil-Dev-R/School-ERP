package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a hostel building in the school
 * @property hostelId Unique identifier for the hostel
 * @property name Hostel name
 * @property type Hostel type (BOYS, GIRLS, STAFF)
 * @property address Hostel address
 * @property capacity Total capacity of the hostel
 * @property numberOfRooms Number of rooms in the hostel
 * @property numberOfFloors Number of floors in the hostel
 * @property wardenName Name of the hostel warden
 * @property wardenContactNumber Contact number of the warden
 * @property facilities List of facilities available (e.g., "WiFi", "Gym")
 * @property description Description of the hostel
 * @property isActive Whether this hostel is currently active
 * @property createdAt Creation timestamp
 * @property updatedAt Last update timestamp
 */
@Entity(tableName = "hostels")
data class Hostel(
    @PrimaryKey
    val hostelId: String,
    val name: String,
    val type: String,
    val address: String,
    val capacity: Int,
    val numberOfRooms: Int,
    val numberOfFloors: Int,
    val wardenName: String,
    val wardenContactNumber: String,
    val facilities: List<String>?,
    val description: String?,
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) 