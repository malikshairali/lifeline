package io.github.malikshairali.lifeline

import android.app.Application
import io.github.malikshairali.lifeline.di.AppModule
import io.github.malikshairali.lifeline.di.RoomModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class Lifeline : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@Lifeline)
            androidLogger()
            modules(
                AppModule().module,
                RoomModule().module
            )
        }
    }
}
