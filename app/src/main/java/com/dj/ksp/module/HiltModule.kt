package com.dj.ksp.module

import com.dj.ksp.lds.ILDS
import com.dj.ksp.lds.LDSImpl
import com.dj.ksp.lds.MyDao
import com.dj.ksp.rds.ApiService
import com.dj.ksp.rds.IRDS
import com.dj.ksp.rds.RDSImpl
import com.dj.ksp.repository.IRepository
import com.dj.ksp.repository.RepositoryImpl
import com.dj.ksp.usecase.IUseCase2
import com.dj.ksp.usecase.UseCase2Impl
import com.dj.ksp.viewmodel.IViewModel
import com.dj.ksp.viewmodel.ViewModel2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class HiltModule {

    @Provides
    internal fun repositoryProvider(
        rds: IRDS, lds: ILDS
    ): IRepository = RepositoryImpl(rds, lds)


    @Provides
    internal fun viewModelProvider(
        usecase2: IUseCase2
    ): IViewModel = ViewModel2(usecase2)


    @Provides
    internal fun useCase2Provider(): IUseCase2 = UseCase2Impl()


    @Provides
    internal fun rdsProvider(): IRDS = RDSImpl(object : ApiService {

    })


    @Provides
    internal fun ldsProvider(): ILDS = LDSImpl(object : MyDao {

    })


}
