package books.api.controllers

import org.springframework.http.ResponseEntity

/**
 * ソート順のパラメータを変換する
 * 変換後は引数mapの形式
 * "-"から始まるパラメータはfalseに、そうでなければtrueにする
 *
 * 例
 * sortParam:"-id,title"
 * map:{id to SomethingA : T, title to SomethingB : T}
 * -> {SomethingA to false, SomethingB to true}
 */
fun <T> String.toSortParamMapWith(map: Map<String, T>) : Map<T, Boolean> {
    return this
        .split(",")
        .filter { it.isNotBlank() }
        .filter { map.containsKey(it) || map.containsKey(it.substring(1)) }
        .associate {
            if (it.startsWith("-")) {
                map[it.substring(1)]!! to false
            } else {
                map[it]!! to true
            } }
}