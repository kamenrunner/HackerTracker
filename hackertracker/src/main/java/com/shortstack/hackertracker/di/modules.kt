package com.shortstack.hackertracker.di

import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.shortstack.hackertracker.utilities.Analytics
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.utilities.NotificationHelper
import com.shortstack.hackertracker.utilities.Storage
import com.shortstack.hackertracker.utilities.TickTimer
import org.koin.dsl.module.module

val appModule = module {

    single { TickTimer() }
    single { Storage(get()) }
    single { NotificationHelper(get()) }
    single { GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create() }
    single { FirebaseJobDispatcher(GooglePlayDriver(get())) }
    single { DatabaseManager(get()) }

    single { Analytics(get()) }

}