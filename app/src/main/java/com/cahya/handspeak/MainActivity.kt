package com.cahya.handspeak

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.RectF
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cahya.handspeak.ml.Handspeak
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var camera: Button
    private lateinit var gallery: Button
    private lateinit var imageView: ImageView
    private lateinit var result: TextView
    private var imageSize = 32

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load labels using ModelLoader
        var labels = ModelLoader.loadLabels(this)

        camera = findViewById(R.id.buttonCamera)
        gallery = findViewById(R.id.buttonGallery)
        imageView = findViewById(R.id.imageView)
        result = findViewById(R.id.result)

        camera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, 3)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 100)
            }
        }

        gallery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, 1)
        }
    }

    private fun classifyImage(image: Bitmap) {
        try {
            val model = Handspeak.newInstance(applicationContext)

            // Create input buffer
            val imageTensor = TensorImage(DataType.FLOAT32)
            imageTensor.load(image)

            // Run model inference and get result
            val outputs = model.process(imageTensor)
            val detectionResult = outputs.detectionResultList[0]

            // Get result from DetectionResult
            val location = detectionResult.locationAsRectF
            val category = detectionResult.categoryAsString
            val score = detectionResult.scoreAsFloat

            // Release model resources if no longer used
            model.close()

            // Display the classification result
            val label = getLabel(category)
            displayClassificationResult(label, score, location)
        } catch (e: IOException) {
            // Handle the exception
            e.printStackTrace()
        }
    }

    // Function to get the label based on the category string
    private fun getLabel(category: String): String {
        // Replace this with your actual label lookup logic
        val labels = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")

        return if (category in labels) {
            category
        } else {
            "Unknown"
        }
    }

    // Function to display the classification result (modify as needed)
    private fun displayClassificationResult(label: String, score: Float, location: RectF) {
        runOnUiThread {
            result.text = "Sign Language: $label"
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                3 -> {
                    val image = data?.extras?.get("data") as Bitmap

                    // Increase the size of the captured image
                    val largerImageSize = 1080 // Set your desired larger size
                    val largerThumbnail = ThumbnailUtils.extractThumbnail(image, largerImageSize, largerImageSize)
                    imageView.setImageBitmap(largerThumbnail)

                    // Resize the image to the required size for classification
                    val scaledImage = Bitmap.createScaledBitmap(largerThumbnail, imageSize, imageSize, false)
                    classifyImage(scaledImage)
                }
                1 -> {
                    val uri = data?.data
                    var image: Bitmap? = null
                    try {
                        image = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    // Use the full-sized image for better accuracy
                    imageView.setImageBitmap(image)

                    image?.let {
                        // Resize the image to the required size for classification
                        val scaledImage = Bitmap.createScaledBitmap(it, imageSize, imageSize, false)
                        classifyImage(scaledImage)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}