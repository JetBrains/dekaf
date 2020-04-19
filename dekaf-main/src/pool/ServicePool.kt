package org.jetbrains.dekaf.main.pool

import java.util.concurrent.BlockingDeque
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min


abstract class ServicePool<S: Any> {

    /// INITIALIZATION \\\

    fun setup(minServices: Int = this.minServices,
              maxServices: Int = this.maxServices,
              rotating: Boolean = this.rotating,
              waitingTime: Long = this.waitingTime) {
        this.minServices = minServices
        this.maxServices = maxServices
        this.rotating = rotating
        this.waitingTime = waitingTime
        this.blockingTime = min((waitingTime+1L)/2, 1_000)
    }


    /// METHODS TO OVERRIDE/IMPLEMENT \\\

    protected abstract fun openService(): S

    protected abstract fun closeService(service: S, wasBroken: Boolean)


    /// INNER CLASSES \\\

    private companion object {

        @JvmStatic
        private fun now(): Long = System.currentTimeMillis()

    }

    private inner class Handle (val service: S) {

        var active: Boolean = false
            private set

        var last: Long = now()
            private set

        internal fun activate(): S {
            active = true
            activeServices.incrementAndGet()
            updateTime()
            return service
        }

        internal fun passivate() {
            active = false
            activeServices.decrementAndGet()
            updateTime()
        }

        internal fun updateTime() {
            last = now()
        }

    }


    /// INTERNAL STATE \\\

    private var minServices: Int = 0
    private var maxServices: Int = 100
    private var rotating: Boolean = false

    private var waitingTime: Long = 10_000 // ms
    private var blockingTime: Long = 500 // ms

    private val services: MutableMap<S, Handle> = ConcurrentHashMap()
    private val deque:    BlockingDeque<Handle> = LinkedBlockingDeque()

    private val activeServices = AtomicInteger(0)

    @Volatile
    private var shuttingDown: Boolean = false

    private val serviceAcquireLock = Object()


    /// IMPLEMENTATION \\\

    fun populate() {
        while (services.size < minServices) {
            val h: Handle = askForNewService() ?: break
            deque.putLast(h)
        }
    }


    fun borrow(): S {
        val t1 = now()
        val tLimit = t1 + waitingTime
        var h: Handle?
        do {
            if (shuttingDown) throw ShuttingDownException()
            val sn = services.size
            if ((deque.isEmpty() || sn < minServices) && sn < maxServices) {
                h = askForNewService()
                if (h != null) return h.activate()
            }
            h = deque.pollFirst(blockingTime, TimeUnit.MILLISECONDS)
            if (h != null) return h.activate()
        } while (now() < tLimit)
        throw ExhaustedException()
    }


    fun release(service: S) {
        val h: Handle = services[service] ?: throw AlienServiceException()
        h.passivate()
        if (rotating) deque.addLast(h)
        else deque.addFirst(h)
    }

    fun releaseBroken(service: S) {
        val h: Handle = services.remove(service) ?: throw AlienServiceException()
        h.passivate()
        closeService(h.service, true)
    }


    fun closeIdleServices() {
        while (deque.isNotEmpty()) {
            val h: Handle = deque.pollLast() ?: break
            closeTheService(h)
        }
    }

    private fun closeTheService(h: Handle) {
        val s: S = h.service
        services.remove(s)
        closeService(s, false)
    }


    private fun askForNewService(): Handle? =
            synchronized(serviceAcquireLock) {
                if (services.size < maxServices) acquireNewService()
                else null
            }

    private fun acquireNewService(): Handle {
        val service: S = openService()
        val handle = Handle(service)
        services.put(service, handle)
        return handle
    }


    /// DIAGNOSTICS \\\

    val activeCount: Int
        get() = totalCount - idleCount

    val idleCount: Int
        get() = deque.size

    val totalCount: Int
        get() = services.size


    /// EXCEPTIONS \\\

    open class Exception (message: String) : RuntimeException(message)

    class ExhaustedException : Exception("Service pool exhausted")
    class ShuttingDownException : Exception("Service pool is shutting down")

    class AlienServiceException : Exception("The given service doesn't belong to this service pool")

}