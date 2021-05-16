package ir.behnoudsh.aroundus.di.component

import dagger.Component
import ir.behnoudsh.aroundus.data.api.ApiHelper
import ir.behnoudsh.aroundus.di.module.ApiModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiModule::class])
interface ApiComponent {
    fun inject(apiHelper: ApiHelper)
}
