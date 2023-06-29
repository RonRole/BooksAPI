package books.api.errors

import java.lang.IllegalArgumentException

/**
 * レコードの重複を表す例外
 */
class DuplicateException(s: String?) : IllegalArgumentException(s)