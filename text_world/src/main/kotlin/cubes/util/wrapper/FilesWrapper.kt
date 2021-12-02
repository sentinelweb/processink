package cubes.util.wrapper

import java.io.File

class FilesWrapper(val filesDir: File) {
    val stateDir: File
        get() = File(filesDir, "state")
    val lastStateFile: File
        get() = File(stateDir, "last_state.json")
    val textDir: File
        get() = File(filesDir, "text")
}
