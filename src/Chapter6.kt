// 6.1 null許容性の区別
fun strLen(s: String) = s.length
/*
>>> strLen(null)
error: null can not be a value of a non-null type String
strLen(null)
*/

// 大事なのはnullでないかをチェックすること
fun strLenSafe(s: String?): Int =
        if (s != null) s. length else 0
/*
>>> val x: String? = null
>>> println(strLenSafe(x))
0
>>> println(strLenSafe("abc"))
3
 */

// 安全呼び出し演算子(safe-call operator) 「?.」 nullチェックとメソッド呼び出しを1つにしたもの
fun printAllCaps(s: String?) {
    val allCaps: String? = s?.toUpperCase()
    println(allCaps)
}
/*
>>> printAllCaps("abc")
ABC
>>> printAllCaps(null)
null
 */

class Employee(val name: String, val manager: Employee?)

fun managerName(employee: Employee): String? = employee.manager?.name
/*
>>> println(managerName(developer))
Da Boss
>>> println(managerName(ceo))
null
 */

// エルビス演算子 「?:」 nullの代わりにデフォルト値を返す
fun foo(s: String?) {
    val t: String = s ?: "" // nullなら空文字
}
fun strLenSafe3(s: String?): Int = s?.length ?: 0
/*
>>> println(strLenSafe3("abc"))
3
>>> println(strLenSafe3(null))
0
 */

fun printshippingLabel(person: Person) {
    val address = person.company?.address ?: throw IllegalArgumentException("No address")
    with (address) {
        println(streetAddress)
        println("$zipCode $city, $country")
    }
}

// 安全キャスト演算子 「as?」 指定された型に値をキャストしようとし、型が違えばnullを返す
class Person(val firstName: String, val lastName: String) {
    override fun equals(o: Any?): Boolean {
        val otherPerson = o as? Person ?: return false
        return otherPerson.firstName == firstName &&
                otherPerson.lastName == lastName // person型にスマートキャストされている
    }
}

// 非null表明 「!!」 値をnull非許容型に変換する(nullの場合は例外)
// 重複したnullチェックの代わりなどに使える
fun ignoreNulls(s: String?) {
    val sNotNull: String = s!! // ここで例外がスローされる
    println(sNotNull.length)
}
/*
>>> ignoreNulls(null)
kotlin.KotlinNullPointerException
        at Line_3.ignoreNulls(Line_3.kts:2)
 */

class CopyRowAction(val list: JList<String>) : AbstractActio() {
    override fun isEnabled(): Boolean =
            list.selectedValue != null

    override fun actionPerformed(e: ActionEvent) {
        val value = list.selectedValue!!

    }
}

// let関数 呼び出されたオブジェクトをラムだの引数へと変換する
fun sendEmailTo(email: String) {
    println("Sending email to $email")
}
/*
>>> var email: String? = "test@gmail.com"

null非許容型にnull許容型を引数で渡そうとするとエラーになる
>>> sendEmailTo(email)
error: type mismatch: inferred type is String? but String was expected

>>> email?.let { sendEmailTo(it) }
Sending email to test@gmail.com
 */

// 遅延初期化プロパティ lateinit 一般的に依存性の注入として使われる
class MyService {
    fun performAction(): String = "foo"
}

class MyTest {
    private lateinit var myService: MyService

    // lateinitを使用しない場合には下記のようになる
    // private var myService: MyService? = null

    // some code
}

// null許容型の拡張関数 nullでもメソッドが呼び出されることを許容する
fun verifyUserInput(input: String?) {
    if (input.isNullOrBlank()) {
        println("...")
    }
}
/*
>>> verifyUserInput(" ")
...
>>> verifyUserInput(null)
...
*/

fun <T> printHashCode(t: T) {
    println(t?.hashCode())
}
/*
>>> printHashCode(null)
null
 */

fun <T: Any> printHashCode2(t: T) {
    println(t.hashCode())
}
/*
>>> printHashCode2(null)
error: type parameter bound for T in fun <T : Any> printHashCode2(t: T): Unit
 is not satisfied: inferred type Nothing? is not a subtype of Any
printHashCode2(null)
^
 */


// プリミティブ型と基本的な型
// kotlinではプリミティブ型とラッパークラスを区別しない

val i = 1
val l: Long = i.toLong() // 明示的に変換する必要がある

// Unitは戻り値がない関数の戻り値の型として使える
interface Processor<T> {
    fun process(): T
}

class NoResultProcessor : Processor<Unit> {
    override fun process() {
        // do stuff
    }
}

// 値を返さない
fun fail(message: String): Nothing {
    throw IllegalStateException(message)
}
/*
>>> fail("Error occurred")
java.lang.IllegalStateException: Error occurred
        at Line_4.fail(Unknown Source)
 */

fun addValidNumbers(numbers: List<Int?>) {
    var sumOfValidNumbers = 0
    var invalidNumbers = 0
    for (number in numbers) {
        if (number != null) {
            sumOfValidNumbers += number
        } else {
            invalidNumbers++
        }
    }
    println("Sum of valid numbers: $sumOfValidNumbers")
    println("Invalid numbers: $invalidNumbers")
}

fun addValidNumbers2(numbers: List<Int?>) {
    val validNumbers = numbers.filterNotNull() // nullの要素が含まれていないことが保障されるので、validNumbersはList<Int>となる
    println("Sum of valid numbers: ${validNumbers.sum()}")
    println("Invalid numbers: ${numbers.size - validNumbers.size}")
}

// どちらを読み取り、どちらを変更するのかがすぐにわかる
fun <T> copyElements(source: Collection<T>, target: MutableCollection<T>) {
    for (item in source) {
        target.add(item)
    }
}

/*
>>> val letters = Array<String>(26) { i -> ('a' + i).toString() }
>>> println(letters.joinToString(""))
abcdefghijklmnopqrstuvwxyz
>>> val strings = listOf("a", "b", "c")
>>> println("%s%s%s".format(*strings.toTypedArray()))
abc

>>> val squares = IntArray(5) { i -> (i+1) * (i+1) }
>>> println(squares.joinToString())
1, 4, 9, 16, 25
 */
