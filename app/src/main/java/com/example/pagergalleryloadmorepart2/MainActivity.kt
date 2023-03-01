package com.example.pagergalleryloadmorepart2

//import android.support.design.widget.BottomNavigationView;

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.ads.*
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    lateinit var mAdManagerAdView : AdManagerAdView
    var TAG="MainActivity"
    lateinit var watch_record_ImageView_finger: ImageView
    lateinit var watch_record_ImageView_group: ImageView
    lateinit var pull_down_text: TextView
    lateinit var mAdView:AdView
/*    @BindView(R.id.bottom_navigation)
    var bottomNavigation: BottomNavigationView? = null*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     /*   setContentView(R.layout.fragment_explore)
        loadFragment(ExploreFragment())*/
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this) {
            val adView = AdView(this)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = this?.resources?.getString(R.string.bannerUnitId) ?: "ca-app-pub-3940256099942544/6300978111"
// TODO: Add adView to your view hierarchy.
        }
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        // Define ActionBar object
        val actionBar: ActionBar?
        actionBar = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#0F9D58"))
        // Set BackgroundDrawable
        actionBar!!.setBackgroundDrawable(colorDrawable)

        val navController = this.findNavController(R.id.fragment)
        val appBarConfiguration = AppBarConfiguration
            .Builder(
                R.id.galleryFragment,
                R.id.pagerPhotoFragment,
                R.id.IndexFragment)
            .build()
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
       // NavigationUI.setupActionBarWithNavController(this,findNavController(R.id.fragment))

        //     找到图片ID  开始设置动画
        setViews()
        // bottomNavigation!!.setOnNavigationItemSelectedListener(this)
        loadBannerAd()
    }
    private fun setViews() {
        watch_record_ImageView_finger =
            findViewById<View>(R.id.watch_record_ImageView_finger) as ImageView
        watch_record_ImageView_group =
            findViewById<View>(R.id.watch_record_ImageView_group) as ImageView
        pull_down_text=
            findViewById<View>(R.id.pulldownstr) as TextView
        Handler().postDelayed(kotlinx.coroutines.Runnable {
            //        设置动画完成时间  毫秒为单位
            watch_record_ImageView_group.setStateListAnimator(null)
            watch_record_ImageView_group.bringToFront()
            watch_record_ImageView_finger.bringToFront()
            pull_down_text.bringToFront()
            rotateFinger()  },
            1000)
        Handler().postDelayed(kotlinx.coroutines.Runnable {
            //        设置动画完成时间  毫秒为单位
            watch_record_ImageView_finger.visibility = View.GONE
            watch_record_ImageView_group.visibility = View.GONE
            pull_down_text.visibility=View.GONE
        },
            1000)
    }

    private fun rotateFinger() {
        watch_record_ImageView_group.bringToFront()
        //    调用小白点动画 小白点在第一次进行先动
        val rotate = RotateAnimation(
            15F, 0F,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 1000
        rotate.setRepeatCount(2)
       // watch_record_ImageView_group.visibility=View.VISIBLE
        watch_record_ImageView_finger.startAnimation(rotate)
        var animFadeIn =
            AnimationUtils.loadAnimation(
                getApplicationContext(),
                R.anim.watch_record_gestures_animation
            );
        animFadeIn.duration=6000
        animFadeIn.setRepeatCount(357)
        pull_down_text.startAnimation(animFadeIn)
        watch_record_ImageView_group.startAnimation(animFadeIn)

    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.swipeIndicator -> {
           //Refresh
            }
            R.id.navigation_explore -> {
                loadFragment(ExploreFragment())
            }
       }
        return true
    }
/*    override fun onSupportNavigateUp(): Boolean  {
        val navController = this.findNavController(R.id.fragment)
        Log.d("currentDest", navController.currentDestination.toString())
        return when(navController.currentDestination?.id) {
            R.id.action_pagerPhotoFragment_to_IndexFragment -> {
                // custom behavior here
                findNavController(R.id.fragment).navigate(R.id.action_pagerPhotoFragment_to_IndexFragment)
                true
            }
            else -> navController.navigateUp()
        }

    }*/
    private fun loadBannerAd(){
        MobileAds.initialize(this){}

        mAdManagerAdView = findViewById(R.id.adManagerAdView)
        val adRequest = AdManagerAdRequest.Builder().build()
        mAdManagerAdView.loadAd(adRequest)

        mAdManagerAdView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d("Ad", "Banner Ad Loaded ")

            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d("Ad", "Banner Ad Loaded finished")
            }
        }
    }
    fun loadFragment(fragment: Fragment) {
        val backStateName = fragment.javaClass.name
        val manager = supportFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) { //fragment not in back stack, create it.
            val ft = manager.beginTransaction()
            ft.replace(R.id.fragment, fragment, backStateName)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft.addToBackStack(backStateName)
            ft.commit()
        }
        //  drawer.closeDrawers();
    }

}
