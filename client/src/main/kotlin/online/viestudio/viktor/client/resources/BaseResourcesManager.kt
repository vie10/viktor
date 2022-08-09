package online.viestudio.viktor.client.resources

import mu.KotlinLogging
import online.viestudio.viktor.client.Client
import online.viestudio.viktor.client.utils.failedWarn
import online.viestudio.viktor.client.utils.measureCatching
import online.viestudio.viktor.client.utils.measuredDebug
import org.koin.core.component.inject
import java.io.File

@Suppress("unused")
abstract class BaseResourcesManager : ResourcesManager {

    private val log = KotlinLogging.logger("Resources Manager")
    private val client by inject<Client>()
    final override val resourcesDir: File = client.dataDir.resolve("resources")

    override fun resolveResource(path: String) = resourcesDir.resolve(path)

    final override suspend fun update() {
        measureCatching {
            if (onUpdate()) {
                load()
                client.onEvent(ResourcesUpdateEvent())
            }
        }.onSuccess {
            log.measuredDebug("Resources updated", it)
        }.onFailure {
            log.failedWarn("Updating resources", it)
        }
    }

    protected abstract suspend fun onUpdate(): Boolean

    protected abstract suspend fun load()

    final override fun toString(): String = "$client Resources Manager"
}