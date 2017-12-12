// 5.1 ラムダ式
// ラムダ式またはラムダとは、本質的には他の関数に渡すことができるコードの小さな断片の事
data class Person(val name: String, val age: Int)

/*
>>> val people = listOf(Person("Alice", 29), Person("Bob", 31))
>>> println(people.maxBy { p: Person -> p.age })
Person(name=Bob, age=31)

>>> println( people.maxBy { p -> p.age }) 引数の型を推論
Person(name=Bob, age=31)

>>> println(people.maxBy {it.age}) 引数をデフォルトのitに置き換える
Person(name=Bob, age=31)
*/

fun printMessagesWithPrefix(messages: Collection<String>, prefix: String) {
    messages.forEach() {
        println("$prefix $it")
    }
}

/*
>>> val errors = listOf("403 Forbidden", "404 Not Found")
>>> printMessagesWithPrefix(errors, "Error:")
Error: 403 Forbidden
Error: 404 Not Found
 */

fun printProblemCounts(responses: Collection<String>) {
    var clientErrors = 0 // ラムダから参照される(ラムダによってキャプチャされていると表現される)
    var serverErrors = 0
    responses.forEach {
        if (it.startsWith("4")) {
            clientErrors++
        } else if (it.startsWith("5")) {
            serverErrors++
        }
    }
    println("$clientErrors client errors, $serverErrors server errors")
}

/*
>>> val responses = listOf("200 OK", "403 Forbidden", "404 Not Found", "500 Internal Server Error")
>>> printProblemCounts(responses)
2 client errors, 1 server errors
 */

// メンバ参照
val getAge = Person::age

fun salute() = println("Salute!")
/*
>>> run(::salute)
error: the feature "callable references to class members with empty l h s" is only available since language version 1.2
*/

/*
コンストラクタ参照
>>> val createPerson = ::Person インスタンスを作る処理を保持
>>> val p = createPerson("Alice", 29)
>>> println(p)
Person(name=Alice, age=29)
 */

// コレクション操作

/*
>>> data class Person(val name: String, val age: Int)
>>> val people = listOf(Person("Alice", 29), Person("Bob", 31), Person("Carol", 31))

filter
>>> println(people.filter { it.age > 30 })
[Person(name=Bob, age=31)]

map
>>> val list = listOf(1,2,3,4)
>>> println(list.map { it * it })
[1, 4, 9, 16]

>>> people.filter { it.age > 30 }.map(Person::name)
[Bob]

all
>>> val canBeInClub27 = { p: Person -> p.age <= 27 }
>>> println(people.all(canBeInClub27))
false

findは最初の要素かnullを返す
>>> println(people.find(canBeInClub27))
null

group by
>>> println(people.groupBy { it.age })
{29=[Person(name=Alice, age=29)], 31=[Person(name=Bob, age=31), Person(name=Carol, age=31)]}

firstは拡張関数
>>> val list = listOf("a", "ab", "b")
>>> println(list.groupBy(String::first))
{a=[a, ab], b=[b]}
*/

class Book(val title: String, val authors: List<String>)

/*
flatMap コレクションにして結合
>>> val strings = listOf("abc", "def")
>>> println(strings.flatMap { it.toList()} )
[a, b, c, d, e, f]

toSetは重複を取り除く
>>> val books = listOf(Book("Thursday Next", listOf("Jasper Fforde")),
...                    Book("Mort", listOf("Terry Pratchett")),
...                    Book("Good Omens", listOf("Terry Pratchett", "Neil Gaiman")))
>>> println(books.flatMap { it.authors }.toSet())
[Jasper Fforde, Terry Pratchett, Neil Gaiman]
 */


// 5.3 遅延コレクション操作(シーケンス)
// 例えば.map .filterでつなぐと、両方ともリストを返すため、それぞれの結果を保持するリストが作成される(中間コレクションの作成)

/*
>>> people.asSequence().map(Person::name).filter { it.startsWith("A") }.toList()
[Alice]

map→filterを要素ごとに行っている
>>> listOf(1,2,3,4).asSequence()
...     .map { print("map($it) "); it * it}
...     .filter { print("filter($it) "); it % 2 == 0 }
...     .toList()
map(1) filter(1) map(2) filter(4) map(3) filter(9) map(4) filter(16) [4, 16]

sumが呼び出されるまで評価されない
>>> val naturalNumbers = generateSequence(0) { it + 1 }
>>> val numbersTo100 = naturalNumbers.takeWhile { it <= 100 }
>>> println(numbersTo100.sum())
*/


// Javaの関数型インターフェイスの使用

// sam変換
fun createAllDoneRunnable(): Runnable {
    return Runnable { println("All done!") }
}
//>>> createAllDoneRunnable().run()
//All done!


// レシーバ付きラムダ
// with 同じオブジェクトに対してその名前を繰り返すことなく複数の操作をする

fun alphabet() = with(StringBuilder()) {
    for (letter in 'A'..'Z') {
        this.append(letter)
    }
    append("\nNow I Know the alphabet!")
    this.toString()
}

// apply withとほぼ同じ動きをするが、常に引数の値とウィて渡されたオブジェクトを返す
fun alphabet2() = StringBuilder().apply {
    for (letter in 'A'..'Z') {
        append(letter)
    }
    append("\nNow I Know the alphabet!")
}.toString()

// buildStringはStringBuilderの生成とtoString呼び出しを行う
fun alphabet3() = buildString {
    for (letter in 'A'..'Z') {
        append(letter)
    }
    append("\nNow I Know the alphabet!")
}
