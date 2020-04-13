package com.example.imagebtapp_v001b001

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_vol_set.*

class FragmentVolSet : Fragment() {
    var srcDevItme = 0
    var srcDevId: Byte = 0x30
    var mode = 8
    var cmdSetVolId: Byte = CmdId.SET_HFP_PSKEY_REQ.value
    var cmdGetVolId: Byte = CmdId.GET_HFP_PSKEY_REQ.value

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vol_set, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnVolRead.setOnClickListener {
            for (i in 0 until 2) {
                val sendMsg = BtDevMsg(0, 0)

                sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
                sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
                sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
                sendMsg.btCmd[3] = srcDevId
                sendMsg.btCmd[4] = cmdGetVolId
                sendMsg.btCmd[5] = 0x02
                sendMsg.btCmd[6] = 0x00
                sendMsg.btCmd[7] = (17 + i).toByte()
                (activity as DevUnitMsg).sendBtServiceMsg(sendMsg)
            }
        }
        btnVolWrite.setOnClickListener {
            val sendMsg = BtDevMsg(0, 0)
            val sendMsgAg = BtDevMsg(0, 0)

            sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
            sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
            sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
            sendMsg.btCmd[3] = srcDevId
            sendMsg.btCmd[4] = cmdSetVolId
            sendMsg.btCmd[5] = 0x10
            sendMsg.btCmd[6] = 0x00
            sendMsg.btCmd[7] = 17
            sendMsg.btCmd[8] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcWireHfpMicVol.toByte()
            sendMsg.btCmd[9] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcWireHfpSpkrVol.toByte()
            sendMsg.btCmd[10] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcUsbHfpMicVol.toByte()
            sendMsg.btCmd[11] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcUsbHfpSpkrVol.toByte()
            sendMsg.btCmd[12] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcBtHfpMicVol.toByte()
            sendMsg.btCmd[13] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcBtHfpSpkrVol.toByte()
            sendMsg.btCmd[14] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcVcsHfpMicVol.toByte()
            sendMsg.btCmd[15] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcVcsHfpSpkrVol.toByte()
            sendMsg.btCmd[16] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcWireAvSpkrVol.toByte()
            sendMsg.btCmd[17] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcUsbAvSpkrVol.toByte()
            sendMsg.btCmd[18] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcBtAvSpkrVol.toByte()
            sendMsg.btCmd[19] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcVcsAvSpkrVol.toByte()
            sendMsg.btCmd[20] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.toByte()
            sendMsg.btCmd[21] = 0x00
            (activity as DevUnitMsg).sendBtServiceMsg(sendMsg)

            sendMsgAg.btCmd[0] = CmdId.CMD_HEAD_FF.value
            sendMsgAg.btCmd[1] = CmdId.CMD_HEAD_55.value
            sendMsgAg.btCmd[2] = CmdId.CMD_DEV_HOST.value
            sendMsgAg.btCmd[3] = srcDevId
            sendMsgAg.btCmd[4] = cmdSetVolId
            sendMsgAg.btCmd[5] = 0x10
            sendMsgAg.btCmd[6] = 0x00
            sendMsgAg.btCmd[7] = 18
            sendMsgAg.btCmd[8] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgWireHfpMicVol.toByte()
            sendMsgAg.btCmd[9] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgWireHfpSpkrVol.toByte()
            sendMsgAg.btCmd[10] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgUsbHfpMicVol.toByte()
            sendMsgAg.btCmd[11] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgUsbHfpSpkrVol.toByte()
            sendMsgAg.btCmd[12] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgBtHfpMicVol.toByte()
            sendMsgAg.btCmd[13] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgBtHfpSpkrVol.toByte()
            sendMsgAg.btCmd[14] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgVcsHfpMicVol.toByte()
            sendMsgAg.btCmd[15] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgVcsHfpSpkrVol.toByte()
            sendMsgAg.btCmd[16] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgWireAvSpkrVol.toByte()
            sendMsgAg.btCmd[17] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgUsbAvSpkrVol.toByte()
            sendMsgAg.btCmd[18] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgBtAvSpkrVol.toByte()
            sendMsgAg.btCmd[19] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgVcsAvSpkrVol.toByte()
            sendMsgAg.btCmd[20] = 0x00
            sendMsgAg.btCmd[21] = 0x00
            (activity as DevUnitMsg).sendBtServiceMsg(sendMsgAg)
        }
        rdGpDev.setOnCheckedChangeListener { group, checkedId ->
            srcDevId =
                when (checkedId) {
                    R.id.rdSrc -> {
                        srcDevItme = 0
                        0x30
                    }
                    R.id.rdHfpAll -> {
                        srcDevItme = 1
                        0x38
                    }
                    else -> {
                        srcDevItme = 0
                        0x30
                    }
                }
            updataData()
        }
        rdGpMode.setOnCheckedChangeListener { group, checkedId ->
            mode =
                when (checkedId) {
                    R.id.rdModeWire -> 1
                    R.id.rdModeUsb -> 2
                    R.id.rdModeBt -> 4
                    R.id.rdModeVcs -> 8
                    else -> 8
                }
            updataData()
        }
        seekSrcHfpMicVol.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                when (mode) {
                    1 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcWireHfpMicVol = seekSrcHfpMicVol.progress
                    2 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcUsbHfpMicVol = seekSrcHfpMicVol.progress
                    4 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcBtHfpMicVol = seekSrcHfpMicVol.progress
                    8 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcVcsHfpMicVol = seekSrcHfpMicVol.progress
                    else -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcWireHfpMicVol = seekSrcHfpMicVol.progress
                }
            }
        })
        seekSrcHfpSpkrVol.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                when (mode) {
                    1 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcWireHfpSpkrVol = seekSrcHfpSpkrVol.progress
                    2 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcUsbHfpSpkrVol = seekSrcHfpSpkrVol.progress
                    4 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcBtHfpSpkrVol = seekSrcHfpSpkrVol.progress
                    8 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcVcsHfpSpkrVol = seekSrcHfpSpkrVol.progress
                    else -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcWireHfpSpkrVol = seekSrcHfpSpkrVol.progress
                }
            }
        })
        seekSrcAvSpkrVol.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                when (mode) {
                    1 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcWireAvSpkrVol = seekSrcAvSpkrVol.progress
                    2 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcUsbAvSpkrVol = seekSrcAvSpkrVol.progress
                    4 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcBtAvSpkrVol = seekSrcAvSpkrVol.progress
                    8 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcVcsAvSpkrVol = seekSrcAvSpkrVol.progress
                    else -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcWireAvSpkrVol = seekSrcAvSpkrVol.progress
                }
            }
        })
        seekAgHfpMicVol.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                when (mode) {
                    1 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgWireHfpMicVol = seekAgHfpMicVol.progress
                    2 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgUsbHfpMicVol = seekAgHfpMicVol.progress
                    4 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgBtHfpMicVol = seekAgHfpMicVol.progress
                    8 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgVcsHfpMicVol = seekAgHfpMicVol.progress
                    else -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgWireHfpMicVol = seekAgHfpMicVol.progress
                }
            }
        })
        seekAgHfpSpkrVol.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                when (mode) {
                    1 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgWireHfpSpkrVol = seekAgHfpSpkrVol.progress
                    2 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgUsbHfpSpkrVol = seekAgHfpSpkrVol.progress
                    4 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgBtHfpSpkrVol = seekAgHfpSpkrVol.progress
                    8 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgVcsHfpSpkrVol = seekAgHfpSpkrVol.progress
                    else -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgWireHfpSpkrVol = seekAgHfpSpkrVol.progress
                }
            }
        })
        seekAgAvSpkrVol.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                when (mode) {
                    1 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgWireAvSpkrVol = seekAgAvSpkrVol.progress
                    2 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgUsbAvSpkrVol = seekAgAvSpkrVol.progress
                    4 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgBtAvSpkrVol = seekAgAvSpkrVol.progress
                    8 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgVcsAvSpkrVol = seekAgAvSpkrVol.progress
                    else -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgWireAvSpkrVol = seekAgAvSpkrVol.progress
                }
            }
        })
        chkSrcHfpSpkrDecade.setOnCheckedChangeListener { buttonView, isChecked ->
            when (mode) {
                1 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade =
                    if(isChecked)
                        (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.or(0x01)
                    else
                        (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x01.inv())
                2 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade =
                    if(isChecked)
                        (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.or(0x02)
                    else
                        (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x02.inv())
                4 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade =
                    if(isChecked)
                        (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.or(0x04)
                    else
                        (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x04.inv())
                8 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade =
                    if(isChecked)
                        (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.or(0x08)
                    else
                        (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x08.inv())
            }
        }
        chkSrcAvSpkrDecade.setOnCheckedChangeListener { buttonView, isChecked ->
            when (mode) {
                1 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade =
                    if(isChecked)
                        (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.or(0x10)
                    else
                        (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x10.inv())
                2 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade =
                    if(isChecked)
                        (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.or(0x20)
                    else
                        (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x20.inv())
                4 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade =
                    if(isChecked)
                        (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.or(0x40)
                    else
                        (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x40.inv())
                8 -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade =
                    if(isChecked)
                        (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.or(0x80)
                    else
                        (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x80.inv())
            }
        }
        updataData()
    }

    fun updataData() {
        when(mode) {
            1 -> {
                seekSrcAvSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcWireAvSpkrVol
                seekSrcHfpSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcWireHfpSpkrVol
                seekSrcHfpMicVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcWireHfpMicVol
                seekAgAvSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgWireAvSpkrVol
                seekAgHfpSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgWireHfpSpkrVol
                seekAgHfpMicVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgWireHfpMicVol
                chkSrcHfpSpkrDecade.isChecked =
                    if((activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x01) == 0x01)
                        true
                    else
                        false
                chkSrcAvSpkrDecade.isChecked =
                    if((activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x10) == 0x10)
                        true
                    else
                        false
            }
            2 -> {
                seekSrcAvSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcUsbAvSpkrVol
                seekSrcHfpSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcUsbHfpSpkrVol
                seekSrcHfpMicVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcUsbHfpMicVol
                seekAgAvSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgUsbAvSpkrVol
                seekAgHfpSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgUsbHfpSpkrVol
                seekAgHfpMicVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgUsbHfpMicVol
                chkSrcHfpSpkrDecade.isChecked =
                    if((activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x02) == 0x02)
                        true
                    else
                        false
                chkSrcAvSpkrDecade.isChecked =
                    if((activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x20) == 0x20)
                        true
                    else
                        false
            }
            4 -> {
                seekSrcAvSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcBtAvSpkrVol
                seekSrcHfpSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcBtHfpSpkrVol
                seekSrcHfpMicVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcBtHfpMicVol
                seekAgAvSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgBtAvSpkrVol
                seekAgHfpSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgBtHfpSpkrVol
                seekAgHfpMicVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgBtHfpMicVol
                chkSrcHfpSpkrDecade.isChecked =
                    if((activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x04) == 0x04)
                        true
                    else
                        false
                chkSrcAvSpkrDecade.isChecked =
                    if((activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x40) == 0x40)
                        true
                    else
                        false
            }
            8 -> {
                seekSrcAvSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcVcsAvSpkrVol
                seekSrcHfpSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcVcsHfpSpkrVol
                seekSrcHfpMicVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcVcsHfpMicVol
                seekAgAvSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgVcsAvSpkrVol
                seekAgHfpSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgVcsHfpSpkrVol
                seekAgHfpMicVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgVcsHfpMicVol
                chkSrcHfpSpkrDecade.isChecked =
                    if((activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x08) == 0x08)
                        true
                    else
                        false
                chkSrcAvSpkrDecade.isChecked =
                    if((activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x80) == 0x80)
                        true
                    else
                        false
            }
            else -> {
                seekSrcAvSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcWireAvSpkrVol
                seekSrcHfpSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcWireHfpSpkrVol
                seekSrcHfpMicVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcWireHfpMicVol
                seekAgAvSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgWireAvSpkrVol
                seekAgHfpSpkrVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgWireHfpSpkrVol
                seekAgHfpMicVol.progress = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeAgWireHfpMicVol
                chkSrcHfpSpkrDecade.isChecked =
                    if((activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x01) == 0x01)
                        true
                    else
                        false
                chkSrcAvSpkrDecade.isChecked =
                    if((activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].modeSrcSpkrDecade.and(0x10) == 0x10)
                        true
                    else
                        false
            }
        }
    }
}
