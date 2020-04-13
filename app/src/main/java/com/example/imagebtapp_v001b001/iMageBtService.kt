package com.example.imagebtapp_v001b001

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import java.io.IOException
import java.lang.Integer.parseInt
import java.lang.System.arraycopy
import java.sql.Types.NULL
import java.util.*

private const val LogService = "testService"

class iMageBtService : Service() {
    private val maxBtDdev = 7
    private val maxReceiverLen = 1024
    private var btSerivceBind = false
    lateinit var btDevArray: Array<iMageBtDev>
    private val btAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var clientMsg: Messenger
    private var serviceHandler = Messenger(Handler(Handler.Callback {
        when(it.what) {
            0 -> {
                val msg = BtDevMsg(0, 0)
                clientMsg = it.replyTo
                msg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                msg.btCmd[1] = CmdId.CMD_HEAD_55.value
                msg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                msg.btCmd[3] = CmdId.CMD_DEV_HOST.value
                msg.btCmd[4] = CmdId.SET_INT_SERVICE_RSP.value
                msg.btCmd[5] = 0x01
                msg.btCmd[6] = 0x00
                sendMainMsg(msg)
                Logger.d(LogService, "client message handle send success")
            }
            else ->{
                val btDevMsg = it.obj as BtDevMsg?
                btDevMsg?.let{
                    rfcCmdParse(it)
/*
                    when(it.btGroup) {
                        0 -> btDevArray[it.btDevNo].rfcCmdSend(it)
                        1-> rfcCmdParse(it)
                        else -> Logger.d(LogService, "other group")
                    }
*/
                }
            }
        }
        true
    }))

    override fun onBind(intent: Intent): IBinder {
        Logger.d(LogService, "onBind")
        btSerivceBind = true
        return serviceHandler.binder
    }

    override fun onCreate() {
        super.onCreate()
        Logger.d(LogService, "onCreate")
        initBtDev()
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d(LogService, "onDestroy")
        btSerivceBind = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.d(LogService, "onStartCommand")
        // btSerivceBind = true
        return super.onStartCommand(intent, flags, startId)
    }

    inner class iMageBtDev(var bdaddr: String) {
        var isReadable = false
        var rfcSocket = btAdapter.getRemoteDevice(bdaddr).createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
        var btConNo = 0
        var thread = Thread()
        val btReadThread = Runnable {
            var isRfcRecHead = false
            var isRfcRecCmd = false
            var rfcRecDataLen = 0
            val rfcRecData = ByteArray(maxReceiverLen)

            Logger.d(LogService, "bluetooth receiver thread running")
            while(rfcSocket.isConnected) {
                if(isRfcRecCmd == false) {
                    try {
                        // Logger.d(BtServiceLog, "bluetooth read data $btDevice")
                        rfcRecDataLen += rfcSocket.inputStream.read(rfcRecData, rfcRecDataLen, maxReceiverLen - rfcRecDataLen)
                    } catch (e: IOException) {
                        Logger.d(LogService, "bluetooth read fail")
                        break
                    }
                }
                // Logger.d(ktLog, "rfc receive data length: ${rfcRecDataLen}")
                isRfcRecCmd = false
                var i = 0
                while(i < rfcRecDataLen) {
                    //for(i: Int in 0 until rfcRecDataLen) {
                    // Logger.d(ktLog, "check command header $i ${rfcReceiverCmd[i]} ${rfcReceiverCmd[i + 1]}")
                    if((rfcRecData[i] == CmdId.CMD_HEAD_FF.value) && (rfcRecData[i + 1] == CmdId.CMD_HEAD_55.value)) {
                        isRfcRecHead = true
                        // Logger.d(ktLog, "rfc header is detected")
                        if(rfcRecDataLen >= i + rfcRecData[i + 5] + 7) {
                            val btDevMsg = BtDevMsg(btConNo, 0)

                            arraycopy(rfcRecData, i, btDevMsg.btCmd, 0, rfcRecData[i + 5] + 7)
                            rfcRecDataLen -= i + rfcRecData[i + 5] + 7
                            arraycopy(rfcRecData, i + 7 + rfcRecData[i + 5], rfcRecData, 0, maxReceiverLen - (i + 7 + rfcRecData[i + 5]))
                            if(checkSum(btDevMsg.btCmd)) {
                                isRfcRecCmd = true
                                // Logger.d(KotlinService, "Receiver iMage command")
                                sendMainMsg(btDevMsg)
                                // sendBroadcast(Intent("iMageBroadcastMain").putExtra("btServiceMsg", btDevMsg))
                                isRfcRecHead = false
                                i = 0
                                // Handler().postDelayed({
                                // }, 100)
                                // Thread.sleep(10)
                                continue
                            }
                        }
                        break
                    }
                    else
                        i++
                }
                if(isRfcRecHead == false) {
                    rfcRecDataLen = 0
                }
            }
            isReadable = false
            Logger.d(LogService, "bluetooth ${btConNo} disconnect and read thread free")
        }

        fun isConnect() = rfcSocket.isConnected

        fun connect() {
            if(isReadable == false) {
                Thread(Runnable {
                    var msg = BtDevMsg(btConNo, 1)

                    msg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                    msg.btCmd[1] = CmdId.CMD_HEAD_55.value
                    msg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                    msg.btCmd[3] = CmdId.CMD_DEV_HOST.value
                    msg.btCmd[4] = CmdId.SET_INT_CON_RSP.value
                    msg.btCmd[5] = 0x01
                    msg.btCmd[6] = 0x02
                    sendMainMsg(msg)
                    Logger.d(LogService, "\tconnect remote bluetooth address: ${bdaddr}")
                    rfcSocket = btAdapter.getRemoteDevice(bdaddr).createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
                    for (i in 0 until 3) {
                        try {
                            Logger.d(LogService, "bluetooth connecting ${btConNo}")
                            rfcSocket.connect()
                        } catch (e: IOException) {
                            Logger.d(LogService, "bluetooth connect fail ${btConNo}")
                        }
                        if (rfcSocket.isConnected) {
                            thread = Thread(btReadThread)
                            isReadable = true
                            thread.start()
                            Logger.d(LogService, "bluetooth connected ${btConNo}")
                            break
                        } else
                            Thread.sleep(5000)
                        sendMainMsg(msg)
                    }
                    msg.btCmd[6] =
                        if(isConnect())
                            0x00
                        else
                            0x01
                    sendMainMsg(msg)
                }).start()
            }
        }

        fun rfcCmdSend(msg: BtDevMsg) {
            if(isReadable) {
                checkSum(msg.btCmd)
                rfcSocket.outputStream.write(msg.btCmd)
            }
        }

        fun close() {
            Logger.d(LogService, "bluetooth ${btConNo} socket close")
            rfcSocket.close()
        }
    }

    inner class iMageBtBroadcastService : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Logger.d(LogService, "iMageBtBroadcastService receive")
            when(intent!!.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothDevice.ERROR)
                    val btPrevState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothDevice.ERROR)

                    Logger.d(LogService, "ACTION_STATE_CHANGED $btState $btPrevState")
                }
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    val btDevice: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    var msg = BtDevMsg(0, 1)
                    var strList = btDevice.address.split(':')

                    msg.btDevNo = when(btDevice.address) {
                        btDevArray[0].bdaddr -> 0
                        btDevArray[1].bdaddr -> 1
                        else -> maxBtDdev
                    }
                    msg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                    msg.btCmd[1] = CmdId.CMD_HEAD_55.value
                    msg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                    msg.btCmd[3] = CmdId.CMD_DEV_HOST.value
                    msg.btCmd[4] = CmdId.SET_INT_CON_RSP.value
                    msg.btCmd[5] = 0x07
                    msg.btCmd[6] = 0x00
                    msg.btCmd[7] = parseInt(strList[3], 16).toByte()
                    msg.btCmd[8] = parseInt(strList[4], 16).toByte()
                    msg.btCmd[9] = parseInt(strList[5], 16).toByte()
                    msg.btCmd[10] = parseInt(strList[2], 16).toByte()
                    msg.btCmd[11] = parseInt(strList[0], 16).toByte()
                    msg.btCmd[12] = parseInt(strList[1], 16).toByte()
                    sendMainMsg(msg)
                    Logger.d(LogService, "ACTION_ACL_CONNECTED ${btDevice.name} ${btDevice.address}")
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    val btDevice: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    var msg = BtDevMsg(0, 1)
                    var strList = btDevice.address.split(':')

                    msg.btDevNo =
                        when(btDevice.address) {
                            btDevArray[0].bdaddr -> 0
                            btDevArray[1].bdaddr -> 1
                            else -> maxBtDdev
                        }
                    msg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                    msg.btCmd[1] = CmdId.CMD_HEAD_55.value
                    msg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                    msg.btCmd[3] = CmdId.CMD_DEV_HOST.value
                    msg.btCmd[4] = CmdId.SET_INT_CON_RSP.value
                    msg.btCmd[5] = 0x07
                    msg.btCmd[6] = 0x01
                    msg.btCmd[7] = parseInt(strList[3], 16).toByte()
                    msg.btCmd[8] = parseInt(strList[4], 16).toByte()
                    msg.btCmd[9] = parseInt(strList[5], 16).toByte()
                    msg.btCmd[10] = parseInt(strList[2], 16).toByte()
                    msg.btCmd[11] = parseInt(strList[0], 16).toByte()
                    msg.btCmd[12] = parseInt(strList[1], 16).toByte()
                    sendMainMsg(msg)
                    Logger.d(LogService, "ACTION_ACL_DISCONNECTED ${btDevice.name} ${btDevice.address}")
                }
                BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED -> {
                    val btDevice: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val btState = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothDevice.ERROR)
                    val btPrevState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE, BluetoothDevice.ERROR)

                    Logger.d(LogService, "ACTION_CONNECTION_STATE_CHANGED $btState $btPrevState")
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    val sendMsg = BtDevMsg(0, 1)

                    sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                    sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                    sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                    sendMsg.btCmd[3] = CmdId.CMD_DEV_HOST.value
                    sendMsg.btCmd[4] = CmdId.SET_INT_DISCOVERY_RSP.value
                    sendMsg.btCmd[5] = 0x01.toByte()
                    sendMsg.btCmd[6] = 0x02.toByte()
                    sendMainMsg(sendMsg)
                    Logger.d(LogService, "ACTION_DISCOVERY_STARTED")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    val sendMsg = BtDevMsg(0, 1)

                    sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                    sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                    sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                    sendMsg.btCmd[3] = CmdId.CMD_DEV_HOST.value
                    sendMsg.btCmd[4] = CmdId.SET_INT_DISCOVERY_RSP.value
                    sendMsg.btCmd[5] = 0x01.toByte()
                    sendMsg.btCmd[6] = 0x00.toByte()
                    sendMainMsg(sendMsg)
                    Logger.d(LogService, "ACTION_DISCOVERY_FINISHED")
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val btDevice: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val btClass = intent.getIntExtra(BluetoothDevice.EXTRA_CLASS, BluetoothDevice.ERROR)
                    val btRssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)
                    val sendMsg = BtDevMsg(0, 1)
                    var strList = btDevice.address.split(':')
                    var s: Int

                    sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                    sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                    sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                    sendMsg.btCmd[3] = CmdId.CMD_DEV_HOST.value
                    sendMsg.btCmd[4] = CmdId.SET_INT_DISCOVERY_RSP.value
                    sendMsg.btCmd[6] = 0x01.toByte()
                    sendMsg.btCmd[7] = parseInt(strList[3], 16).toByte()
                    sendMsg.btCmd[8] = parseInt(strList[4], 16).toByte()
                    sendMsg.btCmd[9] = parseInt(strList[5], 16).toByte()
                    sendMsg.btCmd[10] = parseInt(strList[2], 16).toByte()
                    sendMsg.btCmd[11] = parseInt(strList[0], 16).toByte()
                    sendMsg.btCmd[12] = parseInt(strList[1], 16).toByte()
                    Logger.d(LogGbl, "${String.format("bdaddr %02X %02X %02X %02X %02X %02X ", sendMsg.btCmd[11], sendMsg.btCmd[12], sendMsg.btCmd[10], sendMsg.btCmd[7], sendMsg.btCmd[8], sendMsg.btCmd[9])}")
                    if (btDevice.name != null) {
                        sendMsg.btCmd[5] = (btDevice.name.length * 2 + 7).toByte()
                        for (i in 0 until btDevice.name.length) {
                            s = btDevice.name[i].toInt()
                            sendMsg.btCmd[i * 2 + 13] = s.shr(8).toByte()
                            sendMsg.btCmd[i * 2 + 1 + 13] = s.and(0x00ff).toByte()
                        }
                    }
                    else
                        sendMsg.btCmd[5] = 7
                    sendMainMsg(sendMsg)
                    Logger.d(LogService, "ACTION_FOUND ${btDevice.name} ${btDevice.address} ${btClass.toUInt().toString(16)}")
                }
                BluetoothDevice.ACTION_NAME_CHANGED -> {
                    val btDevice: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    var str = btDevice.address.split(":")

                    Logger.d(LogService, "ACTION_NAME_CHANGED")
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val btDevice: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val btState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)
                    val btPrevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR)

                    Logger.d(LogService, "ACTION_BOND_STATE_CHANGED ${btState.toUInt().toString(16)} ${btPrevState.toUInt().toString(16)}")
                }
                BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {
                    val btMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothDevice.ERROR)
                    val btPrevMode = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, BluetoothDevice.ERROR)

                    Logger.d(LogService, "ACTION_SCAN_MODE_CHANGED $btMode $btPrevMode")
                }
                BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED -> {
                    val btName = intent.getStringExtra(BluetoothAdapter.EXTRA_LOCAL_NAME)

                    Logger.d(LogService, "ACTION_LOCAL_NAME_CHANGED $btName")
                }
                else -> {
                    Logger.d(LogService, "broadcast receiver other message")
                }
            }
        }
    }

    fun initBtDev() {
        var intentFilter = IntentFilter()

        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED)
        registerReceiver(iMageBtBroadcastService(), intentFilter)
        btDevArray = Array<iMageBtDev>(maxBtDdev) { iMageBtDev("C4:FF:BC:4F:FE:00") }
        if(btSerivceBind == false) {
            for (i in 0 until maxBtDdev) {
                //serviceBtDevice[i].btDeviceNo = i
                btDevArray[i].btConNo = i
            }
        }
    }

    fun sendMainMsg(msg: BtDevMsg) {
        val sendMsg = Message.obtain(null, 1, msg)
        checkSum(msg.btCmd)
        clientMsg.send(sendMsg)
        // sendBroadcast(Intent("iMageBroadcastMain").putExtra("btServiceMsg", msg))
    }

    fun checkSum(cmdBuf: ByteArray): Boolean {
        var chksum = 0
        for(i in 2 until cmdBuf[5].toUByte().toInt() + 6)
            chksum += cmdBuf[i].toInt().and(0xff)
        chksum = chksum.inv().and(0xff)
        if(chksum == (cmdBuf[cmdBuf[5].toUByte().toInt() + 6]).toInt().and(0xff))
            return true
        else {
            cmdBuf[cmdBuf[5].toUByte().toInt() + 6] = chksum.toByte()
            return false
        }
    }

    fun rfcCmdParse(msg: BtDevMsg) {
        when(msg.btCmd[4]) {
            CmdId.SET_INT_SERVICE_REQ.value -> {
                if(msg.btCmd[5] == 7.toByte()) {
                    var bda = String.format("%02X:%02X:%02X:%02X:%02X:%02X", msg.btCmd[11], msg.btCmd[12], msg.btCmd[10], msg.btCmd[7], msg.btCmd[8], msg.btCmd[9])

                    btDevArray[msg.btDevNo].bdaddr = bda
                    Logger.d(LogService,"bluetooth address changed ==> device: ${msg.btDevNo} address: ${btDevArray[msg.btDevNo].bdaddr}")
                }
            }
            CmdId.SET_INT_CON_REQ.value -> {
                if(msg.btCmd[5] == 7.toByte()) {
                    var bda = String.format("%02X:%02X:%02X:%02X:%02X:%02X", msg.btCmd[11], msg.btCmd[12], msg.btCmd[10], msg.btCmd[7], msg.btCmd[8], msg.btCmd[9])

                    btDevArray[msg.btDevNo].bdaddr = bda
                    when(msg.btCmd[6]) {
                        0.toByte() -> btDevArray[msg.btDevNo].close()
                        1.toByte() -> {
                            if(btDevArray[msg.btDevNo].isReadable == false)
                                btDevArray[msg.btDevNo].connect()
                        }
                        else -> {
                            Logger.d(LogService, "other connect command")
                        }
                    }
                    Logger.d(LogService, "bluetooth control ==> device: ${msg.btDevNo} address: ${btDevArray[msg.btDevNo].bdaddr}")
                }
            }
            CmdId.SET_INT_DISCOVERY_REQ.value -> {

                when(msg.btCmd[6]) {
                    0x00.toByte() -> btAdapter.cancelDiscovery()
                    0x01.toByte() -> btAdapter.startDiscovery()
                    else -> btAdapter.cancelDiscovery()
                }
                Logger.d(LogService,  "bluetooth DISCOVERY")
            }
            CmdId.SET_INT_PAIR_REQ.value -> {
                val paired = btAdapter.bondedDevices
                var strList: List<String>
                var s: Int

                if(paired.size > 0) {
                    for (device in paired) {
                        val sendMsg = BtDevMsg(0, 1)

                        sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                        sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                        sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                        sendMsg.btCmd[3] = CmdId.CMD_DEV_HOST.value
                        sendMsg.btCmd[4] = CmdId.SET_INT_PAIR_RSP.value
                        sendMsg.btCmd[6] = 0x01.toByte()
                        strList = device.address.split(':')
                        sendMsg.btCmd[7] = parseInt(strList[3], 16).toByte()
                        sendMsg.btCmd[8] = parseInt(strList[4], 16).toByte()
                        sendMsg.btCmd[9] = parseInt(strList[5], 16).toByte()
                        sendMsg.btCmd[10] = parseInt(strList[2], 16).toByte()
                        sendMsg.btCmd[11] = parseInt(strList[0], 16).toByte()
                        sendMsg.btCmd[12] = parseInt(strList[1], 16).toByte()
                        Logger.d(LogGbl, "${String.format("bdaddr %02X %02X %02X %02X %02X %02X ", sendMsg.btCmd[11], sendMsg.btCmd[12], sendMsg.btCmd[10], sendMsg.btCmd[7], sendMsg.btCmd[8], sendMsg.btCmd[9])}")
                        if (device.name != null) {
                            sendMsg.btCmd[5] = (device.name.length * 2 + 7).toByte()
                            for (i in 0 until device.name.length) {
                                s = device.name[i].toInt()
                                sendMsg.btCmd[i * 2 + 13] = s.shr(8).toByte()
                                sendMsg.btCmd[i * 2 + 13 + 1] = s.and(0x00ff).toByte()
                            }
                        }
                        else
                            sendMsg.btCmd[5] = 7
                        sendMainMsg(sendMsg)
                        Logger.d(LogService, "PAIRED_FOUND ${device.name} ${device.address}")
                    }
                }
            }
            else ->{
                btDevArray[msg.btDevNo].rfcCmdSend(msg)
                Logger.d(LogService, " other command data: ${msg.btCmd[2].toUByte().toString(16)} ${msg.btCmd[3].toUByte().toString(16)} ${msg.btCmd[4].toUByte().toString(16)} ${msg.btCmd[5].toUByte().toString(16)}")
            }
        }
    }
}
