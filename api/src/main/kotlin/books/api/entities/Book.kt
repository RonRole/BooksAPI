package books.api.entities

import java.time.LocalDate

data class Book(
    val id: Int,
    val title: String,
    val authorId: Int,
    val publishedAt: LocalDate
)