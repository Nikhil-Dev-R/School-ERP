package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a book in the library
 * @property bookId Unique identifier for the book
 * @property title Book title
 * @property author Book author(s)
 * @property isbn ISBN number
 * @property publisher Publisher name
 * @property publicationYear Year of publication
 * @property edition Edition of the book
 * @property category Book category/genre
 * @property location Physical location in the library
 * @property price Book price
 * @property quantity Total quantity of this book
 * @property availableQuantity Currently available quantity
 * @property coverImageUrl URL of the book cover image
 * @property description Book description
 * @property tags List of tags for the book
 * @property language Book language
 * @property pageCount Number of pages
 * @property isActive Whether this book is currently active in the system
 * @property addedBy ID of the user who added this book
 * @property createdAt Creation timestamp
 * @property updatedAt Last update timestamp
 */
@Entity(
    tableName = "library_books",
    indices = [
        Index("isbn", unique = true)
    ]
)
data class Library(
    @PrimaryKey
    val bookId: String,
    val title: String,
    val author: String,
    val isbn: String,
    val publisher: String?,
    val publicationYear: Int?,
    val edition: String?,
    val category: String,
    val location: String?,
    val price: Double?,
    val quantity: Int,
    val availableQuantity: Int,
    val coverImageUrl: String?,
    val description: String?,
    val tags: List<String>?,
    val language: String?,
    val pageCount: Int?,
    val isActive: Boolean = true,
    val addedBy: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) 