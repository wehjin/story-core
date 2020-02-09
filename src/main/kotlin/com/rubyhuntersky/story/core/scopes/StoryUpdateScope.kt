package com.rubyhuntersky.story.core.scopes

import com.rubyhuntersky.story.core.Story

interface StoryUpdateScope<V : Any> {
    val storyName: String
    val vision: V
    val offer: (action: Any) -> Boolean
    fun <W : Any> atStoryEnd(substory: Story<W>, block: StoryOverScope<W>.() -> Unit)
}

fun <V : Any, W : Any> StoryUpdateScope<V>.offerAtStoryEnd(
    substory: Story<W>,
    toOffering: StoryOverScope<W>.() -> Any
) = atStoryEnd(substory) { offer(toOffering()) }
