package com.example.landtech.di

import android.app.Application
import androidx.room.Room
import com.example.landtech.data.database.LandtechDatabase
import com.example.landtech.data.datastore.LandtechDataStore
import com.example.landtech.data.datastore.dataStore
import com.example.landtech.data.remote.LandtechAPI
import com.example.landtech.data.remote.LandtechServiceAPI
import com.example.landtech.data.repository.LandtechRepository
import com.example.landtech.data.repository.LandtechRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideLandtechDatabase(app: Application): LandtechDatabase {
        return Room.databaseBuilder(
            app, LandtechDatabase::class.java, "landtech_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideLandtechDataStore(app: Application): LandtechDataStore {
        return LandtechDataStore(app.dataStore)
    }

    @Provides
    @Singleton
    fun provideLandtechApi(dataStore: LandtechDataStore): LandtechServiceAPI {
        return LandtechAPI(dataStore).retrofitService
    }

    @Provides
    @Singleton
    fun provideRepository(
        db: LandtechDatabase,
        api: LandtechServiceAPI,
        dataStore: LandtechDataStore,
        app: Application
    ): LandtechRepository {
        return LandtechRepositoryImpl(db, api, dataStore, app)
    }
}