package info.nightscout.androidaps.plugins.general.themes

import androidx.appcompat.app.AppCompatDelegate
import dagger.android.HasAndroidInjector
import info.nightscout.androidaps.R
import info.nightscout.androidaps.events.EventPreferenceChange
import info.nightscout.androidaps.events.EventThemeSwitch
import info.nightscout.androidaps.interfaces.PluginBase
import info.nightscout.androidaps.interfaces.PluginDescription
import info.nightscout.androidaps.interfaces.PluginType
import info.nightscout.androidaps.logging.AAPSLogger
import info.nightscout.androidaps.plugins.bus.RxBusWrapper
import info.nightscout.androidaps.utils.resources.ResourceHelper
import info.nightscout.androidaps.utils.sharedPreferences.SP
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeSwitcherPlugin @Inject constructor(
    injector: HasAndroidInjector,
    resourceHelper: ResourceHelper,
    aapsLogger: AAPSLogger,
    private val rxBusWrapper: RxBusWrapper,
    private val sp: SP
) : PluginBase(PluginDescription()
    .mainType(PluginType.GENERAL)
    .neverVisible(true)
    .alwaysEnabled(true)
    .showInList(false),
    aapsLogger, resourceHelper, injector
) {

    private val compositeDisposable = CompositeDisposable()

    override fun onStart() {
        compositeDisposable.add(rxBusWrapper.toObservable(EventPreferenceChange::class.java).subscribe {
            if (it.isChanged(resourceHelper, id = R.string.key_use_darkmode)) switchTheme()
        })
    }

    private fun switchTheme() {
        if (sp.getBoolean(R.string.key_use_darkmode, true)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        rxBusWrapper.send(EventThemeSwitch())
    }

    override fun onStop() {
        compositeDisposable.dispose()
    }
}