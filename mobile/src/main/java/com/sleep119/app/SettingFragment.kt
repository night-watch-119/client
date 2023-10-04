package com.sleep119.app

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import java.io.IOException
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val PERMISSION_REQUEST_BLUETOOTH_CONNECT = 123

class SettingFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private val REQUEST_ENABLE_BT = 1
    private val REQUEST_DISCOVERABLE = 2
    private lateinit var bluetoothEnableLauncher: ActivityResultLauncher<Intent>
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bluetoothBtn = view.findViewById<LinearLayout>(R.id.watchRegister)
        val logoutBtn = view.findViewById<LinearLayout>(R.id.userLogout)


        logoutBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Alert")
                .setMessage("기능 구현 중 입니다.")
                .setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
        }

        bluetoothBtn.setOnClickListener {
            val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
            val builder = AlertDialog.Builder(requireContext())

            if (bluetoothAdapter == null) {
                builder.setTitle("Alert")
                    .setMessage("해당 휴대폰이 블루투스를 지원하지 않습니다.")
                    .setPositiveButton("확인") { dialog, _ ->
                        dialog.dismiss()
                    }
                val dialog = builder.create()
                dialog.show()

            } else {
                if (!bluetoothAdapter.isEnabled) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    bluetoothEnableLauncher.launch(enableBtIntent)
                } else {
                    showBluetoothDevicesDialog(bluetoothAdapter)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetoothManager =
            requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
        val builder = AlertDialog.Builder(requireContext())

        bluetoothEnableLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                showBluetoothDevicesDialog(bluetoothAdapter)
            } else {
                builder.setTitle("Alert")
                    .setMessage("요청을 거부했거나 오류가 발생했습니다. 다시 시도해주세요.")
                    .setPositiveButton("확인") { dialog, _ ->
                        dialog.dismiss()
                    }
                val dialog = builder.create()
                dialog.show()
            }
        }

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun showBluetoothDevicesDialog(bluetoothAdapter: BluetoothAdapter?) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("블루투스 기기 선택")
        val deviceList = ArrayList<BluetoothDevice>()

        // Bluetooth 연결 권한 확인
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf("android.permission.BLUETOOTH_CONNECT"),
                PERMISSION_REQUEST_BLUETOOTH_CONNECT
            )
            return
        }

        bluetoothAdapter?.bondedDevices?.forEach { device ->
            deviceList.add(device)
        }

        if (deviceList.isEmpty()) {
            builder.setMessage("페어링된 블루투스 기기가 없습니다.")
        } else {
            val deviceNames = deviceList.map { it.name }.toTypedArray()
            builder.setItems(deviceNames) { _, which ->
                val selectedDevice = deviceList[which]
                connectToDevice(selectedDevice)
            }
        }

        builder.setPositiveButton("확인") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun connectToDevice(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                PERMISSION_REQUEST_BLUETOOTH_CONNECT
            )
            return
        }

        val socket: BluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID)

        try {
            socket.connect()
        } catch (e: IOException) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("연결 오류")
                .setMessage("다시 한번 시도해주세요.")
                .setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}