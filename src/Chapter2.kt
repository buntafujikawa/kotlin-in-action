// スターインポート
import java.io.BufferedReader
import java.util.*

fun main(args: Array<String>) {
    println("Hello, world!")
    println(max(1, 2))

    val name = if (args.size > 0) args[0] else "Kotlin"
    println("Hello, $name!")

    if (args.size > 0) {
        println("Hello, ${args[0]}!")
    }

    println("Hello, ${if (args.size > 0) args[0] else "someone"}!")
}

fun max(a: Int, b: Int): Int {
    // ifは式ではなく文であるため、別の式の一部として使用できる
    return if (a > b) a else b
}

// 戻り値を省略するとさらに短く書ける(式本体の関数のみ)
fun max2(a: Int, b: Int) = if (a > b) a else b

val answer: Int = 42
val yearsToCompute = 7.5e6 // これはDouble型と推論してくれる


// publicがデフォルト
// 実装コードがないクラスは値オブジェクトと呼ばれる
class Person(val name: String)

class Rectangle(val height: Int, val width: Int) {
    val isSquare: Boolean
        get() { // プロパティのgetter
            return height == width
        }
}

fun createRandomReatangle(): Rectangle {
    val random = Random()
    return Rectangle(random.nextInt(), random.nextInt())
}

// enumとwhen(switchの代わり)
// enumはclassの前に置いたときだけ特別な意味を持つ
enum class Color(
        val r: Int, val g: Int, val b: Int
) {
    RED(255, 0, 0), ORANGE(255, 165, 0), YELLOW(255, 255, 0); // kotlinの中で唯一セミコロンが必要

    fun rgb() = (r * 256 + g) * 256 + b
}

fun getMnemonic(color: Color) =
        when (color) {
            Color.RED -> "Richard"
            Color.ORANGE -> "Of"
            Color.YELLOW -> "York"
        }

fun mix(c1: Color, c2: Color) =
        when (setOf(c1, c2)) {
            setOf(Color.RED, Color.YELLOW) -> Color.ORANGE // 混ぜることのできる色のペアを列挙
            else -> throw Exception("Dirty color")
        }

// スマートキャスト
interface Expr

class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr

// ifで書かないでこんな感じで書くのがkotlinっぽいらしい
fun eval(e: Expr): Int =
        when (e) {
            is Num -> // isで目的の型かどうかを判定できる
                e.value
            is Sum ->
                eval(e.right) + eval(e.left)
            else ->
                throw IllegalArgumentException("Unknown expression")
        }

// ブロック内で最後の式が結果になる(すべての状況で適用される)
fun evalWithLoggin(e: Expr): Int =
        when (e) {
            is Num -> {
                println("num: ${e.value}")
                e.value // 最後の式
            }
            is Sum -> {
                val left = evalWithLoggin(e.left)
                val right = evalWithLoggin(e.right)
                println("sum: $left + $right")
                left + right // 最後の式
            }
            else -> throw IllegalArgumentException("Unknown expression")
        }


// 2.4 whileとfor
fun fizzBuzz(i: Int) = when {
    i % 15 == 0 -> "FizzBuzz "
    i % 3 == 0 -> "Fizz "
    i % 5 == 0 -> "Buzz "
    else -> "$i "
}
/*
kotlinのレンジは包括的で、2つ目の値が常にレンジに含まれる
for (i in 1..100) {
    print(fizzBuzz(i))
}

for (i in 100 downTo 1 step 2) {
        print(fizzBuzz(i))
    }
*/

// マップ https://qiita.com/opengl-8080/items/36351dca891b6d9c9687
fun test() {
    val binaryReps = TreeMap<Char, String>()

    for (c in 'A'..'F') { // AからFまでの文字で処理を行う
        val binary = Integer.toBinaryString(c.toInt())
        binaryReps[c] = binary
    }

    for ((letter, binary) in binaryReps) { // キーと値を2つの変数に割りあてる
        println("$letter = $binary")
    }

    val list = arrayListOf("10", "11", "1001")
    for ((index, element) in list.withIndex()) { // インデックス付きで繰り返し処理をする
        println("$index: $element")
    }
}

fun isLetter(c: Char) = c in 'a'..'z' || c in 'A'..'Z'
fun isNotDigit(c: Char) = c !in '0'..'9'
fun recognize(c: Char) = when (c) {
    in '0'..'9' -> "It's a digit!"
    in 'a'..'z', in 'A'..'Z' -> "It's a letter!"
    else -> "I don't know..."
}

// 例外
// https://qiita.com/k5n/items/6fe586bf2e5530684681#%E4%BE%8B%E5%A4%96
// 明示的にthrowを書く必要がない
fun readNumber(reader: BufferedReader): Int? {
    try {
        val line = reader.readLine()
        return Integer.parseInt(line)
    } catch (e: NumberFormatException) {
        return null
    } finally {
        reader.close() // 例外が起こっても起こらなくても実行される
    }

    // tryも式として使用される
    val number = try {
        Integer.parseInt(reader.readLine())
    } catch (e: NumberFormatException) {
        return
    }

    println(number)

    // catch節でも値を返すことができる
    val number2 = try {
        Integer.parseInt(reader.readLine())
    } catch (e: NumberFormatException) {
        null // 例外が発生した場合にnullが使用される
    }

    println(number)
}
