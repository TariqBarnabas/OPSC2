package com.example.myapplication


import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {


    //create variable for reference

    val imageView = findViewById<ImageView>(R.id.imageView)

    //constants that show which mode is picked(used later)
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        //imageview code to handle when a person drops an image
        imageView.setOnDragListener(OnDragListener { v, event ->
            val action = event.action
            when (action) {
                DragEvent.ACTION_DROP -> {

                    handleImageDrop(event.clipData.getItemAt(0).uri)
                    true
                }

                else -> false
            }
        })

        //click listerner event to allow a person to upload an image from storage
        imageView.setOnClickListener(View.OnClickListener { // Open image picker
            val pickImageIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICK)
        })
    }

    //dropped image method
    private fun handleImageDrop(imageUri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            imageView!!.setImageBitmap(bitmap)
            // Save the dropped image to storage if needed
            saveImageToStorage(bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //method that saves image to storage
    private fun saveImageToStorage(bitmap: Bitmap): String? {

        val directory = applicationContext.getDir("imageDir", MODE_PRIVATE)

        //creates a temp file to save the image
        val file = File(directory, "image_" + System.currentTimeMillis() + ".jpg")
        return try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file.absolutePath //basically returns the path where the iamge is saved
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val extras = data?.extras
                    if (extras != null) {
                        val imageBitmap = extras["data"] as Bitmap?
                        if (imageBitmap != null) {
                            imageView?.setImageBitmap(imageBitmap)
                            // Save the captured image to storage if needed
                            saveImageToStorage(imageBitmap)
                        }
                    }
                }
            }
        }
    }
}