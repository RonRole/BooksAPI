package books.api.services

import books.api.entities.Book
import books.api.errors.DuplicateException
import books.api.infra.jooq.tables.records.BookRecord
import books.api.infra.jooq.tables.references.BOOK
import org.jooq.DSLContext
import org.jooq.TableField
import org.springframework.stereotype.Service
import java.time.LocalDate

enum class BookColumn(override val tableField : TableField<BookRecord, *>) : Column<BookRecord> {
    ID(BOOK.ID),
    TITLE(BOOK.TITLE),
    AUTHOR_ID(BOOK.AUTHOR_ID),
    PUBLISHED_AT(BOOK.PUBLISHED_AT)
}

@Service
class BookService(
    private val dsl: DSLContext
) {
    companion object {
        private val DEFAULT_SORT = mapOf(
            BookColumn.ID to true,
            BookColumn.TITLE to true,
            BookColumn.AUTHOR_ID to true,
        )
    }
    fun search(
        title     : String  = "",
        authorId  : Int?    = null,
        publishedFrom : LocalDate? = null,
        publishedTo : LocalDate? = null,
        sortMap   : Map<BookColumn, Boolean> = DEFAULT_SORT,
        offset    : Int     = 0,
        limit     : Int     = 10,
    ): List<Book> {
        return dsl
                .select()
                .from(BOOK)
                .where(
                    title.toTrueConditionIfEmpty {
                        BOOK.TITLE.like("%${it.escapeForLike()}%")
                    }
                )
                .and(
                    authorId.toTrueConditionIfNull {
                        BOOK.AUTHOR_ID.eq(it)
                    }
                )
                .and(
                    publishedFrom.toTrueConditionIfNull {
                        BOOK.PUBLISHED_AT.ge(publishedFrom)
                    }
                )
                .and(
                    publishedTo.toTrueConditionIfNull {
                        BOOK.PUBLISHED_AT.le(publishedTo)
                    }
                )
                .orderBy(
                    sortMap.toOrderByCondition().ifEmpty {
                        DEFAULT_SORT.toOrderByCondition()
                    }
                )
                .limit(limit)
                .offset(offset)
                .fetch()
                .map {
                    Book (
                        it.getValue(BOOK.ID)!!,
                        it.getValue(BOOK.TITLE)!!,
                        it.getValue(BOOK.AUTHOR_ID)!!,
                        it.getValue(BOOK.PUBLISHED_AT)!!
                    )
                }
    }

    fun findById(id: Int) : Book? {
        return dsl
            .select()
            .from(BOOK)
            .where(BOOK.ID.eq(id))
            .fetchOne()
            ?.map {
                Book(
                    it.getValue(BOOK.ID)!!,
                    it.getValue(BOOK.TITLE)!!,
                    it.getValue(BOOK.AUTHOR_ID)!!,
                    it.getValue(BOOK.PUBLISHED_AT)!!
                )
            }
    }

    fun register(
        title: String,
        authorId: Int,
        publishedAt: LocalDate
    ) : Book? {
        val duplicated = this.search(
            title = title,
            authorId = authorId
        )
        if(duplicated.isNotEmpty()) {
            throw DuplicateException("同じタイトル・著者の書籍が既に存在しています")
        }
        return dsl
            .insertInto(BOOK, BOOK.TITLE, BOOK.AUTHOR_ID, BOOK.PUBLISHED_AT)
            .values(title, authorId, publishedAt)
            .returningResult(BOOK.ID, BOOK.TITLE, BOOK.AUTHOR_ID, BOOK.PUBLISHED_AT)
            .fetchOne()
            ?.map {
                Book(
                    it.getValue(BOOK.ID)!!,
                    it.getValue(BOOK.TITLE)!!,
                    it.getValue(BOOK.AUTHOR_ID)!!,
                    it.getValue(BOOK.PUBLISHED_AT)!!
                )
            }
    }

    fun update(book : Book) : Int {
        return dsl
            .update(BOOK)
            .set(BOOK.TITLE, book.title)
            .set(BOOK.AUTHOR_ID, book.authorId)
            .set(BOOK.PUBLISHED_AT, book.publishedAt)
            .where(BOOK.ID.eq(book.id))
            .execute()
    }
}