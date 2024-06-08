package com.beapps.starmaxtestproject.UI

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.beapps.starmaxtestproject.data.BleDeviceAdapter
import com.beapps.starmaxtestproject.domain.BleInfo
import com.beapps.starmaxtestproject.R
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import kotlinx.coroutines.launch

class ScannedDevices : AppCompatActivity() {
    @SuppressLint("MissingPermission")
    lateinit var devicesAdapter: BleDeviceAdapter
    lateinit var loadingHeader: LinearLayout
    lateinit var devicesRecycleView: RecyclerView

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanned_devices)
        devicesAdapter = BleDeviceAdapter(this , mutableListOf())
        devicesRecycleView = findViewById(R.id.devices_rcv)
        devicesRecycleView.adapter = devicesAdapter
        loadingHeader = findViewById(R.id.loading_header)
        lifecycleScope.launch {
            BleInfo.scannedDevices.collect {
                if (it.size != 0) {
                    devicesAdapter.updateDevices(it)
                }
                for (device in it) {
                    Log.i("ab_do", "device with name : ${device.device.name}")
                }
            }
        }
        startScan()
    }

    private fun startScan() {
        val newDevices: MutableList<BleDevice> = mutableListOf()
        BleManager.getInstance().scan(object : BleScanCallback() {
            override fun onScanStarted(success: Boolean) {
                Log.i("ab_do", "scan : $success")
                loadingHeader.visibility = View.VISIBLE
            }

            @SuppressLint("MissingPermission")
            override fun onScanning(bleDevice: BleDevice?) {
                Log.i("ab_do", "onScanning")

                if (bleDevice != null) {
//                    if(bleDevice.name.contains("GTS3-") || bleDevice.name.contains("GTS5-")){
//                        newDevices.add(bleDevice)
//                        devices = newDevices.toList()
//                    }
                    Log.i("ab_do", "addDevice")

                    newDevices.add(bleDevice)
                    BleInfo.scannedDevices.value = newDevices.toMutableList()
                }
            }

            @SuppressLint("MissingPermission")
            override fun onScanFinished(scanResultList: MutableList<BleDevice>?) {
                loadingHeader.visibility = View.GONE
            }

        })
    }
}