package org.jetbrains.dekaf.mainTest.pool

import lb.yaka.expectations.*
import lb.yaka.gears.expect
import org.jetbrains.dekaf.main.pool.ServicePool
import org.jetbrains.dekaf.test.utils.UnitTest
import org.jetbrains.dekaf.test.utils.delay
import org.jetbrains.dekaf.test.utils.join
import org.jetbrains.dekaf.test.utils.startMass
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep
import java.util.concurrent.atomic.AtomicInteger


class ServicePoolTest : UnitTest {

    companion object {
        private val counter = AtomicInteger(0)
    }

    private class TestService {
        val nr = counter.incrementAndGet()
        var closed: Boolean = false
        var broken: Boolean = false
        fun close(broken: Boolean) { closed = true; this.broken = broken }
        override fun toString() = "TestService № $nr" + (if (closed) " closed" else "") + (if (broken) " broken" else "")
    }

    private class TestPool: ServicePool<TestService>() {
        override fun openService(): TestService = TestService()
        override fun closeService(service: TestService, wasBroken: Boolean) = service.close(broken = wasBroken)
    }


    @BeforeEach
    fun resetCounter() {
        counter.set(0)
    }


    private fun TestPool.borrowAndRelease() {
        val s = this.borrow()
        sleep(7L)
        this.release(s)
        sleep(3L)
    }


    @Test
    fun initialIsEmpty() {
        val pool = TestPool()
        pool.setup(minServices = 3, maxServices = 10)

        expect that pool.activeCount iz zero
        expect that pool.idleCount iz zero
        expect that pool.totalCount iz zero
    }


    @Test
    fun borrowAndRelease_basic() {
        val pool = TestPool()

        val s1 = pool.borrow()

        expect that pool.activeCount equalsTo 1
        expect that pool.idleCount equalsTo 0
        expect that pool.totalCount equalsTo 1

        pool.release(s1)

        expect that pool.activeCount equalsTo 0
        expect that pool.idleCount equalsTo 1
        expect that pool.totalCount equalsTo 1

        val s2 = pool.borrow()

        expect that pool.activeCount equalsTo 1
        expect that pool.idleCount equalsTo 0
        expect that pool.totalCount equalsTo 1

        pool.release(s2)

        expect that pool.activeCount equalsTo 0
        expect that pool.idleCount equalsTo 1
        expect that pool.totalCount equalsTo 1

        expect that s2 sameAs s1
    }


    @Test
    fun borrowAndRelease_mass() {
        val pool = TestPool()

        startMass(10, 30) { _, _ ->
            pool.borrowAndRelease()
        }.join()

        expect that pool.activeCount iz zero
        expect that pool.idleCount inRange 1..10
    }


    @Test
    fun borrowAndRelease_massThrottling() {
        val pool = TestPool()
        pool.setup(maxServices = 5, rotating = false)

        startMass(20, 30) { _, _ ->
            pool.borrowAndRelease()
        }.join()

        expect that pool.activeCount iz zero
        expect that pool.idleCount inRange 3..5
    }


    @Test
    fun borrowAndRelease_massThrottlingRotating() {
        val pool = TestPool()
        pool.setup(maxServices = 5, rotating = true)

        startMass(20, 30) { _, _ ->
            pool.borrowAndRelease()
        }.join()

        expect that pool.activeCount iz zero
        expect that pool.idleCount inRange 3..5
    }


    @Test
    fun releaseBroken_basic() {
        val pool = TestPool()

        val s1 = pool.borrow()
        pool.release(s1)

        expect that pool.idleCount equalsTo 1
        expect that pool.totalCount equalsTo 1

        val s2 = pool.borrow()
        pool.releaseBroken(s2)

        expect that pool.idleCount iz zero
        expect that pool.totalCount iz zero
    }

    @Test
    fun releaseBroken_replacement() {
        val pool = TestPool()
        pool.setup(minServices = 1, maxServices = 1, waitingTime = 10_000L)

        val threads = delay(time = 70L) { _ ->
            pool.borrowAndRelease()
        }

        val s = pool.borrow()
        pool.releaseBroken(s)

        threads.join(2_000L)
    }



    @Test
    fun populate_basic() {
        val pool = TestPool()
        pool.setup(minServices = 3, maxServices = 10)
        pool.populate()

        expect that pool.activeCount iz zero
        expect that pool.idleCount equalsTo 3
        expect that pool.totalCount equalsTo 3
    }

    @Test
    fun populate_exact() {
        val pool = TestPool()
        pool.setup(minServices = 3, maxServices = 3)
        pool.populate()

        expect that pool.activeCount iz zero
        expect that pool.idleCount equalsTo 3
        expect that pool.totalCount equalsTo 3
    }

    @Test
    fun populate_inChaos() {
        val pool = TestPool()
        pool.setup(minServices = 5, maxServices = 10, rotating = false)

        val mass = startMass(3, 25) { _, _ ->
            pool.borrowAndRelease()
        }

        pool.populate()

        mass.join()

        expect that pool.activeCount iz zero
        expect that pool.idleCount inRange 5..6
   }

}