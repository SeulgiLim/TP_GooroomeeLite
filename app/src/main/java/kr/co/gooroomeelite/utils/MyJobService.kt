package kr.co.gooroomeelite.utils

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MyJobService : JobService() {

    companion object {
        private val TAG= "MyJobService"
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.e("TEST","Job")
        return false

    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.e("TEST","Stop")
        return false
    }
}