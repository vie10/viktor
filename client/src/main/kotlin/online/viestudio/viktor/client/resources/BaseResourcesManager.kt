package online.viestudio.viktor.client.resources

import kotlinx.coroutines.flow.MutableStateFlow
import mu.KotlinLogging
import online.viestudio.viktor.client.Client
import online.viestudio.viktor.client.utils.failedWarn
import online.viestudio.viktor.client.utils.getValue
import online.viestudio.viktor.client.utils.measureCatching
import online.viestudio.viktor.client.utils.measuredDebug
import org.koin.core.component.inject
import java.io.File

@Suppress("unused")
abstract class BaseResourcesManager<T>(
    default: T,
) : ResourcesManager<T> {

    private val log = KotlinLogging.logger("Resources Manager")
    final override val resourcesFlow = MutableStateFlow(default)
    final override val resources by resourcesFlow
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