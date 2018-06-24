package org.jetbrains.dekaf.teamcity

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.MethodSource


import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import java.util.*


object TeamCityListener : TestExecutionListener {

    private enum class Category { NONE, CLASS, METHOD, PARAMETER }

    private data class Entity (val id: String, val category: Category, val name: String, val terminal: Boolean)

    private object UnknownSource : TestSource

    private val entities = HashMap<String, Entity>()


    private fun entityOf(identifier: TestIdentifier): Entity {
        var e = entities[identifier.uniqueId]
        if (e == null) e = makeEntity(identifier)
        return e
    }

    private fun makeEntity(identifier: TestIdentifier): Entity {
        val id = identifier.uniqueId
        val parent: Entity? = entities[identifier.parentId.value ?: ""]
        val parentCategory = parent?.category ?: Category.NONE
        val source = identifier.source.value ?: UnknownSource

        var category: Category = Category.NONE
        var name: String = identifier.displayName
        var delimiter = '.'
        when (source) {
            is ClassSource -> {
                category = Category.CLASS
                name = source.className
            }
            is MethodSource -> {
                category = if (parentCategory == Category.METHOD || parentCategory == Category.PARAMETER) Category.PARAMETER else Category.METHOD
                if (category == Category.PARAMETER && parentCategory == Category.METHOD) delimiter = ':'
            }
        }

        if (parent != null && parent.category != Category.NONE) name = parent.name + delimiter + name

        val terminal = category != Category.NONE && identifier.type != TestDescriptor.Type.CONTAINER
        val entity = Entity(id, category, name, terminal)
        entities[id] = entity
        return entity
    }


    override fun executionStarted(testIdentifier: TestIdentifier) {
        val entity = entityOf(testIdentifier)
        if (!entity.terminal) return
        TeamCityMessages.reportTestStarted(entity.name)
    }

    override fun executionFinished(testIdentifier: TestIdentifier, testExecutionResult: TestExecutionResult) {
        val entity = entityOf(testIdentifier)
        val status = testExecutionResult.status
        if (status != TestExecutionResult.Status.SUCCESSFUL) {
            val exception = testExecutionResult.throwable.orElse(null)
            TeamCityMessages.reportTestFailure(entity.name, status.name, exception)
        }
        TeamCityMessages.reportTestFinished(entity.name)
    }

    override fun executionSkipped(testIdentifier: TestIdentifier, reason: String?) {
        val entity = entityOf(testIdentifier)
        TeamCityMessages.reportTestIgnored(entity.name)
    }


    val <T> Optional<T>.value: T? get() = if (isPresent) get() else null

}