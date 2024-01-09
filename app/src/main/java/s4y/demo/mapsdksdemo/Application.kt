package s4y.demo.mapsdksdemo

import s4y.demo.mapsdksdemo.di.Di

class Application: android.app.Application() {
    override fun onCreate() {
        Di.init(this)
        super.onCreate()
    }
}