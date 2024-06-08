package com.beapps.starmaxtestproject.UI

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.beapps.starmaxtestproject.R
import com.clj.fastble.BleManager

class MainActivity : AppCompatActivity() {
    lateinit var scanBtn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scanBtn = findViewById(R.id.scanBtn);
        init()
        scanBtn.setOnClickListener {
             startActivity(Intent(this , ScannedDevices::class.java))
        }
    }

    private fun init() {
        BleManager.getInstance().init(application)
        BleManager.getInstance()
            .enableLog(true)
            .setReConnectCount(0, 1000)
            .setConnectOverTime(10000)
            .setOperateTimeout(5000)


//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.BLUETOOTH
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this, arrayOf(
//                    Manifest.permission.BLUETOOTH,
//                ), 1000
//            )
//        }

        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
            ), 1000
        )
        if (!isBluetoothEnabled()) {
            requestBluetoothEnable()
        }

    }

    private fun isBluetoothEnabled(): Boolean {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled
    }

    @SuppressLint("MissingPermission")
    private fun requestBluetoothEnable() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, 100)
    }

}