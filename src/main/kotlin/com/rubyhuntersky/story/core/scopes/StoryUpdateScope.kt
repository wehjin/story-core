package com.rubyhuntersky.story.core.scopes

import com.rubyhuntersky.story.core.Story

interface StoryUpdateScope<V : Any> : StoryInitScope<V> {
    val vision: V
}

interface StoryInitScope<V : Any> {
    val storyName: String
    val offer: (action: Any) -> Boolean
    fun <W : Any> whenSubstoryEnds(substory: Story<W>, block: StoryOverScope<W>.() -> Unit)
}

fun <V : Any, W : Any> StoryInitScope<V>.offerWhenStoryEnds(
    substory: Story<W>,
    toOffering: StoryOverScope<W>.() -> Any
) = whenSubstoryEnds(substory) { offer(toOffering()) }
