package books.api.controllers

import books.api.entities.Author
import books.api.errors.DuplicateException
import books.api.services.AuthorColumn
import books.api.services.AuthorService
import books.api.services.BookService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Positive

data class RegisterAuthorParam(
    @Max(255)
    @NotBlank
    val name: String
)

data class PatchAuthorParam(
    @Max(255)
    val name: String?
)

@Validated
@RestController
@RequestMapping("/authors")
class AuthorController(
    private val authorService: AuthorService,
    private val bookService: BookService
) {
    companion object {
        private val sortParamToAuthorColumn = mapOf(
            "id" to AuthorColumn.ID,
            "name" to AuthorColumn.NAME
        )
    }

    @GetMapping
    fun search(
        @RequestParam(name = "name", defaultValue = "") name : String,
        @RequestParam(name = "sort", defaultValue = "") sort : String,
        @Valid @Positive @RequestParam(name = "offset", defaultValue = "0") offset: Int,
        @Valid @Positive @Max(10) @RequestParam(name = "limit", defaultValue =  "10") limit: Int
    ) : ResponseEntity<List<Author>> {
        val sortMap = sort.toSortParamMapWith(sortParamToAuthorColumn)
        return ResponseEntity.ok(
            authorService.search(
                name    = name,
                sortMap = sortMap,
                offset  = offset,
                limit   = limit
            )
        )
    }

    @PostMapping
    fun register(@RequestBody @Validated param : RegisterAuthorParam) : ResponseEntity<Any> {
        try {
            val created = authorService.register(name = param.name)
            return ResponseEntity(created, HttpStatus.CREATED)
        }
        catch(e: DuplicateException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    @GetMapping("/{id}")
    fun show(@PathVariable("id") id : Int) = authorService.findById(id) ?: ResponseEntity.notFound().build<Author>()


    @PatchMapping("/{id}")
    fun update(@PathVariable("id") id : Int, @RequestBody @Validated param : PatchAuthorParam): ResponseEntity<Int> {
        val author = authorService.findById(id)
        if(author === null) {
            return ResponseEntity.notFound().build()
        }
        val targetAuthor = Author(
            author.id,
            param.name ?: author.name
        )
        authorService.update(targetAuthor)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/books")
    fun searchBooks(@PathVariable("id") id : Int) : ResponseEntity<Any> {
        val author = authorService.findById(id)
        if(author === null) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(bookService.search(authorId = id))
    }
}