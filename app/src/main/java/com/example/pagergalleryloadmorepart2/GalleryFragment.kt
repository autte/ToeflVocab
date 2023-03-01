package com.example.pagergalleryloadmorepart2
import android.app.Application
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import butterknife.BindView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import kotlinx.android.synthetic.main.fragment_gallery.*

/**
 * A simple [Fragment] subclass.
 */
class GalleryFragment : Fragment() {
    private lateinit var galleryViewModel: GalleryViewModel
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null
    private var scrollTime =0
    @BindView(R.id.timer)
@JvmField
    var textView: TextView? = null
    private var countDownTimer: CountDownTimer? = null
    private var gameOver = false
    private val COUNTER_TIME: Long = 10
    //Auror本人
    private val AD_UNIT_ID ="ca-app-pub-5268108478853992/6364323758"
    //测试用
    //private val AD_UNIT_ID ="ca-app-pub-3940256099942544/5354046379"
    private var timeRemaining: Long = 0
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var gamePaused = false
    var isLoadingAds = false
    val FirstkeyWord ="Air"
    private var currentKeyfromIndex =FirstkeyWord
    var accessPosition :Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      //设置颜色
        var actionBar = (getActivity() as AppCompatActivity?)!!.supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#009D59"))
        actionBar!!.setBackgroundDrawable(colorDrawable)
        actionBar!!.setStackedBackgroundDrawable(colorDrawable)
        // Inflate the layout for this fragment
        if(getArguments()!=null){
            //取出保存的值
            currentKeyfromIndex = getArguments()?.getString("CURRENT_KEY") ?: "NUUUU"
            accessPosition=getArguments()?.getInt("ACCESS_POSITION")?: 0
        }else{
            val galleryViewModel: GalleryViewModel by viewModels {
                MyViewModelFactory(
                    context?.applicationContext as Application,
                    "NULLL",
                    0
                )
            }
            accessPosition=galleryViewModel.getAccessTime()
            currentKeyfromIndex= galleryViewModel.getKeyByPos(accessPosition).toString()
        }
        Log.d("CurKey in Gall",currentKeyfromIndex+" pos: "+accessPosition)
        val view:View=layoutInflater.inflate(R.layout.fragment_gallery, null)
        return layoutInflater.inflate(R.layout.fragment_gallery, null)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.swipeIndicator -> {
                swipeLayoutGallery.isRefreshing = true
                Handler().postDelayed(Runnable {
                    val galleryViewModel: GalleryViewModel by viewModels {
                        MyViewModelFactory(
                            context?.applicationContext as Application,
                            "NULLL",
                            0
                        )
                    }
                    galleryViewModel.resetQuery("NULLL") },1000)
            }
            R.id.navigation_explore -> {
                view?.let { Navigation.findNavController(it).navigate(R.id.action_galleryFragment_to_IndexFragment) };
            }
            R.id.rate_this_app->{
                val manager = context?.let { ReviewManagerFactory.create(it) }
                val request = manager?.requestReviewFlow()
                if (request != null) {
                    request.addOnCompleteListener { request ->
                        if (request.isSuccessful) {
                            // We got the ReviewInfo object
                            val reviewInfo = request.result
                            val flow = activity?.let { manager.launchReviewFlow(it, reviewInfo) }
                            if (flow != null) {
                                flow.addOnCompleteListener { _ ->
                                    Log.d("flow","flow rate has finished")
                                    // The flow has finished. The API does not indicate whether the user
                                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                                    // matter the result, we continue our app flow.
                                }
                            }

                        } else {
                            // There was some problem, continue regardless of the result.
                      /*      MaterialAlertDialogBuilder(this)
                                .setTitle(R.string.rate_app_title)
                                .setMessage(R.string.rate_app_message)
                                .setPositiveButton("Submit", (dialog, which) -> {

                            })
                            .setOnDismissListener(dialog -> {
                            })
                            .show();*/
                        }
                        }
                    }
                }
            }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        //Aurora 传参
        val galleryViewModel: GalleryViewModel by viewModels {
            MyViewModelFactory(
                context?.applicationContext as Application,
                currentKeyfromIndex,
                accessPosition
            )
        }
        galleryViewModel.setCurrentKey(currentKeyfromIndex)
        galleryViewModel.setAccessTime(accessPosition)
        Log.d("CurKey in Gall", "onActiveKey: "+currentKeyfromIndex+" access time is: "+galleryViewModel.getAccessTime())
        //galleryViewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(GalleryViewModel::class.java)
        val galleryAdapter = GalleryAdapter(galleryViewModel)
        recyclerView.apply {
            adapter = galleryAdapter
            layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        }
        galleryViewModel.photoListLive.observe(viewLifecycleOwner, Observer {
            if (galleryViewModel.needToScrollToTop) {
                recyclerView.scrollToPosition(0)
                galleryViewModel.needToScrollToTop = false
            }
            galleryAdapter.submitList(it)
            swipeLayoutGallery.isRefreshing = false
        })
        galleryViewModel.dataStatusLive.observe(viewLifecycleOwner, Observer {
            galleryAdapter.footerViewStatus = it
            galleryAdapter.notifyItemChanged(galleryAdapter.itemCount - 1)
            //Aurora changed.
            if (it == DATA_STATUS_NETWORK_ERROR) swipeLayoutGallery.isRefreshing = false
        })
        swipeLayoutGallery.setOnRefreshListener {
            if(accessPosition==0){accessPosition=1}
            accessPosition++
            Log.e("currentKey","accessPosition: "+accessPosition)
            galleryViewModel.setAccessTime(accessPosition)
            var cKey: String? =galleryViewModel.getKeyByPos(accessPosition)
           /* if (cKey != null) {
                galleryViewModel.setCurrentKey(cKey)
            }*/
            //galleryViewModel.getKeyByPos(accessPosition)?.let { galleryViewModel.setCurrentKey(it) }
            //Log.e("currentKey","Ckey:"+cKey+" pos:"+accessPosition+" get:"+galleryViewModel.getCurrentKey())
            if(accessPosition%3==0){startGame()}
            galleryViewModel.resetQuery("NULLL")
        }
        //loadInterAd()
        loadRewardedInterstitialAd()
        recyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0 ) return
                scrollTime++
                val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                val intArray = IntArray(2)
                layoutManager.findLastVisibleItemPositions(intArray)
                if (intArray[0] == galleryAdapter.itemCount - 1) {
                    galleryViewModel.fetchData()
                }
            }
        }
        )
    }
    private fun pauseGame() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        gamePaused = true
    }
    private fun createTimer(time: Long) {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        countDownTimer = object : CountDownTimer(0, 20) {
            override fun onTick(millisUnitFinished: Long) {
                /*   timeRemaining = ((millisUnitFinished / 1000) + 1);
                        textView.setText("seconds remaining: " + timeRemaining);*/
                timeRemaining = 1
            }
            override fun onFinish() {
                if (rewardedInterstitialAd == null) {
                    Log.d("Timer", "The rewarded interstitial ad is not ready.")
                    return
                }
                if (rewardedInterstitialAd!!.rewardItem != null) {
                    val rewardItem = rewardedInterstitialAd!!.rewardItem
                    showRewardedVideo()
                }
            }
        }
        (countDownTimer as CountDownTimer).start()
    }
    private fun resumeGame() {
        createTimer(timeRemaining)
        gamePaused = false
    }
    private fun loadRewardedInterstitialAd() {
        if (rewardedInterstitialAd == null) {
            isLoadingAds = true
            val adRequest = AdRequest.Builder().build()
            // Use the test ad unit ID to load an ad.
            try {
                RewardedInterstitialAd.load(
                    this.activity,
                    AD_UNIT_ID,
                    adRequest,
                    object : RewardedInterstitialAdLoadCallback() {
                        override fun onAdLoaded(ad: RewardedInterstitialAd) {
                            Log.d("Timer", "onAdLoaded")
                            rewardedInterstitialAd = ad
                            isLoadingAds = false
                            //  Toast.makeText(MainActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            Log.d("Timer", "onAdFailedToLoad: " + loadAdError.message)
                            // Handle the error.
                            rewardedInterstitialAd = null
                            isLoadingAds = false
                        }
                    })
            }catch(e:Exception){exitTransition}
        }
    }
    private fun startGame() {
        // Hide the retry button, load the ad, and start the timer.
//        retryButton.setVisibility(View.INVISIBLE);
        if (rewardedInterstitialAd ==null || !isLoadingAds) {
            loadRewardedInterstitialAd()
            Log.d("Timer", "startGame reload Add.")
        }
        createTimer(COUNTER_TIME)
        gamePaused = false
        gameOver = false
    }
    private fun showRewardedVideo() {
        if (rewardedInterstitialAd == null) {
            Log.d("Timer", "The rewarded interstitial ad wasn't ready yet.")
            return
        }
        rewardedInterstitialAd!!.setFullScreenContentCallback(
            object : FullScreenContentCallback() {
                /** Called when ad showed the full screen content.  */
                override fun onAdShowedFullScreenContent() {
                    Log.d("Timer", "onAdShowedFullScreenContent")

                    // Toast.makeText(MainActivity.this, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT).show();
                }

                /** Called when the ad failed to show full screen content.  */
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d("Timer", "onAdFailedToShowFullScreenContent: " + adError.message)

                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    rewardedInterstitialAd = null
                    loadRewardedInterstitialAd()
                }

                /** Called when full screen content is dismissed.  */
                override fun onAdDismissedFullScreenContent() {
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    rewardedInterstitialAd = null
                    Log.d("Timer", "onAdDismissedFullScreenContent")
                    //                        Toast.makeText(MainActivity.this, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT)
//                                .show();
                    // Preload the next rewarded interstitial ad.
                    // loadRewardedInterstitialAd();
                }
            })
        val activityContext: FragmentActivity? = this.activity
        rewardedInterstitialAd!!.show(
            activityContext
        ) { // Handle the reward.
            Log.d("Timer", "The user earned the reward.")
        }
    }
    companion object {
        fun newInstance(currentKey: String?,accessTime:Int?): Companion {
            val fragmentOne = GalleryFragment()
            val bundle = Bundle()
            bundle.putString("currentKey", currentKey)
            if (accessTime != null) {
                bundle.putInt("accessTime",accessTime)
            }
            //fragment保存参数，传入一个Bundle对象
            fragmentOne.setArguments(bundle)
            return GalleryFragment
        }

    }
}

















