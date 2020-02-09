package com.rubyhuntersky.story.core.scopes

data class MatchedScope<A : Any, W : Any>(
    val action: A,
    val vision: W
)
