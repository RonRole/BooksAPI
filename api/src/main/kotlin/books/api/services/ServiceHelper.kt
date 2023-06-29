package books.api.services

import org.jooq.Condition
import org.jooq.Record
import org.jooq.TableField
import org.jooq.impl.DSL

interface Column<R : Record> {
    val tableField : TableField<R, *>
}

/**
 * Where句用
 * Stringを拡張
 *
 * emptyの時、trueConditionにする
 * そうでなければ、引数を評価する
 */
fun String.toTrueConditionIfEmpty(onNotEmpty : (target: String) -> Condition) : Condition {
    if(this.isNullOrEmpty()) {
        return DSL.trueCondition()
    }
    return onNotEmpty(this)
}

fun <T> T.toTrueConditionIfNull(onNotNull : (target : T) -> Condition) : Condition {
    if(this === null) {
        return DSL.trueCondition()
    }
    return onNotNull(this)
}

/**
 * Where句用
 * Stringを拡張
 *
 * like句のワイルドカード(%,_,\)と被るものをエスケープして
 * like句で使えるようにする
 */
fun String.escapeForLike() = this.replace(Regex("[\\_%]")) { "\\${it.value}" }

/**
 * Order By句用
 * Map<T : Column, Boolean>を拡張
 *
 * Tのインスタンスtがtrueならt.asc()を、falseならt.desc()に変換し、
 * List<T>として返却する
 *
 * ColumnとBoolean型の表からOrderBy句の中を作成するfunctionを用意することで、
 * より簡易にOrderBy句を作成することが狙い
 */
fun <T: Column<*>> Map<T, Boolean>.toOrderByCondition() = this.map { if(it.value) it.key.tableField.asc() else it.key.tableField.desc() }