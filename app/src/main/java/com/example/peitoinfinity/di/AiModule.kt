package com.example.peitoinfinity.di

import android.content.Context
import com.example.peitoinfinity.data.ai.AiProviderSelector
import com.example.peitoinfinity.data.ai.LocalAiProvider
import com.example.peitoinfinity.data.ai.LlmResponseParser
import com.example.peitoinfinity.data.ai.PromptBuilder
import com.example.peitoinfinity.data.ai.RemoteAiProvider
import com.example.peitoinfinity.data.local.preferences.AppPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    @Provides
    @Singleton
    fun provideLocalAiProvider(
        @ApplicationContext context: Context
    ): LocalAiProvider = LocalAiProvider(context)

    @Provides
    @Singleton
    fun provideRemoteAiProvider(): RemoteAiProvider = RemoteAiProvider()

    @Provides
    @Singleton
    fun provideAiProviderSelector(
        localProvider: LocalAiProvider,
        remoteProvider: RemoteAiProvider,
        preferences: AppPreferences
    ): AiProviderSelector = AiProviderSelector(
        localProvider, remoteProvider, preferences
    )

    @Provides
    @Singleton
    fun providePromptBuilder(): PromptBuilder = PromptBuilder()

    @Provides
    @Singleton
    fun provideLlmResponseParser(): LlmResponseParser = LlmResponseParser()
}
