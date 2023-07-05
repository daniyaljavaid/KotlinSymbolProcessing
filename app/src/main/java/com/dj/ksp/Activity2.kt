package com.dj.ksp

import com.dj.testannotation.RepositoryAnnotation
import com.dj.testannotation.ViewModelAnnotation


val a = 1

class ViewModel3 constructor(repository: Repository, randomDouble: Double) : IViewModel {

}

class ViewModel constructor(repository: Repository, randomString: String) : IViewModel {

}

class ViewModel2 constructor(repository: Repository, randomInt: Int) : IViewModel {

}

@RepositoryAnnotation
interface Repository

@ViewModelAnnotation
interface IViewModel

class RepositoryImpl : Repository