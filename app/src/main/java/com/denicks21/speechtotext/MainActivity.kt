package com.denicks21.speechtotext

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private var textOutput: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textOutput = findViewById(R.id.textOutput)

        findViewById<Button>(R.id.speech_button).setOnClickListener {
            speech()
        }

        findViewById<Button>(R.id.save_button).setOnClickListener {
            saveFile()
        }
    }

    // Voice input
    fun speech() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        try {
            startActivityForResult(intent, REQUEST_CODE)
        } catch (a: ActivityNotFoundException) {
        }
    }

    // Convert voice input to text
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE -> {
                if (resultCode == RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    textOutput!!.text = result!![0]
                }
            }
        }
    }

    // Save voice input as text file
    fun saveFile() {
        if (textOutput?.length() == 0) {
            // Message of missing input
            Toast.makeText(baseContext, "Missing input", Toast.LENGTH_SHORT).show()

        } else {
            // Save the file
            var output = File(applicationContext.getExternalFilesDir(null),
                "fileText.txt")

            // If file exists do not overwrite but increment name
            var n = 0
            while (output.exists()) {
                ++n
                output = File(applicationContext.getExternalFilesDir(null),
                    "fileText$n.txt")
            }

            try {
                val fileout = FileOutputStream(output)
                val outputWriter = OutputStreamWriter(fileout)
                outputWriter.write(textOutput!!.text.toString())
                outputWriter.close()

                // Confirmation message saving
                val savedUri = Uri.fromFile(output)
                val msg = "File salvato: "+savedUri!!.lastPathSegment
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 100
    }
}