package com.denicks21.speechtotext

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private var textOutput: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RequestPermissions()
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
            var output = File(getExternalFilesDir("")?.absolutePath, "TextFile.txt")

            // If file exists do not overwrite but increment name
            var n = 0
            while (output.exists()) {
                ++n
                output = File(getExternalFilesDir("")?.absolutePath,"TextFile$n.txt")
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

    private fun RequestPermissions() {
        ActivityCompat.requestPermissions(this@MainActivity,
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_CODE)
    }

    companion object {
        private const val REQUEST_CODE = 100
    }
}