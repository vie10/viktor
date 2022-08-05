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
import online.viestudio.viktor.client.state.State
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get

class ClientTest : BehaviorSpec(), KoinTest {

    private val testComponent = object : BaseComponent("test") {}

    override fun extensions(): List<Extension> = listOf(KoinExtension(module {
        factory { object : BaseClient("test") {} } bind Client::class
    }))

    init {
        given("implementation") {
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
                        getKoin().loadModules(listOf(
                            module {
                                single {
                                    object : BaseComponent("test", true) {

                                        override suspend fun onStart() {
                                            thread.send(Thread.currentThread())
                                        }
                                    }
                                } bind Component::class
                            }
                        ))
                        start()
                        thread.receive().shouldNotBe(Thread.currentThread())
                    }
                }
                then("components are started in the same thread") {
                    val thread = Channel<Thread>(Channel.BUFFERED)
                    with(get<Client>()) {
                        getKoin().loadModules(listOf(
                            module {
                                single {
                                    object : BaseComponent("test") {

                                        override suspend fun onStart() {
                                            thread.send(Thread.currentThread())
                                        }
                                    }
                                } bind Component::class
                            }
                        ))
                        start()
                        thread.receive().shouldBe(Thread.currentThread())
                    }
                }
                then("components are active") {
                    with(get<Client>()) {
                        getKoin().loadModules(listOf(
                            module {
                                single { testComponent } bind Component::class
                            }
                        ))
                        start()
                        components.all { it.state == State.Active }.shouldBe(true)
                        stop()
                    }
                }
                and("stop") {
                    then("components are stopped in the same thread") {
                        val thread = Channel<Thread>(Channel.BUFFERED)
                        with(get<Client>()) {
                            getKoin().loadModules(listOf(
                                module {
                                    single {
                                        object : BaseComponent("test") {

                                            override suspend fun onStop() {
                                                thread.send(Thread.currentThread())
                                            }
                                        }
                                    } bind Component::class
                                }
                            ))
                            start(); stop()
                            thread.receive().shouldBe(Thread.currentThread())
                        }
                    }
                    then("async components are stopped in the same thread") {
                        val thread = Channel<Thread>(Channel.BUFFERED)
                        with(get<Client>()) {
                            getKoin().loadModules(listOf(
                                module {
                                    single {
                                        object : BaseComponent("test", true) {

                                            override suspend fun onStop() {
                                                thread.send(Thread.currentThread())
                                            }
                                        }
                                    } bind Component::class
                                }
                            ))
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
                            getKoin().loadModules(listOf(
                                module {
                                    single { testComponent } bind Component::class
                                }
                            ))
                            start(); stop()
                            components.all { it.state == State.Inactive }.shouldBe(true)
                        }
                    }
                }
            }
        }
    }
}
