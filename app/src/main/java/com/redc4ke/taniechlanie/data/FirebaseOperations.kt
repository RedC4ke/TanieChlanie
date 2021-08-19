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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.File
import java.nio.file.CopyOption

fun setImage(context: Context, name: String, iv: ImageView,
             url: Uri?) {

    val imageFile = File(context.filesDir, "$name.jpg")
    if (imageFile.exists()) {
        Glide.with(context).load(imageFile.absolutePath).into(iv)
    }
    if ("gs://" in url.toString()) {
        Log.d("setImage", "url: $url")
        val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url.toString())
        Log.d("setImage", "imageRef: $imageRef")
        imageRef.getFile(imageFile).addOnSuccessListener {
            Glide.with(context).load(imageFile).into(iv)
        }.addOnFailureListener {
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
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        } catch (e: Exception) {
            Log.d("setImage","$e")
        }
    }
}
