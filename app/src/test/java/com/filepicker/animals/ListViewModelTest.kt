package com.filepicker.animals

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.filepicker.animals.di.AppModule
import com.filepicker.animals.di.DaggerViewModelComponent
import com.filepicker.animals.model.Animal
import com.filepicker.animals.model.AnimalApiService
import com.filepicker.animals.model.ApiKey
import com.filepicker.animals.util.SharedPreferencesHelper
import com.filepicker.animals.view_model.ListviewModel
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor

class ListViewModelTest {
    @get:Rule
    var rule=InstantTaskExecutorRule()

    @Mock
    lateinit var animalApiService: AnimalApiService

    @Mock
    lateinit var prefs:SharedPreferencesHelper

    val application=Mockito.mock(Application::class.java)

    var listViewModel=ListviewModel(application,true)

    private val key ="Test key"

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)

        val testComponent=DaggerViewModelComponent.builder()
            .appModule(AppModule(application))
            .apiModule(ApiModuleTest(animalApiService))
            .prefsModule(PrefsModuleTest(prefs))
            .build()
            .inject(listViewModel)
    }

    @Test
    fun getAnimalSuccess(){
        Mockito.`when`(prefs.getApiKey()).thenReturn(key)
        val animal=Animal("cow",null,null,null,null,null,null)
        val animalList= listOf(animal)

        val testSingle=Single.just(animalList)

        Mockito.`when`(animalApiService.getAnimals(key)).thenReturn(testSingle)

        listViewModel.refresh()

        Assert.assertEquals(1,listViewModel.animals.value?.size)
        Assert.assertEquals(false,listViewModel.loadError.value)
        Assert.assertEquals(false,listViewModel.loading.value)
    }

    @Test
    fun getAnimalsFailure(){
        Mockito.`when`(prefs.getApiKey()).thenReturn(key)
        val testSingle=Single.error<List<Animal>>(Throwable())
        val keySingle=Single.just(ApiKey("ok",key))

        Mockito.`when`(animalApiService.getAnimals(key)).thenReturn(testSingle)
        Mockito.`when`(animalApiService.getApiKey()).thenReturn(keySingle)

        listViewModel.refresh()

        Assert.assertEquals(null,listViewModel.animals.value)
        Assert.assertEquals(false,listViewModel.loading.value)
        Assert.assertEquals(true,listViewModel.loadError.value)
    }

    @Test
    fun getKeySuccess(){
        Mockito.`when`(prefs.getApiKey()).thenReturn(null)
        val apiKey=ApiKey("ok",key)
        val keySingle=Single.just(apiKey)

        Mockito.`when`(animalApiService.getApiKey()).thenReturn(keySingle)

        val animal = Animal("cow",null,null,null,null,null,null)
        val animalsList= listOf(animal)

        val testSingle=Single.just(animalsList)
        Mockito.`when`(animalApiService.getAnimals(key)).thenReturn(testSingle)

        listViewModel.refresh()

        Assert.assertEquals(1,listViewModel.animals.value?.size)
        Assert.assertEquals(false,listViewModel.loadError.value)
        Assert.assertEquals(false,listViewModel.loading.value)
    }

    @Test
    fun getKeyFailure(){
        Mockito.`when`(prefs.getApiKey()).thenReturn(null)
        val keySingle=Single.error<ApiKey>(Throwable())

        Mockito.`when`(animalApiService.getApiKey()).thenReturn(keySingle)

        listViewModel.refresh()

        Assert.assertEquals(null,listViewModel.animals.value)
        Assert.assertEquals(false,listViewModel.loading.value)
        Assert.assertEquals(true,listViewModel.loadError.value)
    }

    @Before
    fun setupRxSchedulers(){
        val immediate=object :Scheduler(){
            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run() },true)
            }

        }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler->immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler->immediate }
    }
}