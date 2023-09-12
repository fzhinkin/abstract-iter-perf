package org.example

import kotlinx.benchmark.*

private enum class StateEnum {
    FAILED,
    READY,
    NOT_READY,
    DONE
}

private object StateConstants {
    const val FAILED = 0
    const val READY = 1
    const val NOT_READY = 2
    const val DONE = 3
}

abstract class AbstractIteratorWithTableSwitchOverEnum<T> : Iterator<T> {
    private var state = StateEnum.NOT_READY
    private var nextValue: T? = null

    override fun hasNext(): Boolean {
        require(state != StateEnum.FAILED)
        return when (state) {
            StateEnum.DONE -> false
            StateEnum.READY -> true
            else -> tryToComputeNext()
        }
    }

    override fun next(): T {
        if (!hasNext()) throw NoSuchElementException()
        state = StateEnum.NOT_READY
        @Suppress("UNCHECKED_CAST")
        return nextValue as T
    }

    private fun tryToComputeNext(): Boolean {
        state = StateEnum.FAILED
        computeNext()
        return state == StateEnum.READY
    }

    protected abstract fun computeNext(): Unit

    protected fun setNext(value: T): Unit {
        nextValue = value
        state = StateEnum.READY
    }
    protected fun done() {
        state = StateEnum.DONE
    }
}

abstract class AbstractIteratorWithMultipleIfsOverEnum<T> : Iterator<T> {
    private var state = StateEnum.NOT_READY
    private var nextValue: T? = null

    override fun hasNext(): Boolean {
        require(state != StateEnum.FAILED)
        if (state == StateEnum.DONE) return false
        if (state == StateEnum.READY) return true
        return tryToComputeNext()
    }

    override fun next(): T {
        if (!hasNext()) throw NoSuchElementException()
        state = StateEnum.NOT_READY
        @Suppress("UNCHECKED_CAST")
        return nextValue as T
    }

    private fun tryToComputeNext(): Boolean {
        state = StateEnum.FAILED
        computeNext()
        return state == StateEnum.READY
    }

    protected abstract fun computeNext(): Unit

    protected fun setNext(value: T): Unit {
        nextValue = value
        state = StateEnum.READY
    }
    protected fun done() {
        state = StateEnum.DONE
    }
}

abstract class AbstractIteratorWithTableSwitchOverIntCon<T> : Iterator<T> {
    private var state = StateConstants.NOT_READY
    private var nextValue: T? = null

    override fun hasNext(): Boolean {
        require(state != StateConstants.FAILED)
        return when (state) {
            StateConstants.DONE -> false
            StateConstants.READY -> true
            else -> tryToComputeNext()
        }
    }

    override fun next(): T {
        if (!hasNext()) throw NoSuchElementException()
        state = StateConstants.NOT_READY
        @Suppress("UNCHECKED_CAST")
        return nextValue as T
    }

    private fun tryToComputeNext(): Boolean {
        state = StateConstants.FAILED
        computeNext()
        return state == StateConstants.READY
    }

    protected abstract fun computeNext(): Unit

    protected fun setNext(value: T): Unit {
        nextValue = value
        state = StateConstants.READY
    }
    protected fun done() {
        state = StateConstants.DONE
    }
}

class IteratorWithTableSwitchOverEnum(private val count: Int) : AbstractIteratorWithTableSwitchOverEnum<String>() {
    private var current = 0

    override fun computeNext() {
        if (current < count) {
            setNext("VALUE")
            current++
        } else {
            done()
        }
    }
}

class IteratorWithTableIfsOverEnum(private val count: Int) : AbstractIteratorWithMultipleIfsOverEnum<String>() {
    private var current = 0

    override fun computeNext() {
        if (current < count) {
            setNext("VALUE")
            current++
        } else {
            done()
        }
    }
}

class IteratorWithTableSwitchOverIntCon(private val count: Int) : AbstractIteratorWithTableSwitchOverIntCon<String>() {
    private var current = 0

    override fun computeNext() {
        if (current < count) {
            setNext("VALUE")
            current++
        } else {
            done()
        }
    }
}

@State(Scope.Benchmark)
open class IteratorsBenchmark {
    @Param("10", "100", "1000")
    var size: Int = 0

    @Benchmark
    fun tableSwitchEnum(blackhole: Blackhole) {
        val iter = IteratorWithTableSwitchOverEnum(size)
        while (iter.hasNext()) {
            blackhole.consume(iter.next())
        }
    }

    @Benchmark
    fun ifsEnum(blackhole: Blackhole) {
        val iter = IteratorWithTableIfsOverEnum(size)
        while (iter.hasNext()) {
            blackhole.consume(iter.next())
        }
    }

    @Benchmark
    fun tableSwitchIntCon(blackhole: Blackhole) {
        val iter = IteratorWithTableSwitchOverIntCon(size)
        while (iter.hasNext()) {
            blackhole.consume(iter.next())
        }
    }
}

fun main() {
    val c = IteratorsBenchmark::class.java.classLoader.loadClass(
        "org.example.AbstractIteratorWithTableSwitchOverEnum\$WhenMappings")
    val mapping = c.declaredFields.first().get(c) as IntArray


    for (e in StateEnum.values()) {
        println("${e.name} => ${e.ordinal} => ${mapping[e.ordinal]}")
    }
}
