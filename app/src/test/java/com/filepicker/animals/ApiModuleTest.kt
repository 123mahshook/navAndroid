package com.filepicker.animals

import com.filepicker.animals.di.ApiModule
import com.filepicker.animals.model.AnimalApiService

class ApiModuleTest(val mockService: AnimalApiService):ApiModule() {
    override fun provideAnimalApiService(): AnimalApiService {
        return mockService
    }

}