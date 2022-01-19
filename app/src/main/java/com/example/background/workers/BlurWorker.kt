package com.example.background.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.KEY_IMAGE_URI
import com.example.background.R
import java.lang.IllegalArgumentException

class BlurWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val appContext = applicationContext

        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("Starting Blurring process", appContext)

        sleep()

        return try {

            if(TextUtils.isEmpty(resourceUri)){
                Log.e(TAG, "Invalid input uri")
                throw IllegalArgumentException("Invalid Input Uri")
            }

            val picture = BitmapFactory.decodeStream(appContext.contentResolver.openInputStream(Uri.parse(resourceUri)))

            val blurredBitmap = blurBitmap(picture, appContext)
            val uri = writeBitmapToFile(appContext, blurredBitmap)

            makeStatusNotification("Output is $uri", appContext)

            val data = Data.Builder().putString(KEY_IMAGE_URI, uri.toString()).build()

            Result.success(data)
        } catch (e: Throwable) {
            Log.e(TAG, "Error applying blur.")
            Result.failure()
        }
    }
}