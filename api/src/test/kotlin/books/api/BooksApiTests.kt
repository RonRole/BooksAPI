package books.api

import books.api.infra.jooq.tables.references.AUTHOR
import books.api.infra.jooq.tables.references.BOOK
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import java.time.LocalDate
import kotlin.math.exp

@SpringBootTest
@AutoConfigureMockMvc
class BooksApiTests @Autowired constructor(
    private val mockMvc: MockMvc,
    private val dsl : DSLContext
) {
    @BeforeEach
    @AfterEach
    fun clearTables() {
        dsl.deleteFrom(BOOK).execute()
        dsl.deleteFrom(AUTHOR).execute()
    }

    @Test
    fun searchBooksWithNoParameters() {
        val localDateNow = LocalDate.now()
        dsl.insertInto(
            AUTHOR,
            AUTHOR.ID,
            AUTHOR.NAME
        )
            .values(1, "Author1")
            .execute()
        dsl.insertInto(
            BOOK,
            BOOK.ID,
            BOOK.TITLE,
            BOOK.AUTHOR_ID,
            BOOK.PUBLISHED_AT
        )
            .values(1, "title1", 1, localDateNow)
            .values(2, "title2", 1, localDateNow)
            .values(3, "title3", 1, localDateNow)
            .execute()

        val expect = """
            [
                {"id":1,"title":"title1","authorId":1,"publishedAt":"${localDateNow}"},
                {"id":2,"title":"title2","authorId":1,"publishedAt":"${localDateNow}"},
                {"id":3,"title":"title3","authorId":1,"publishedAt":"${localDateNow}"}
            ]
        """.trimIndent()

        mockMvc
            .get("/books")
            .andExpect { status { isOk() } }
            .andExpect {
                content {
                    json(expect)
                }
            }
    }

    @Test
    fun searchBooksWithTitle() {
        val localDateNow = LocalDate.now()
        dsl.insertInto(
            AUTHOR,
            AUTHOR.ID,
            AUTHOR.NAME
        )
            .values(1, "Author1")
            .execute()
        dsl.insertInto(
            BOOK,
            BOOK.ID,
            BOOK.TITLE,
            BOOK.AUTHOR_ID,
            BOOK.PUBLISHED_AT
        )
            .values(1, "title1", 1, localDateNow)
            .values(2, "title2", 1, localDateNow)
            .values(3, "title3", 1, localDateNow)
            .execute()

        val expect = """
            [
                {"id":1,"title":"title1","authorId":1,"publishedAt":${localDateNow}}
            ]
        """.trimIndent()

        mockMvc
            .get("/books?title=1")
            .andExpect { status { isOk() } }
            .andExpect {
                content {
                    json(expect)
                }
            }
    }

    @Test
    fun searchBooksWithAuthorId() {
        val localDateNow = LocalDate.now()
        dsl.insertInto(
            AUTHOR,
            AUTHOR.ID,
            AUTHOR.NAME
        )
            .values(1, "Author1")
            .values(2, "Author2")
            .values(3,"Author3")
            .execute()
        dsl.insertInto(
            BOOK,
            BOOK.ID,
            BOOK.TITLE,
            BOOK.AUTHOR_ID,
            BOOK.PUBLISHED_AT
        )
            .values(1, "title1", 1, localDateNow)
            .values(2, "title2", 2, localDateNow)
            .values(3, "title3", 3, localDateNow)
            .execute()

        val expect = """
            [
                {"id":2,"title":"title2","authorId":2,"publishedAt":${localDateNow}}
            ]
        """.trimIndent()

        mockMvc
            .get("/books?author-id=2")
            .andExpect { status { isOk() } }
            .andExpect {
                content {
                    json(expect)
                }
            }
    }

    @Test
    fun searchBooksWithPublishedFrom() {
        dsl.insertInto(
            AUTHOR,
            AUTHOR.ID,
            AUTHOR.NAME
        )
            .values(1, "Author1")
            .values(2, "Author2")
            .values(3,"Author3")
            .execute()
        dsl.insertInto(
            BOOK,
            BOOK.ID,
            BOOK.TITLE,
            BOOK.AUTHOR_ID,
            BOOK.PUBLISHED_AT
        )
            .values(1, "title1", 1, LocalDate.parse("2018-01-01"))
            .values(2, "title2", 2, LocalDate.parse("2018-02-01"))
            .values(3, "title3", 3, LocalDate.parse("2018-03-01"))
            .execute()

        val expect = """
            [
                {"id":2,"title":"title2","authorId":2,"publishedAt":"2018-02-01"},
                {"id":3,"title":"title3","authorId":3,"publishedAt":"2018-03-01"}
            ]
        """.trimIndent()

        mockMvc
            .get("/books?published-from=2018-01-02")
            .andExpect { status { isOk() } }
            .andExpect {
                content {
                    json(expect)
                }
            }
    }

    @Test
    fun searchBooksWithPublishedTo() {
        dsl.insertInto(
            AUTHOR,
            AUTHOR.ID,
            AUTHOR.NAME
        )
            .values(1, "Author1")
            .values(2, "Author2")
            .values(3,"Author3")
            .execute()
        dsl.insertInto(
            BOOK,
            BOOK.ID,
            BOOK.TITLE,
            BOOK.AUTHOR_ID,
            BOOK.PUBLISHED_AT
        )
            .values(1, "title1", 1, LocalDate.parse("2018-01-01"))
            .values(2, "title2", 2, LocalDate.parse("2018-02-01"))
            .values(3, "title3", 3, LocalDate.parse("2018-03-01"))
            .execute()

        val expect = """
            [
                {"id":1,"title":"title1","authorId":1,"publishedAt":"2018-01-01"},
                {"id":2,"title":"title2","authorId":2,"publishedAt":"2018-02-01"}
            ]
        """.trimIndent()

        mockMvc
            .get("/books?published-to=2018-02-28")
            .andExpect { status { isOk() } }
            .andExpect {
                content {
                    json(expect)
                }
            }
    }

    @Test
    fun searchBooksWithSortingByTitle() {
        val localDateNow = LocalDate.now()
        dsl.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.NAME)
            .values(1, "Author1")
            .execute()
        dsl.insertInto(BOOK, BOOK.ID, BOOK.TITLE, BOOK.AUTHOR_ID, BOOK.PUBLISHED_AT)
            .values(1, "aaaa", 1, localDateNow)
            .values(2, "cccc", 1, localDateNow)
            .values(3, "bbbb", 1, localDateNow)
            .execute()

        val expect = """
            [
                {"id":1,"title":"aaaa","authorId":1,"publishedAt":"${localDateNow}"},
                {"id":3,"title":"bbbb","authorId":1,"publishedAt":"${localDateNow}"},
                {"id":2,"title":"cccc","authorId":1,"publishedAt":"${localDateNow}"}
            ]
        """.trimIndent()

        mockMvc
            .get("/books?sort=title")
            .andExpect { status { isOk() } }
            .andExpect {
                content {
                    json(expect)
                }
            }
    }

    @Test
    fun searchBooksWithSortingMulti() {
        val localDateNow = LocalDate.now()
        dsl.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.NAME)
            .values(1, "Author1")
            .values(2, "Author2")
            .execute()
        dsl.insertInto(BOOK, BOOK.ID, BOOK.TITLE, BOOK.AUTHOR_ID, BOOK.PUBLISHED_AT)
            .values(5, "bbbb", 1, localDateNow)
            .values(1, "aaaa", 1, localDateNow)
            .values(2, "cccc", 1, localDateNow)
            .values(3, "aaaa", 2, localDateNow)
            .values(4, "bbbb", 2, localDateNow)
            .execute()

        val expect = """
            [
                {"id":1,"title":"aaaa","authorId":1,"publishedAt":"${localDateNow}"},
                {"id":3,"title":"aaaa","authorId":2,"publishedAt":"${localDateNow}"},
                {"id":4,"title":"bbbb","authorId":2,"publishedAt":"${localDateNow}"},
                {"id":5,"title":"bbbb","authorId":1,"publishedAt":"${localDateNow}"},
                {"id":2,"title":"cccc","authorId":1,"publishedAt":"${localDateNow}"}
            ]
        """.trimIndent()

        mockMvc
            .get("/books?sort=title,id")
            .andExpect { status { isOk() } }
            .andExpect {
                content {
                    json(expect)
                }
            }
    }

    @Test
    fun searchBooksWithLimit() {
        val localDateNow = LocalDate.now()
        dsl.insertInto(
            AUTHOR,
            AUTHOR.ID,
            AUTHOR.NAME
        )
            .values(1, "Author1")
            .execute()
        dsl.insertInto(
            BOOK,
            BOOK.ID,
            BOOK.TITLE,
            BOOK.AUTHOR_ID,
            BOOK.PUBLISHED_AT
        )
            .values(1, "title1", 1, localDateNow)
            .values(2, "title2", 1, localDateNow)
            .values(3, "title3", 1, localDateNow)
            .execute()

        val expect = """
            [
                {"id":1,"title":"title1","authorId":1,"publishedAt":"${localDateNow}"}
            ]
        """.trimIndent()

        mockMvc
            .get("/books?limit=1")
            .andExpect { status { isOk() } }
            .andExpect {
                content {
                    json(expect)
                }
            }
    }

    @Test
    fun searchBooksWithOffset() {
        val localDateNow = LocalDate.now()
        dsl.insertInto(
            AUTHOR,
            AUTHOR.ID,
            AUTHOR.NAME
        )
            .values(1, "Author1")
            .execute()
        dsl.insertInto(
            BOOK,
            BOOK.ID,
            BOOK.TITLE,
            BOOK.AUTHOR_ID,
            BOOK.PUBLISHED_AT
        )
            .values(1, "title1", 1, localDateNow)
            .values(2, "title2", 1, localDateNow)
            .values(3, "title3", 1, localDateNow)
            .execute()

        val expect = """
            [
                {"id":2,"title":"title2","authorId":1,"publishedAt":"${localDateNow}"},
                {"id":3,"title":"title3","authorId":1,"publishedAt":"${localDateNow}"}
            ]
        """.trimIndent()

        mockMvc
            .get("/books?offset=1")
            .andExpect { status { isOk() } }
            .andExpect {
                content {
                    json(expect)
                }
            }
    }

    @Test
    fun findByBookId() {
        dsl.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.NAME)
            .values(1, "Author1")
            .execute()
        val localDateNow = LocalDate.now()
        dsl.insertInto(BOOK, BOOK.ID, BOOK.TITLE, BOOK.AUTHOR_ID, BOOK.PUBLISHED_AT)
            .values(1,"title",1, localDateNow)
            .execute()

        val expect = """
            {"id":1,"title":"title","authorId":1,"publishedAt":"${localDateNow}"}
        """.trimIndent()

        mockMvc
            .get("/books/1")
            .andExpect {
                status {
                    isOk()
                }
            }
            .andExpect {
                content {
                    json(expect)
                }
            }
    }

    @Test
    fun findByIdNotFound() {
        dsl.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.NAME)
            .values(1, "Author1")
            .execute()
        val localDateNow = LocalDate.now()
        val insertedBookId = dsl
            .insertInto(BOOK, BOOK.ID, BOOK.TITLE, BOOK.PUBLISHED_AT, BOOK.AUTHOR_ID)
            .values(1,"title",localDateNow,1)
            .returningResult(BOOK.ID)
            .fetchOne()!!
            .map {
                it.getValue(BOOK.ID)!!
            }

        mockMvc
            .get("/books/${insertedBookId+1}")
            .andExpect {
                status {
                    isNotFound()
                }
            }

    }

    @Test
    fun registerBook() {
        dsl.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.NAME)
            .values(1, "Author1")
            .execute()

        val localDateNow = LocalDate.now()
        val body = """
            {"title":"sample","authorId":"1","publishedAt":"${localDateNow}"}
        """.trimIndent()
        val expect = """
            {"title":"sample","authorId":1,"publishedAt":"${localDateNow}"}
        """.trimIndent()

        mockMvc
            .post("/books") {
                contentType = MediaType.APPLICATION_JSON
                content = body
            }
            .andExpect {
                status {
                    isCreated()
                }
            }
            .andExpect {
                content {
                    json(expect)
                }
            }
    }

    @Test
    fun registerDuplicateBook() {
        dsl.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.NAME)
            .values(1, "Author1")
            .execute()

        val localDateNow = LocalDate.now()

        dsl.insertInto(BOOK, BOOK.ID, BOOK.TITLE, BOOK.PUBLISHED_AT, BOOK.AUTHOR_ID)
            .values(1, "sample", localDateNow, 1)
            .execute()

        val body = """
            {"title":"sample","authorId":"1","publishedAt":"${localDateNow}"}
        """.trimIndent()

        mockMvc
            .post("/books") {
                contentType = MediaType.APPLICATION_JSON
                content = body
            }
            .andExpect {
                status {
                    isConflict()
                }
            }
    }

    @Test
    fun updateBook() {
        dsl.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.NAME)
            .values(1, "Author1")
            .execute()

        val localDateNow = LocalDate.now()

        dsl.insertInto(BOOK, BOOK.ID, BOOK.TITLE, BOOK.PUBLISHED_AT, BOOK.AUTHOR_ID)
            .values(1, "sample", localDateNow, 1)
            .execute()

        val body = """
            {"title":"patched"}
        """.trimIndent()

        val expect = """
            {"id":1,"title":"patched","publishedAt":"${localDateNow}"}
        """.trimIndent()

        mockMvc
            .patch("/books/1") {
                contentType = MediaType.APPLICATION_JSON
                content = body
            }
            .andExpect {
                status {
                    isNoContent()
                }
            }

        mockMvc
            .get("/books/1")
            .andExpect {
                status {
                    isOk()
                }
            }
            .andExpect {
                content {
                    json(expect)
                }
            }
    }

    @Test
    fun updateNotFound() {
        dsl.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.NAME)
            .values(1, "Author1")
            .execute()

        val localDateNow = LocalDate.now()

        val insertedBookId = dsl.insertInto(BOOK, BOOK.ID, BOOK.TITLE, BOOK.PUBLISHED_AT, BOOK.AUTHOR_ID)
            .values(1, "sample", localDateNow, 1)
            .returningResult(BOOK.ID)
            .fetchOne()!!
            .map {
                it.getValue(BOOK.ID)!!
            }

        val body = """
            {"title":"patched"}
        """.trimIndent()

        mockMvc
            .patch("/books/${insertedBookId+1}") {
                contentType = MediaType.APPLICATION_JSON
                content = body
            }
            .andExpect {
                status {
                    isNotFound()
                }
            }
    }
}