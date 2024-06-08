package com.beapps.starmaxtestproject.UI

import android.Manifest.permission
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beapps.starmaxtestproject.domain.BleInfo
import com.beapps.starmaxtestproject.data.DataAdapter
import com.beapps.starmaxtestproject.domain.DataList
import com.beapps.starmaxtestproject.domain.PDFHelper
import com.beapps.starmaxtestproject.R
import com.beapps.starmaxtestproject.domain.Utils
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleMtuChangedCallback
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


class ConnectingAndDisplayData : AppCompatActivity() {
    private val PERMISSION_REQUEST_CODE: Int = 100
    private lateinit var viewTreeLifecycleOwner: LinearLayout
    lateinit var connectingLayout: LinearLayout
    lateinit var connectingProgressBar: ProgressBar
    lateinit var connectingTxtView: TextView
    lateinit var dataTxtView: TextView
    lateinit var dataView: LinearLayout
    lateinit var tryAgainBtn: Button
    private lateinit var createPDF: TextView
    private lateinit var saveData: TextView
    private val notifyServiceUUID: UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
    private val notifyCharacteristicUUID: UUID =
        UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
    var value: String = "No Data Received yet !"
    var isDataRetreived = false
    private var order = 0;
    private lateinit var dataAdapter: DataAdapter
    private lateinit var dataRecyclerView: RecyclerView

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connecting_and_display_data)
        val animateTxtView = findViewById<TextView>(R.id.animateText)
        val anim = ObjectAnimator.ofFloat(animateTxtView , View.ALPHA , 1f)
        anim.duration = 1200
        anim.repeatMode = ValueAnimator.REVERSE
        anim.repeatCount = ValueAnimator.INFINITE
        anim.start()
        dataAdapter =
            DataAdapter(ArrayList(), this)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        dataRecyclerView = findViewById(R.id.list)
        dataRecyclerView.layoutManager = linearLayoutManager
        dataRecyclerView.adapter = dataAdapter
        connectingLayout = findViewById(R.id.connectingLayout)
        connectingProgressBar = findViewById(R.id.connectingProgressBar)
        connectingTxtView = findViewById(R.id.connectingTextView)
        dataView = findViewById(R.id.dataView)
        dataTxtView = findViewById(R.id.dataTextView)
        tryAgainBtn = findViewById(R.id.tryAgainBtn)
        tryAgainBtn.setOnClickListener { startConnecting() }
        viewTreeLifecycleOwner = findViewById(R.id.view_tree_lifecycle_owner)
        createPDF = findViewById(R.id.CreatePdf)
        saveData = findViewById(R.id.SavaData)
        createPDF.setOnClickListener(View.OnClickListener {
            generatePDF()
        })
        saveData.setOnClickListener(View.OnClickListener {
            if (!isDataRetreived) {
                    Toast.makeText(this , "No Data Retrieved yet !" , Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            order++
            val data = DataList("Data $order", value)
            dataAdapter.updateList(data)
            dataRecyclerView.scrollToPosition(dataAdapter.itemCount - 1)
        })

        // for test
//        connectingLayout.visibility = View.GONE
//        dataView.visibility = View.VISIBLE
//        value = "Test Data"
//        dataTxtView.text = value
//        isDataRetreived = true

        //

        // for production
        startConnecting()

    }

    private fun genferatePDF() {

//            if (datalist.size > 0) {
//                if (checkPermission()) {
//                    Url.setVisibility(View.VISIBLE)
//                    Logo.setVisibility(View.VISIBLE)
//                    val file =
//                        File(Environment.getExternalStorageDirectory().absolutePath + "/download/PDF" + System.currentTimeMillis())
//                    val pdfHelper = PDFHelper(file, this@MainActivity)
//                    pdfHelper.saveImageToPDF(
//                        view_tree_lifecycle_owner,
//                        getScreenshotFromRecyclerView(l),
//                        "Maintenance" + SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(
//                            Date()
//                        )
//                    )
//                    Url.setVisibility(View.GONE)
//                    Logo.setVisibility(View.GONE)
//                } else {
//                    requestPermission()
//                }
//            }
    }

    private fun generatePDF() {
        if (dataAdapter.itemCount == 0) {
            Toast.makeText(this , "Save Your Readings First !" , Toast.LENGTH_SHORT).show()
            return
        }
        var outputStream: OutputStream? = null
        val fileName = "Hm-10 Reading (${
            SimpleDateFormat(
                "dd-MM-yyyy - HH:mm:ss",
                Locale.getDefault()
            ).format(Date())
        })"
        val mimeType = "application/pdf"
        val relativePath = "Hm-10 Reports"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues()
            // Enter the name of the file here. Note the extension isn't necessary
            contentValues.put(
                MediaStore.Downloads.DISPLAY_NAME,
                fileName
            )
            contentValues.put(MediaStore.Downloads.RELATIVE_PATH, "Download/$relativePath");
            // Here we define the file type. Do check the MIME_TYPE for your file. For jpegs it is "image/jpeg"
            contentValues.put(MediaStore.Downloads.MIME_TYPE, mimeType)
            val uri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val itemUri = contentResolver.insert(uri, contentValues)
            if (itemUri != null) {
                outputStream = contentResolver.openOutputStream(itemUri)
            }
        }
        else {
            if (checkPermission()) {
                val file =
                    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/" + relativePath)
                if (!file.exists()) file.mkdirs()
                val mFile = File(file, "$fileName.pdf")
                if (!mFile.exists()) {
                    try {
                        mFile.createNewFile()
                        outputStream = FileOutputStream(mFile)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            else {
                requestPermission()
                outputStream = null
            }
        }

        outputStream?.let { it ->
            PDFHelper.saveImageToPDF(
                this,
                Utils.getScreenshotFromRecyclerView(dataRecyclerView),
                it
            )
        }
    }

    private fun checkPermission(): Boolean {
        // checking of permissions.
        val permission1 =
            ContextCompat.checkSelfPermission(applicationContext, permission.WRITE_EXTERNAL_STORAGE)
        val permission2 =
            ContextCompat.checkSelfPermission(applicationContext, permission.READ_EXTERNAL_STORAGE)
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                // after requesting permissions we are showing
                // users a toast message of permission granted.
                val writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (!writeStorage || !readStorage) {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun defaultProp() {
        connectingTxtView.text = "Connecting To The Device\n\nPlease Wait ..."
        tryAgainBtn.visibility = View.GONE
        connectingLayout.visibility = View.VISIBLE
        connectingProgressBar.visibility = View.VISIBLE
        connectingTxtView.visibility = View.VISIBLE
    }

    private fun startConnecting() {
        defaultProp()
        val bleGattCallback: BleGattCallback = object : BleGattCallback() {

            override fun onStartConnect() {
            }

            override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
                dataView.visibility = View.GONE
                connectingProgressBar.visibility = View.GONE
                connectingLayout.visibility = View.VISIBLE
                tryAgainBtn.visibility = View.VISIBLE
                connectingTxtView.visibility = View.VISIBLE
                connectingTxtView.text =
                    "Connect To The Device Was Fail (${exception?.description}) Please Try Again "
            }

            override fun onConnectSuccess(
                bleDevice: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {
                dataView.visibility = View.VISIBLE
                connectingLayout.visibility = View.GONE
                tryAgainBtn.visibility = View.GONE
                connectingTxtView.visibility = View.GONE
                requestPermission()
                openNotify(bleDevice)

            }

            override fun onDisConnected(
                isActiveDisConnected: Boolean,
                device: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {
                connectingProgressBar.visibility = View.GONE
                connectingTxtView.text = "The Device Was DisConnected"
                tryAgainBtn.visibility = View.VISIBLE
            }

            override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {

            }

        }
        if (BleManager.getInstance().isConnected(BleInfo.selectedDevice.value)) {
            connectingLayout.visibility = View.GONE
            tryAgainBtn.visibility = View.GONE
            dataView.visibility = View.VISIBLE
            openNotify(BleInfo.selectedDevice.value)
            return
        }
        BleManager.getInstance().connect(BleInfo.selectedDevice.value, bleGattCallback)
    }

    fun openNotify(newBleDevice: BleDevice?) {
        dataTxtView.text = value
        BleManager.getInstance().notify(
            newBleDevice,
            notifyServiceUUID.toString(),
            notifyCharacteristicUUID.toString(),
            object : BleNotifyCallback() {
                override fun onNotifySuccess() {
                    changeMtu {

                    }
                }

                override fun onNotifyFailure(exception: BleException) {
                    dataTxtView.text = "onNotifyFailure ${exception.description}"
                }

                @SuppressLint("MissingPermission", "NewApi")
                override fun onCharacteristicChanged(data: ByteArray) {
                    isDataRetreived = true
                    value = String(data)
                    dataTxtView.text = value
                }
            })
    }


    fun changeMtu(onMtuChanged: () -> Unit) {
        BleManager.getInstance()
            .setMtu(BleInfo.selectedDevice.value, 512, object : BleMtuChangedCallback() {
                override fun onSetMTUFailure(exception: BleException) {
                    // 设置MTU失败
                }

                override fun onMtuChanged(mtu: Int) {
                    BleManager.getInstance().setSplitWriteNum(mtu - 3)
                    onMtuChanged()
                }
            })
    }

}