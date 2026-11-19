package com.anupdeveloper99.nexusterminal

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * One independent Android shell process.
 *
 * The process runs as this application's normal Android UID. It is not root
 * and does not provide a separate Linux distribution or package manager.
 */
internal class TerminalSession(
    val id: Int,
    private val workingDirectory: File,
    private val onOutput: (Int, String) -> Unit,
    private val onClosed: (Int, String?) -> Unit
) {
    private val lock = Any()

    @Volatile
    var isAlive: Boolean = false
        private set

    private var process: Process? = null
    private var input: BufferedWriter? = null

    init {
        startShell()
    }

    private fun startShell() {
        try {
            val shell = File("/system/bin/sh")
            if (!shell.exists() || !shell.canExecute()) {
                throw IOException("Android shell not found at /system/bin/sh")
            }

            val child = ProcessBuilder(shell.absolutePath)
                .directory(workingDirectory)
                .redirectErrorStream(true)
                .apply {
                    environment()["HOME"] = workingDirectory.absolutePath
                    environment()["TMPDIR"] = System.getProperty("java.io.tmpdir") ?: workingDirectory.absolutePath
                    environment()["PATH"] = "/system/bin:/system/xbin"
                }
                .start()

            process = child
            input = BufferedWriter(OutputStreamWriter(child.outputStream))
            isAlive = true

            Thread({
                var failure: String? = null
                try {
                    BufferedReader(InputStreamReader(child.inputStream)).use { reader ->
                        while (true) {
                            val line = reader.readLine() ?: break
                            onOutput(id, "$line\n")
                        }
                    }
                } catch (error: IOException) {
                    if (isAlive) failure = error.message ?: "Shell output stream failed"
                } finally {
                    isAlive = false
                    onClosed(id, failure)
                }
            }, "nexus-shell-$id").apply {
                isDaemon = true
                start()
            }

            onOutput(id, "NEXUS session $id ready. Android app sandbox shell.\n")
        } catch (error: Exception) {
            isAlive = false
            onOutput(id, "Unable to start shell: ${error.message ?: error.javaClass.simpleName}\n")
            onClosed(id, error.message)
        }
    }

    fun execute(command: String) {
        synchronized(lock) {
            if (!isAlive) {
                onOutput(id, "Session is not running. Create a new session.\n")
                return
            }

            try {
                input?.apply {
                    write(command)
                    newLine()
                    flush()
                } ?: onOutput(id, "Shell input is unavailable.\n")
            } catch (error: IOException) {
                onOutput(id, "Command input failed: ${error.message ?: "I/O error"}\n")
            }
        }
    }

    fun close() {
        synchronized(lock) {
            isAlive = false
            try {
                input?.close()
            } catch (_: IOException) {
            }
            input = null

            process?.let { child ->
                try {
                    child.destroy()
                } catch (_: Exception) {
                }
            }
            process = null
        }
    }
}
