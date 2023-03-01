/*
package com.example.pagergalleryloadmorepart2

import android.app.Application
import android.app.ProgressDialog
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ApplyActivity : AppCompatActivity() {
    @BindView(R.id.ivWallpaper)
    var ivWallpaper: ImageView? = null

    @JvmField
    @BindView(R.id.textView2)
    var textView2: TextView? = null

    @BindView(R.id.btnAplay)
    var btnAplay: Button? = null

    @BindView(R.id.cv_like)
    var cvLike: CheckBox? = null

    */
/*   @BindView(R.id.cv_Share)
       ImageView cvShare;*//*

    var photoid: String? = null
    var phototitle: String? = null
    var url: String? = null
    var wallpaperManager: WallpaperManager? = null
    var bitmap1: Bitmap? = null
    var bitmap2: Bitmap? = null
    var mInterstitialAd: InterstitialAd? = null
    var anImage: Bitmap? = null
    val current_position:Int =1
    lateinit var photoString: String
    private var progressDialog: ProgressDialog? = null
    private val interstitialAd: InterstitialAd? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply)
        ButterKnife.bind(this)
        ProgressDialogSetup()
        val intent: Intent = getIntent()
        val bundle: Bundle? = intent.getExtras()
        //Aurora 传参
        val galleryViewModel: GalleryViewModel by viewModels { MyViewModelFactory(this?.applicationContext as Application, "Rain",0) }
        val photoList = arguments?.getParcelableArrayList<PhotoItem>("PHOTO_LIST")
        = arguments?.getInt("PHOTO_POSITION")?: 1

        if (bundle != null) {
            photoid = bundle.getString("PHOTO_LIST")
        }
        if (bundle != null) {
            photoString = bundle.getString("PHOTO_POSITION")
        }

        textView2.setText(phototitle)
        val typeface: Typeface = Typeface.createFromAsset(
            this.getApplicationContext().getAssets(),
            "fonts/AslinaBold-2.otf"
        ) // create a typeface from the raw ttf
        textView2?.setTypeface(typeface) // apply the typeface to the textview
        PhotoDetails()
        //InterstitialAd()
    }

    fun ProgressDialogSetup() {
        progressDialog = ProgressDialog(this@ApplyActivity)
        progressDialog!!.setMessage(getResources().getString(R.string.please_wait))
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog!!.setCancelable(false)
    }

    private fun PhotoDetails() {
        // progressDialog.show();
        Log.i("ApplyActivity", "photoid is:  $photoid")
        val id = photoid
 */
/*       Picasso.get()
            .load(url)
            .into(ivWallpaper)
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
        } else {
            val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference()
            ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favourites")
                .child(photoid).addListenerForSingleValueEvent(object : ValueEventListener() {
                fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // use "username" already exists
                        // Let the user know he needs to pick another username.
                        cvLike!!.isChecked = true
                    } else {
                        // User does not exist. NOW call createUserWithEmailAndPassword
                        // Your previous code here.
                        cvLike!!.isChecked = false
                    }
                }

                fun onCancelled(databaseError: DatabaseError?) {}
            })*//*

*/
/*
        cvLike!!.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(compoundButton: CompoundButton, b: Boolean) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(this@ApplyActivity, "login require", Toast.LENGTH_SHORT).show()
                    compoundButton.setChecked(false)
                    return
                }
                val uid: String
                val sharedObjects: SharedObjects
                sharedObjects = SharedObjects(this@ApplyActivity)
                uid = sharedObjects.getUserID()
                val databaseReference: DatabaseReference =
                    FirebaseDatabase.getInstance().getReference()
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("favourites")
                        .child(id)
                if (b) {
                    databaseReference.child("Id").setValue(id)
                    databaseReference.child("url").setValue(url)
                    databaseReference.child("title").setValue(phototitle)
                } else {
                    databaseReference.child("Id").removeValue()
                    databaseReference.child("url").removeValue()
                    databaseReference.child("title").removeValue()
                }
            }
        })*//*

    }

     fun saveImageToExternalStorage(finalBitmap: Bitmap?) {
        // String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        //   String filename = "MyApp/MediaTag/MediaTag-"+"objectId"+".png";
        // File file = new File(Environment.getExternalStorageDirectory(), filename);
        val root =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        // File myDir = new File(root + "/saved_images_1");
        val myDir = File("$root/Wallsplash")
        myDir.mkdirs()
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val fname = "Image-$n.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        //MediaStore.Images.Media.insertImage(getContentResolver(), yourBitmap, yourTitle , yourDescription)
        MediaScannerConnection.scanFile(this@ApplyActivity, arrayOf(file.toString()), null,
            object : OnScanCompletedListener {
                override fun onScanCompleted(path: String, uri: Uri) {
                    Log.i("ExternalStorage", "Scanned $path:")
                    Log.i("ExternalStorage", "-> uri=$uri")
                }
            })
    }

    @OnClick(R.id.ivWallpaper, R.id.btnAplay, R.id.cv_download)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.ivWallpaper -> {
            }
            R.id.cv_download -> {
                anImage = (ivWallpaper!!.drawable as BitmapDrawable).getBitmap()
                saveImageToExternalStorage(anImage)
                Toast.makeText(this@ApplyActivity, "save success", Toast.LENGTH_SHORT).show()
            }
            R.id.btnAplay -> {
                wallpaperManager = WallpaperManager.getInstance(this@ApplyActivity)
                try {
                    showInterstitial()
                    bitmap1 = (ivWallpaper!!.drawable as BitmapDrawable).getBitmap()
                    val bitmap2: Bitmap = createBitmap(bitmap1, phototitle, 200, 500)
                    wallpaperManager.setBitmap(bitmap2)
                    finish()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun InterstitialAd() {
        mInterstitialAd = InterstitialAd(this@ApplyActivity)
        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.intertatial_ad_id))
        // mInterstitialAd.setAdUnitId("ca-app-pub-4906098412516661/9907689540");
        val adRequest = AdRequest.Builder()
            .build()
        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest)
        mInterstitialAd.setAdListener(object : AdListener() {
            override fun onAdLoaded() {
                val handler = Handler()
                handler.postDelayed({ showInterstitial() }, 5000)
            }
        })
        mInterstitialAd.setAdListener(object : AdListener() {
            override fun onAdLoaded() {
                //showInterstitial();
            }
        })
        //Aurora Add this method where needed to show ads.
    }

    private fun showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show()
        } else {
            InterstitialAd()
            mInterstitialAd.show()
            // Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
        }
    }

    private fun createBitmap(bitmap1: Bitmap?, phototitle: String?, i: Int, i1: Int): Bitmap {
        val w: Int = bitmap1.getWidth()
        val h: Int = bitmap1.getHeight()
        val bmpTemp: Bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmpTemp)
        val p = Paint()
        val familyName = "宋体"
        //      Typeface font = Typeface.create(familyName, Typeface.BOLD);
        val typeface: Typeface = Typeface.createFromAsset(
            this.getApplicationContext().getAssets(),
            "fonts/AslinaBold-2.otf"
        ) // create a typeface from the raw ttf
        p.color = Color.WHITE
        p.typeface = typeface
        p.textSize = 45f
        canvas.drawBitmap(bitmap1, 0f, 0f, p)
        val ww = w / 8
        val hh = h / 2
        canvas.drawText(phototitle!!, ww.toFloat(), hh.toFloat(), p)
        canvas.save()
        canvas.restore()
        return bmpTemp
    }
}*/
