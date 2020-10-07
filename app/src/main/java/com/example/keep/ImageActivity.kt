package com.example.keep

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.activity_image.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImageActivity : AppCompatActivity() {
    private val db get() = Database.getInstance(this)

    private val items = mutableListOf<TextItems>()

    private lateinit var adapter: KeepRecyclerAdapter
    val REQUEST_CODE = 100
    private lateinit var currentPhotoPath: String
    private lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getLongExtra(MainActivity.EXTRA_ID, 0)
        val item = db.textItemDao().getItemById(id)

       if (id > 0 ){
           imageView.scaleType = ImageView.ScaleType.FIT_XY
           imageView.setImageURI(item.text_image.toUri())
       }

    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)

    }


    private fun removeItem() {
        val id = intent.getLongExtra(MainActivity.EXTRA_ID, 0)
        val item = db.textItemDao().getItemById(id)
        db.textItemDao().delete(item)
        val intent = Intent().putExtra(MainActivity.EXTRA_ID, item.tid).putExtra(
            MainActivity.EXTRA2,
            3L
        )
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)?.let {
            val photoFile = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            photoFile?.let {
                photoUri = FileProvider.getUriForFile(
                    this,
                    "com.example.keep.fileprovider",
                    it
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        }else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        }

        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).also     {
            currentPhotoPath = it.absolutePath
        }
    }

    private fun setPictures(){
        val bmOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath)

            val scaleFactor = Math.max(
                1, Math.min(
                    outHeight / imageView.height,
                    outWidth / imageView.width
                )
            )

            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        val bipmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        imageView.setScaleType(ImageView.ScaleType.FIT_XY)
        imageView.setImageBitmap(bipmap)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addToGalleryMediaStore(){
        val file = File(currentPhotoPath)
        val bitmap =
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, photoUri))
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val imageMediaTableUri =
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val imageUri = contentResolver.insert(imageMediaTableUri, values)!!
        contentResolver.openOutputStream(imageUri).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        contentResolver.update(imageUri, values, null, null)
    }

    private fun addToGallery(){
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)

        val file = File(currentPhotoPath)
        val uri = Uri.fromFile(file)

        intent.data = uri
        sendBroadcast(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //camera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            setPictures()
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                addToGalleryMediaStore()
            }
            else{
                addToGallery()
            }

            val item = TextItems("", "", "", currentPhotoPath)
            item.tid = db.textItemDao().insertAll(item).first()
        }
        //gallery
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            imageView.setImageURI(data?.data) // handle chosen image

//            val item = TextItems("", "", "", data?.data)
//            item.tid = db.textItemDao().insertAll(item).first()
        }
    }

    //menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val id = intent.getLongExtra(MainActivity.EXTRA_ID, 0)
        if (id > 0)
            menuInflater.inflate(R.menu.image_menu_settings_act, menu)
        else
            menuInflater.inflate(R.menu.image_menu_settings, menu)
        return true
    }

    //menu buttons
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent().putExtra(MainActivity.EXTRA2, 3L)
                setResult(RESULT_OK, intent)
                finish()
                true
            }
            R.id.camera -> {

                takePicture()

                true
            }
            R.id.gallery -> {

                openGalleryForImage()

                true
            }
            R.id.deleteItem -> {

                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.delete)
                    .setMessage(R.string.delete_info)
                    .setPositiveButton(R.string.delete) { _, _ ->
                        removeItem()
                        Toast.makeText(this, R.string.item_delete, Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                val dialog = builder.create()
                dialog.show()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }

}