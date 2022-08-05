package online.viestudio.viktor.client.component

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import online.viestudio.viktor.client.state.State

class ComponentTest : BehaviorSpec() {

    private val testComponent: Component get() = object : BaseComponent("test") {}
    private val brokenStartTestComponent: Component
        get() = object : BaseComponent("test") {
            override suspend fun onStart() = throw IllegalStateException()
        }
    private val brokenStopTestComponent: Component
        get() = object : BaseComponent("test") {
            override suspend fun onStop() = throw IllegalStateException()
        }


    init {
        given("implementation") {
            `when`("start failed due to an exception") {
                then("state is inactive") {
                    with(brokenStartTestComponent) {
                        runCatching { start() }
                        state.shouldBe(State.Inactive)
                    }
                }
                then("throws the exception to the caller") {
                    with(brokenStartTestComponent) {
                        shouldThrow<Exception> { start() }
                    }
                }
            }
            `when`("stop failed due to an exception") {
                then("state is inactive") {
                    with(brokenStopTestComponent) {
                        start()
                        runCatching { stop() }
                        state.shouldBe(State.Inactive)
                    }
                }
                then("throws the exception to the caller") {
                    with(brokenStopTestComponent) {
                        start()
                        shouldThrow<Exception> { stop() }
                    }
                }
            }
            `when`("start") {
                then("state is active") {
                    with(testComponent) {
                        start()
                        state.shouldBe(State.Active)
                    }
                }
                and("stop") {
                    then("state is inactive") {
                        with(testComponent) {
                            start(); stop()
                            state.shouldBe(State.Inactive)
                        }
                    }
                }
            }
        }
    }
}
