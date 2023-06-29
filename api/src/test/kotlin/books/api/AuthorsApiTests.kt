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
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import java.time.LocalDate
import kotlin.math.exp

@SpringBootTest
@AutoConfigureMockMvc
class AuthorsApiTests @Autowired constructor(
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
    fun searchAuthorsWithNoParameters() {
        dsl.insertInto(
            AUTHOR,
            AUTHOR.ID,
            AUTHOR.NAME
        )
            .values(1,"author1")
            .values(2,"author2")
            .values(3,"author3")
            .execute()

        val expect = """
            [
                {"id":1,"name":"author1"},
                {"id":2,"name":"author2"},
                {"id":3,"name":"author3"}
            ]
        """.trimIndent()

        mockMvc
            .get("/authors")
            .andExpect { status { isOk() } }
            .andExpect {
                content {
                    json(expect)
                }
            }
    }

    @Test
    fun searchAuthorsWithName() {
        dsl.insertInto(
            AUTHOR,
            AUTHOR.ID,
            AUTHOR.NAME
        )
            .values(1, "target1")
            .values(2, "target2")
            .values(3, "except")
            .execute()

        val expect = """
            [
                {"id":1,"name":"target1"},
                {"id":2,"name":"target2"}
            ]
        """.trimIndent()

        mockMvc
            .get("/authors?name=target")
            .andExpect { status { isOk() } }
            .andExpect {
                content {
                    json(expect)
                }
            }
    }

    @Test
    fun searchAuthorsWithSorting() {
        dsl.insertInto(
            AUTHOR,
            AUTHOR.ID,
            AUTHOR.NAME
        )
            .values(1, "target1")
            .values(2, "target2")
            .values(3, "except")
            .execute()

        val expect = """
            [
                {"id":3,"name":"except"},
                {"id":2,"name":"target2"},
                {"id":1,"name":"target1"}
            ]
        """.trimIndent()

        mockMvc
            .get("/authors?sort=-id")
            .andExpect { status { isOk() } }
            .andExpect {
                content {
                    json(expect)
                }
            }
    }

    @Test
    fun searchAuthorsWithLimit() {
        dsl.insertInto(
            AUTHOR,
            AUTHOR.ID,
            AUTHOR.NAME
        )
            .values(1, "target1")
            .values(2, "target2")
            .values(3, "except")
            .execute()

        val expect = """
            [
                {"id":1,"name":"target1"},
                {"id":2,"name":"target2"}
            ]
        """.trimIndent()

        mockMvc
            .get("/authors?limit=2")
            .andExpect { status { isOk() } }
            .andExpect {
                content {
                    json(expect)
                }
            }
    }

    @Test
    fun searchAuthorsWithOffset() {
        dsl.insertInto(
            AUTHOR,
            AUTHOR.ID,
            AUTHOR.NAME
        )
            .values(1, "target1")
            .values(2, "target2")
            .values(3, "except")
            .execute()

        val expect = """
            [
                {"id":2,"name":"target2"},
                {"id":3,"name":"except"}
            ]
        """.trimIndent()

        mockMvc
            .get("/authors?offset=1")
            .andExpect { status { isOk() } }
            .andExpect {
                content {
                    json(expect)
                }
            }
    }

    @Test
    fun findByAuthorId() {
        dsl.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.NAME)
            .values(1, "author1")
            .execute()

        val expect = """
            {"id":1, "name":"author1"}
        """.trimIndent()

        mockMvc
            .get("/authors/1")
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
    fun findByAuthorIdNotFound() {
        mockMvc
            .get("/authors/12")
            .andExpect {
                status {
                    isNotFound()
                }
            }
    }

    @Test
    fun registerAuthor() {
        val body = """
            {"name":"author1"}
        """.trimIndent()
        val expect = """
            {"name":"author1"}
        """.trimIndent()

        mockMvc
            .post("/authors") {
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
    fun registerDuplicateAuthor() {
        dsl.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.NAME)
            .values(1,"author1")
            .execute()

        val body = """
            {"name":"author1"}
        """.trimIndent()

        mockMvc
            .post("/authors") {
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
    fun updateAuthor() {
        dsl.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.NAME)
            .values(1, "author1")
            .execute()

        val body = """
            {"name":"patched"}
        """.trimIndent()

        val expect = """
            {"id":1,"name":"patched"}
        """.trimIndent()

        mockMvc
            .patch("/authors/1") {
                contentType = MediaType.APPLICATION_JSON
                content = body
            }
            .andExpect {
                status {
                    isNoContent()
                }
            }

        mockMvc
            .get("/authors/1")
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

        @Test
        fun updateNotFound() {
            val body = """
                {"name":"patched"}
            """.trimIndent()

            mockMvc
                .patch("/authors/12") {
                    contentType = MediaType.APPLICATION_JSON
                    content = body
                }
                .andExpect {
                    status {
                        isNotFound()
                    }
                }
        }

        @Test
        fun getAuthorsBooks() {
            dsl.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.NAME)
                .values(1, "author1")
                .execute()

            val localDateNow = LocalDate.now()

            dsl.insertInto(BOOK, BOOK.ID, BOOK.TITLE, BOOK.PUBLISHED_AT, BOOK.AUTHOR_ID)
                .values(1, "book1", localDateNow, 1)
                .values(2, "book2", localDateNow, 1)
                .values(3, "book3", localDateNow, 1)
                .execute()

            val expect = """
                {"id":1,"title":"book1","publisedAt":"${localDateNow}"},
                {"id":2,"title":"book2","publisedAt":"${localDateNow}"},
                {"id":3,"title":"book3","publisedAt":"${localDateNow}"}
            """.trimIndent()

            mockMvc
                .get("/authors/1/books")
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
    }
}