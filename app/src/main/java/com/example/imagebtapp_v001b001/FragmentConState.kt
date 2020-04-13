package com.example.imagebtapp_v001b001

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_con_state.*
import java.text.FieldPosition
import kotlin.experimental.and
import kotlin.experimental.or

class FragmentConState : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d(LogGbl, "FragmentConState on Create")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Logger.d(LogGbl, "FragmentConState on Create View")
        return inflater.inflate(R.layout.fragment_con_state, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var strIndMsg = Array(5) {"OK"}
        val devUnitAdapter = BtDevUnitAdapter((activity as DevUnitMsg).getBtDevUnitList(), strIndMsg)
        val lmg = GridLayoutManager(context, 2)

        Logger.d(LogGbl, "FragmentConState on Activity created")
        lmg.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if(position == 0)
                    return 2
                else
                    return 1
            }
        }
        recyclerDevList.layoutManager = lmg
        recyclerDevList.adapter = devUnitAdapter
        strIndMsg[0] = context!!.resources.getString(R.string.txvStaRssi)
        strIndMsg[1] = context!!.resources.getString(R.string.txvStaBat)
        strIndMsg[2] = context!!.resources.getString(R.string.txvStaBatLow)
        strIndMsg[3] = context!!.resources.getString(R.string.txvStaBatChg)
        strIndMsg[4] = context!!.resources.getString(R.string.txvStaBatPwr)

        devUnitAdapter.setOnItemClickListener(object : BtDevUnitAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, btDevUnit: BtDevUnit) {
                val msgItem = arrayOf(btDevUnit.nameAlias, btDevUnit.verFirmwareHfp, btDevUnit.localNameHfp, btDevUnit.verFirmwareAg, btDevUnit.localNameAg, btDevUnit.bdaddr, btDevUnit.bdaddrPair)

                AlertDialog.Builder(activity).setTitle("Device message").setItems(msgItem) {
                    dialog, which ->
                }.setPositiveButton("OK") {
                    dialog, which ->
                }.show()
                Logger.d(LogGbl, "btdevunit on item click")
                // Toast.makeText(activity, "${btDevUnit.verFirmwareAg}\n${btDevUnit.localNameHfp}", Toast.LENGTH_LONG).show()
            }
        })
        devUnitAdapter.setOnSpkrVolListener(object : BtDevUnitAdapter.OnSpkrVolListener {
            override fun onSpkrVol(position: Int, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    val sendMsg = BtDevMsg(0, 0)

                    sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                    sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                    sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                    sendMsg.btCmd[3] = getDevId(position)
                    sendMsg.btCmd[4] = CmdId.SET_HFP_VOL_REQ.value
                    sendMsg.btCmd[5] = 0x01
                    sendMsg.btCmd[6] = progress.toByte().or(0x20.toByte())
                    (activity as DevUnitMsg).sendBtServiceMsg(sendMsg)
                }
                Logger.d(LogGbl, "spkr volume id:$position progress:$progress")
            }
        })
        devUnitAdapter.setOnSpkrMuteListener(object : BtDevUnitAdapter.OnSpkrMuteListener {
            override fun onSpkrMute(position: Int, mute: Boolean) {
                val sendMsg = BtDevMsg(0, 0)

                sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                sendMsg.btCmd[3] = getDevId(position)
                sendMsg.btCmd[4] = CmdId.SET_HFP_VOL_REQ.value
                sendMsg.btCmd[5] = 0x01
                sendMsg.btCmd[6] =
                    if(mute)
                        (activity as DevUnitMsg).getBtDevUnitList()[position].volSpkrHfp.toByte().and(0x0f.toByte())or(0x00.toByte()).or(0x20.toByte())
                    else
                        (activity as DevUnitMsg).getBtDevUnitList()[position].volSpkrHfp.toByte().and(0x0f.toByte())or(0x80.toByte()).or(0x20.toByte())
                (activity as DevUnitMsg).sendBtServiceMsg(sendMsg)
                Logger.d(LogGbl, "spkr mute id:$position $mute")
            }
        })
        devUnitAdapter.setOnLongSpkrMuteListener(object : BtDevUnitAdapter.OnLongSpkrMuteListener {
            override fun onLongSpkrMute(position: Int, mute: Boolean) {
                val sendMsg = BtDevMsg(0, 0)

                if (position == 0) {
                    sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                    sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                    sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                    sendMsg.btCmd[3] = getDevId(position)
                    sendMsg.btCmd[4] = CmdId.SET_HFP_VOL_REQ.value
                    sendMsg.btCmd[5] = 0x01
                    sendMsg.btCmd[6] =
                        if(mute)
                            (activity as DevUnitMsg).getBtDevUnitList()[position].volSpkrHfp.toByte().and(0x0f.toByte())or(0x00.toByte()).or(0x20.toByte())
                        else
                            (activity as DevUnitMsg).getBtDevUnitList()[position].volSpkrHfp.toByte().and(0x0f.toByte())or(0x80.toByte()).or(0x20.toByte())
                    (activity as DevUnitMsg).sendBtServiceMsg(sendMsg)
                    Logger.d(LogGbl, "spkr mute id:$position $mute")
                } else {
                    sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                    sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                    sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                    sendMsg.btCmd[3] = CmdId.CMD_DEV_AG_ALL.value
                    sendMsg.btCmd[4] = CmdId.SET_HFP_VOL_REQ.value
                    sendMsg.btCmd[5] = 0x01
                    sendMsg.btCmd[6] =
                        if (mute)
                            (activity as DevUnitMsg).getBtDevUnitList()[position].volSpkrHfp.toByte().and(0x0f.toByte()) or (0x00.toByte()).or(0x20.toByte())
                        else
                            (activity as DevUnitMsg).getBtDevUnitList()[position].volSpkrHfp.toByte().and(0x0f.toByte()) or (0x80.toByte()).or(0x20.toByte())
                    (activity as DevUnitMsg).sendBtServiceMsg(sendMsg)
                    Logger.d(LogGbl, "spkr mute id:$position $mute")
                }
            }
        })
        devUnitAdapter.setOnMicMuteListener(object : BtDevUnitAdapter.OnMicMuteListener{
            override fun onMicMute(position: Int, mute: Boolean) {
                val sendMsg = BtDevMsg(0, 0)

                sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                sendMsg.btCmd[3] = getDevId(position)
                sendMsg.btCmd[4] = CmdId.SET_HFP_VOL_REQ.value
                sendMsg.btCmd[5] = 0x01
                    if(mute)
                        sendMsg.btCmd[6] = 0x00.toByte().or(0x10.toByte())
                    else
                        sendMsg.btCmd[6] =0x40.toByte().or(0x10.toByte())
                (activity as DevUnitMsg).sendBtServiceMsg(sendMsg)
                Logger.d(LogGbl, "mic mute id:$position $mute")
            }
        })
        devUnitAdapter.setOnLongMicMuteListener(object : BtDevUnitAdapter.OnLongMicMuteListener {
            override fun onLongMicMute(position: Int, mute: Boolean) {
                val sendMsg = BtDevMsg(0, 0)

                if (position == 0) {
                    sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                    sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                    sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                    sendMsg.btCmd[3] = getDevId(position)
                    sendMsg.btCmd[4] = CmdId.SET_HFP_VOL_REQ.value
                    sendMsg.btCmd[5] = 0x01
                    sendMsg.btCmd[6] =
                        if(mute)
                            0x00.toByte().or(0x10.toByte())
                        else
                            0x40.toByte().or(0x10.toByte())
                    (activity as DevUnitMsg).sendBtServiceMsg(sendMsg)
                    Logger.d(LogGbl, "mic mute id:$position $mute")

                } else {
                    sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                    sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                    sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                    sendMsg.btCmd[3] = CmdId.CMD_DEV_AG_ALL.value
                    sendMsg.btCmd[4] = CmdId.SET_HFP_VOL_REQ.value
                    sendMsg.btCmd[5] = 0x01
                    sendMsg.btCmd[6] =
                        if (mute)
                            0x00.toByte().or(0x10.toByte())
                        else
                            0x40.toByte().or(0x10.toByte())
                    (activity as DevUnitMsg).sendBtServiceMsg(sendMsg)
                    Logger.d(LogGbl, "mic mute id:$position $mute")
                }
            }
        })
        devUnitAdapter.setOnTalkListener(object : BtDevUnitAdapter.OnTalkListener {
            override fun onTalk(position: Int) {
                val sendMsg = BtDevMsg(0, 0)

                sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                sendMsg.btCmd[3] = CmdId.CMD_DEV_SRC.value
                sendMsg.btCmd[4] = CmdId.SET_HFP_EXT_STA_REQ.value
                sendMsg.btCmd[5] = 0x02
                sendMsg.btCmd[6] = 0x00
                sendMsg.btCmd[7] = 0x81.toByte()
                (activity as DevUnitMsg).sendBtServiceMsg(sendMsg)
                Logger.d(LogGbl, "talk click id:$position")
            }
        })
        devUnitAdapter.setOnLongTalkListener(object : BtDevUnitAdapter.OnLongTalkListener {
            override fun onLongTalk(position: Int) {
                val sendMsg = BtDevMsg(0, 0)

                sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                sendMsg.btCmd[3] = CmdId.CMD_DEV_SRC.value
                sendMsg.btCmd[4] = CmdId.SET_HFP_EXT_STA_REQ.value
                sendMsg.btCmd[5] = 0x02
                sendMsg.btCmd[6] = 0x00
                sendMsg.btCmd[7] = 0x80.toByte()
                (activity as DevUnitMsg).sendBtServiceMsg(sendMsg)
                Logger.d(LogGbl, "talk long click id:$position")
            }
        })
    }

    fun getDevId(position: Int): Byte =
        when(position) {
            0 -> 0x30.toByte()
            1 -> 0x00.toByte()
            2 -> 0x08.toByte()
            3 -> 0x10.toByte()
            4 -> 0x18.toByte()
            5 -> 0x20.toByte()
            6 -> 0x28.toByte()
            else -> 0xff.toByte()
        }

    fun updataData() {
        recyclerDevList.adapter!!.notifyDataSetChanged()
    }
}
