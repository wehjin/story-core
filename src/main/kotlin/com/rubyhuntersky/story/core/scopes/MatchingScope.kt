package com.rubyhuntersky.story.core.scopes

interface MatchingScope<V : Any> {

    fun <A : Any, W : V> match(
        actionClass: Class<A>,
        visionClass: Class<W>,
        update: MatchedScope<A, V, W>.() -> V
    )
}

inline fun <reified A : Any, V : Any, reified W : V> MatchingScope<V>.on(
    noinline update: MatchedScope<A, V, W>.() -> V
) = this.match(A::class.java, W::class.java, update)

inline fun <reified A : Any, reified V : Any> MatchingScope<V>.onAction(
    noinline update: MatchedScope<A, V, V>.() -> V
) = this.match(A::class.java, V::class.java, update)
