package online.viestudio.viktor.client.koin

import online.viestudio.viktor.client.resources.DefaultResourcesManager
import online.viestudio.viktor.client.resources.ResourcesManager
import org.koin.dsl.bind
import org.koin.dsl.module

@Suppress("unused")
object Modules {

    val defaults
        get() = module {
            single { DefaultResourcesManager() } bind ResourcesManager::class
        }
}