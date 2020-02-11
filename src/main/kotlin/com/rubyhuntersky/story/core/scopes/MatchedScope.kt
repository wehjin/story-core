package com.rubyhuntersky.story.core.scopes

interface MatchedScope<A : Any, V : Any, W : V> : StoryInitScope<V> {
    val action: A
    val vision: W
}
