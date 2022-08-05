package online.viestudio.viktor.client.task

@Suppress("unused")
interface Task {

    var name: String
    var progress: Float

    fun name(name: String) {
        this.name = name
    }

    fun progress(progress: Float) {
        this.progress = progress
    }
}