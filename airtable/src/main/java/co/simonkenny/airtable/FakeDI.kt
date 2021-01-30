package co.simonkenny.airtable

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class FakeDI private constructor() {

    companion object {
        val instance: FakeDI = FakeDI()
    }

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        // override in call with @Url but need a value or Retrofit complains
        .baseUrl("https://example.com")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val airtableRepo = AirtableRepo(retrofit)
}