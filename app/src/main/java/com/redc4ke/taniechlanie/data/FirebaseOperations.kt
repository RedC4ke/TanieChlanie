package com.redc4ke.taniechlanie.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.File
import java.nio.file.CopyOption

fun setImage(context: Context, name: String, iv: ImageView,
             url: Uri?) {

    val imageFile = File(context.filesDir, "$name.jpg")
    Log.d("setImage", "url: $url")
    if (File(context.filesDir, "$name.jpg").exists()) {
        iv.setImageBitmap(BitmapFactory.decodeFile(imageFile.path))
    }
    if ("jabot" in url.toString()) {
        val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url.toString())
        imageRef.getFile(imageFile).addOnSuccessListener {
            iv.setImageBitmap(BitmapFactory.decodeFile(imageFile.path))
            Log.d("setImage","success ${imageFile.path}")
        }.addOnFailureListener {
            Log.d("setImage","$it")
        }
    } else {
        try {
            Glide.with(context).asBitmap().load(url)
                .into(object: CustomTarget<Bitmap>(){
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        iv.setImageBitmap(resource)
                        val tempFile = imageFromBitmap(context, resource, name)
                        imageFile.delete()
                        tempFile.copyTo(imageFile)
                        Log.d("setImage","success ${imageFile.path}")
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        } catch (e: Exception) {
            Log.d("setImage","$e")
        }
    }
}