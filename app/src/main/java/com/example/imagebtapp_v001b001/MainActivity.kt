package com.example.imagebtapp_v001b001

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.*
import android.os.SystemClock.sleep
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_con_state.*
import kotlinx.android.synthetic.main.fragment_feature_set.*
import kotlinx.android.synthetic.main.fragment_pair_set.*
import java.lang.Integer.parseInt
import java.lang.Long.parseLong

private const val LogMain = "testMain"
const val LogGbl = "testGlobal"
enum class CmdId(val value: Byte) {
    CMD_HEAD_FF(0xff.toByte()),
    CMD_HEAD_55(0x55.toByte()),
    CMD_DEV_HOST(0x80.toByte()),
    CMD_DEV_SRC(0x30.toByte()),
    CMD_DEV_AG(0x00.toByte()),
    CMD_DEV_AG_ALL(0x38.toByte()),
    SET_HFP_PAIR_REQ(0x02.toByte()),
    SET_HFP_PAIR_RSP(0x03.toByte()),
    SET_AG_VOL_REQ(0x06.toByte()),
    SET_AG_VOL_RSP(0x07.toByte()),
    SET_HFP_VOL_REQ(0x08.toByte()),
    SET_HFP_VOL_RSP(0x09.toByte()),
    SET_HFP_STA_REQ(0x0a.toByte()),
    SET_HFP_STA_RSP(0x0b.toByte()),
    SET_HFP_EXT_STA_REQ(0x0c.toByte()),
    SET_HFP_EXT_STA_RSP(0x0d.toByte()),
    SET_HFP_PSKEY_REQ(0x10.toByte()),
    SET_HFP_PSKEY_RSP(0x11.toByte()),
    SET_AG_PSKEY_REQ(0x12.toByte()),
    SET_AG_PSKEY_RSP(0x13.toByte()),
    SET_HFP_LOCAL_NAME_REQ(0x14.toByte()),
    SET_HFP_LOCAL_NAME_RSP(0x15.toByte()),
    SET_AG_LOCAL_NAME_REQ(0x16.toByte()),
    SET_AG_LOCAL_NAME_RSP(0x17.toByte()),
    SET_HFP_FEATURE_REQ(0x1c.toByte()),
    SET_HFP_FEATURE_RSP(0x1d.toByte()),
    SET_AG_FEATURE_REQ(0x1e.toByte()),
    SET_AG_FEATURE_RSP(0x1f.toByte()),
    SET_HFP_DIAL_REQ(0x20.toByte()),
    SET_HFP_DIAL_RSP(0x21.toByte()),
    SET_DFU_REQ(0x3e.toByte()),
    SETDFU_RSP(0x3f.toByte()),
    SET_INT_SERVICE_REQ(0xe0.toByte()),
    SET_INT_SERVICE_RSP(0xe1.toByte()),
    SET_INT_CON_REQ(0xe2.toByte()),
    SET_INT_CON_RSP(0xe3.toByte()),
    SET_INT_DISCOVERY_REQ(0xe6.toByte()),
    SET_INT_DISCOVERY_RSP(0xe7.toByte()),
    SET_INT_PAIR_REQ(0xe8.toByte()),
    SET_INT_PAIR_RSP(0xe9.toByte()),

    GET_HFP_PAIR_REQ(0x42.toByte()),
    GET_HFP_PAIR_RSP(0x43.toByte()),
    GET_AG_VOL_REQ(0x46.toByte()),
    GET_AG_VOL_RSP(0x47.toByte()),
    GET_HFP_VOL_REQ(0x48.toByte()),
    GET_HFP_VOL_RSP(0x49.toByte()),
    GET_HFP_STA_REQ(0x4a.toByte()),
    GET_HFP_STA_RSP(0x4b.toByte()),
    GET_HFP_EXT_STA_REQ(0x4c.toByte()),
    GET_HFP_EXT_STA_RSP(0x4d.toByte()),
    GET_SRC_DEV_NO_REQ(0x4e.toByte()),
    GET_SRC_DEV_NO_RSP(0x4f.toByte()),
    GET_HFP_PSKEY_REQ(0x50.toByte()),
    GET_HFP_PSKEY_RSP(0x51.toByte()),
    GET_AG_PSKEY_REQ(0x52.toByte()),
    GET_AG_PSKEY_RSP(0x53.toByte()),
    GET_HFP_LOCAL_NAME_REQ(0x54.toByte()),
    GET_HFP_LOCAL_NAME_RSP(0x55.toByte()),
    GET_AG_LOCAL_NAME_REQ(0x56.toByte()),
    GET_AG_LOCAL_NAME_RSP(0x57.toByte()),
    GET_HFP_VRESION_REQ(0x58.toByte()),
    GET_HFP_VRESION_RSP(0x59.toByte()),
    GET_AG_VRESION_REQ(0x5a.toByte()),
    GET_AG_VRESION_RSP(0x5b.toByte()),
    GET_HFP_FEATURE_REQ(0x5c.toByte()),
    GET_HFP_FEATURE_RSP(0x5d.toByte()),
    GET_AG_FEATURE_REQ(0x5e.toByte()),
    GET_AG_FEATURE_RSP(0x5f.toByte()),
    GET_HFP_DIAL_REQ(0x60.toByte()),
    GET_HFP_DIAL_RSP(0x61.toByte()),
    GET_HFP_RSSI_REQ(0x70.toByte()),
    GET_HFP_RSSI_RSP(0x71.toByte()),
    GET_HFP_BDA_REQ(0x78.toByte()),
    GET_HFP_BDA_RSP(0x79.toByte()),
    GET_AG_BDA_REQ(0x7a.toByte()),
    GET_AG_BDA_RSP(0x7b.toByte())
}
class BtDevMsg(var btDevNo: Int = 0, var btGroup: Int = 0) {
    var btCmd = ByteArray(256 + 7)
}

class BtDevUnit {
    var bdaddr = "00:00:00:00:00:00"
    var bdaddrPair = "00:00:00:00:00:00"
    var bdaddrFilterHfp = "00:00:00:00:00:00"
    var bdaddrFilterAg = "00:00:00:00:00:00"
    var maxTalkNo = 0
    var maxAgNo = 0
    var ledLightHfp = arrayOf(0, 0, 0, 0)
    var ledLightAg = arrayOf(0, 0, 0, 0)
    var verFirmwareHfp: String = ""
    var verFirmwareAg: String = ""
    var nameAlias: String = "alias name"
    var localNameHfp: String = ""
    var localNameAg: String = ""
    var featureHfp: Int = 0
    var featureAg: Int = 0
    var featureMode: Int = 0x01000000
    var stateCon: Int = 0
    var stateDial: Int = 0
    var stateExtra: Int = 0
    var rssi: Int = 0
    var volSpkrHfp: Int = 15
    var volMicHfp: Int = 15
    var muteSpkr: Boolean = false
    var muteMic: Boolean = false
    var batLow: Boolean = false
    var devDfu: Boolean = false
    var volSpkrAg: Int = 15
    var volMicAg: Int = 15
    var batLevel: Int = 5
    var modeSrcWireHfpMicVol = 15
    var modeSrcWireHfpSpkrVol = 15
    var modeSrcWireAvSpkrVol = 15
    var modeSrcUsbHfpMicVol = 15
    var modeSrcUsbHfpSpkrVol = 15
    var modeSrcUsbAvSpkrVol = 15
    var modeSrcBtHfpMicVol = 15
    var modeSrcBtHfpSpkrVol = 15
    var modeSrcBtAvSpkrVol = 15
    var modeSrcVcsHfpMicVol = 15
    var modeSrcVcsHfpSpkrVol = 15
    var modeSrcVcsAvSpkrVol = 15
    var modeSrcSpkrDecade = 0
    var modeAgWireHfpMicVol = 15
    var modeAgWireHfpSpkrVol = 15
    var modeAgWireAvSpkrVol = 15
    var modeAgUsbHfpMicVol = 15
    var modeAgUsbHfpSpkrVol = 15
    var modeAgUsbAvSpkrVol = 15
    var modeAgBtHfpMicVol = 15
    var modeAgBtHfpSpkrVol = 15
    var modeAgBtAvSpkrVol = 15
    var modeAgVcsHfpMicVol = 15
    var modeAgVcsHfpSpkrVol = 15
    var modeAgVcsAvSpkrVol = 15
}

interface DevUnitMsg {
    fun getpreferData(): SharedPreferences
    fun getBtList(): ArrayList<String>
    fun getBtDevUnitList(): ArrayList<BtDevUnit>
    fun sendBtServiceMsg(msg: BtDevMsg)
    fun getPairState(): Int
}

val ViewPagerArray = arrayOf(FragmentConState(), FragmentPairSet(), FragmentFeatureSet(), FragmentVolSet())

class MainActivity : AppCompatActivity(), DevUnitMsg {
    private var BtList = ArrayList<String>()
    private var BtDevUnitList = ArrayList<BtDevUnit>()
    private val MaxBtDev = 2
    private var PairState = 0
    private val adapterPager = ViewPagerAdapter(supportFragmentManager)
    private val BtPermissionReqCode = 1
    private val BtActionReqCode = 3
    private lateinit var preferData: SharedPreferences
    private var iMageBtServiceBind = false
    private lateinit var iMageBtServiceMsg: Messenger
    private val clientMsgHandler = Messenger(Handler(Handler.Callback {
        when(it.what) {
            0 -> {
                Logger.d(LogMain, "iMage service message send success")
            }
            else -> {
                val btDevMsg = it.obj as BtDevMsg?
                btDevMsg?.let{
                    when(it.btDevNo) {
                        0 -> {

                        }
                        1 -> {

                        }
                        else -> {

                        }
                    }
                    iMageCmdParse(it)
                }
            }
        }
        true
    }))
    private val iMageBtServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val msg = Message.obtain(null, 0, BtDevMsg(0, 0))

            Logger.d(LogMain, "onServiceConnected")
            iMageBtServiceBind = true
            iMageBtServiceMsg = Messenger(service)                  // send client messenger handler to iMageBtService
            msg.replyTo = clientMsgHandler
            iMageBtServiceMsg.send(msg)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Logger.d(LogMain, "onServiceDisconnected")
            iMageBtServiceBind = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // val intentFilter = IntentFilter()

        setContentView(R.layout.activity_main)
        Logger.d(LogMain, "onCreate")
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT                 // set screen
        // requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // intentFilter.addAction("iMageBroadcastMain")                            // register broadcast receiver
        // registerReceiver(iMageBtBroadcast(), intentFilter)

        initBt()

        btnStaUpdate.setOnClickListener {
            stateUpdate()
        }
        btnCon.setOnClickListener {
            var sendMsg = BtDevMsg(0, 1)
            var strList = preferData.getString("bdaddr${0}", "00:00:00:00:00:00")!!.split(':')

            sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
            sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
            sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
            sendMsg.btCmd[3] = CmdId.CMD_DEV_HOST.value
            sendMsg.btCmd[4] = CmdId.SET_INT_CON_REQ.value
            sendMsg.btCmd[5] = 0x07
            sendMsg.btCmd[6] = 0x01
            sendMsg.btCmd[7] = parseInt(strList[3], 16).toByte()
            sendMsg.btCmd[8] = parseInt(strList[4], 16).toByte()
            sendMsg.btCmd[9] = parseInt(strList[5], 16).toByte()
            sendMsg.btCmd[10] = parseInt(strList[2], 16).toByte()
            sendMsg.btCmd[11] = parseInt(strList[0], 16).toByte()
            sendMsg.btCmd[12] = parseInt(strList[1], 16).toByte()
            Logger.d(LogGbl, "${String.format("bdaddr %02X %02X %02X %02X %02X %02X ", sendMsg.btCmd[11], sendMsg.btCmd[12], sendMsg.btCmd[10], sendMsg.btCmd[7], sendMsg.btCmd[8], sendMsg.btCmd[9])}")
            sendBtServiceMsg(sendMsg)
        }
        btnCon.setOnLongClickListener {
            var sendMsg = BtDevMsg(1, 1)
            var strList: List<String> = preferData.getString("bdaddr${1}", "00:00:00:00:00:00")!!.split(':')

            sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
            sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
            sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
            sendMsg.btCmd[3] = CmdId.CMD_DEV_HOST.value
            sendMsg.btCmd[4] = CmdId.SET_INT_CON_REQ.value
            sendMsg.btCmd[5] = 0x07
            sendMsg.btCmd[6] = 0x01
            sendMsg.btCmd[7] = parseInt(strList[3], 16).toByte()
            sendMsg.btCmd[8] = parseInt(strList[4], 16).toByte()
            sendMsg.btCmd[9] = parseInt(strList[5], 16).toByte()
            sendMsg.btCmd[10] = parseInt(strList[2], 16).toByte()
            sendMsg.btCmd[11] = parseInt(strList[0], 16).toByte()
            sendMsg.btCmd[12] = parseInt(strList[1], 16).toByte()
            Logger.d(LogGbl, "${String.format("bdaddr %02X %02X %02X %02X %02X %02X ", sendMsg.btCmd[11], sendMsg.btCmd[12], sendMsg.btCmd[10], sendMsg.btCmd[7], sendMsg.btCmd[8], sendMsg.btCmd[9])}")
            sendBtServiceMsg(sendMsg)
            true
        }
        stateUpdateAuto(parseInt(resources.getString(R.string.timeUpdate)).toLong())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Logger.d(LogMain, "request permission result")
        if(requestCode == BtPermissionReqCode) {
            if(grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                initBt()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.d(LogMain, " activity result:$resultCode requestCode:$requestCode")
        when(requestCode) {
            BtActionReqCode -> {
                if(resultCode == Activity.RESULT_OK) {
                    Logger.d(LogMain, "bluetooth enabled")
                    initBt()
                } else {
                    Logger.d(LogMain, "bluetooth disable")
                    finish()
                }
            }
        }
    }
/*
    inner class iMageBtBroadcast : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Logger.d(LogMain, "iMageBtBroadcast receive")
            when(intent!!.action) {
                "iMageBroadcastMain" -> {
                }
                else -> {
                }
            }
        }
    }
*/
    fun initBt() {
        Logger.d(LogMain, "initBt")
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Logger.d(LogMain, "bluetooth permission request")
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), BtPermissionReqCode)
        } else if (BluetoothAdapter.getDefaultAdapter().isEnabled) {
            Logger.d(LogMain, "bluetooth enabled")
            if (!iMageBtServiceBind) {
                // startService(Intent(this, iMageBtService::class.java))
                bindService(Intent(this, iMageBtService::class.java), iMageBtServiceConnection, Context.BIND_AUTO_CREATE)
                txvConSta0.text = "Enable"
                txvConSta1.text = "Enable"
                preferData = getSharedPreferences("iMageBdaList", Context.MODE_PRIVATE)     // create prefer data
                viewPagerM6.adapter = adapterPager
                BtDevUnitList.add(BtDevUnit())
            }
        } else {
            Logger.d(LogMain, "bluetooth action request")
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BtActionReqCode)
        }
    }

    override fun sendBtServiceMsg(msg: BtDevMsg) {
        val sendMsg = Message.obtain(null, 1, msg)

        if(iMageBtServiceBind)
            iMageBtServiceMsg.send(sendMsg)
/*
        when(msg.btGroup) {
            0 -> sendBroadcast(Intent("iMageBtBroadcastService").putExtra("btServiceMsg", msg))
            1 -> sendBroadcast(Intent("iMageBtBroadcastService").putExtra("btServiceMsg", msg))
            else -> Logger.d(LogMain, "send broadcast other message")
        }
 */
    }

    override fun getBtList(): ArrayList<String> = BtList
    override fun getBtDevUnitList(): ArrayList<BtDevUnit> = BtDevUnitList
    override fun getpreferData(): SharedPreferences = preferData
    override fun getPairState(): Int = PairState

    fun stateUpdateAuto(time: Long) {
        Handler().postDelayed({
            stateUpdate()
            stateUpdateAuto(time)
        }, time)
    }

    fun stateUpdate() {
        val srcDevId = arrayOf(CmdId.CMD_DEV_SRC.value, CmdId.CMD_DEV_AG_ALL.value)
        val pskey = arrayOf(9, 16, 17, 18)
        val cmdId = arrayOf(
            CmdId.GET_HFP_LOCAL_NAME_REQ.value,
            CmdId.GET_AG_LOCAL_NAME_REQ.value,
            CmdId.GET_HFP_VRESION_REQ.value,
            CmdId.GET_AG_VRESION_REQ.value,
            CmdId.GET_HFP_FEATURE_REQ.value,
            CmdId.GET_AG_FEATURE_REQ.value,
            CmdId.GET_HFP_BDA_REQ.value,
            CmdId.GET_AG_BDA_REQ.value,
            CmdId.GET_HFP_VOL_REQ.value,
            CmdId.GET_HFP_STA_REQ.value,
            CmdId.GET_HFP_EXT_STA_REQ.value,
            CmdId.GET_HFP_PAIR_REQ.value,
            CmdId.GET_HFP_RSSI_REQ.value)

        for(j in 0 until srcDevId.size) {
            var srcDevId = srcDevId[j]
            for (i in 0 until cmdId.size) {
                var sendMsg = BtDevMsg(0, 0)

                sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                sendMsg.btCmd[3] = srcDevId
                sendMsg.btCmd[4] = cmdId[i]
                sendMsg.btCmd[5] = 0x00
                sendBtServiceMsg(sendMsg)
            }
        }
        for(j in 0 until srcDevId.size) {
            var srcDevId = srcDevId[j]
            for(i in 0 until pskey.size) {
                var sendMsg = BtDevMsg(0, 0)

                sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                sendMsg.btCmd[3] = srcDevId
                sendMsg.btCmd[4] = CmdId.GET_HFP_PSKEY_REQ.value
                sendMsg.btCmd[5] = 0x02
                sendMsg.btCmd[6] = 0x00
                sendMsg.btCmd[7] = pskey[i].toByte()
                sendBtServiceMsg(sendMsg)
            }
        }
    }

    fun getBtDevId(id: Byte) =
        when(id) {
            0x30.toByte() -> 0
            0x00.toByte() -> 1
            0x08.toByte() -> 2
            0x10.toByte() -> 3
            0x18.toByte() -> 4
            0x20.toByte() -> 5
            0x28.toByte() -> 6
            else -> 127
        }
    fun iMageCmdParse(msg: BtDevMsg) {
/*
        val len = msg.btCmd[5] + 6
        var cmdStr = ""
        for(i: Int in 0..len)
            cmdStr += String.format("%02X ", msg.btCmd[i])
        Logger.d(LogMain, "${cmdStr}")
 */
        // Logger.d(LogMain, "${String.format("command src:%02X id:%02X", msg.btCmd[2], msg.btCmd[4])}")
        var id = getBtDevId(msg.btCmd[2])

        when(msg.btCmd[4]) {
            CmdId.SET_AG_VOL_RSP.value -> Logger.d(LogMain, "${String.format("src:%02X AG volume set", msg.btCmd[2])}")
            CmdId.SET_HFP_VOL_RSP.value -> Logger.d(LogMain, "${String.format("src:%02X HFP volume set", msg.btCmd[2])}")
            CmdId.SET_HFP_PAIR_RSP.value -> Logger.d(LogMain, "${String.format("src:%02X set hfp pair bda", msg.btCmd[2])}")
            CmdId.SET_HFP_PSKEY_RSP.value -> Logger.d(LogMain, "${String.format("src:%02X set hfp pskey", msg.btCmd[2])}")
            CmdId.SET_AG_PSKEY_RSP.value -> Logger.d(LogMain, "${String.format("src:%02X set ag pskey", msg.btCmd[2])}")
            CmdId.SET_HFP_FEATURE_RSP.value -> Logger.d(LogMain, "${String.format("src:%02X set hfp feature", msg.btCmd[2])}")
            CmdId.GET_SRC_DEV_NO_REQ.value -> Logger.d(LogMain, "${String.format("src:%02X get device number", msg.btCmd[2])}")
            CmdId.GET_HFP_STA_RSP.value -> {
                BtDevUnitList[id].stateCon = msg.btCmd[6].toInt().and(0xff).shl(24) + msg.btCmd[7].toInt().and(0xff).shl(16) + msg.btCmd[8].toInt().and(0xff).shl(8) + msg.btCmd[9].toInt().and(0xff)
                BtDevUnitList[id].volSpkrHfp = msg.btCmd[7].toInt().and(0x0f)
                BtDevUnitList[id].muteSpkr = msg.btCmd[7].toInt().and(0x10) == 0x10
                BtDevUnitList[id].muteMic = msg.btCmd[7].toInt().and(0x20) == 0x20
                Logger.d(LogMain, "${String.format("src:%02X get hfp state", msg.btCmd[2])}")
            }
            CmdId.GET_HFP_EXT_STA_RSP.value -> {
                BtDevUnitList[id].stateExtra = msg.btCmd[6].toInt().and(0xff).shl(8) + msg.btCmd[7].toInt().and(0xff)
                BtDevUnitList[id].batLevel = BtDevUnitList[id].stateExtra.shr(12)
                BtDevUnitList[id].batLow =
                    if(BtDevUnitList[id].stateExtra.and(0x04) == 0x04)
                        true
                    else
                        false
                Logger.d(LogMain, "${String.format("src:%02X get hfp extra state", msg.btCmd[2])}")
            }
            CmdId.GET_HFP_VOL_RSP.value -> {
                BtDevUnitList[id].volSpkrHfp = msg.btCmd[6].toInt().and(0x0f)
                BtDevUnitList[id].muteSpkr = msg.btCmd[6].toInt().and(0x80) == 0x80
                BtDevUnitList[id].muteMic = msg.btCmd[6].toInt().and(0x40) == 0x40
                viewPagerM6.setCurrentItem(0)
                Logger.d(LogMain, "${String.format("src %02X get hfp vol", msg.btCmd[2])}")
            }
            CmdId.GET_HFP_RSSI_RSP.value -> {
                BtDevUnitList[id].rssi = (0x10000 - (msg.btCmd[6].toInt().and(0xff).shl(8) + msg.btCmd[7].toInt().and(0xff))).and(0xffff)
            }
            CmdId.GET_HFP_PSKEY_RSP.value -> {
                val pskId = msg.btCmd[6].toInt().and(0xff).shl(8) + msg.btCmd[7].toInt().and(0xff)
                Logger.d(LogMain, "${String.format("get hfp pskey id:%d", pskId)}")
                when(pskId) {
                    9 -> {
                        BtDevUnitList[id].featureMode = msg.btCmd[8].toInt().and(0xff).shl(24) + msg.btCmd[9].toInt().and(0xff).shl(16) + msg.btCmd[10].toInt().and(0xff).shl(8) + msg.btCmd[11].toInt().and(0xff)
                        if(viewPagerM6.currentItem == 2)
                            (ViewPagerArray[2] as FragmentFeatureSet).updataData()
                    }
                    16 -> {

                    }
                    17 -> {
                        BtDevUnitList[id].modeSrcWireHfpMicVol = msg.btCmd[8].toInt()
                        BtDevUnitList[id].modeSrcWireHfpSpkrVol = msg.btCmd[9].toInt()
                        BtDevUnitList[id].modeSrcUsbHfpMicVol = msg.btCmd[10].toInt()
                        BtDevUnitList[id].modeSrcUsbHfpSpkrVol = msg.btCmd[11].toInt()
                        BtDevUnitList[id].modeSrcBtHfpMicVol = msg.btCmd[12].toInt()
                        BtDevUnitList[id].modeSrcBtHfpSpkrVol = msg.btCmd[13].toInt()
                        BtDevUnitList[id].modeSrcVcsHfpMicVol = msg.btCmd[14].toInt()
                        BtDevUnitList[id].modeSrcVcsHfpSpkrVol = msg.btCmd[15].toInt()
                        BtDevUnitList[id].modeSrcWireAvSpkrVol = msg.btCmd[16].toInt()
                        BtDevUnitList[id].modeSrcUsbAvSpkrVol = msg.btCmd[17].toInt()
                        BtDevUnitList[id].modeSrcBtAvSpkrVol = msg.btCmd[18].toInt()
                        BtDevUnitList[id].modeSrcVcsAvSpkrVol = msg.btCmd[19].toInt()
                        BtDevUnitList[id].modeSrcSpkrDecade = msg.btCmd[20].toInt()
                        if(viewPagerM6.currentItem == 3)
                            (ViewPagerArray[3] as FragmentVolSet).updataData()
                    }
                    18 -> {
                        BtDevUnitList[id].modeAgWireHfpMicVol = msg.btCmd[8].toInt()
                        BtDevUnitList[id].modeAgWireHfpSpkrVol = msg.btCmd[9].toInt()
                        BtDevUnitList[id].modeAgUsbHfpMicVol = msg.btCmd[10].toInt()
                        BtDevUnitList[id].modeAgUsbHfpSpkrVol = msg.btCmd[11].toInt()
                        BtDevUnitList[id].modeAgBtHfpMicVol = msg.btCmd[12].toInt()
                        BtDevUnitList[id].modeAgBtHfpSpkrVol = msg.btCmd[13].toInt()
                        BtDevUnitList[id].modeAgVcsHfpMicVol = msg.btCmd[14].toInt()
                        BtDevUnitList[id].modeAgVcsHfpSpkrVol = msg.btCmd[15].toInt()
                        BtDevUnitList[id].modeAgWireAvSpkrVol = msg.btCmd[16].toInt()
                        BtDevUnitList[id].modeAgUsbAvSpkrVol = msg.btCmd[17].toInt()
                        BtDevUnitList[id].modeAgBtAvSpkrVol = msg.btCmd[18].toInt()
                        BtDevUnitList[id].modeAgVcsAvSpkrVol = msg.btCmd[19].toInt()
                        if(viewPagerM6.currentItem == 3)
                            (ViewPagerArray[3] as FragmentVolSet).updataData()
                    }
                }
            }
            CmdId.GET_AG_PSKEY_RSP.value -> {
                val pskId = msg.btCmd[6].toInt().and(0xff).shl(8) + msg.btCmd[7].toInt().and(0xff)
                Logger.d(LogMain, "${String.format("get ag pskey id:%d", pskId)}")
            }
            CmdId.GET_HFP_VRESION_RSP.value -> {
                BtDevUnitList[id].verFirmwareHfp = String(msg.btCmd, 6, msg.btCmd[5].toInt())
            }
            CmdId.GET_AG_VRESION_RSP.value -> {
                BtDevUnitList[id].verFirmwareAg = String(msg.btCmd, 6, msg.btCmd[5].toInt())
                if(id.and(0x1) == 0x1)
                    BtDevUnitList[id + 1].verFirmwareAg = String(msg.btCmd, 6, msg.btCmd[5].toInt())

            }
            CmdId.GET_HFP_LOCAL_NAME_RSP.value -> {
                BtDevUnitList[id].localNameHfp = String(msg.btCmd, 6, msg.btCmd[5].toInt())
            }
            CmdId.GET_AG_LOCAL_NAME_RSP.value -> {
                BtDevUnitList[id].localNameAg = String(msg.btCmd, 6, msg.btCmd[5].toInt())
                if(id.and(0x1) == 0x1)
                    BtDevUnitList[id + 1].localNameAg = String(msg.btCmd, 6, msg.btCmd[5].toInt())
            }
            CmdId.GET_SRC_DEV_NO_RSP.value -> {
                Logger.d(LogMain, "${String.format("src:%02X source device number:%02d", msg.btCmd[2], msg.btCmd[6])}")
                if(msg.btCmd[2] == 0x30.toByte()) {
                    BtDevUnitList.removeAll(BtDevUnitList)
                    BtDevUnitList.add(BtDevUnit())
                    for (i in 0 until msg.btCmd[6])
                        BtDevUnitList.add(BtDevUnit())
                }
                stateUpdate()
            }
            CmdId.GET_HFP_FEATURE_RSP.value -> {
                BtDevUnitList[id].featureHfp = msg.btCmd[6].toInt().and(0xff).shl(8) + msg.btCmd[7].toInt().and(0xff)
                BtDevUnitList[id].maxAgNo = msg.btCmd[8].toInt()
                BtDevUnitList[id].maxTalkNo = msg.btCmd[9].toInt()
                BtDevUnitList[id].bdaddrFilterHfp = String.format("%02X:%02X:%02X:%02X:%02X:%02X", msg.btCmd[15], msg.btCmd[16], msg.btCmd[14], msg.btCmd[10], msg.btCmd[11], msg.btCmd[12])
                for(i in 0 until 4) {
                    BtDevUnitList[id].ledLightHfp[i] = msg.btCmd[17 + i * 2].toUByte().toInt().shl(8) + msg.btCmd[17 + i * 2 + 1].toUByte().toInt()
                }
                if(viewPagerM6.currentItem == 2)
                    (ViewPagerArray[2] as FragmentFeatureSet).updataData()
                Logger.d(LogMain, "${String.format("src:%02X source feature:%04X", msg.btCmd[2], BtDevUnitList[id].featureHfp)}")
            }
            CmdId.GET_AG_FEATURE_RSP.value -> {
                BtDevUnitList[id].featureAg = msg.btCmd[6].toInt().and(0xff).shl(8) + msg.btCmd[7].toInt().and(0xff)
                BtDevUnitList[id].bdaddrFilterAg = String.format("%02X:%02X:%02X:%02X:%02X:%02X", msg.btCmd[15], msg.btCmd[16], msg.btCmd[14], msg.btCmd[10], msg.btCmd[11], msg.btCmd[12])
                for(i in 0 until 4) {
                    BtDevUnitList[id].ledLightAg[i] = msg.btCmd[17 + i * 2].toUByte().toInt().shl(8) + msg.btCmd[17 + i * 2 + 1].toUByte().toInt()
                }
                if(id.and(0x1) == 0x1) {
                    BtDevUnitList[id + 1].featureAg = msg.btCmd[6].toInt().and(0xff).shl(8) + msg.btCmd[7].toInt().and(0xff)
                    BtDevUnitList[id + 1].bdaddrFilterAg = String.format("%02X:%02X:%02X:%02X:%02X:%02X", msg.btCmd[15], msg.btCmd[16], msg.btCmd[14], msg.btCmd[10], msg.btCmd[11], msg.btCmd[12])
                    for(i in 0 until 4) {
                        BtDevUnitList[id + 1].ledLightAg[i] = msg.btCmd[17 + i * 2].toUByte().toInt().shl(8) + msg.btCmd[17 + i * 2 + 1].toUByte().toInt()
                    }
                }
                if(viewPagerM6.currentItem == 2)
                    (ViewPagerArray[2] as FragmentFeatureSet).updataData()
                Logger.d(LogMain, "${String.format("src:%02X source feature:%04X", msg.btCmd[2], BtDevUnitList[id].featureAg)}")
            }
            CmdId.GET_HFP_PAIR_RSP.value -> {
                BtDevUnitList[id].bdaddrPair = String.format("%02X:%02X:%02X:%02X:%02X:%02X", msg.btCmd[11], msg.btCmd[12], msg.btCmd[10], msg.btCmd[7], msg.btCmd[8], msg.btCmd[9])
            }
            CmdId.GET_AG_BDA_RSP.value -> {
                BtDevUnitList[id].bdaddr = String.format("%02X:%02X:%02X:%02X:%02X:%02X", msg.btCmd[11], msg.btCmd[12], msg.btCmd[10], msg.btCmd[7], msg.btCmd[8], msg.btCmd[9])
                if(id.and(0x1) == 0x1)
                    BtDevUnitList[id + 1].bdaddr = String.format("%02X:%02X:%02X:%02X:%02X:%02X", msg.btCmd[11], msg.btCmd[12], msg.btCmd[10], msg.btCmd[7], msg.btCmd[8], msg.btCmd[9])
            }
            CmdId.SET_INT_SERVICE_RSP.value ->
                when(msg.btCmd[6]) {
                    0x00.toByte() -> {
                        var strList: List<String>

                        for (i in 0 until MaxBtDev) {
                            var sendMsg =BtDevMsg(0, 1)

                            strList = preferData.getString("bdaddr${i}", "00:00:00:00:00:00")!!.split(':')
                            sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                            sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                            sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                            sendMsg.btCmd[3] = CmdId.CMD_DEV_HOST.value
                            sendMsg.btCmd[4] = CmdId.SET_INT_CON_REQ.value
                            sendMsg.btCmd[5] = 0x07
                            sendMsg.btCmd[6] = 0x01
                            sendMsg.btCmd[7] = parseInt(strList[3], 16).toByte()
                            sendMsg.btCmd[8] = parseInt(strList[4], 16).toByte()
                            sendMsg.btCmd[9] = parseInt(strList[5], 16).toByte()
                            sendMsg.btCmd[10] = parseInt(strList[2], 16).toByte()
                            sendMsg.btCmd[11] = parseInt(strList[0], 16).toByte()
                            sendMsg.btCmd[12] = parseInt(strList[1], 16).toByte()
                            Logger.d(LogGbl, "${String.format("bdaddr %02X %02X %02X %02X %02X %02X ", sendMsg.btCmd[11], sendMsg.btCmd[12], sendMsg.btCmd[10], sendMsg.btCmd[7], sendMsg.btCmd[8], sendMsg.btCmd[9])}")
                            sendMsg.btDevNo = i
                            sendBtServiceMsg(sendMsg)
                        }
                    }
                }
            CmdId.SET_INT_CON_RSP.value -> when(msg.btDevNo) {
                0 -> txvConSta0.text =
                        when (msg.btCmd[6]) {
                            0x00.toByte() -> {
                                var sendMsg = BtDevMsg(0, 0)

                                sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                                sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                                sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                                sendMsg.btCmd[3] = CmdId.CMD_DEV_SRC.value
                                sendMsg.btCmd[4] = CmdId.GET_SRC_DEV_NO_REQ.value
                                sendMsg.btCmd[5] = 0x01
                                sendMsg.btCmd[6] = 0x00
                                sendBtServiceMsg(sendMsg)
                                // "Connected"
                                applicationContext.resources.getString(R.string.txvStaConnected)
                            }
                            0x01.toByte() -> applicationContext.resources.getString(R.string.txvStaDiscon)
                            0x02.toByte() -> applicationContext.resources.getString(R.string.txvStaConnecting)
                            else -> applicationContext.resources.getString(R.string.txvStaEnable)
                        }
                1 -> txvConSta1.text =
                    when (msg.btCmd[6]) {
                        0x00.toByte() -> applicationContext.resources.getString(R.string.txvStaConnected)
                        0x01.toByte() -> applicationContext.resources.getString(R.string.txvStaDiscon)
                        0x02.toByte() -> applicationContext.resources.getString(R.string.txvStaConnecting)
                        else -> applicationContext.resources.getString(R.string.txvStaEnable)
                    }
                else -> {
                }
            }
            CmdId.SET_INT_DISCOVERY_RSP.value -> {
                when(msg.btCmd[6]) {
                    0x00.toByte() -> {
                        PairState = 0
                    }
                    0x01.toByte() -> {
                        var str = ""

                        PairState = 1
                        for(i in 0 until (msg.btCmd[5] - 7) / 2) {
                            str += msg.btCmd[i * 2 + 13].toInt().shl(8).or(msg.btCmd[i * 2 + 13 + 1].toInt()).toChar()
                        }
                        str += String.format(" + %02X:%02X:%02X:%02X:%02X:%02X", msg.btCmd[11], msg.btCmd[12], msg.btCmd[10], msg.btCmd[7], msg.btCmd[8], msg.btCmd[9])
                        if(!BtList.contains(str))
                            BtList.add(str)
                    }
                    0x02.toByte() -> {
                        PairState = 2
                        BtList.removeAll(BtList)
                        BtList.add("clear paired device + 00:00:00:00:00:00")
                    }
                }
                if(viewPagerM6.currentItem == 1)
                    (ViewPagerArray[1] as FragmentPairSet).updataData()
                Logger.d(LogMain, " cmmmand SET_INT_DISCOVERY_RSP")
            }
            CmdId.SET_INT_PAIR_RSP.value -> {
                var str = ""
                PairState = 3
                for(i in 0 until (msg.btCmd[5] - 7) / 2) {
                    str += msg.btCmd[i * 2 + 13].toInt().shl(8).or(msg.btCmd[i * 2 + 13 + 1].toInt()).toChar()
                }
                str += String.format(" + %02X:%02X:%02X:%02X:%02X:%02X", msg.btCmd[11], msg.btCmd[12], msg.btCmd[10], msg.btCmd[7], msg.btCmd[8], msg.btCmd[9])
                BtList.add(str)
                if(viewPagerM6.currentItem == 1)
                    (ViewPagerArray[1] as FragmentPairSet).updataData()
                Logger.d(LogMain, "command SET_INT_PAIR_RSP")
            }
            else -> {
                Logger.d(LogMain, "${String.format("other command src:%02X id:%02X", msg.btCmd[2], msg.btCmd[4])}")
            }
        }
        if(viewPagerM6.currentItem == 0)
            (ViewPagerArray[0] as FragmentConState).updataData()
        // ((viewPagerM6.adapter as ViewPagerAdapter).getItem(0) as FragmentConState).recyclerDevList.adapter!!.notifyDataSetChanged()
        // viewPagerM6.adapter!!.notifyDataSetChanged()
    }
}

class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment =
        when(position) {
            0 -> ViewPagerArray[0]
            1 -> ViewPagerArray[1]
            2 -> ViewPagerArray[2]
            3 -> ViewPagerArray[3]
            else -> ViewPagerArray[0]
        }

    override fun getCount(): Int = ViewPagerArray.size
}