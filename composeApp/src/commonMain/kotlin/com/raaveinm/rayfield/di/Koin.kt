package com.raaveinm.rayfield.di

import com.raaveinm.rayfield.data.database.AppDatabase
import com.raaveinm.rayfield.data.database.getRoomDatabase
import com.raaveinm.rayfield.domain.ssh.SshClient
import com.raaveinm.rayfield.domain.xray.CypherService
import com.raaveinm.rayfield.domain.xray.ShareLinkGenerator
import com.raaveinm.rayfield.ui.state.MainScreenModel
import com.raaveinm.rayfield.ui.state.RawSshScreenModel
import com.raaveinm.rayfield.ui.state.configuration.EditScreenModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(commonModule, platformModule)
    }
}

val commonModule = module {
    single { getRoomDatabase(get()) }
    single { get<AppDatabase>().serverDao() }
    factory { SshClient() }
    single { CypherService() }
    single { ShareLinkGenerator() }
    factory { MainScreenModel(get()) }
    factory { RawSshScreenModel(get()) }
    factory { params ->
        EditScreenModel(
            serverDao = get(),
            cypherService = get(),
            shareLinkGenerator = get(),
            initialConfigId = params.values.getOrNull(0) as? String,
            initialServerId = params.values.getOrNull(1) as? String
        )}
}

expect val platformModule: Module
