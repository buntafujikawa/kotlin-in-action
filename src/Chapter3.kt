// 3.1 コレクションの生成
val set = hashSetOf(1, 7, 53)
val list = arrayListOf(1, 7, 53)
val map = hashMapOf(1 to "one", 7 to "seven", 53 to "fifty-three")


// 3.3 拡張関数と拡張プロパティ(拡張関数は静的メソッドのシンタックスシュガー)

// この例だとStringがレシーバ型で、"Kotlin"がレシーバオブジェクト
fun String.lastChar(): Char = this.get(this.length - 1)
// println("Kotlin".lastChar())n


fun <T> Collection<T>.joinToString(
        separator: String = ", ",
        prefix: String = "",
        postfix: String = ""
): String {
    val result = StringBuilder(prefix)

    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }

    result.append(postfix)
    return result.toString()
}

val String.lastChar: Char
    get() = get(length - 1)

// 3.4 コレクションの扱い
fun listOf<T>(vararg values: T): List<T> {} //任意の数の引数を渡すことができる

fun main(args: Array<String>) {
    val list = listOf("args: ", *args) // スプレッド演算子、配列の全要素が呼び出される
    println(list)
}

// 中置呼び出し(infix call)/
val map = mapOf(1 to "one", 7 to "seven")

infix fun Any.to(other: Any) = Pair(this, other)

// 分解宣言
val (number, name) = 1 to "one"

// mapOfは可変長の引数を受け取るが、引数はキーと値のペアとする
fun <K, V> mapOf(vararg values: Pair<K, V>): Map<K, V>

// 3.5 文字列と正規表現

/*
明示的に正規表現を生成する
>>> println("12.345-6.A".split("\\.|-".toRegex()))
[12, 345, 6, A]

>>> println("12.345-6.A".split(".", "-"))
[12, 345, 6, A]
*/


fun parsePath(path: String) {
    val directory = path.substringBeforeLast("/")
    val fullName = path.substringAfterLast("/")

    val fileName = fullName.substringBeforeLast(".")
    val extension = fullName.substringAfterLast(".")

    println("Dir: $directory, name: $fileName, ext: $extension")
}
//>>> parsePath("/Users/yole/kotlin-book/chapter.adoc")
//Dir: /Users/yole/kotlin-book, name: chapter, ext: adoc

/*
メモ
トリプルクオート文字列はいかなる文字もエスケープする必要がない
改行を含むテキストを埋め込むことが簡単にできる
 */
fun parsePath2(path: String) {
    val regex = """(.+)/(.+)\.(.+)""".toRegex()
    val matchResult = regex.matchEntire(path)
    if (matchResult != null) {
        val (directory, fileName, extension) = matchResult.destructured
        println("Dir: $directory, name: $fileName, ext: $extension")
    }
}
//>>> parsePath2("/Users/yole/kotlin-book/chapter.adoc")
//Dir: /Users/yole/kotlin-book, name: chapter, ext: adoc

/*
>>> val kotlinLogo = """| //
...                    .|//
...                    .|/ \"""
>>> println(kotlinLogo.trimMargin("."))
| //
|//
|/ \
*/

// 3.6 ローカル関数と拡張
class User(val id: Int, val name: String, val address: String)

fun User.validateBeforeSave() {
    fun validate(value: String, fieldName: String) {
        if (value.isEmpty()) {
            throw IllegalArgumentException(
                    "Can't save user $id: empty $fieldName")
        }
    }

    validate(name, "Name")
    validate(address, "Address")
}

fun saveUser(user: User) {
    user.validateBeforeSave()

    // save ...
}
