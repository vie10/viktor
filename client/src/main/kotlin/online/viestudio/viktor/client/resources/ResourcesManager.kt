package online.viestudio.viktor.client.resources

import org.koin.core.component.KoinComponent
import java.io.File

interface ResourcesManager : KoinComponent {

    val resourcesDir: File

    fun update()
}