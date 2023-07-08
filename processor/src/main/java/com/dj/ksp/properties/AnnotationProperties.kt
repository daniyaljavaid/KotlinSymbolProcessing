package com.dj.ksp.properties

import com.dj.testannotation.ApiServiceAnnotation
import com.dj.testannotation.LDSAnnotation
import com.dj.testannotation.RDSAnnotation
import com.dj.testannotation.RepositoryAnnotation
import com.dj.testannotation.UseCaseAnnotation

enum class AnnotationProperties(val annotationName: String, val inclusions: List<String>) {
    LDS(
        LDSAnnotation::class.java.canonicalName,
        listOf("androidx.room.Dao")
    ),
//    RDS(
//        RDSAnnotation::class.java.canonicalName,
//        listOf(ApiServiceAnnotation::class.java.simpleName)
//    ),
//    REPOSITORY(
//        RepositoryAnnotation::class.java.canonicalName,
//        listOf(LDSAnnotation::class.java.simpleName, RDSAnnotation::class.java.simpleName)
//    ),
//    USECASE(
//        UseCaseAnnotation::class.java.canonicalName,
//        listOf(
//            RepositoryAnnotation::class.java.simpleName,
//            UseCaseAnnotation::class.java.simpleName
//        )
//    ),
//    VIEWMODEL(
//        "dagger.hilt.android.lifecycle.HiltViewModel",
//        listOf(
//            UseCaseAnnotation::class.java.simpleName
//        )
//    )
}
