package ir.behnoudsh.aroundus

import android.app.Application
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import ir.behnoudsh.aroundus.data.model.ApiURL
import ir.behnoudsh.aroundus.di.component.DaggerDiComponent
import ir.behnoudsh.aroundus.di.component.DiComponent
import ir.behnoudsh.aroundus.di.module.DiModule

class App : Application() {

    companion object {
        lateinit var diComponent: DiComponent
    }

    private fun initDaggerComponent(): DiComponent {
        diComponent = DaggerDiComponent
            .builder()
            .diModule(DiModule(ApiURL.BASE_URL, applicationContext))
            .build()
        return diComponent
    }

    override fun onCreate() {
        super.onCreate()
        diComponent = initDaggerComponent()
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/IRANSansMobile_FaNum_.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )
    }
}
