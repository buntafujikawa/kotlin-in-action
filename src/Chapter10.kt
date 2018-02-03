//　アノテーションの適用と定義

import org.junit.*
import kotlin.reflect.KClass

@Deprecated("Use remove(index) instead.", ReplaceWith("removeAt(index)"))

fun remove(index: Int) {
    println(1)
}

const val TEST_TIMEOUT = 100L
@Test(timeout = TEST_TIMEOUT)
fun testMethod() {
    // some code
}


class HasTempFolder {
    @get:Rule // getterにアノテーションがついている
    val folder = TemporaryFolder()

    @Test
    fun testUsingTempFolder() {

    }
}

fun test(list: List<*>) {
    @Suppress("UNCHECKED_CAST")
    val strings = list as List<String>
}


data class Person(val name: String, val age: Int)

val person = Person("Alice", 29)
println(serialize(person))

data class Person2 {
    @JsonName("alias")
    val firstName: String = ""

    annotation class JsonName(val value: String)

    @JsonExclude
    val age: Int? = null

    // アノテーションクラスに適用できるアノテーションをメタアノテーションと呼ぶ
    @Target(AnnotationTarget.PROPERTY)
    annotation class JsonExclude
}

@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class BindingAnnotation

@BindingAnnotation
annotation class MyBinding


// アノテーションの引数としてのクラス
interface Company {
    val name: String
}

data class CompanyImpl(override val name: String) : Company

data class Person3 {
    val name: String,
    @DeserializeInterface(CompanyImpl::class)
    val company: Company

    annotation class DeserializeInterface(val targetClass: KClass<out Any>)
}

// アノテーションの引数としてのジェネリクスクラス
interface ValueSerializer<T> {
    fun toJsonValue(value: T): Any?
    fun fromJsonValue(jsonValue: Any?): T
}

data class Person4 {
    val name: String,
    @CustomSerializer(DateSerializer::class) val birthDate: Date

    annotation class CustomSerializer(val serializerClass: kClass<out ValueSerializer<*>>)
}

