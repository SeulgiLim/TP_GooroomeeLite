package kr.co.gooroomeelite.views.statistics.share.util

import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import kr.co.gooroomeelite.R
import java.io.File
import java.io.FileOutputStream

object PathUtil {

    fun getPath(context: Context, uri: Uri): String? {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return context.getExternalFilesDir(null)?.absolutePath.toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), id.toLong()
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    @TargetApi(Build.VERSION_CODES.Q)
    fun getOutputDirectoryAndWrite(
        resolver: ContentResolver,
        uri: Uri,
        write: (FileOutputStream) -> Unit
    ) {
        resolver.openFileDescriptor(uri, "w")?.use {
            write(FileOutputStream(it.fileDescriptor))
        }
    }

    fun getOutputDirectory(activity: Activity): File = with(activity) {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

}

//object PathUtil {
//    fun getPath(context: Context, uri: Uri?): String? {
//        if (DocumentsContract.isDocumentUri(context, uri)) {
//            if (isExternalStorageDocument(uri)) { //외장스토리지 다큐먼트를 확인하기 위해
//                val docId = DocumentsContract.getDocumentId(uri)
//                val split = docId.split(":").toTypedArray()
//                val type = split[0]
//                if ("primary".equals(type, ignoreCase = true)) {
//                    return context.getExternalFilesDir(null)?.absolutePath.toString() + "/" + split[1]
//                }
//            } else if (isDownloadsDocument(uri)) {
//                val id = DocumentsContract.getDocumentId(uri)
//                val contentUri = ContentUris.withAppendedId(
//                    Uri.parse("content://downloads/public_downloads"), id.toLong()
//                )
//                return getDataColumn(context, contentUri, null, null)
//            } else if (isMediaDocument(uri)) {//그중에서도 이미지 파일을 가지고 올 것이기 때문에 여기있는
//                val docId = DocumentsContract.getDocumentId(uri)
//                val split = docId.split(":").toTypedArray()
//                val type = split[0]
//                var contentUri: Uri? = null
//                if ("image" == type) {
//                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI // 여기서 이미지 파일을 가지고 갈 것이기 때문에 content_uri를 가져오기 되
//                } else if ("video" == type) {
//                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//                } else if ("audio" == type) {
//                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//                }
//                val selection = "_id=?"
//                val selectionArgs = arrayOf(
//                    split[1]
//                )
//                //위에 있는 uri와 content를 읽여 들여서 반환을 해주게 된다.
//                return getDataColumn(context, contentUri, selection, selectionArgs)
//            }
//        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
//            return getDataColumn(context, uri, null, null)
//        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
//            return uri.path
//        }
//        return null
//    }
//
//    private fun getDataColumn(
//        context: Context,
//        uri: Uri?,
//        selection: String?,
//        selectionArgs: Array<String>?
//    ): String? {
//        var cursor: Cursor? = null
//        val column = "_data"
//        val projection = arrayOf(column)
//        try {
//            cursor = context.contentResolver.query(
//                uri!!, projection, selection, selectionArgs,
//                null
//            )
//            if (cursor != null && cursor.moveToFirst()) {
//                val column_index = cursor.getColumnIndexOrThrow(column)
//                return cursor.getString(column_index)
//            }
//        } finally {
//            cursor?.close()
//        }
//        return null
//    }
//
//    //외장 스토이지 다큐먼트인지 확인을 해주기 위해서 다음과 같이 넣어서 확인힌다.!!
//    private fun isExternalStorageDocument(uri: Uri): Boolean { //외장 스토리지 다큐먼트
//        return "com.android.externalstorage.documents" == uri.authority //권한
//    }
//
//    private fun isDownloadsDocument(uri: Uri): Boolean {
//        return "com.android.providers.downloads.documents" == uri.authority
//    }
//
//    private fun isMediaDocument(uri: Uri): Boolean {
//        return "com.android.providers.media.documents" == uri.authority
//    }
//
//
//    fun getOutputDirectory(activity: Activity): File = with(activity){
//        val mediaDir = externalMediaDirs.firstOrNull()?.let{//activity에서 externalMediaDirs하는 함수에 접근해서 만약에 null이 아니면
//            File(it,getString(R.string.app_name)).apply{mkdirs()}}//현재 외장 디렉토리에 app_name(gooroomelite)의 갤러리용 폴더를 만들어서 파일을 넣어준다.
//        return if(mediaDir != null && mediaDir.exists())
//                mediaDir else filesDir
//
//
//    }
//}