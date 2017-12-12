import java.io.Serializable

// 4.1 クラス階層の定義
interface Clickable {
    fun click()
    fun showOff() = println("I'm clickable!") // デフォルト実装
}

interface Focusable {
    fun setFocus(b: Boolean) =
            println("I ${if (b) "got" else "lost"} focus.")
    fun showOff() = println("I'm focusable!")
}

class Button : Clickable, Focusable {
    override fun click() = println("I was clicked") // overrideは必須

    override fun showOff() {
        super<Clickable>.showOff() //
        super<Focusable>.showOff()
    }

    // 片方しか継承しない場合
    // override fun showOff() = super<Clickable>.showOff()
}

fun main(args: Array<String>) {
    val button = Button()
    button.showOff()
    button.setFocus(true)
    button.click()
}

// デフォルトではfinalとなるため、継承させるためには明示的にopenにする
open class RichButton : Clickable {
    fun disable() {} // final
    open fun animate() {}
    override fun click() {} // インターフェースのメンバをオーバーライドしているためopenとなる
    // final override fun click() {}
}

// 抽象クラスも常にopen
abstract class Animated {
    abstract fun animate()
    open fun stopanimating() { // 非抽象メソッドではないのでデフォルトでopenではない

    }

    fun animateTwice() {

    }
}

// アクセス制御 デフォルトはpublic(どこからでも参照可能)
internal open class TalkactiveButton : Focusable {
    private fun yell() = println("Hey!")
    protected fun whisper() = println("Let's talk!")
}

/*
fun TalkactiveButton.giveSpeech() { // internalをpublicとして定義しているのでエラー
    yell() // privateのためエラー
    whisper() // protectedのためエラー
}
*/


// 内部クラス

interface State: Serializable
interface View {
    fun getCurrentState(): State
    fun restoreState(state: State) {}
}

class Button : View {
    override fun getCurrentState(): State = ButtonState()
    override fun restoreState(state: State) {}

    class ButtonState : State {}
}

class Outer {
    inner class Inner {
        fun getOuterReference(): Outer = this@Outer // InnerクラスからOuterクラスへアクセスするための書き方
    }
}

// シールドクラス
// クラスの外側で定義されたクラスを継承先とすることはできない
sealed class Expr {
    class Num(val value: Int) : Expr()
    class Sum(val left: Expr, val right: Expr) : Expr()
}

fun eval(e: Expr): Int =
        when (e) {
            is Expr.Num -> e.value
            is Expr.Sum -> eval(e.right) + eval(e.left)
        }


// 4.2 非自明なコンストラクタやプロパティ
// プライマリコンストラクタ

// 丸括弧で囲まれたコードブロックをプライマリコンストラクタ
// コンストラクタの引数を指定、初期化されるプロパティを定義
class User1(val nickanme: String)

// アノテーションや可視性修飾子がつかないのであればconstructorは省略することができる
class User2 constructor(_nickname: String) {
    val nickname: String

    init {
        nickname = _nickname
    }
}

class User3(_nickname: String) {
    val nickname = _nickname
}

class User4(val nickname: String) // valをつけるとコンストラクタ引数に対応するプロパティが生成される

class User5(val nickname: String, val isSubscribed: Boolean = true)

class Secretive private constructor() {} // privateなコンストラクタ


// セカンダリコンストラクタ

open class View {
    constructor(ctx: Context) {
        // some code
    }
    constructor(ctx: Context, attr: AttributeState) {
        // some code
    }
}

// this()キーワードは自分のクラスの別のコンストラクタを呼び出す
class MyButton : View {
    constructor(ctx: Context) : this(ctx, MY_STYLE) {
        // some code
    }

    constructor(ctx: Context, attr: AttributeSet) : super(ctx, attr) {
        // some code
    }
}


// インターフェース

interface User6 {
    val nickname: String
}

class PrivateUser(override  val nickname: String) : User6

class SubscribingUser(val email: String) : User6 {
    override val nickname: String
        get() = email.substringBefore('@') // カスタムgetter
}
//>>> println(SubscribingUser("test@gmail.com").nickname)
//test

class FacebookUser(val accountId: Int) : User6 {
    override val nickname = getFacebookName(accountId)
}

interface User7 {
    val email: String
    val nickname: String
        get() = email.substringBefore('@')
}

// クラスに実装されたプロパティはバッキグフィールドを参照できる
class User8(val name: String) {
    var address: String = "unspecified"
        set(value: String) {
            println("""
                Address was changed for $name:
                "$field" -> "$value".""".trimIndent()) // バッキングフィールドの値を読む
            field = value
        }
}

/*
>>> val user = User8("alice")
>>> user.address = "Tokyo"
Address was changed for alice:
"unspecified" -> "Tokyo".
*/

class LengthCounter {
    var counter: Int = 0
        private set // 外側からは変更できない

    fun addWord(word: String) {
        counter += word.length
    }
}


// 4.3 データクラスとクラス移譲

class Client(val name: String, val postalCode: Int) {
    override fun toString() = "Client(name=$name, postalCode = $postalCode)"
}

/*
>>> val client1 = Client("Alice", 1111)
>>> println(client1)
Client(name=Alice, postalCode = 1111)
>>> val client2 = Client("Alice", 1111)
>>> println(client1 == client2)
false
*/

class Client2(val name: String, val postalCode: Int) {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Client) // isは特定の型であるかを確認している
            return false
        return name == other.name &&
                postalCode == other.postalCode
    }

    override fun toString() = "Client(name=$name, postalCode = $postalCode)"

    // 2つのオブジェクトが等しいなら、同じハッシュコードを返さなければならない
    override fun hashCode(): Int = name.hashCode() * 31 + postalCode

    fun copy(name: String = this.name,
             postalCode: Int = this.postalCode) =
            Client2(name, postalCode)
}

// Client2のように書かなくても、dataをつければ自動で生成してくれる
// データクラスのインスタンスをイミュータブルにすることが強く推奨されている　　
data class Client3(val name: String, val postalCode: Int)


// クラス移譲

class DelegatingCollection<T>(
        innerList: Collection<T> = ArrayList<T>()
) : Collection<T> by innerList {}

class CountingSet<T>(
        val innerSet: MutableCollection<T> = HashSet<T>()
) : MutableCollection<T> by innerSet { // MutableCollectionの実装をinnerSetに移譲している
    var objectsAdded = 0

    override fun add(element: T): Boolean {
        objectsAdded++
        return innerSet.add(element)
    }

    override fun addAll(c: Collection<T>): Boolean {
        objectsAdded += c.size
        return innerSet.addAll(c)
    }
}

// 4.4 objectキーワード
// objectキーワードはクラスの宣言と宣言したクラスのインスタンスの生成を同時に行う

// objectキーワードでオブジェクト宣言をするとシングルトンとなる
object Payroll {
    val allEmployees = arrayListOf<Person>()

    fun calculateSalary() {
        for (person in allEmployees) {
            // some code
        }
    }
}
// Payroll.allEmployees.add(Peraon(...))
// Payroll.calculateSalary()

class File(_path: String) {
    var path = _path
}

object CaseInsensitiveFileComparator : Comparator<File> {
    override fun compare(file1: File, file2: File): Int {
        return file1.path.compareTo(file2.path, ignoreCase = true)
    }
}

data class Person(val name: String) {
    object NameComparator : Comparator<Person> {
        override fun compare(p1: Person, p2: Person): Int =
                p1.name.compareTo(p2.name)
    }
}

// コンパニオンオブジェクト、クラス内のオブジェクトの宣言は、companionキーワードでマークすることができる

class A {
    companion object {
        fun bar() {
            println("Companion object called")
        }
    }
}
// >>> A.bar()
// Companion object called

class User private constructor(val nickname: String) {
    companion object {
        fun newSubscribingUser(email: String) =
                User(email.substringBefore('@'))

        fun newFacebookuser(accountId: Int) =
                User(getFacebookName(accountId))
    }
}

interface JSONFactory<T> {
    fun fromJSON(jsonText: String): T
}

class Person(val name: String) {
    companion object : JSONFactory<Person> {
        override fun fromJSON(jsonText: String): Person = ...
    }
}

// コンパニオンオブジェクトの拡張関数
// business logic module
class Person2(val firstName: String, val lastName: String) {
    companion object {
    }
}

fun Person2.Companion.fromJSON(json: String): Person {
    // some code
}
val p = Person2.fromJSON(json)
