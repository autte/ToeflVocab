package com.example.pagergalleryloadmorepart2

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import java.util.*

const val DATA_STATUS_CAN_LOAD_MORE = 0
const val DATA_STATUS_NO_MORE = 1
const val DATA_STATUS_NETWORK_ERROR = 2
class GalleryViewModel(application: Application, mParam: String, accessTime: Int) : AndroidViewModel(application) {
    private val _dataStatusLive = MutableLiveData<Int>()
    val dataStatusLive:LiveData<Int> get() = _dataStatusLive
    private val accessTimeLive: MutableLiveData<Int> = MutableLiveData<Int>()
    private var keyLive: MutableLiveData<String> = MutableLiveData<String>()
    private val _photoListLive = MutableLiveData<List<PhotoItem>>()
    val photoListLive: LiveData<List<PhotoItem>>
        get() = _photoListLive
    var needToScrollToTop = true
    private val perPage = 6

    var TAG="GalleryViewModel"
    private lateinit var context: Context
    val pixabaykey=getApplication<Application>().resources.getString(R.string.pixabaykey)
    val keyWords = getApplication<Application>().resources.getStringArray(R.array.itemAllEng)
    val actualkeyWords = getApplication<Application>().resources.getStringArray(R.array.itemActualEng)
    val allKeyWords=getApplication<Application>().resources.getStringArray(R.array.itemActualEng)
    private var currentPage = 1
    private var totalPage = 1
    private var totalReturn=0

    @RequiresApi(Build.VERSION_CODES.O)
    val currentDateTime = LocalDateTime.now()
    @RequiresApi(Build.VERSION_CODES.O)
    val seconds = currentDateTime.getSecond();
    var currentKey =keyWords.get(seconds*15)
    private var  AccessTime:Int =seconds*15
    private var isNewQuery = true
    private var isLoading = false
    fun GalleryViewModel(currentKey1: String?, accessTime:Int?) {
        //super(application)
        if (currentKey1 != null) {
            this.currentKey=currentKey1
        }
        if (accessTime != null) {
            this.AccessTime=accessTime
            //Log.d(TAG, "currentKey1 HAS BEEN SET BY INITIAL WAY: "+this.currentKey+" access time is: "+this.getAccessTime())
        }
    }
    @JvmName("getCurrentKey1")
    fun getCurrentKey(): String? {
        return currentKey
    }
    @JvmName("setCurrentKey1")
    fun setCurrentKey(currentKey: String) {
        this.currentKey = currentKey
    }
    @JvmName("getAccessTime")
    fun getAccessTime(): Int {
        return AccessTime
    }
    @JvmName("setAccessTime")
    fun setAccessTime(AccessTime: Int) {
        this.AccessTime = AccessTime
    }
    fun getKeyByPos(AccessTime:Int): String? {
        if(AccessTime<allKeyWords.size){
           return allKeyWords[AccessTime]
        }
        else{
            //val rnds = (50..100).random()
            var rnds=AccessTime% allKeyWords.size
            return allKeyWords[rnds]
        }

    }
    fun resetQuery(currentKey1:String) {
        currentPage = 1
        totalPage = 1
        var accessTime: Int = getAccessTimes()
        Log.d("currentKey", "Curk resetQuery: "+ currentKey+" AccessTime: "+AccessTime)
        currentKey =actualkeyWords[AccessTime]
        isNewQuery = true
        needToScrollToTop = true
        fetchData()
    }
    fun getAccessTimes(): Int {
        if (AccessTime == (keyWords.size).toInt() ) {
            AccessTime = 0
        } else if (AccessTime > (keyWords.size).toInt()) {
            AccessTime = AccessTime % (keyWords.size).toInt()
        } else {
            AccessTime = getAccessTime()
        }
        return AccessTime
    }

    fun fetchData() {
        //Log.d("AAA", "FFF 1st: "+ currentKey)
        if (isLoading) return
        if (currentPage > totalPage) {
            _dataStatusLive.value = DATA_STATUS_NO_MORE
            return
        }
        //Log.d("currentKey", "AAA: "+ currentKey+" actualKey: "+actualkeyWords[AccessTime]+" AccessTime: "+getAccessTime())
        isLoading = true
        var surl = getUrl(actualkeyWords[AccessTime])
        //Log.d("AAA", "FFF 3rd: "+ surl)
        val stringRequest = StringRequest(
            Request.Method.GET,
            surl,
            Response.Listener {
                with(Gson().fromJson(it, Pixabay::class.java)) {
                    //totalPage = ceil(totalHits.toDouble() / perPage).toInt()
                    totalReturn=totalHits.toInt();
                    totalPage=1
                    if (isNewQuery) {
                        _photoListLive.value = hits.toList()
                    } else {
                        _photoListLive.value =
                            arrayListOf(_photoListLive.value!!, hits.toList()).flatten()
                    }
                }
                _dataStatusLive.value = DATA_STATUS_CAN_LOAD_MORE
                isLoading = false
                isNewQuery = false
                currentPage++
            },
            Response.ErrorListener {

                _dataStatusLive.value = DATA_STATUS_NETWORK_ERROR
                isLoading = false
            }
        )
        VolleySingleton.getInstance(getApplication()).requestQueue.add(stringRequest)
        isLoading = false
    }
    private fun getUrl(kk:String): String {
        //Log.d(TAG,"https://pixabay.com/api/?key=12472743-874dc01dadd26dc44e0801d61&q=${kk}&per_page=${perPage}&page=${currentPage}" )
        //Log.d("getURL Inner1st", "kk "+ kk)
        var tt:String=kk
        if(kk.equals("Abigail")){tt="Maid"}
        if(kk.equals("Abound")){tt="Flourish"}
        if(kk.equals("Abrupt")){tt="sudden"}
        if(kk.equals("Aboveground")){tt="ground"}
        if(kk.equals("Abruptly")){tt="Suddenly"}
        if(kk.equals("Abruptness")){tt="Suddenly"}
        if(kk.equals("Absenteeism")){tt="Absent"}
        if(kk.equals("Abundantly")){tt="a lot"}
        if(kk.equals("Abreast")){tt="side by side"}
        if(kk.equals("Accustom")){tt="familiarize"}
        if(kk.equals("Achingly")){tt="pain"}
        if(kk.equals("Achondrite")){tt="Meteorite"}
        if(kk.equals("Concoct")){tt="Devise"}
        if(kk.equals("Concomitant")){tt="Accompany"}
        if(kk.equals("Concurrent")){tt="simultaneous"}
        if(kk.equals("Conestoga")){tt="Caravan"}
        if(kk.equals("Concurrent")){tt="Simultaneous"}
        if(kk.equals("Congenial")){tt="Similar personality"}
        if(kk.equals("Conscription")){tt="Solider"}
        if(kk.equals("Consort")){tt="Associate with"}
        if(kk.equals("Consortium")){tt="financial group"}
        if(kk.equals("Forefront")){tt="front"}
        if(kk.equals("Foresightedness")){tt="foresight"}
        if(kk.equals("Forestall")){tt="foresee"}
        if(kk.equals("Fortuitous")){tt="fortunate"}
        if(kk.equals("Frenetic")){tt="crazy"}
        if(kk.equals("Fruitless")){tt="fail"}
        if(kk.equals("Functionalism")){tt="Function"}
        if(kk.equals("Functionalism")){tt="Function"}
        var sss = "https://pixabay.com/api/?key=${pixabaykey}&q=${tt}&per_page=${perPage}&page=${kk}"
        Log.d("getURL Inner2nd", "===sss====" + sss)

        return sss
    }

}












