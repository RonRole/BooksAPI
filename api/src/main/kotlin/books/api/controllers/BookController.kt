package books.api.controllers

import books.api.entities.Book
import books.api.errors.DuplicateException
import books.api.services.BookColumn
import books.api.services.BookService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.*
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import javax.validation.Valid
import javax.validation.constraints.*

data class RegisterBookParam(
    @Max(255)
    @NotBlank
    val title: String,
    @NotBlank
    val authorId: Int,
    @NotBlank
    val publishedAt: LocalDate
)

data class PatchBookParam(
    @Max(255)
    val title: String?,
    val authorId: Int?,
    val publishedAt: LocalDate?
)

@Validated
@RestController
@RequestMapping("/books")
class BookController (
    private val service: BookService
) {
    companion object {
        private val sortParamToBookColumn = mapOf(
            "id" to BookColumn.ID,
            "title" to BookColumn.TITLE,
            "author-id" to BookColumn.AUTHOR_ID,
            "published-at" to BookColumn.PUBLISHED_AT
        )
    }
     @GetMapping
     fun search(
         @RequestParam(name = "title", defaultValue = "") title : String,
         @RequestParam(name = "author-id", defaultValue = "") authorId : Int?,
         @RequestParam(name = "published-from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") publishedFrom : LocalDate?,
         @RequestParam(name = "published-to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") publishedTo : LocalDate?,
         @RequestParam(name = "sort", defaultValue = "") sort : String,
         @Valid @Positive @RequestParam(name = "offset", defaultValue = "0") offset: Int,
         @Valid @Positive @Max(10) @RequestParam(name = "limit", defaultValue = "10") limit : Int
     ) : ResponseEntity<List<Book>> {
         // sortParamをMap<BookColumn, Boolean>に変換する
         // マイナスのついたパラメータはdesc, そうでなければasc
         val sortMap = sort.toSortParamMapWith(sortParamToBookColumn)
         return ResponseEntity.ok(service.search(
             title   = title,
             authorId= authorId,
             publishedFrom = publishedFrom,
             publishedTo = publishedTo,
             sortMap = sortMap,
             offset  = offset,
             limit   = limit
         ))
     }

    @PostMapping
    fun register(@RequestBody @Validated param : RegisterBookParam) : ResponseEntity<Any> {
        try {
            val created = service.register(title = param.title, authorId = param.authorId, publishedAt = param.publishedAt)
            return ResponseEntity(created, HttpStatus.CREATED)
        }
        catch(e: DuplicateException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable("id") id: Int) = ResponseEntity.ofNullable(service.findById(id))

    @PatchMapping("/{id}")
    fun update(@PathVariable("id") id:Int, @RequestBody @Validated param: PatchBookParam): ResponseEntity<Any> {
        val book = service.findById(id)
        if(book === null) {
            return ResponseEntity.notFound().build()
        }
        val targetBook = Book(
            book.id,
            param.title ?: book.title,
            param.authorId ?: book.authorId,
            publishedAt = param.publishedAt ?: book.publishedAt
        )
        service.update(targetBook)
        return ResponseEntity.noContent().build()
    }
}