/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.rubyhuntersky.story.core

import com.rubyhuntersky.story.core.scopes.offerAtStoryEnd
import com.rubyhuntersky.story.core.scopes.on
import com.rubyhuntersky.story.core.scopes.onAction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class LibraryTest {

    private object Increment

    private fun counterStory() = story("Sample", 5, { it == 6 }) { action ->
        when (action) {
            is Increment -> vision + 1
            else -> null
        }
    }

    @Test
    fun `matching story changes after action rule is matched`() {
        val story = matchingStory("Matcher", 1, { false }) {
            onAction<Increment, Int> { vision + 1 }
        }
        story.offer(Increment)
        val value = runBlocking {
            withTimeoutOrNull(1000) {
                var value = 0
                for (vision in story.subscribe()) {
                    value = vision
                    if (vision == 2) break
                }
                value
            }
        }
        assertEquals(2, value)
    }

    @Test
    fun `matching story changes after action-vision rule is matched`() {
        val story = matchingStory("Matcher", 1, { false }) {
            on<Increment, Int, Int> { vision + 1 }
        }
        story.offer(Increment)
        val value = runBlocking {
            withTimeoutOrNull(1000) {
                var value = 0
                for (vision in story.subscribe()) {
                    value = vision
                    if (vision == 2) break
                }
                value
            }
        }
        assertEquals(2, value)
    }

    @Test
    fun `story starts with init`() {
        val story = counterStory()
        val visions = runBlocking { story.subscribe() }
        assertSame(5, runBlocking { visions.receive() })
    }

    @Test
    fun `story changes after action`() {
        val story = counterStory()
        story.offer(Increment)
        val visions = runBlocking { story.subscribe() }
        assertSame(6, runBlocking { visions.receive() })
    }

    @Test
    fun `story ignores unknown actions`() {
        val story = counterStory()
        story.offer(Unit)
        val visions = runBlocking { story.subscribe() }
        assertSame(5, runBlocking { visions.receive() })
    }

    data class Substory(val substory: Story<Int>? = null)
    data class Start(val notify: SendChannel<Unit>)
    data class NotifyTop(val notify: SendChannel<Unit>)

    @Test
    fun `onStoryOver notifies when substory ends`() {
        val topStory = story("Top", Substory(), { false }) { action ->
            when (action) {
                is Start -> {
                    val substory = counterStory().also {
                        atStoryEnd(it) { action.notify.sendBlocking(Unit) }
                    }
                    vision.copy(substory = substory)
                }
                else -> null
            }
        }

        val channel = Channel<Unit>()
        topStory.offer(Start(notify = channel))
        runBlocking {
            for (vision in topStory.subscribe()) {
                val substory = vision.substory
                if (substory != null) {
                    substory.offer(Increment)
                    break
                }
            }
        }
        assertNotNull(runBlocking { withTimeoutOrNull(500) { channel.receive() } })
    }

    @Test
    fun `update scope allows offering an action when substory ends`() {
        val topStory = story("Top", Substory(), { false }) { action ->
            when (action) {
                is Start -> {
                    val substory = counterStory().also {
                        offerAtStoryEnd(it) { NotifyTop(action.notify) }
                    }
                    vision.copy(substory = substory)
                }
                is NotifyTop -> null.also { action.notify.sendBlocking(Unit) }
                else -> null
            }
        }

        val channel = Channel<Unit>()
        topStory.offer(Start(notify = channel))
        runBlocking {
            for (vision in topStory.subscribe()) {
                val substory = vision.substory
                if (substory != null) {
                    substory.offer(Increment)
                    break
                }
            }
        }
        assertNotNull(runBlocking { withTimeoutOrNull(500) { channel.receive() } })
    }
}