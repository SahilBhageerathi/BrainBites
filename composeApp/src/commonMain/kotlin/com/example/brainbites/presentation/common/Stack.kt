package com.example.brainbites.presentation.common

class Stack<T> {
    private val deque = ArrayDeque<T>()

    fun push(item: T) = deque.addLast(item)

    fun pop(): T {
        if (deque.isEmpty()) throw NoSuchElementException("Stack empty")
        return deque.removeLast()
    }

    fun peek(): T = deque.last()

    fun isEmpty() = deque.isEmpty()

    fun size() = deque.size

    fun clear() {
        deque.clear()
    }
}