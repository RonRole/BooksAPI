package books.api.services

import books.api.entities.Author
import books.api.errors.DuplicateException
import books.api.infra.jooq.tables.records.AuthorRecord
import books.api.infra.jooq.tables.references.AUTHOR
import org.jooq.DSLContext
import org.jooq.TableField
import org.springframework.stereotype.Service

enum class AuthorColumn(override val tableField : TableField<AuthorRecord, *>) : Column<AuthorRecord> {
    ID(AUTHOR.ID),
    NAME(AUTHOR.NAME)
}

@Service
class AuthorService(
    private val dsl: DSLContext
) {
    companion object {
        val DEFAULT_SORT = mapOf(
            AuthorColumn.ID to true,
            AuthorColumn.NAME to true
        )
    }

    fun search(
        name    : String = "",
        sortMap : Map<AuthorColumn, Boolean> = DEFAULT_SORT,
        offset  : Int = 0,
        limit   : Int = 10,
    ) : List<Author> {
        return dsl
            .select()
            .from(AUTHOR)
            .where(
                name.toTrueConditionIfEmpty {
                    AUTHOR.NAME.like("%${it.escapeForLike()}%")
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
                Author(
                    it.getValue(AUTHOR.ID)!!,
                    it.getValue(AUTHOR.NAME)!!
                )
            }
    }

    fun findById(id: Int) : Author? {
        return dsl
            .select()
            .from(AUTHOR)
            .where(AUTHOR.ID.eq(id))
            .fetchOne()
            ?.map {
                Author(
                    it.getValue(AUTHOR.ID)!!,
                    it.getValue(AUTHOR.NAME)!!
                )
            }
    }

    fun register(
        name: String
    ) : Author? {
        val duplicated = this.search(name = name)
        if(duplicated.isNotEmpty()) {
            throw DuplicateException("同名の著者が存在しています");
        }
        return dsl
            .insertInto(AUTHOR, AUTHOR.NAME)
            .values(name)
            .returningResult(AUTHOR.ID, AUTHOR.NAME)
            .fetchOne()
            ?.map {
                Author(
                    it.get(AUTHOR.ID)!!,
                    it.getValue(AUTHOR.NAME)!!
                )
            }
    }

    fun update(author: Author) : Int {
        return dsl
            .update(AUTHOR)
            .set(AUTHOR.NAME, author.name)
            .where(AUTHOR.ID.eq(author.id))
            .execute()
    }
}