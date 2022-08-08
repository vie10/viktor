package online.viestudio.viktor.client.resources

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import java.io.File

interface ResourcesManager<T> : KoinComponent {

    val resourcesFlow: StateFlow<T>
    val resources: T
    val resourcesDir: File

    fun update()
}