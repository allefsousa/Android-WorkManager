package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R
import timber.log.Timber

/**
 * @author allef.santos on 02/09/20
 */
class BlurWorker (ctx: Context, params: WorkerParameters): Worker(ctx, params) {
    override fun doWork(): Result {
        val appContext = applicationContext
        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("Blurring image", appContext)

        sleep()


        return try {
            val resolver = appContext.contentResolver

            val picture = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)))

            val output = blurBitmap(picture, appContext)

            // Write bitmap to a temp file
            val outputUri = writeBitmapToFile(appContext, output)

            makeStatusNotification("Output is $outputUri", appContext)

            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())


            Result.success(outputData)
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error applying blur")
            Result.failure()
        }
    }
}