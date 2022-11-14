package com.filepicker.animals.di

import com.filepicker.animals.view_model.ListviewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiModule::class,PrefsModule::class,AppModule::class])
interface ViewModelComponent {
    fun inject(viewModel: ListviewModel)
}