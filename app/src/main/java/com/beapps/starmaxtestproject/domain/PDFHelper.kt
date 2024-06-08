package com.beapps.starmaxtestproject.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.widget.Toast
import com.beapps.starmaxtestproject.R
import java.io.IOException
import java.io.OutputStream

object PDFHelper {

    fun saveImageToPDF(context: Context, bitmap: Bitmap?, outputStream: OutputStream) {
        // create bitmap for the logo
        var logo = BitmapFactory.decodeResource(context.resources, R.drawable.logo_manga3)
        logo = Bitmap.createScaledBitmap(logo , 200 , 68        , true)
        bitmap?.let {
            val height = bitmap.height + logo.height + 200
            val paint = Paint()
//            paint.textSize = 35f
            val document = PdfDocument()
            val pageInfo = PageInfo.Builder(bitmap.width, height, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
//            canvas.drawText("Hm-10 Reading", pageInfo.pageWidth/3f, 100f, paint)
            //title.draw(canvas)
            val centerX: Float = (canvas.width - logo.width) / 2f

            canvas.drawBitmap(
                logo, centerX, 100f,
                paint
            )

            canvas.drawBitmap(
                bitmap,
                null,
                Rect(0, 150 + logo.height, bitmap.width, bitmap.height),
                null
            )
            document.finishPage(page)
            try {
                document.writeTo(outputStream)
                document.close()
                outputStream.close()
                Toast.makeText(context, "File saved", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } ?: Toast.makeText(context, "An Error Occurred Please Try again", Toast.LENGTH_SHORT)
            .show()
        outputStream.close()
    }
}