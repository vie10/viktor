package online.viestudio.viktor.client

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import online.viestudio.viktor.client.component.BaseComponent
import online.viestudio.viktor.client.component.Component
import online.viestudio.viktor.client.event.Event
import online.viestudio.viktor.client.event.TestEvent
import online.viestudio.viktor.client.state.State
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.mock.declare

class ClientTest : BehaviorSpec(), KoinTest {

    private val testComponent = object : BaseComponent("test") {}

    override fun extensions(): List<Extension> = listOf(KoinExtension(module {
        factory { object : BaseClient("test") {} } bind Client::class
    }))

    init {
        given("implementation") {
            `when`("fire event") {
                then("inactive components don't receive it") {
                    with(get<Client>()) {
                        val channel = Channel<Event>(Channel.BUFFERED)
                        declare<Component> {
                            object : BaseComponent("test") {

                                override suspend fun onStart() {
                                    subscribe<TestEvent> { channel.send(it) }
                                }
                            }
                        }
                        val event = TestEvent()
                        onEvent(event)
                        channel.tryReceive().getOrNull().shouldBe(null)
                    }
                }
                then("active components receive it") {
                    with(get<Client>()) {
                        val channel = Channel<Event>(Channel.BUFFERED)
                        declare<Component> {
                            object : BaseComponent("test") {

                                override suspend fun onStart() {
                                    subscribe<TestEvent> { channel.send(it) }
                                }
                            }
                        }
                        start()
                        val event = TestEvent()
                        onEvent(event)
                        channel.receive().shouldBe(event)
                    }
                }
            }
            `when`("get state") {
                then("the same as the value of flow") {
                    with(get<Client>()) {
                        start()
                        state.shouldBe(stateFlow.value)
                        stop()
                    }
                }
            }
            `when`("start") {
                then("state is active") {
                    with(get<Client>()) {
                        start()
                        state.shouldBe(State.Active)
                        stop()
                    }
                }
                then("async components are started in another thread") {
                    val thread = Channel<Thread>()
                    with(get<Client>()) {
                        declare<Component> {
                            object : BaseComponent("test", true) {

                                override suspend fun onStart() {
                                    thread.send(Thread.currentThread())
                                }
                            }
                        }
                        start()
                        thread.receive().shouldNotBe(Thread.currentThread())
                    }
                }
                then("components are started in the same thread") {
                    val thread = Channel<Thread>(Channel.BUFFERED)
                    with(get<Client>()) {
                        declare<Component> {
                            object : BaseComponent("test") {

                                override suspend fun onStart() {
                                    thread.send(Thread.currentThread())
                                }
                            }
                        }
                        start()
                        thread.receive().shouldBe(Thread.currentThread())
                    }
                }
                then("components are active") {
                    with(get<Client>()) {
                        declare<Component> { testComponent }
                        start()
                        components.all { it.state == State.Active }.shouldBe(true)
                        stop()
                    }
                }
                and("stop") {
                    then("components are stopped in the same thread") {
                        val thread = Channel<Thread>(Channel.BUFFERED)
                        with(get<Client>()) {
                            declare<Component> {
                                object : BaseComponent("test") {

                                    override suspend fun onStop() {
                                        thread.send(Thread.currentThread())
                                    }
                                }
                            }
                            start(); stop()
                            thread.receive().shouldBe(Thread.currentThread())
                        }
                    }
                    then("async components are stopped in the same thread") {
                        val thread = Channel<Thread>(Channel.BUFFERED)
                        with(get<Client>()) {
                            declare<Component> {
                                object : BaseComponent("test", true) {

                                    override suspend fun onStop() {
                                        thread.send(Thread.currentThread())
                                    }
                                }
                            }
                            start()
                            while (components.all { it.state != State.Active }) delay(10)
                            stop()
                            thread.receive().shouldBe(Thread.currentThread())
                        }
                    }
                    then("state is inactive") {
                        with(get<Client>()) {
                            start(); stop()
                            state.shouldBe(State.Inactive)
                        }
                    }
                    then("components are inactive") {
                        with(get<Client>()) {
                            declare<Component> { testComponent }
                            start(); stop()
                            components.all { it.state == State.Inactive }.shouldBe(true)
                        }
                    }
                }
            }
        }
    }
}
