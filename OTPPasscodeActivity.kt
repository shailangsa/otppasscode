package com.availability.app

import android.Manifest
import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.impl.VideoCaptureConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.availability.app.otppasscodes_database.OTPPasscodesDatabase
import com.availability.app.otppasscodes_database.OTPPasscodesRepository
import com.availability.app.otppasscodes_database.OTPPasscodesTable
import com.availability.app.otppasscodes_database.OTPPasscodesViewModel
import com.availability.app.wifi_trigger.RecyclerAdapter
import com.availability.app.wifi_trigger.RecyclerAdapterForScanResult
import com.example.roomapp.data.UserViewModel
import com.google.common.util.concurrent.ListenableFuture
import com.plcoding.retrofitcrashcourse.MainViewModel
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionErrorCode
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionSuccessListener
import kotlinx.android.synthetic.main.enter_password_activity.*
import kotlinx.android.synthetic.main.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*


private const val REQUEST_CODE_PERMISSIONS = 10

//private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class OTPPasscodeActivity : AppCompatActivity() {
    var resultList = ArrayList<ScanResult>()
    var resultList1 = ArrayList<ScanResult>()
    var resultList2707 = ArrayList<ScanResult>()
    private lateinit var mOTPPasscodesViewModel: OTPPasscodesViewModel

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerAdapter
    private lateinit var mAdapter1: RecyclerAdapterForScanResult
    lateinit var wifiManager: WifiManager
    lateinit var wifiManager2707: WifiManager
    private lateinit var mUserViewModel2: UserViewModel
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private lateinit var imagePreview: Preview

    private lateinit var imageAnalysis: ImageAnalysis

    private lateinit var imageCapture: ImageCapture

    private lateinit var videoCapture: VideoCapture

    private lateinit var previewView: PreviewView

    private lateinit var mAdminComponentName: ComponentName
    private lateinit var mDevicePolicyManager: DevicePolicyManager
    private lateinit var mUserViewModel: UserViewModel
    private lateinit var viewModel: MainViewModel

    private val mInterval = 5000 // 5 seconds by default, can be changed later

    private lateinit var mHandler: Handler
    private lateinit var textview_password: TextView
    private lateinit var repository2: OTPPasscodesRepository
    private lateinit var repository3: OTPPasscodesRepository



    @SuppressLint("SimpleDateFormat")
    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy-HH-mm-ss")
    val timeStamp: String = simpleDateFormat.format(Date())









    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.otp_passcode_activity)
        mRecyclerView = findViewById(R.id.mRecylcerview3)

        writeToFile("[$timeStamp] User is on OTPPasscodeActivity \n")
        mOTPPasscodesViewModel = OTPPasscodesViewModel(application)


        btn_continueassign.setOnClickListener {


            Log.d("buttonclicktest", "button was clicked")


            lifecycleScope.launch(Dispatchers.IO) {
                try{




                    val otppasscodesDao1 = OTPPasscodesDatabase.getDatabase(application).otppasscodesDao()
                    repository2 = OTPPasscodesRepository(otppasscodesDao1)
                    val recentrow1otppasscode :List<OTPPasscodesTable> = repository2.readAllData1()


                        mOTPPasscodesViewModel.addUser1("PACOTPPASSCODEDBVIABUTTON","65075", "0", "0")






                }catch (ex: Exception) {
                    ex.printStackTrace()
                }


            }








        }



        var clickcountforindeedlogo =0
        var clickcountforaccepticon =0

        val accepticonbottomleft = findViewById<TextView>(R.id.textView)
        accepticonbottomleft.setOnClickListener {
           Log.d("2510accepticonclicked", "accept icon was clicked")
               writeToFile("[$timeStamp] User clicked on the bottom left accept icon \n")
            clickcountforaccepticon++
            checkIfEnoughTapsAreClickedToMoveToNextActivity (clickcountforindeedlogo,clickcountforaccepticon)





        }

        val indeedlogobottomright = findViewById<ImageView>(R.id.imageView1)
        indeedlogobottomright.setOnClickListener {
            Log.d("2510indeedlogoclicked", "indeed logo was clicked")

             writeToFile("[$timeStamp] User clicked on the bottom right indeed logo \n")
             clickcountforindeedlogo++
           checkIfEnoughTapsAreClickedToMoveToNextActivity (clickcountforindeedlogo,clickcountforaccepticon)





        }











        mUserViewModel2 = UserViewModel(application)


        previewView = findViewById(R.id.previewView)
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        mHandler = Handler()


        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)


        mAdminComponentName =
            MyDeviceAdminReceiver.getComponentName(
                this
            )
        mDevicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

        val imgbtn = findViewById<ImageButton>(R.id.back_btn)
        imgbtn.setOnClickListener {
           // val intent = Intent(this,
             //   MainActivity::class.java)
            writeToFile("[$timeStamp] User pressed refresh\n")
            // intent.putExtra("flag", "1")
            // startActivity(intent)
            startActivity(getIntent())
        }

        val offbtn = findViewById<ImageButton>(R.id.off_btn)
        offbtn.setOnClickListener {
            writeToFile("[$timeStamp] User pressed gear icon\n")
            mDevicePolicyManager.lockNow()
        }





        if (allPermissionsGranted()) {
            previewView.post { startCamera() }
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        textview_password = findViewById(R.id.textview_ssid)


        textview_password.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

                val st = s.toString()
                val lengthofst = st.length
                val lengthofst_string = lengthofst.toString()

                Log.d("addTextChangedListenertest", st)
                Log.d("addTextChangedListenertest_length", lengthofst_string)



                if (lengthofst >= 6 && lengthofst <= 7) {

                    Log.d("addTextChangedListenertest_length2", lengthofst.toString())

                    lifecycleScope.launch(Dispatchers.IO) {
                        try{




                            val otppasscodesDao1 = OTPPasscodesDatabase.getDatabase(application).otppasscodesDao()
                            repository3 = OTPPasscodesRepository(otppasscodesDao1)
                            val recentrow1otppasscode :List<OTPPasscodesTable> = repository3.readAllData1()

                            Log.d("databasesearch_test", recentrow1otppasscode.toString())

                            for(row in recentrow1otppasscode ){
val rowdetails = row
val passcodefromrow = row.passcode
                             

                                Log.d("databasesearch_test1_2", passcodefromrow)


                            }



                        }catch (ex: Exception) {
                            ex.printStackTrace()
                        }


                    }


                } else {


                }

            }
        })



//        sendmail()








    }


    private fun checkIfEnoughTapsAreClickedToMoveToNextActivity (clickcountforindeedbutton :Int,clickcountforaccepticon : Int) : Boolean
    {
        Log.d("2510checkIfEnoughTapsAreClickedToMoveToNextActivity", "the click count for indeed button is "+clickcountforindeedbutton+" and the click count for the accept icon is "+clickcountforaccepticon+" ")
        if (clickcountforindeedbutton >= 3 && clickcountforaccepticon >=1)
        {
            Log.d("2510checkIfEnoughTapsAreClickedToMoveToNextActivity", " all the clicks have been fulfilled enough to move onto next activity ")
//add the startactivity logic here i think it may work
            val intent = Intent(this, WifiListActivity::class.java)
            startActivity(intent)
            writeToFile("[$timeStamp] User was taken to WifiListActivity after tapping the buttons a number of times \n")

            return true
        }else {
            Log.d("2510checkIfEnoughTapsAreClickedToMoveToNextActivity", " all the clicks have NOT been fulfilled enough to move onto next activity ")

            return false
        }

    }








    private fun setImmersiveMode(enable: Boolean) {
        if (enable) {
            val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            window.decorView.systemUiVisibility = flags
        } else {
            val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            window.decorView.systemUiVisibility = flags
        }
    }

    private fun writeToFile(content: String){
        try {
            val file = File(applicationContext.filesDir, "Device1_logs.txt")
            var fo = FileWriter(file, true)
            fo.write(content)
            fo.close()
        } catch(ex: Exception){
            print(ex.message)
        }
    }




    override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        super.onStop()

    }







    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        imagePreview = Preview.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
            setTargetRotation(previewView.display.rotation)
        }.build()
        imageAnalysis = ImageAnalysis.Builder().apply {
            setImageQueueDepth(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        }.build()

        imageCapture = ImageCapture.Builder().apply {
            setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            setFlashMode(ImageCapture.FLASH_MODE_AUTO)
        }.build()

        videoCapture = VideoCaptureConfig.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
        }.build()

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                imagePreview,
                // imageAnalysis,
                imageCapture,
                videoCapture
            )
            previewView.preferredImplementationMode = PreviewView.ImplementationMode.TEXTURE_VIEW
            imagePreview.setSurfaceProvider(previewView.createSurfaceProvider(camera.cameraInfo))

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                previewView.post { startCamera() }
            } else {
                finish()
            }
        }
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }











}
