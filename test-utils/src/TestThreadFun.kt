package org.jetbrains.dekaf.test.utils

import lb.yaka.Yaka
import java.lang.Thread.sleep


fun delay(threadCount: Int = 1, time: Long, block: (threadNr: Int) -> Unit): Array<Thread> =
        startMass(threadCount) { threadNr, _ ->
            sleep(time)
            block(threadNr)
        }


fun startMass(threadCount: Int = 1,
              iterationsPerThread: Int = 1,
              block: (threadNr: Int, iterationNr: Int) -> Unit): Array<Thread> {

    val threads: Array<Thread> = Array(threadCount) { threadIndex ->
        object : Thread() {
            private val threadNr: Int = threadIndex + 1
            override fun run() {
                for (i in 1..iterationsPerThread) {
                    sleep(1L)
                    block(threadNr, i)
                }
            }
        }
    }

    threads.start()

    return threads
}


fun performMass(threadCount: Int = 1,
                iterationsPerThread: Int = 1,
                timeout: Long = 1_000L,
                block: (threadNr: Int, iterationNr: Int) -> Unit) {
    val threads = startMass(threadCount, iterationsPerThread, block)
    sleep(1L)
    threads.join(timeout)
}



fun Array<Thread>.start() {
    for (thread in this) {
        thread.start()
    }
}


fun Array<Thread>.join() {
    for (thread in this) {
        thread.join()
    }
}


fun Array<Thread>.join(wait: Long) {
    val limit: Long = System.currentTimeMillis() + wait + 1L
    for (thread in this) {
        val waitThisOne = limit - System.currentTimeMillis()
        thread.join(waitThisOne)
    }

    var active = 0
    for (thread in this) if (thread.isAlive) active++
    if (active > 0) Yaka.fail("$active threads were not ended in $wait milliseconds")
}




