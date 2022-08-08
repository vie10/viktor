package online.viestudio.viktor.client.resources

import mu.KotlinLogging
import online.viestudio.viktor.client.Client
import online.viestudio.viktor.client.utils.failedWarn
import online.viestudio.viktor.client.utils.measureCatching
import online.viestudio.viktor.client.utils.measuredDebug
import org.koin.core.component.inject
import java.io.File

abstract class BaseResourcesManager : ResourcesManager {

    private val log = KotlinLogging.logger("Resources Manager")
    private val client by inject<Client>()
    final override val resourcesDir: File = client.dataDir.resolve("resources")

    final override fun update() {
        measureCatching {
            if (onUpdate()) load()
        }.onSuccess {
            log.measuredDebug("Resources updated", it)
        }.onFailure {
            log.failedWarn("Updating resources", it)
        }
    }

    protected abstract fun onUpdate(): Boolean

    protected abstract fun load()

    final override fun toString(): String = "$client Resources Manager"
}