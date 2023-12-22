package com.cahya.handspeak

import android.content.Context
import java.io.IOException

object ModelLoader {

    // Function to load labels from labels.txt
    fun loadLabels(context: Context): List<String> {
        val labels: MutableList<String> = mutableListOf()

        try {
            context.assets.open("labels.txt").use { inputStream ->
                labels.addAll(inputStream.bufferedReader().readLines())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return labels
    }
}
