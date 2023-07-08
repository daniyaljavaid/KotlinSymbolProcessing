package com.dj.ksp.properties

import com.dj.testannotation.ApiServiceAnnotation
import com.dj.testannotation.LDSAnnotation
import com.dj.testannotation.RDSAnnotation
import com.dj.testannotation.RepositoryAnnotation
import com.dj.testannotation.UseCaseAnnotation

enum class AnnotationProperties(val annotation: String, val inclusions: List<String>) {
    LDS(
        LDSAnnotation::class.java.canonicalName,
        listOf("androidx.room.Dao")
    ),
    RDS(
        RDSAnnotation::class.java.canonicalName,
        listOf(ApiServiceAnnotation::class.java.canonicalName)
    ),
    REPOSITORY(
        RepositoryAnnotation::class.java.canonicalName,
        listOf(LDSAnnotation::class.java.canonicalName, RDSAnnotation::class.java.canonicalName)
    ),
    USECASE(
        UseCaseAnnotation::class.java.canonicalName,
        listOf(
            RepositoryAnnotation::class.java.canonicalName,
            UseCaseAnnotation::class.java.canonicalName
        )
    ),
    VIEWMODEL(
        "dagger.hilt.android.lifecycle.HiltViewModel",
        listOf(
            UseCaseAnnotation::class.java.canonicalName
        )
    )
}
