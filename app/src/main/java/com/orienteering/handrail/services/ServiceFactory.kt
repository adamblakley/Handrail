package com.orienteering.handrail.services

import com.orienteering.handrail.httprequests.AuthenticationInterceptor
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Creates serice interfaces
 */
object ServiceFactory {

    // url of rest service
    // Test http://192.168.0.21:8080/
    // AWS http://handrail-env-2.eba-wmrcuzkd.us-east-2.elasticbeanstalk.com/
    private const val API_BASE_URL = "http://handrail-env-2.eba-wmrcuzkd.us-east-2.elasticbeanstalk.com/"

    /**
     * Retrofit make service function, create and connect a http client with rest service, associate service class
     *
     * @param S
     * @param serviceClass
     * @return
     */
    fun <S> makeService(serviceClass : Class<S>): S{

        // define connection specification
        val specs = listOf(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS)

        // define logging interceptor and apply logging level
        val logging = HttpLoggingInterceptor()
        logging.apply { logging.level=HttpLoggingInterceptor.Level.BODY}
        // create http client, add interceptors and connection spec
        val httpClient = OkHttpClient.Builder().readTimeout(60,TimeUnit.SECONDS).connectTimeout(60,TimeUnit.SECONDS).addInterceptor(AuthenticationInterceptor()).addInterceptor(logging).connectionSpecs(specs)

        val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
        // build http client via http builder to manage ORM and apply base url, associate with service class passed through
        return builder
            .client(httpClient.build())
            .build().create(serviceClass)
    }


}