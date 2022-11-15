package com.filepicker.animals

import android.app.Application
import com.filepicker.animals.di.PrefsModule
import com.filepicker.animals.util.SharedPreferencesHelper

class PrefsModuleTest(val mockPrefs:SharedPreferencesHelper):PrefsModule() {
    override fun provideSharedPreferences(app: Application): SharedPreferencesHelper {
        return mockPrefs
    }
}