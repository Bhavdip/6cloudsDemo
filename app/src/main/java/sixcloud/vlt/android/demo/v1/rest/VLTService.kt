package sixcloud.vlt.android.demo.v1.rest

import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Created by cs03 on 16/5/18.
 */
interface VLTService {

  @GET("getvideos")
  fun getVideos(): Observable<Model.Response>

  //"http://sixclouds.cn/beta/"
  //http://192.168.1.182/sixclouds-web/
  companion object {
    fun create(): VLTService {
      val okayClient = OkHttpClient.Builder()
      val loggingIntercept = HttpLoggingInterceptor()
      loggingIntercept.level = HttpLoggingInterceptor.Level.BODY
      okayClient.addInterceptor(loggingIntercept)
      val retrofit = Retrofit.Builder()
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .baseUrl("http://sixclouds.cn/beta/")
          .client(okayClient.build())
          .build()
      return retrofit.create(VLTService::class.java)
    }
  }
}