package ca.benwu.uwinfosessions.ui

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import ca.benwu.uwinfosessions.R
import ca.benwu.uwinfosessions.adapters.DateAdapter
import ca.benwu.uwinfosessions.models.InfoSession
import ca.benwu.uwinfosessions.models.NetworkResponse
import ca.benwu.uwinfosessions.utils.bindView
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager
import com.gigamole.infinitecycleviewpager.VerticalViewPager
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import rx.Subscription
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName

    private val BASE_URL = "https://api.uwaterloo.ca/v2/"

    var sessionLoadSubscription: Subscription? = null

    val INTERNET_PERMISSION_CODE = 2143;

    val loadingCircle: ProgressBar by bindView<ProgressBar>(R.id.loadingCircle)

    val datePager: VerticalViewPager by bindView<VerticalViewPager>(R.id.dateViewPager)

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

        val internetPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)

        if (internetPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), INTERNET_PERMISSION_CODE)
        } else {
            loadSessions()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if(sessionLoadSubscription != null && !sessionLoadSubscription!!.isUnsubscribed) {
            sessionLoadSubscription!!.unsubscribe()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == INTERNET_PERMISSION_CODE) {
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSessions()
            } else {
                Toast.makeText(this, "Internet permission is required", Toast.LENGTH_LONG).show();
            }
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

    fun loadSessions() {
        sessionLoadSubscription = sessionLoadSingle.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                {retrievedSessions ->
                    Log.i(TAG, "${retrievedSessions.size}")
                    processSessions(retrievedSessions)
                },
                { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                    loadingCircle.visibility = View.GONE
                }
        )
    }

    fun processSessions(sessions: List<InfoSession>) {
        val sortedSessions = sessions.sortedBy { it.date }
        sortedSessions.map { it.date = it.day + "\n" + SimpleDateFormat("MMMM d, yyyy").format(SimpleDateFormat("yyyy-MM-dd").parse(it.date)) }

        val sessionsByDate: MutableList<MutableList<InfoSession>> = mutableListOf()

        var sessionIndex = 0
        var prevDate: String? = null
        var currentDateSessions: MutableList<InfoSession> = mutableListOf()
        while (sessionIndex < sortedSessions.size) {
            if(prevDate != null && sortedSessions[sessionIndex].date != prevDate) {
                sessionsByDate.add(currentDateSessions)
                currentDateSessions = mutableListOf()
            }
            prevDate = sortedSessions[sessionIndex].date
            currentDateSessions.add(sortedSessions[sessionIndex])
            ++sessionIndex
        }

        if(!sessionsByDate.contains(currentDateSessions)) {
            sessionsByDate.add(currentDateSessions)
        }

        loadingCircle.visibility = View.GONE

        datePager.adapter = DateAdapter(this, sessionsByDate)
    }
}
