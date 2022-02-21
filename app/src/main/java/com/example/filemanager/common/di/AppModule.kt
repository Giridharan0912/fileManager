package com.example.filemanager.common.di

import com.alphaverse.grocerymart.common.DispatchCoroutineProviders
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDispatchers(): DispatchCoroutineProviders {
        return DispatchCoroutineProviders()
    }
}

