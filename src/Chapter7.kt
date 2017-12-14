import java.beans.PropertyChangeSupport
import java.beans.PropertyChangeListener
import java.lang.reflect.Type
import java.security.cert.Extension
import java.time.LocalDate
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

// 7.1 算術演算子のオーバーロード

data class Point(val x: Int, val y: Int) {
    // 演算子のオーバーロードに使われる関数にはoperatorキーワードをつける
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }
}
/*
>>> val p1 = Point(10, 20)
>>> val p2 = Point(30, 40)
>>> println(p1 + p2) 記号によってplus関数が呼ばれる
Point(x=40, y=60)
 */

// こちらが一般的
operator fun Point.plus(other: Point): Point {
    return Point(x + other.x, y + other.y)
}

// kotlinの演算子は可換性を自動的にサポートしていない
operator fun Point.times(scale: Double): Point {
    return Point((x * scale).toInt(), (y * scale).toInt())
}

operator fun Char.times(count: Int): String {
    return toString().repeat(count)
}
/*
>>> println('a' * 3)
aaa
 */


/*
+演算子は常に新しいコレクションを返す
+=演算子はミュータブルコレクションに対してはその場でコレクションを変更
読み取り専用についてはそのコピーを返す
>>> val list = arrayListOf(1,2)
>>> list += 3
>>> val newList = list + listOf(4,5)
>>> println(list)
[1, 2, 3]
>>> println(newList)
[1, 2, 3, 4, 5]
 */

operator fun Point.unaryMinus(): Point {
    return Point(-x, -y)
}
/*
>>> val p = Point(10, 20)
>>> println(-p)
Point(x=-10, y=-20)
 */


// 7.2 比較演算子のオーバーロード

class Person(val firstName: String, val lastName: String) : Comparable<Person> {
    override fun compareTo(other: Person): Int {
        return compareValuesBy(this, other, Person::lastName, Person::firstName)
    }
}

operator fun Point.get(index: Int) {
    return when (index) {
        0 -> x
        1 -> y
        else ->
            throw IndexOutOfBoundsException("Invalid coordinate $index")
    }
}

data class MutablePoint(var x: Int, var y: Int) {
    operator fun MutablePoint.set(index: Int, value: Int) {
        when (index) {
            0 -> x
            1 -> y
            else ->
                throw IndexOutOfBoundsException("Invalid coordinate $index")
        }
    }
}

data class Rectangle(val upperLeft: Point, val lowerRight: Point)

operator fun Rectangle.contains(p: Point): Boolean {
    return p.x in upperLeft.x until lowerRight.x &&
            p.y in upperLeft.y until lowerRight.y
}
/*
>>> val rect = Rectangle(Point(10, 20), Point(50,50))
>>> println(Point(20,30) in rect)
true
>>> println(Point(5,5) in rect)
false
 */


// start..end → start.rangeTo(end)
//operator fun <T: Comparable<T>> T.rangeTo(that: T): ClosedRange<T> {}

/*
>>> val now = LocalDate.now()
>>> val vacation = now..now.plusdays(10)
>>> println(now.plusWeeks(1) in vacation)
true
 */


operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> =
        object : Iterator<LocalDate> {
            var current = start

            override fun hasNext() =
                    current <= endInclusive

            override fun next() = current.apply {
                current = plusDays(1)
            }
        }
/*
>>> val newYear = LocalDate.ofYearDay(2017, 1)
>>> val daysOff = newYear.minusDays(1)..nowYear
>>> val daysOff = newYear.minusDays(1)..newYear
>>> for (dayOff in daysOff) { println(dayOff) }
 */

// 分解宣言 componentNという関数を呼び出している
/*
>>> val p = Point(10, 20)
>>> val (x, y) = p
>>> println(x)
10
>>> println(y)
20
 */

class Point2(val x: Int, val y: Int) {
    operator fun component1() = x
    operator fun component2()4 = y
}

data class NameComponents(val name: String, val extension: String)

fun splitFilename(fullName: String): NameComponents {
    val result = fullName.split(',', limit = 2)
    return NameComponents(result[0], result[1])
}

for printEntries(map: Map<String, String>) {
    for ((key, value) in map) {
        println("$key -> $value")
    }
}

//class Foo {
//    private val delegate = Delegate()
//
//    var p: Type
//        set(value: Type) = delegate.saveValue(..., value)
//        get() = delegate.getValue(...)
//}

class Delegate {
    operator fun getValue(...) {
        ...
    }

    operator fun setValue(..., value: Type) {
        ...
    }
}

class Foo {
    var p: Type by Delegate()
}
// val foo = Foo()
// val oldValue = foo.p
// foo.p = newValue

// 遅延初期化

class Person2(val name: String) {
    private var _emails: List<Email>? = null // emailsが移譲するデータを格納する

    val emails: List<Email> // _emailsへの読み取りアクセスを提供するプロパティ
        get() {
            if (_emails == null) {
                _emails = loadEmails(this)
            }
            return _emails!!
        }
}

class Person3(val name: String) {
    val emails by lazy { loadEmails(this) }
}

// 移譲プロパティ
// プロパティの値を格納し、その値が変更されたら自動的にプロパティの変更通知をするクラスを作成

// PropertyChangeSupportを利用するためのヘルパークラス
open class PropertyChangeAware {
    protected val changeSupport = PropertyChangeSupport(this)

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        changeSupport.removePropertyChangeListener(listener)
    }
}

class ObservableProperty (
        var propValue: Int, val changeSupport: PropertyChangeSupport
){
    operator fun getValue(p: Person4, prop: KProperty<*>): Int = propValue

    operator fun setValue(p: Person4, prop: KProperty<*>, newValue: Int) {
        val oldValue = propValue
        propValue = newValue
        changeSupport.firePropertyChange(prop.name, oldValue, newValue)
    }
}

class Person4(
        val name: String, age: Int, salary: Int
) : PropertyChangeAware() {

    private val observer = {
        prop: KProperty<*>, oldValue: Int, newValue: Int ->
        changeSupport.firePropertyChange(prop.name, oldValue, newValue)
    }

    var age: Int by ObservableProperty(age, ChangeSupport) // byの右側が移譲オブジェクト
    var salary: Int by ObservableProperty(salary, ChangeSupport)
}

class Person5 {
    private _attributes = hashMapOf<String, String>()

    fun setAtribute(attrName: String, value: String) {
        _attributes[attrName] = value
    }

    val name: String by _attributes // mapを移譲プロパティとして使う
}

object Users : Idtable() { // テーブルを記述しているためインスンタンスは1つで良い
    val name = varchar("name", length = 50).index()
    val age = integer("age")
}

class User(id: EnityId) : Entity(id) {
    var name: String by Users.name
    var age: Int by Users.age
}
