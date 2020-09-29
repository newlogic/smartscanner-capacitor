package com.newlogic.mlkitlib.library.newlogic

import android.content.Context
import java.io.*


object FileUtils {
    private val tag = FileUtils::class.java.simpleName
    const val TAMWINI_PREFS = "TamwiniPrefs"
    const val TAMWINI_PREFS_TESS_FILE_OPS_DONE = "TESS_FILE_OPS_DONE"
    fun tesseractPathExists(context: Context): Boolean {
        val f = createTesseractFile(context)
        return f.isDirectory && f.exists()
    }

    fun getTesseractExtFilePathAsString(context: Context): String {
        var path = context.getExternalFilesDir(null)!!.absolutePath
        path = "$path/tessdata"
        return path
    }

    fun createTesseractSubDir(context: Context): Boolean {
        return if (!tesseractPathExists(context)) {
            val f = createTesseractFile(context)
            f.mkdir()
        } else {
            //directory exists
            true
        }
    }

    private fun createTesseractFile(context: Context): File {
        val extDirPath = getTesseractExtFilePathAsString(context)
//        Log.d(tag, "path: %s", extDirPath)
        return File(extDirPath)
    }

    fun copyFilesToSdCard(context: Context) {
        copyFileOrDir(
            "tessdata",
            context
        ) // copy all files in assets folder in my project
        val editor = context.getSharedPreferences(
            TAMWINI_PREFS,
            Context.MODE_PRIVATE
        ).edit()
        editor.putBoolean(TAMWINI_PREFS_TESS_FILE_OPS_DONE, true)
        editor.apply()
    }

    private fun copyFileOrDir(path: String, context: Context) {
        val assetManager = context.assets
        var assets: Array<String>?
        try {
//            Log.i(tag, "copyFileOrDir() %s", path)
            assets = assetManager.list(path)
            if (assets!!.isEmpty()) {
//                Log.i(tag, "copyFileOrDir() ; assets.length == 0 ")
                copyFile(path, context)
            } else {
//                Log.i(tag, "copyFileOrDir()  assets.length != 0 [" + assets.size + "]")
                val fullPath =
                    context.getExternalFilesDir(null).toString() + "/" + path
//                Timber.i(tag, "path=%s", fullPath)
                val dir = File(fullPath)
                if (!dir.exists()
                    && !path.startsWith("fallback-locales") && !path.startsWith("stored-locales")
                    && !path.startsWith("images") && !path.startsWith("public")
                    && !path.startsWith("sounds") && !path.startsWith("webkit")
                )
                    if (!dir.mkdirs()) {
                        //Timber.i(tag, "could not create dir %s", fullPath)
                    }
                for (i in assets.indices) {
                    var p: String
                    p = if (path == "") "" else "$path/"
                    if (!path.startsWith("fallback-locales") && !path.startsWith(
                            "stored" +
                                    "-locales"
                        ) &&
                        !path.startsWith("images") && !path.startsWith("public") &&
                        !path.startsWith("sounds") && !path.startsWith("webkit")
                    ) copyFileOrDir(p + assets[i], context)
                }
            }
        } catch (ex: IOException) {
//            Timber.e(ex, tag, "I/O Exception")
        }
    }

    private fun copyFile(filename: String, context: Context) {
        val assetManager = context.assets
        var `in`: InputStream?
        var out: OutputStream?
        var newFileName: String?
        try {
//            Timber.i(tag, "copyFile() %s", filename)
            `in` = assetManager.open(filename)
            newFileName =
                if (filename.endsWith(".jpg")) // extension was added to avoid compression on APK file
                    context.getExternalFilesDir(null).toString() + "/" + filename.substring(
                        0,
                        filename.length - 4
                    ) else context.getExternalFilesDir(null).toString() + "/" + filename
            out = FileOutputStream(newFileName)
            val buffer = ByteArray(1024)
            var read: Int
            while (`in`.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            `in`.close()
            out.flush()
            out.close()
        } catch (e: Exception) {
//            Timber.e(tag, "Exception in copyFile() of %s", newFileName)
//            Timber.e(tag, "Exception : %s", e.toString())
        }
    }
}

