package online.viestudio.viktor.client.resources

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import online.viestudio.viktor.api.Endpoints
import online.viestudio.viktor.api.resources.Resource
import online.viestudio.viktor.client.utils.forEachAsync
import online.viestudio.viktor.client.utils.getBody
import online.viestudio.viktor.client.utils.getBodyToFile
import online.viestudio.viktor.client.utils.verify
import org.koin.core.component.inject
import java.io.File
import java.nio.file.Files

class DefaultResourcesManager : BaseResourcesManager() {

    private val httpClient: HttpClient by inject()
    private val endpoints: Endpoints by inject()
    private var resourcesForDownload: Set<Resource> = emptySet()
    private var removedResources: Set<File> = emptySet()

    override suspend fun onUpdate(): Boolean {
        val resources: Set<Resource> = httpClient.getBody {
            url(endpoints.resourcesIndex)
        }
        removedResources = resourcesDir.walkTopDown().filter { it.isFile }.filter { file ->
            !resources.any { it.path == file.relativeTo(resourcesDir).path }
        }.toSet()
        resourcesForDownload = resources.filter {
            val resourceFile = resolveResource(it.path)
            !resourceFile.exists() || !resourceFile.verify(it.hash)
        }.toSet()
        return resourcesForDownload.isNotEmpty()
    }

    override suspend fun load() {
        resourcesForDownload.forEachAsync { it.download() }
        deleteRemovedResources()
    }

    private suspend fun Resource.download() {
        val tempFile = withContext(Dispatchers.IO) { Files.createTempFile("", "").toFile() }
        httpClient.getBodyToFile(tempFile) { url(this@download.url) }
        tempFile.copyTo(resolveFile(), true)
    }

    private fun Resource.resolveFile() = resolveResource(path)

    private fun deleteRemovedResources() {
        removedResources.forEach { it.delete() }
    }
}