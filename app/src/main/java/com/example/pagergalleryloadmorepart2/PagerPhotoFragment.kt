package com.example.pagergalleryloadmorepart2
import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsClient.getPackageName
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import butterknife.BindView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import kotlinx.android.synthetic.main.fragment_pager_photo.*
import kotlinx.android.synthetic.main.pager_photo_view.view.*
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*
import android.app.Activity as Activity1
/**
 * A simple [Fragment] subclass.
 */
const val REQUEST_WRITE_EXTERNAL_STORAGE = 1
var currentKey1 ="Alberta"

class PagerPhotoFragment : Fragment(), TextToSpeech.OnInitListener {
    private lateinit var galleryViewModel: GalleryViewModel
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null
    @BindView(R.id.timer)
    @JvmField
    var textView: TextView? = null
    var txts: TextToSpeech? = null
    private var countDownTimer: CountDownTimer? = null
    private var gameOver = false
    private val COUNTER_TIME: Long = 10

    //private val AD_UNIT_ID =getArguments()?.getString("AD_UNIT_ID")
    //Auror本人
    private val AD_UNIT_ID ="ca-app-pub-5268108478853992/6364323758"
    //private val AD_UNIT_ID ="ca-app-pub-3940256099942544/5354046379"
    private var timeRemaining: Long = 0
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var gamePaused = false
    var isLoadingAds = false
    var activity: MainActivity? = null
    //读法语

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pager_photo, container, false)
    }
    @SuppressLint("NonConstantResourceId")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        txts = TextToSpeech(activity, this)
        //Aurora 传参
        val galleryViewModel: GalleryViewModel by viewModels { MyViewModelFactory(context?.applicationContext as Application, "Rain",0) }
        val photoList = arguments?.getParcelableArrayList<PhotoItem>("PHOTO_LIST")
        val current_position = arguments?.getInt("PHOTO_POSITION")?: 1
        //context=this.context
        //设置背景为灰色
        var actionBar = (getActivity() as AppCompatActivity?)!!.supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#0c0c0c"))
        actionBar!!.setBackgroundDrawable(colorDrawable)
        actionBar!!.setStackedBackgroundDrawable(colorDrawable)
        //全屏
       //getActivity()?.getWindow()?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        currentKey1= arguments?.getString("CURRENT_KEY").toString()
        //Log.d(TAG, "currentKey111A is: "+currentKey1+" access time is: "+galleryViewModel.getAccessTime())
        PagerPhotoListAdapter().apply {
            viewPager2.adapter = this
            submitList(photoList)
        }
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                photoTag.text = getString(R.string.photo_tag, position + 1, photoList?.size)
                textView2.text = currentKey1
                val typeface = Typeface.createFromAsset(
                    context?.getAssets(),
                    "fonts/AslinaBold-2.otf"
                ) // create a typeface from the raw ttf
                textView2.setTypeface(typeface) // apply the typeface to the textview

            }
        })
        viewPager2.setCurrentItem(arguments?.getInt("PHOTO_POSITION") ?: 0, false)
        viewPager2.orientation = ViewPager2.ORIENTATION_VERTICAL
        //loadInterAd()
        saveButton.setOnClickListener {
            Handler().postDelayed(Runnable {
                startGame()
                                           },1000)
            //showInterAd()},3000)
            if (Build.VERSION.SDK_INT < 29 && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_EXTERNAL_STORAGE
                )
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    savePhoto()
                }
            }
        }
        index_button?.setOnClickListener {
            //Toast.makeText(getActivity(), "Now APPEAR!!", Toast.LENGTH_LONG).show()
            val allEnglishString = context?.resources?.getStringArray(R.array.itemAllEng)
            val engMeaningStr=context?.resources?.getStringArray(R.array.itemMeaningEng)
            var posInt:Int=allEnglishString?.indexOf(currentKey1)?: 0
            Log.d("posInt","posInt: "+ posInt)
            var meaning:String= engMeaningStr?.get(posInt).toString()

            var str111:String= "A$posInt"
            //Log.d("str111","str: "+str111)
            var strToastLocal:String=getStringResourceByName(str111)
            //Log.d("str111",str111)
            if (engMeaningStr != null) {
                if (allEnglishString != null) {
                    Log.d("currentKey1","posInt:"+posInt+" meaning: "+meaning+" meaningStrlen "+engMeaningStr.size+" all size: "+allEnglishString.size)
                    //Toast.makeText(getActivity(), "meaning len:"+engMeaningStr.size+" allStr.SIZE: "+allEnglishString.size+" "+str111+" "+strToastLocal+" meaning: "+meaning, Toast.LENGTH_LONG).show()
                    val builder = context?.let { it1 -> AlertDialog.Builder(it1) }

                    with(builder)
                    {
                        this?.setTitle(currentKey1)
                        this?.setMessage(strToastLocal+'\n'+meaning)
                        this?.setPositiveButton("OK", null)
                        //this?.setNegativeButton("CANCEL", null)
                        this?.show()
                    }

                    //Toast.makeText(getActivity(), strToastLocal+" "+meaning, Toast.LENGTH_LONG).show()
                }
            }
        }
        meaning_toast?.setOnClickListener {
            view?.let {
                Navigation.findNavController(it)
                    .navigate(R.id.action_pagerPhotoFragment_to_IndexFragment)
            }
        }
        read_button?.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                txts?.speak(
                    //"AAA",
                    currentKey1,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    //"AAA"
                    currentKey1
                )
            } else {
                txts?.speak(
                    currentKey1,
                    TextToSpeech.QUEUE_FLUSH,
                    null
                )
            }
        }
    }
    private fun getStringResourceByName(aString: String): String {
        val packageName: String = context?.packageName ?: "Null"
        val resId = resources.getIdentifier(aString, "string", packageName)
        return getString(resId)
        Log.d("str111","str strToast : "+getString(resId))
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        savePhoto()
                    }
                } else {
                    Toast.makeText(context, "Failed.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private suspend fun savePhoto() {
        withContext(Dispatchers.IO) {
            val holder =
                (viewPager2[0] as RecyclerView).findViewHolderForAdapterPosition(viewPager2.currentItem)
                        as PagerPhotoViewHolder
            val bitmap = holder.itemView.pagerPhoto.drawable.toBitmap()

            val saveUri = context?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues()
            )?: kotlin.run {
                Toast.makeText(context, "Failed to save the image", Toast.LENGTH_SHORT).show()
                return@withContext
            }
            requireContext().contentResolver.openOutputStream(saveUri).use {
                if (bitmap.compress(Bitmap.CompressFormat.JPEG,90,it) && requireContext() != null) {
                    val wpManager: WallpaperManager = WallpaperManager.getInstance(requireContext())

                    try {
                        val bitmap2 = createBitmap(bitmap, currentKey1, 200, 500)
                        wpManager.setBitmap(bitmap2)
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }

                    MainScope().launch {
                        Toast.makeText(
                            activity,
                            "Apply successful!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    MainScope().launch { Toast.makeText(requireContext(), "Failed.", Toast.LENGTH_SHORT).show() }
                }
            }
        }
    }
    private fun createBitmap(bitmap1: Bitmap, phototitle: String, i: Int, i1: Int): Bitmap {
        val w = bitmap1.width
        val h = bitmap1.height
        val bmpTemp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmpTemp)
        val p = Paint()
        val typeface = Typeface.createFromAsset(
            context?.getAssets(),
            "fonts/AslinaBold-2.otf"
        ) // create a typeface from the raw ttf
        p.color = Color.WHITE
        p.typeface = typeface
        p.textSize = 45f
        canvas.drawBitmap(bitmap1, 0f, 0f, p)
        val ww = w / 8
        val hh = h / 2
        canvas.drawText(phototitle, ww.toFloat(), hh.toFloat(), p)
        canvas.save()
        canvas.restore()
        return bmpTemp
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
            } catch (e: Exception) {
                Log.d("Ad Exception", "load Ad error.")
            }
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
    override fun onAttach(activity:Activity1) {
        super.onAttach(activity)
        this.activity  = activity as MainActivity?
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            //TextToSpeech txts = new TextToSpeech(getActivity(), this);
            val result = txts!!.setLanguage(Locale.US)
            val text = "AAA"
        }
    }
}




















