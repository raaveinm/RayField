package com.raaveinm.rayfield.di

import com.raaveinm.rayfield.data.database.AppDatabase
import com.raaveinm.rayfield.data.database.getRoomDatabase
import com.raaveinm.rayfield.domain.ssh.SshClient
import com.raaveinm.rayfield.ui.state.MainScreenModel
import com.raaveinm.rayfield.ui.state.RawSshScreenModel
import com.raaveinm.rayfield.ui.state.configuration.SshScreenModel
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
    factory { MainScreenModel(get()) }
    factory { RawSshScreenModel(get()) }
    factory { (configId: String?, serverId: String?) ->
        SshScreenModel(
            serverDao = get(),
            initialConfigId = configId,
            initialServerId = serverId
        )
    }}

expect val platformModule: Module
