package ca.benwu.uwinfosessions.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import butterknife.BindView
import ca.benwu.uwinfosessions.R
import ca.benwu.uwinfosessions.models.InfoSession
import ca.benwu.uwinfosessions.models.NetworkResponse
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable
import rx.Subscription
import java.util.concurrent.Callable
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MainActivity : AppCompatActivity() { // TODO: runtime permissions

    private val TAG = this.javaClass.simpleName

    private val BASE_URL = "https://api.uwaterloo.ca/v2/"

    var sessionLoadSubscription: Subscription? = null

    @BindView(R.id.loadingCircle)
    lateinit var loadingCircle: ProgressBar

    @BindView(R.id.twoWayViewPager)
    lateinit var sessionPager: HorizontalInfiniteCycleViewPager

    private val sessionLoadSingle: Single<List<InfoSession>> = Single.create {
        subscriber ->
        val response = sessionCall.getInfoSessions(resources.getString(R.string.api_key)).execute()
        if(response.isSuccessful) {
            val infoSessions = response.body().data
            Log.i(TAG, "Info sessions retrieved: ${infoSessions.size}")
            subscriber.onSuccess(infoSessions.asList())
        } else {
            Log.e(TAG, "HTTP error: ${response.errorBody().string()}")
            subscriber.onError(Throwable(response.errorBody().string()))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupApiCall()

        sessionLoadSubscription = sessionLoadSingle.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                {retrievedSessions ->
                    Log.i(TAG, "${retrievedSessions.size}")
                },
                { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                }
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        if(sessionLoadSubscription != null && !sessionLoadSubscription!!.isUnsubscribed) {
            sessionLoadSubscription!!.unsubscribe()
        }
    }

    interface SessionCall {
        @GET("resources/infosessions.json")
        fun getInfoSessions(@Query("key") key: String): Call<NetworkResponse>
    }

    private lateinit var sessionCall: SessionCall

    fun setupApiCall() {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        sessionCall = retrofit.create(SessionCall::class.java)
    }
}
