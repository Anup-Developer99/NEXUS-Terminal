package com.anupdeveloper99.nexusterminal

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.io.File

class MainActivity : Activity() {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val sessions = mutableListOf<TerminalSession>()
    private val sessionLogs = mutableMapOf<Int, StringBuilder>()
    private var nextSessionId = 1
    private var selectedSessionId = -1

    private lateinit var outputView: TextView
    private lateinit var outputScroll: ScrollView
    private lateinit var commandInput: EditText
    private lateinit var sessionBar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildInterface()
        createSession()
    }

    private fun buildInterface() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(20), dp(14), dp(12))
            setBackgroundColor(0xFF10151D.toInt())
        }

        val header = TextView(this).apply {
            text = "NEXUS TERMINAL"
            textSize = 22f
            typeface = Typeface.MONOSPACE
            setTextColor(0xFF70E1B5.toInt())
        }
        root.addView(header)

        val subtitle = TextView(this).apply {
            text = "Android /system/bin/sh • app sandbox • Linux distro not installed"
            textSize = 11f
            setTextColor(0xFF9AA8B8.toInt())
            setPadding(0, dp(5), 0, dp(12))
        }
        root.addView(subtitle)

        val sessionScroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
        }
        sessionBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        sessionScroll.addView(sessionBar)
        root.addView(sessionScroll)

        outputScroll = ScrollView(this).apply {
            isFillViewport = true
        }
        outputView = TextView(this).apply {
            textSize = 13f
            typeface = Typeface.MONOSPACE
            setTextColor(0xFFE6EDF3.toInt())
            setTextIsSelectable(true)
            setLineSpacing(dp(3).toFloat(), 1f)
            setPadding(dp(4), dp(10), dp(4), dp(10))
        }
        outputScroll.addView(outputView)
        root.addView(outputScroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        val inputRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        commandInput = EditText(this).apply {
            hint = "Enter shell command..."
            textSize = 13f
            singleLine = true
            imeOptions = EditorInfo.IME_ACTION_DONE
            typeface = Typeface.MONOSPACE
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFF8492A3.toInt())
            setPadding(dp(10), dp(4), dp(10), dp(4))
            setBackgroundColor(0xFF202936.toInt())
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitCommand()
                    true
                } else {
                    false
                }
            }
        }
        inputRow.addView(commandInput, LinearLayout.LayoutParams(0, dp(50), 1f))

        val runButton = Button(this).apply {
            text = "RUN"
            setOnClickListener { submitCommand() }
        }
        inputRow.addView(runButton)
        root.addView(inputRow)

        setContentView(root)
    }

    private fun createSession() {
        if (sessions.size >= MAX_SESSIONS) {
            Toast.makeText(this, "Maximum of $MAX_SESSIONS sessions reached", Toast.LENGTH_SHORT).show()
            return
        }

        val id = nextSessionId++
        sessionLogs[id] = StringBuilder(
            "NEXUS Terminal • session $id\n" +
                "Commands run with the app's Android UID and permissions.\n" +
                "Try: pwd, ls, id, uname -a, echo hello\n\n"
        )
        val session = TerminalSession(
            id = id,
            workingDirectory = filesDir,
            onOutput = ::receiveOutput,
            onClosed = ::sessionClosed
        )
        sessions.add(session)
        selectedSessionId = id
        rebuildSessionBar()
        showSelectedSession()
    }

    private fun receiveOutput(id: Int, text: String) {
        mainHandler.post {
            val log = sessionLogs[id] ?: return@post
            log.append(text)
            if (log.length > MAX_LOG_CHARS) {
                log.delete(0, log.length - MAX_LOG_CHARS)
                log.insert(0, "[Earlier output trimmed]\n")
            }
            if (id == selectedSessionId) {
                showSelectedSession()
            }
        }
    }

    private fun sessionClosed(id: Int, reason: String?) {
        if (reason != null) {
            receiveOutput(id, "Shell stopped: $reason\n")
        } else {
            receiveOutput(id, "Shell process ended.\n")
        }
        mainHandler.post { rebuildSessionBar() }
    }

    private fun submitCommand() {
        val command = commandInput.text.toString().trim()
        if (command.isEmpty()) return

        val log = sessionLogs[selectedSessionId]
        log?.append("\n$ $command\n")

        when (command) {
            "clear" -> {
                log?.clear()
                log?.append("NEXUS Terminal • session $selectedSessionId\n")
                showSelectedSession()
            }
            "help" -> {
                log?.append(
                    "This is Android's restricted app shell, not a Linux distribution.\n" +
                        "Examples: pwd, ls, cd, id, uname -a, echo hello, cat, mkdir.\n" +
                        "Each session has its own shell process and working directory.\n" +
                        "No root access or Linux package manager is provided.\n"
                )
                showSelectedSession()
            }
            else -> {
                val active = sessions.firstOrNull { it.id == selectedSessionId }
                if (active == null) {
                    log?.append("No active session. Tap + to create one.\n")
                    showSelectedSession()
                } else {
                    active.execute(command)
                    showSelectedSession()
                }
            }
        }

        commandInput.text.clear()
    }

    private fun selectSession(id: Int) {
        if (sessions.none { it.id == id }) return
        selectedSessionId = id
        rebuildSessionBar()
        showSelectedSession()
    }

    private fun closeSelectedSession() {
        val selected = sessions.firstOrNull { it.id == selectedSessionId } ?: return
        selected.close()
        sessions.remove(selected)
        sessionLogs.remove(selected.id)

        if (sessions.isEmpty()) {
            selectedSessionId = -1
            createSession()
        } else {
            selectedSessionId = sessions.last().id
            rebuildSessionBar()
            showSelectedSession()
        }
    }

    private fun rebuildSessionBar() {
        if (!::sessionBar.isInitialized) return
        sessionBar.removeAllViews()

        sessions.forEach { session ->
            val button = Button(this).apply {
                text = if (session.id == selectedSessionId) {
                    "● S${session.id}"
                } else {
                    "S${session.id}"
                }
                isAllCaps = false
                textSize = 12f
                setOnClickListener { selectSession(session.id) }
            }
            sessionBar.addView(button)
        }

        sessionBar.addView(Button(this).apply {
            text = "+"
            contentDescription = "New terminal session"
            setOnClickListener { createSession() }
        })
        sessionBar.addView(Button(this).apply {
            text = "CLOSE"
            isAllCaps = false
            setOnClickListener { closeSelectedSession() }
        })
    }

    private fun showSelectedSession() {
        outputView.text = sessionLogs[selectedSessionId]?.toString()
            ?: "No terminal session. Tap + to create one."
        outputScroll.post { outputScroll.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        sessions.forEach { it.close() }
        sessions.clear()
        super.onDestroy()
    }

    companion object {
        private const val MAX_SESSIONS = 8
        private const val MAX_LOG_CHARS = 120_000
    }
}
