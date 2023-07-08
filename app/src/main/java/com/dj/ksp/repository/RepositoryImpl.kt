package com.dj.ksp.repository

import com.dj.ksp.lds.ILDS
import com.dj.ksp.rds.IRDS

class RepositoryImpl(val rds: IRDS, val lds: ILDS) : IRepository