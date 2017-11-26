package com.loloof64.android.basicchessendgamestrainer

import android.app.Application
import android.content.Context

class MyApplication: Application(){

    companion object {
        fun getApplicationContext() = appContext
        fun setApplicationContext(ctx: Context) {
            appContext = ctx
        }

        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        setApplicationContext(this)
    }

}
