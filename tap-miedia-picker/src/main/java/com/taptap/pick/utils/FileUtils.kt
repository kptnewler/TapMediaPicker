package com.taptap.pick.utils

import android.content.Context
import android.net.Uri
import com.taptap.pick.data.constant.FileSizeUnitConstant
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.Closeable
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale
import kotlin.math.roundToInt

object FileUtils {

    fun copyFile(
        context: Context,
        from: String,
        target: String,
    ): String? {
        var inBuffer: BufferedInputStream? = null
        var osBuffer: BufferedOutputStream? = null
        var fileInputStream: InputStream? = null
        return try {
            fileInputStream = if (MediaUtils.isContent(from)) {
                context.contentResolver.openInputStream(Uri.parse(from))
            } else {
                FileInputStream(from)
            }
            inBuffer = BufferedInputStream(fileInputStream)
            osBuffer = BufferedOutputStream(FileOutputStream(target))
            val data = ByteArray(1024)
            var len: Int
            while (inBuffer.read(data).also { len = it } != -1) {
                osBuffer.write(data, 0, len)
            }
            osBuffer.flush()
            target
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            close(inBuffer)
            close(osBuffer)
            close(fileInputStream)
        }
    }

    fun writeFileFromIS(input: InputStream, os: OutputStream): Boolean {
        var osBuffer: OutputStream? = null
        var isBuffer: BufferedInputStream? = null
        return try {
            isBuffer = BufferedInputStream(input)
            osBuffer = BufferedOutputStream(os)
            val data = ByteArray(1024)
            var len: Int
            while (isBuffer.read(data).also { len = it } != -1) {
                os.write(data, 0, len)
            }
            os.flush()
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        } finally {
            close(isBuffer)
            close(osBuffer)
        }
    }

    fun createFileName(tag: String): String {
        return "${tag}_${DateUtils.sf.format(System.currentTimeMillis())}"
    }


    /**
     * Size of byte to fit size of memory.
     *
     * to three decimal places
     *
     * @param byteSize  Size of byte.
     * @return fit size of memory
     */
    fun formatAccurateUnitFileSize(byteSize: Long): String {
        var unit = ""
        val newByteSize: Double
        require(byteSize >= 0) { "byteSize shouldn't be less than zero!" }
        if (byteSize < FileSizeUnitConstant.ACCURATE_KB) {
            newByteSize = byteSize.toDouble()
        } else if (byteSize < FileSizeUnitConstant.ACCURATE_MB) {
            unit = "KB"
            newByteSize = byteSize.toDouble() / FileSizeUnitConstant.ACCURATE_KB
        } else if (byteSize < FileSizeUnitConstant.ACCURATE_GB) {
            unit = "MB"
            newByteSize = byteSize.toDouble() / FileSizeUnitConstant.ACCURATE_MB
        } else {
            unit = "GB"
            newByteSize = byteSize.toDouble() / FileSizeUnitConstant.ACCURATE_GB
        }
        val format = String.format(Locale("zh"), "%." + 2 + "f", newByteSize)
        return (if (toDouble(format).roundToInt() - toDouble(format) == 0.0) toDouble(format).roundToInt() else format).toString() + unit
    }

    private fun toDouble(o: Any?, defaultValue: Int = 0): Double {
        if (o == null) {
            return defaultValue.toDouble()
        }
        val value: Double = try {
            o.toString().trim { it <= ' ' }.toDouble()
        } catch (e: Exception) {
            defaultValue.toDouble()
        }
        return value
    }

    fun close(c: Closeable?) {
        if (c is Closeable) {
            try {
                c.close()
            } catch (e: Exception) {
                // silence
            }
        }
    }
}