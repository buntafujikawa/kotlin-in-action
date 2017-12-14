import java.io.BufferedReader
import java.io.FileReader
import java.util.concurrent.locks.Lock

// 高階関数の宣言

var canReturnNull: (Int, Int) -> Int? = { null }
var funOrNull: ((Int, Int) -> Int)? = null

fun twoAndThree(operation: (Int, Int) -> Int) { // 関数型の引数の宣言
    val result = operation(2, 3) // 関数型の引数の呼び出し
    println("The result is $result")
}
/*
>>> twoAndThree { a, b -> a + b }
The result is 5
>>> twoAndThree { a, b -> a * b }
The result is 6
 */

fun String.filter(predicate: (Char) -> Boolean): String {
    val sb = StringBuilder()
    for (index in 0 until length) {
        val element = get(index)
        if (predicate(element)) sb.append(element)
    }
    return sb.toString()
}
/*
>>> println("ab1c".filter { it in 'a'..'z' })
abc
 */

enum class Delivery { STANDARD, EXPEDITED }

class Order(val itemCount: Int)

fun getShippingCostCalculator(delivery: Delivery): (Order) -> Double {
    if (delivery == Delivery.EXPEDITED) {
        return { order -> 6 + 2.1 * order.itemCount }
    }
    return { order -> 1.2 * order.itemCount }
}
/*
>>> val calculator = getShippingCostCalculator(Delivery.EXPEDITED)
>>> println("Shipping costs ${calculator(Order(3))}")
Shipping costs 12.3
 */


class ContactListFilters {
    var prefix: String = ""
    var onlyWithPhoneNumber: Boolean = false
}

data class Person(val firstName: String, val lastName: String, val phoneNumber: String?)

class contactListFilters {
    var prefix: String = ""
    var onlyWithPhoneNumber: Boolean = false

    fun getPredicate(): (Person) -> Boolean {
        val startsWithPrefix = { p: Person -> p.firstName.startsWith(prefix) || p.lastName.startsWith(prefix)}
        if (!onlyWithPhoneNumber) {
            return startsWithPrefix
        }
        return { startsWithPrefix(it) && it.phoneNumber != null }
    }
}

data class SiteVisit(val path: String, val duration: Double, val os: OS)

enum class OS { WINDOWS, LINUX, MAC ,IOS, ANDROID }

val log = listOf(
        SiteVisit("/", 34.0, OS.WINDOWS),
        SiteVisit("/", 22.0, OS.MAC),
        SiteVisit("/login", 12.0, OS.WINDOWS),
        SiteVisit("/signup", 8.0, OS.IOS),
        SiteVisit("/", 26.3, OS.ANDROID)
)

val averageWindowsDuration = log
        .filter { it.os == OS.WINDOWS }
        .map(SiteVisit::duration)
        .average()

/*
>>> println(averageWindowsDuration)
23.0
*/

fun List<SiteVisit>.averageDurationFor(os: OS) = filter { it.os == os }.map(SiteVisit::duration).average()
/*
>>> println(log.averageDurationFor(OS.WINDOWS))
23.0
>>> println(log.averageDurationFor(OS.MAC))
22.0
 */

// ストラテジーパターンなんかもこれで表現できる
fun List<SiteVisit>.averageDurationFor2(predicate: (SiteVisit) -> Boolean) =
        filter(predicate).map(SiteVisit::duration).average()
/*
>>> println(log.averageDurationFor2 { it.os in setOf(OS.ANDROID, OS.IOS) })
17.15
>>>
>>> println(log.averageDurationFor2 { it.os == OS.IOS && it.path == "/signup" })
8.0
 */

// 8.2　インライン関数
inline fun <T> synchronized(lock: Lock, action: () -> T): T {
    lock.lock()
    try {
        return action()
    }
    finally {
        lock.unlock()
    }
}

val l = Lock()
synchronized(l) {
    // some code
}

fun foo(l: Lock) {
    println("Before sync")
    synchronized(l) {
        println("Action")
    }
    println("After sync")
}

fun <T, R> Sequence<T>.map(transform: (T) -> R): Sequence<R> {
    return TransformingSequence(this, transform)
}

// 非インラインラムダを受け入れる引数にはnoinlie修飾子をマークできる
inline fun foo(inlined: () -> Unit, noinline notInlined: () -> Unit) {
    // some code
}

fun readFirstLineFile(path: String): String {
    BufferedReader(FileReader(path)).use { br -> return br.readLine() } // 行を関数から返す
}


// 8.3 高階関数における制御フロー

// 非ローカルリターン
fun lookForAlice(people: List<Person>) {
    people.forEach {
        if (it.name == "Alice") {
            println("Found!")
            return // 関数からreturn
        }
    }
    println("Alice is not found")
}

// ラムダ式からのローカルリターン
fun lookForAlice2(people: List<Person>) {
    people.forEach label@{
        if (it.name == "Alice") return@label // 任意の名前
    }
    println("Alice might be somewhere") // この行は常に出力
}

// 無名関数 名前と引数の型が省略
// funキーワードを使って宣言された最も近い関数からリターンする
fun lookForAlice3(people: List<Person>) {
    people.forEach(fun(person) {
        if (person.name == "Alice") return
        println("${person.name} is not alice")
    })
}
