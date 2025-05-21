package com.example.birdapp

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil

class BirdClassifier(context: Context) {
    private val tfliteModel = FileUtil.loadMappedFile(context, "best.tflite")
    private val interpreter = Interpreter(tfliteModel)

    fun predict(bitmap: Bitmap): String {
        val resized = Bitmap.createScaledBitmap(bitmap, 640, 640, true)
        val input = preprocess(resized)
        val output = Array(1) { Array(8400) { FloatArray(6) } }  // Adjust based on model

        interpreter.run(input, output)
        return postprocess(output)
    }

    private fun preprocess(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val input = Array(1) { Array(640) { Array(640) { FloatArray(3) } } }

        for (y in 0 until 640) {
            for (x in 0 until 640) {
                val pixel = bitmap.getPixel(x, y)
                input[0][y][x][0] = ((pixel shr 16 and 0xFF) / 255.0f) // R
                input[0][y][x][1] = ((pixel shr 8 and 0xFF) / 255.0f)  // G
                input[0][y][x][2] = ((pixel and 0xFF) / 255.0f)        // B
            }
        }

        return input
    }

    private fun postprocess(output: Array<Array<FloatArray>>): String {
        var maxScore = 0f
        var bestClassId = -1

        for (i in output[0].indices) {
            val conf = output[0][i][4]  // Confidence score
            if (conf > maxScore) {
                maxScore = conf
                bestClassId = output[0][i][5].toInt()  // Class ID
            }
        }

        // Map class ID to bird species name
        val classNames = listOf("Bar-tailed Godwit", "Barn Swallow", "Black-bellied Plover", "Black-crowned Night Heron", "Chirruping Nightjar", "Eurasian Tree Sparrow", "Gray-rumped Swiftlet", "Great Knot", "Marsh Sandpiper", "Pied Avocet", "Rock Pigeon", "Rose-ringed Parakeet", "Scaly-breast Munia", "Whiskered Tern", "Wood Sandpiper") // Replace with your actual classes
        return if (bestClassId in classNames.indices) classNames[bestClassId] else "Unknown"
    }

}
