import kotlin.reflect.KClass

// ジェネリック型パラメータ
val authors = listOf("Dmitry", "Svetlana")
val readers = mutableListOf<String>(/* ... */)

fun <T> List<T>.filter(predicate: (T) -> Boolean): List<T>

val <T> List<T>.penultimate: T // 任意のリストで呼び出し可能
    get() = this[size - 2]

interface List2<T> {
    operator fun get(index: Int): T
}

class StringList: List2<String> {
    override fun get(index: Int): String = "test"
}

class ArrayList<T> : List2<T> {
    override fun get(index: Int): T = // some code
}

interface Comparable<T> {
    fun compareTo(other: T): Int
}

class String : Comparable<String> {
    override fun compareTo(other: String): Int = /* some code */
}

// 型パラメータ制約
fun <T : Number> oneHalf(value: T): Double {
    return value.toDouble() / 2.0
}
/*
>>> println(oneHalf(3))
1.5
 */

fun <T : Comparable<T>> max(first: T, second: T): T {
    return if (first > second) first else second
}
/*
>>> println(max("kotlin", 42))
error: type parameter bound for T in fun <T : Comparable<T>> max(first: T, second: T): T
 is not satisfied: inferred type Any is not a subtype of Comparable<Any>
println(max("kotlin", 42))
 */

// CharSequenceとAppendableを必ず実装するよう指定する
fun <T> ensureTrailingPeriod(seq: T) where T : CharSequence, T : Appendable {
    if (!seq.endsWith('.')) {
        seq.append('.')
    }
}
/*
>>> val helloWorld = StringBuilder("Hello World")
>>> ensureTrailingPeriod(helloWorld)
>>> println(helloWorld)
Hello World.
 */

// null非許容型
class Processor<T : Any> {
    fun process(value: T) {
        value.hashCode()
    }
}
/*
>>> val nullableStringProcessor = Processor<String?>()
error: type argument is not within its bounds: should be subtype of 'Any'
val nullableStringProcessor = Processor<String?>()
 */


// 9.2 型消去と具象系パラメータ
// ジェネリクスは実行時に消去されるため、このような判定ができない
if (value is List<String>) { ... }

// スター投影
fun printSum(c: Collection<*>) {
    val intList = c as? List<Int> ?: throw IllegalArgumentException("List is expected")
    println(intList.sum())
}
/*
>>> printSum(listOf(1,2,3))
6
>>> printSum(setOf(1,2,3))
java.lang.IllegalArgumentException: List is expected
        at Line_26.printSum(Line_26.kts:2)

List<Int>かどうかはチェックできないため、sumが呼び出されてその中で例外がスローされている
>>> printSum(listOf("a","b"))
java.lang.ClassCastException: java.base/java.lang.String cannot be cast to java.base/java.lang.Number
        at kotlin.collections.CollectionsKt___CollectionsKt.sumOfInt(_Collections.kt:2172)
        at Line_26.printSum(Line_26.kts:3)
 */

fun printSum2(c: Collection<Int>) {
    if (c is List<Int>) { // 明示的に書かれている場合には可能
        println(c.sum())
    }
}

// inlineを使用すると実行時に実際の型引数を参照できる
inline fun <reified T> isA(value: Any) = value is T

/*
型引数は実行時にわかっている例
>>> val item = listOf("one", 2, "three")
>>> println(item.filterIsInstance<String>())
[one, three]
*/

// reifiedは型パラメータが実行時に消去されないことを宣言している
inline fun <reified T> Iterable<*>.filterIsInstance(): List<T> {
    val destination = mutableListOf<T>()
    for (element in this) { // 型引数のクラスのインスタンスかどうかを判定
        if (element is T) {
            destination.add(element)
        }
    }
    return destination
}


// 9.3 変位
// 基本となる型が同じであっても型引数が異なるような方同士が相互にどのように関連しているのかを表現する

// クラスが特定の型パラメータについて共変であると宣言するうためにoutキーワードを使用する
interface Producer<out T> {
    fun produce(): T
}

open class Animal {
    fun feed() { /*...*/ }
}

class Herd<T : Animal> {
    val size: Int get() = 1
    operator fun get(i: Int): T { /*...*/ }
}

fun feedAll(animals: Herd<Animal>) {
    for (i in 0 until animals.size) {
        animals[i].feed()
    }
}

class Cat : Animal {
    fun cleanLitter() {
        /**/
    }
}

fun takeCareOfCats(cats: Herd<Cat>) {
    for (i in 0 until cats.size) {
        cats[i].cleanLitter()
    }
    feedAll(cats)
}

interface MutableList<T> : List<T>, MutableCollection<T> {
    override fun add(element: T): Boolean
}


interface Comparator<in T> {
    fun compare(e1: T, e2: T): Int { /*...*/ }
}


fun <T> copyData(source: MutableList<T>, destination: MutableList<T>) {
    for (item in source) {
        destination.add(item)
    }
}

fun <T> copyData2(source: MutableList<T>, destination: MutableList<in T>) {
    for (item in source) {
        destination.add(item)
    }
}

interface FieldValidator<in T> {
    fun validate(input: T): Boolean
}

object DefaultStringValidator : FieldValidator<String> {
    override fun validate(input: String) = input.isNotEmpty()
}

object DefaultIntValidator : FieldValidator<Int> {
    override override fun validate(input: Int) = input >= 0
}

object Validators {
    private val validators = mutableMapOf<KClass<*>, FieldValidator<*>>()
    fun <T : Any> registerValidator(kClass: KClass<T>, fieldValidator: FieldValidator<T>) {
        validators[kClass] = fieldValidator
    }

    @Suppress("UNCHECKD_CAST")
    operator fun <T: Any> get(kClass: KClass<T>): FieldValidator<T> =
            validators[kClass] as? FieldValidator<T>
            ?: throw IllegalArgumentException("No validator for ${kClass.simpleName}")
}
