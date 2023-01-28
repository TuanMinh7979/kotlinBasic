package com.example.drawapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.get
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.provider.MediaStore
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private var drawingView: DrawingView? = null
    private var mImageButtonCurrentPaint: ImageButton? = null

    var customProgressDialog: Dialog? = null

    private var openGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { rs ->
            if (rs.resultCode == RESULT_OK && rs.data != null) {
                val imageBg: ImageView = findViewById(R.id.iv_background)

                imageBg.setImageURI(rs.data?.data)
            }
        }

    //phan tich doan code nay voi link sau: https://developer.android.com/guide/topics/ui/dialogs?hl=vi#java
    private val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions.entries.forEach {
                val perName = it.key
                val isGranted = it.value

                if (isGranted) {
                    if (perName == Manifest.permission.READ_EXTERNAL_STORAGE) {


                        val pickIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                        openGalleryLauncher.launch(pickIntent)
                    }


                } else {
                    if (perName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(this, "Xin quyền truy cập file thất bại", Toast.LENGTH_LONG)
                            .show()

                    }
                }
            }


        }

    private val justRequestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions.entries.forEach {
                val perName = it.key
                val isGranted = it.value

                if (isGranted) {
                    if (perName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(this, "Đã cấp quyền truy cập file", Toast.LENGTH_LONG)
                            .show()

                    }


                } else {
                    if (perName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(this, "Xin quyền truy cập file thất bại", Toast.LENGTH_LONG)
                            .show()

                    }
                }
            }


        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView = findViewById(R.id.drawing_view)
        drawingView?.setSizeForBrush(5.toFloat())

        val ibBrush: ImageButton = findViewById(R.id.ib_brush)
        ibBrush.setOnClickListener {

            showBrushSizeChooserDialog()
        }
        val ibUndo: ImageButton = findViewById(R.id.ib_undo)
        ibUndo.setOnClickListener {
            drawingView?.onClickUndo()

        }

        val linearLayoutPaintColors = findViewById<LinearLayout>(R.id.ll_paint_colors)
        mImageButtonCurrentPaint = linearLayoutPaintColors[1] as ImageButton

        mImageButtonCurrentPaint?.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_pressed)

        )
        val ibGallery: ImageButton = findViewById(R.id.ib_gallery)
        ibGallery.setOnClickListener {
            requestStoragePermission()

        }
        val ibSave: ImageButton = findViewById(R.id.ib_save)
        //set onclick listener
        ibSave.setOnClickListener {
            //check if permission is allowed
            println("-------------run here")
            if (isReadStorageAllowed()) {
                println("-------------run here")
                showProgressDialog()
                //launch a coroutine block
                lifecycleScope.launch {
                    //reference the frame layout
                    val flDrawingView: FrameLayout = findViewById(R.id.fl)
                    //Save the image to the device
                    println("-------------run here")

                    saveBitmapFile(getBitmapFromView(flDrawingView))
                }
            } else {
                justRequestStoragePermission()
            }
        }


    }


    private fun showBrushSizeChooserDialog() {
        var brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("BrushSize: ")
        val smallBtn: ImageButton = brushDialog.findViewById(R.id.ib_small_brush)
        smallBtn.setOnClickListener {

            drawingView?.setSizeForBrush(5.toFloat())
            brushDialog.dismiss()
        }

        val mediumBtn: ImageButton = brushDialog.findViewById(R.id.ib_medium_brush)
        mediumBtn.setOnClickListener {

            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }

        val largeBtn: ImageButton = brushDialog.findViewById(R.id.ib_large_brush)
        largeBtn.setOnClickListener {

            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }


        brushDialog.show()

    }


    fun paintClicked(view: View) {

        if (view !== mImageButtonCurrentPaint) {
            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            drawingView?.setColor(colorTag)
            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed)

            )
            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_normal)

            )

            mImageButtonCurrentPaint = view
        }


    }


    private fun requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_EXTERNAL_STORAGE

            )
        ) {
            showRationaleDialog("Drawapp", "Ứng dụng không đc cấp quyền truy cập")
        } else {
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    private fun justRequestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_EXTERNAL_STORAGE

            )
        ) {
            showRationaleDialog("Drawapp", "Ứng dụng không đc cấp quyền truy cập")
        } else {
            justRequestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    private fun showRationaleDialog(title: String, message: String) {


        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title).setMessage(message)
            .setPositiveButton("Hủy") { dialog, _ ->

                dialog.dismiss()

            }
        builder.create().show()
    }


    private fun isReadStorageAllowed(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        )

        /**
         *
         * @return {@link android.content.pm.PackageManager#PERMISSION_GRANTED} if you have the
         * permission, or {@link android.content.pm.PackageManager#PERMISSION_DENIED} if not.
         *
         */
        //If permission is granted returning true and If permission is not granted returning false
        return result == PackageManager.PERMISSION_GRANTED
    }


    private fun getBitmapFromView(view: View): Bitmap {

        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(returnedBitmap)


        val bgDrawable = view.background
        bgDrawable.draw(canvas)


        view.draw(canvas)

        return returnedBitmap
    }


    private fun showProgressDialog() {
        customProgressDialog = Dialog(this)

        customProgressDialog?.setContentView(R.layout.dialog_custom_progress)

        customProgressDialog?.show()
    }

    private fun cancelProgressDialog() {
        if (customProgressDialog != null) {
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }

    private suspend fun saveBitmapFile(mBitmap: Bitmap?): String {
        var result = ""
        withContext(Dispatchers.IO) {
            if (mBitmap != null) {
                println("-------------run here bit map not null")


                try {
                    val bytes = ByteArrayOutputStream() // Creates a new byte array output stream.
                    // The buffer capacity is initially 32 bytes, though its size increases if necessary.

                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)


                    var filePathStr:String= externalCacheDir?.absoluteFile.toString()+ File.separator + "drawapp_" + System.currentTimeMillis() / 1000 + ".jpg"

                    println("++++++++++++++++++++++++File path $filePathStr")
                    val f = File(
                        externalCacheDir?.absoluteFile.toString()
                                + File.separator + "drawapp_" + System.currentTimeMillis() / 1000 + ".jpg"
                    )

                    val fo =
                        FileOutputStream(f) // Creates a file output stream to write to the file represented by the specified object.
                    fo.write(bytes.toByteArray()) // Writes bytes from the specified byte array to this file output stream.
                    fo.close() // Closes this file output stream and releases any system resources associated with this stream. This file output stream may no longer be used for writing bytes.
                    result = f.absolutePath // The file absolute path is return as a result.
                    //We switch from io to ui thread to show a toast
                    runOnUiThread {
                        cancelProgressDialog()
                        if (!result.isEmpty()) {

                            println("-------------Result not null ${result}")
                            Toast.makeText(
                                this@MainActivity,
                                "Lưu thành công :$result",
                                Toast.LENGTH_SHORT
                            ).show()
//                            shareImage(result)
                        } else {
                            println("-------------Result is null")

                            Toast.makeText(
                                this@MainActivity,
                                "Lưu thất bại",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    result = ""
                    println(e)
                    e.printStackTrace()
                }
            }
        }
        return result
    }


}

