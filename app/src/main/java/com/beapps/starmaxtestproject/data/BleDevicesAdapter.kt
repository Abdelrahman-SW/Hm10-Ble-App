package com.beapps.starmaxtestproject.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beapps.starmaxtestproject.R
import com.beapps.starmaxtestproject.UI.ConnectingAndDisplayData
import com.beapps.starmaxtestproject.domain.BleInfo
import com.clj.fastble.data.BleDevice


class BleDeviceAdapter(private val context : Context , private val bleDeviceList: MutableList<BleDevice>) : RecyclerView.Adapter<BleDeviceAdapter.BleDeviceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDeviceViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.device_item_layout, parent, false)
        return BleDeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: BleDeviceViewHolder, position: Int) {
        val bleDevice = bleDeviceList[position]
        holder.bind(bleDevice)
    }

    override fun getItemCount(): Int {
        return bleDeviceList.size
    }

    fun updateDevices(devices : List<BleDevice>) {
        bleDeviceList.clear()
        bleDeviceList.addAll(devices)
        notifyDataSetChanged()
    }

    @SuppressLint("MissingPermission")
    inner class BleDeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView

        init {
            nameTextView = itemView.findViewById(R.id.device_name)
            nameTextView.setOnClickListener {
                val bleDevice = bleDeviceList[adapterPosition]
                BleInfo.selectedDevice.value = bleDevice
                context.startActivity(Intent(context , ConnectingAndDisplayData::class.java))
            }
        }

        @SuppressLint("MissingPermission")
        fun bind(bleDevice: BleDevice) {
            val str1 = bleDevice.name ?: bleDevice.device.name
            val str = str1?.ifEmpty { bleDevice.device.address } ?: bleDevice.device.address
            nameTextView.text = str
        }
    }
}


