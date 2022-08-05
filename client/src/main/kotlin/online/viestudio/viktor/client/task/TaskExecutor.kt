package online.viestudio.viktor.client.task

import kotlinx.coroutines.Deferred

@Suppress("unused")
interface TaskExecutor {

    suspend fun <T> execute(block: Task.() -> T): Result<T>

    suspend fun <T> executeAsync(block: Task.() -> T): Deferred<Result<T>>
}