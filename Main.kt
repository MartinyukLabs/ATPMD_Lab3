
import kotlinx.coroutines.*
import kotlin.random.Random

class Student {
    private var _name: String = "Unknown"
    private var _age: Int = 0
    private var _grades: MutableList<Int> = mutableListOf()

    var name: String
        get() = _name
        set(value) {
            _name = value.trim().replaceFirstChar { it.uppercase() }
        }

    var age: Int
        get() = _age
        set(value) {
            if (value >= 0) _age = value
        }

    val isAdult: Boolean
        get() = age >= 18

    val status: String by lazy {
        if (isAdult) "Adult" else "Minor"
    }

    constructor(name: String, age: Int, grades: List<Int>) {
        this.name = name
        this.age = age
        this._grades = grades.toMutableList()
        println("Student object created with primary constructor.")
    }

    constructor(name: String) {
        this.name = name
        println("Student object created with secondary constructor.")
    }

    fun getAverage(): Double = _grades.average()

    fun processGrades(operation: (Int) -> Int) {
        _grades = _grades.map(operation).toMutableList()
    }

    fun updateGrades(grades: List<Int>) {
        _grades = grades.toMutableList()
    }

    operator fun plus(other: Student): Student {
        val combinedGrades = this._grades + other._grades
        return Student(name = this.name + "-" + other.name, age = 0, grades = combinedGrades)
    }

    operator fun times(multiplier: Int): Student {
        val newGrades = _grades.map { it * multiplier }
        return Student(name = this.name, age = this.age, grades = newGrades)
    }

    override operator fun equals(other: Any?): Boolean {
        if (other !is Student) return false
        return this.name == other.name && this.getAverage() == other.getAverage()
    }

    fun printInfo() {
        println("Name: $name, Age: $age, Status: $status, Grades: $_grades, Average: ${"%.2f".format(getAverage())}")
    }
}

class Group(vararg students: Student) {
    private val studentList = students.toList()

    operator fun get(index: Int): Student = studentList[index]

    fun getTopStudent(): Student? = studentList.maxByOrNull { it.getAverage() }
}

suspend fun fetchGradesFromServer(): List<Int> {
    delay(2000)
    return List(5) { Random.nextInt(60, 100) }
}

fun main() = runBlocking {
    val s1 = Student(name = "alice", age = 20, grades = listOf(90, 80, 70))
    val s2 = Student("bob")
    s2.age = 17
    s2.updateGrades(listOf(60, 70, 65))

    s1.printInfo()
    s2.printInfo()

    val s3 = s1 + s2
    val s4 = s1 * 2

    println("\nAfter combining and multiplying grades:")
    s3.printInfo()
    s4.printInfo()

    println("\nTop student in group:")
    val group = Group(s1, s2, s3, s4)
    group.getTopStudent()?.printInfo()

    println("\nFetching grades asynchronously for bob...")
    val fetchedGrades = async { fetchGradesFromServer() }
    s2.updateGrades(fetchedGrades.await())

    println("Updated grades for bob:")
    s2.printInfo()
}
