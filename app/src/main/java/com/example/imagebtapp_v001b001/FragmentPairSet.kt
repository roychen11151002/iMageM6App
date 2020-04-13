package com.example.imagebtapp_v001b001

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_con_state.*
import kotlinx.android.synthetic.main.fragment_pair_set.*
import java.lang.Integer.parseInt

class FragmentPairSet : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pair_set, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val recyclerListAdapter = BtListAdapter((activity as DevUnitMsg).getBtList())

        recyclerListPair.layoutManager = LinearLayoutManager(context)
        recyclerListPair.adapter = recyclerListAdapter

        btnPair.setOnClickListener{
            val sendMsg = BtDevMsg(0, 1)

            (activity as DevUnitMsg).getBtList().removeAll((activity as DevUnitMsg).getBtList())
            recyclerListPair.layoutManager!!.scrollToPosition(0)
            sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
            sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
            sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
            sendMsg.btCmd[3] = CmdId.CMD_DEV_HOST.value
            sendMsg.btCmd[4] = CmdId.SET_INT_PAIR_REQ.value
            sendMsg.btCmd[5] = 0x01
            sendMsg.btCmd[6] = 0x01
            (activity as DevUnitMsg).sendBtServiceMsg(sendMsg)
        }

        btnDiscovery.setOnClickListener {
            val sendMsg = BtDevMsg(0, 1)

            (activity as DevUnitMsg).getBtList().removeAll((activity as DevUnitMsg).getBtList())
            recyclerListPair.layoutManager!!.scrollToPosition(0)
            sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
            sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
            sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
            sendMsg.btCmd[3] = CmdId.CMD_DEV_HOST.value
            sendMsg.btCmd[4] = CmdId.SET_INT_DISCOVERY_REQ.value
            sendMsg.btCmd[5] = 0x01
            sendMsg.btCmd[6] = 0x01
            (activity as DevUnitMsg).sendBtServiceMsg(sendMsg)
        }

        recyclerListAdapter.setOnItemClickListener(object : BtListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val listItem = arrayOf("source", "aghfp0", "aghfp1", "aghfp2", "aghfp3", "aghfp4", "aghfp5")
                var sendMsg = BtDevMsg(0, 1)
                var str = (activity as DevUnitMsg).getBtList()[position].removeRange(0, (activity as DevUnitMsg).getBtList()[position].lastIndexOf(" + ") + 3)
                var strList = str.split(':')
                var preferDataEdit = (activity as DevUnitMsg).getpreferData().edit()

                AlertDialog.Builder(activity).setTitle("Pair device").setItems(listItem) {
                    dialog, which ->
                    sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                    sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                    sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                    sendMsg.btCmd[5] = 0x07
                    if(which == 0) {
                        preferDataEdit.putString("bdaddr0", str)
                        preferDataEdit.apply()

                        sendMsg.btCmd[3] = CmdId.CMD_DEV_HOST.value
                        sendMsg.btCmd[4] = CmdId.SET_INT_CON_REQ.value
                        sendMsg.btCmd[6] = 0x01
                    }
                    else {
                        sendMsg.btCmd[3] = getBtCmdId(which)
                        sendMsg.btCmd[4] = CmdId.SET_HFP_PAIR_REQ.value
                        sendMsg.btCmd[6] = 0x00
                    }
                    sendMsg.btCmd[7] = parseInt(strList[3], 16).toByte()
                    sendMsg.btCmd[8] = parseInt(strList[4], 16).toByte()
                    sendMsg.btCmd[9] = parseInt(strList[5], 16).toByte()
                    sendMsg.btCmd[10] = parseInt(strList[2], 16).toByte()
                    sendMsg.btCmd[11] = parseInt(strList[0], 16).toByte()
                    sendMsg.btCmd[12] = parseInt(strList[1], 16).toByte()
                    Logger.d(LogGbl, "${String.format("bdaddr %02X %02X %02X %02X %02X %02X ", sendMsg.btCmd[11], sendMsg.btCmd[12], sendMsg.btCmd[10], sendMsg.btCmd[7], sendMsg.btCmd[8], sendMsg.btCmd[9])}")
                    (activity as DevUnitMsg).sendBtServiceMsg(sendMsg)
                }.setNegativeButton("Cancel") {
                    dialog, which ->
                }.show()
            }
        })

        recyclerListAdapter.setOnItemLongClickListener(object : BtListAdapter.OnItemLongClickListener {
            override fun onItemLongClick(view: View, position: Int): Boolean {
                var sendMsg = BtDevMsg(1, 1)
                var str = (activity as DevUnitMsg).getBtList()[position].removeRange(0, (activity as DevUnitMsg).getBtList()[position].lastIndexOf(" + ") + 3)
                var strList = str.split(':')
                var preferDataEdit = (activity as DevUnitMsg).getpreferData().edit()

                preferDataEdit.putString("bdaddr1", str)
                preferDataEdit.apply()
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
                (activity as DevUnitMsg).sendBtServiceMsg(sendMsg)
                return true
            }
        })
        updataData()
    }

    fun updataData() {
        recyclerListPair.adapter!!.notifyDataSetChanged()
        txvPairTitle.text =
            when((activity as DevUnitMsg).getPairState()) {
                0 -> {
                    btnPair.isEnabled = true
                    context!!.resources.getString(R.string.txvStaDiscoveryEnd)
                }
                1 -> {
                    btnPair.isEnabled = false
                    context!!.resources.getString(R.string.txvStaDiscovey)
                }
                2 -> {
                    btnPair.isEnabled = false
                    context!!.resources.getString(R.string.txvStaDiscoveryStart)
                }
                3 -> {
                    btnPair.isEnabled = true
                    context!!.resources.getString(R.string.txvStaPaired)
                }
                else -> {
                    btnPair.isEnabled = true
                    context!!.resources.getString(R.string.txvPairTitle)

                }
        }
    }

    fun getBtCmdId(dev: Int): Byte =
        when(dev) {
            0 -> 0x30
            1 -> 0x00
            2 -> 0x08
            3 -> 0x10
            4 -> 0x18
            5 -> 0x20
            6 -> 0x28
            else -> 0x00
        }
}
