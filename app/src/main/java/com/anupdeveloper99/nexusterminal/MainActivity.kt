package com.anupdeveloper99.nexusterminal

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

/**
 * Initial NEXUS Terminal screen.
 * UI scaffold only: shell command execution is not connected yet.
 */
class MainActivity : Activity() {
    private lateinit var outputView: TextView
    private lateinit var commandInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 24, 20, 16)
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
            text = "Android terminal environment • v0.1.0-alpha"
            textSize = 12f
            setTextColor(0xFF9AA8B8.toInt())
            setPadding(0, 6, 0, 18)
        }
        root.addView(subtitle)

        val scroll = ScrollView(this)
        outputView = TextView(this).apply {
            text = "NEXUS Terminal [development build]\n---------------------------------\nApplication UI initialized.\nCommand engine: not connected.\n\nType help and tap RUN to test the interface.\n"
            textSize = 14f
            typeface = Typeface.MONOSPACE
            setTextColor(0xFFE6EDF3.toInt())
            setLineSpacing(5f, 1f)
        }
        scroll.addView(outputView)
        root.addView(scroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        commandInput = EditText(this).apply {
            hint = "Enter a command..."
            textSize = 14f
            singleLine = true
            typeface = Typeface.MONOSPACE
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFF8492A3.toInt())
            setBackgroundColor(0xFF202936.toInt())
        }
        root.addView(commandInput)

        root.addView(Button(this).apply {
            text = "RUN"
            setOnClickListener { submitCommand() }
        })

        setContentView(root)
    }

    private fun submitCommand() {
        val command = commandInput.text.toString().trim()
        if (command.isEmpty()) return

        outputView.append("\n$ $command\n")
        outputView.append("NEXUS: command execution is not implemented in this build.\n")
        commandInput.text.clear()
    }
}
