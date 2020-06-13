package co.simonkenny.row.core.di

import co.simonkenny.row.core.ROW_BASE_URL
import co.simonkenny.row.core.article.ArticleRepo
import co.simonkenny.row.coresettings.SettingsRepo
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

// TODO : use proper DI
class FakeDI private constructor() {

    companion object {
        val instance: FakeDI = FakeDI()
    }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(ROW_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val articleRepo = ArticleRepo()

    val settingsRepo = SettingsRepo()
}