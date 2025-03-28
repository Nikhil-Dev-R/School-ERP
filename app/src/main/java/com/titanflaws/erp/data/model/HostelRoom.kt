package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a room in a hostel
 * @property roomId Unique identifier for the room
 * @property hostelId ID of the hostel this room belongs to
 * @property roomNumber Room number
 * @property floor Floor number
 * @property capacity Room capacity (number of beds)
 * @property occupiedBeds Number of beds currently occupied
 * @property roomType Type of room (SINGLE, DOUBLE, DORMITORY, etc.)
 * @property facilities List of facilities in the room
 * @property monthlyRent Monthly rent amount
 * @property occupantIds List of student/staff IDs occupying this room
 * @property isActive Whether this room is currently active
 * @property createdAt Creation timestamp
 * @property updatedAt Last update timestamp
 */
@Entity(
    tableName = "hostel_rooms",
    foreignKeys = [
        ForeignKey(
            entity = Hostel::class,
            parentColumns = ["hostelId"],
            childColumns = ["hostelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("hostelId"),
        Index(value = ["hostelId", "roomNumber"], unique = true)
    ]
)
data class HostelRoom(
    @PrimaryKey
    val roomId: String,
    val hostelId: String,
    val roomNumber: String,
    val floor: Int,
    val capacity: Int,
    val occupiedBeds: Int = 0,
    val roomType: String,
    val facilities: List<String>?,
    val monthlyRent: Double,
    val occupantIds: List<String>?,
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) 