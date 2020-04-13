package com.example.imagebtapp_v001b001

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_feature_set.*

class FragmentFeatureSet : Fragment() {
    var srcDevItme = 0
    var srcDevId: Byte = 0x30
    var cmdSetFeatureId: Byte = CmdId.SET_HFP_FEATURE_REQ.value
    var cmdGetFeatureId: Byte = CmdId.GET_HFP_FEATURE_REQ.value

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feature_set, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnFeatureRead.setOnClickListener {
            val sendMsg = BtDevMsg(0, 0)
            val sendMsgMode = BtDevMsg(0, 0)

            sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
            sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
            sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
            sendMsg.btCmd[3] = srcDevId
            sendMsg.btCmd[4] = cmdGetFeatureId
            sendMsg.btCmd[5] = 0x00
            (activity as DevUnitMsg).sendBtServiceMsg(sendMsg)

            sendMsgMode.btCmd[0] = CmdId.CMD_HEAD_FF.value
            sendMsgMode.btCmd[1] = CmdId.CMD_HEAD_55.value
            sendMsgMode.btCmd[2] = CmdId.CMD_DEV_HOST.value
            sendMsgMode.btCmd[3] = srcDevId
            sendMsgMode.btCmd[4] = CmdId.GET_HFP_PSKEY_REQ.value
            sendMsgMode.btCmd[5] = 0x02
            sendMsgMode.btCmd[6] = 0x00
            sendMsgMode.btCmd[7] = 9
            (activity as DevUnitMsg).sendBtServiceMsg(sendMsgMode)
        }
        btnFeatureWrite.setOnClickListener {
            val sendMsgMode = BtDevMsg(0, 0)
            val sendMsg = BtDevMsg(0, 0)
            val strList = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].bdaddrFilterHfp.split(':')

            sendMsgMode.btCmd[0] = CmdId.CMD_HEAD_FF.value
            sendMsgMode.btCmd[1] = CmdId.CMD_HEAD_55.value
            sendMsgMode.btCmd[2] = CmdId.CMD_DEV_HOST.value
            sendMsgMode.btCmd[3] = srcDevId
            sendMsgMode.btCmd[4] = CmdId.SET_HFP_PSKEY_REQ.value
            sendMsgMode.btCmd[5] = 0x6
            sendMsgMode.btCmd[6] = 0x00
            sendMsgMode.btCmd[7] = 9
            sendMsgMode.btCmd[8] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureMode.shr(24).toByte()
            sendMsgMode.btCmd[9] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureMode.shr(16).toByte()
            sendMsgMode.btCmd[10] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureMode.shr(8).toByte()
            sendMsgMode.btCmd[11] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureMode.shr(0).toByte()
            (activity as DevUnitMsg).sendBtServiceMsg(sendMsgMode)

            sendMsg.btCmd[0] = CmdId.CMD_HEAD_FF.value
            sendMsg.btCmd[1] = CmdId.CMD_HEAD_55.value
            sendMsg.btCmd[2] = CmdId.CMD_DEV_HOST.value
            sendMsg.btCmd[3] = srcDevId
            sendMsg.btCmd[4] = cmdSetFeatureId
            sendMsg.btCmd[5] = 0x13
            sendMsg.btCmd[6] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureHfp.shr(8).toByte()
            sendMsg.btCmd[7] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureHfp.and(0xff).toByte()
            sendMsg.btCmd[8] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].maxAgNo.and(0xff).toByte()
            sendMsg.btCmd[9] = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].maxTalkNo.and(0xff).toByte()
            sendMsg.btCmd[10] = 0x00
            sendMsg.btCmd[11] = Integer.parseInt(strList[3], 16).toByte()
            sendMsg.btCmd[12] = Integer.parseInt(strList[4], 16).toByte()
            sendMsg.btCmd[13] = Integer.parseInt(strList[5], 16).toByte()
            sendMsg.btCmd[14] = Integer.parseInt(strList[2], 16).toByte()
            sendMsg.btCmd[15] = Integer.parseInt(strList[0], 16).toByte()
            sendMsg.btCmd[16] = Integer.parseInt(strList[1], 16).toByte()
            sendMsg.btCmd[17] = seekLedPwr.progress.shr(8).toByte()
            sendMsg.btCmd[18] = seekLedPwr.progress.and(0xff).toByte()
            sendMsg.btCmd[19] = seekLedMfb.progress.shr(8).toByte()
            sendMsg.btCmd[20] = seekLedMfb.progress.and(0xff).toByte()
            sendMsg.btCmd[21] = seekLedBcb.progress.shr(8).toByte()
            sendMsg.btCmd[22] = seekLedBcb.progress.and(0xff).toByte()
            sendMsg.btCmd[23] = seekLedRev.progress.shr(8).toByte()
            sendMsg.btCmd[24] = seekLedRev.progress.and(0xff).toByte()
            (activity as DevUnitMsg).sendBtServiceMsg(sendMsg)
        }
        rdGpDev.setOnCheckedChangeListener { group, checkedId ->
            srcDevId =
                when(checkedId) {
                    R.id.rdSrc -> {
                        srcDevItme = 0
                        cmdSetFeatureId = CmdId.SET_HFP_FEATURE_REQ.value
                        cmdGetFeatureId = CmdId.GET_HFP_FEATURE_REQ.value
                        0x30
                    }
                    R.id.rdAgAll -> {
                        srcDevItme = 1
                        cmdSetFeatureId = CmdId.SET_AG_FEATURE_REQ.value
                        cmdGetFeatureId = CmdId.GET_AG_FEATURE_REQ.value
                        0x38
                    }
                    R.id.rdHfpAll -> {
                        srcDevItme = 1
                        cmdSetFeatureId = CmdId.SET_HFP_FEATURE_REQ.value
                        cmdGetFeatureId = CmdId.GET_HFP_FEATURE_REQ.value
                        0x38
                    }
                    else -> {
                        srcDevItme = 0
                        cmdSetFeatureId = CmdId.SET_HFP_FEATURE_REQ.value
                        cmdGetFeatureId = CmdId.GET_HFP_FEATURE_REQ.value
                        0x30
                    }
                }
            updataData()
        }
        rdGpMode.setOnCheckedChangeListener { group, checkedId ->
            (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureMode = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureMode.and(0x0f000000.inv())
            (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureMode =
                when(checkedId) {
                    R.id.rdModeUsb ->  (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureMode.or(0x01000000)
                    R.id.rdModeBt -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureMode.or(0x02000000)
                    R.id.rdModeVcs -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureMode.or(0x04000000)
                    R.id.rdModeWire -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureMode.or(0x08000000)
                    else -> (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureMode.or(0x01000000)
                }
        }
        chkFeature0.setOnCheckedChangeListener { buttonView, isChecked ->
            checkUpdate(buttonView, isChecked)
        }
        chkFeature1.setOnCheckedChangeListener { buttonView, isChecked ->
            checkUpdate(buttonView, isChecked)
        }
        chkFeature2.setOnCheckedChangeListener { buttonView, isChecked ->
            checkUpdate(buttonView, isChecked)
        }
        chkFeature3.setOnCheckedChangeListener { buttonView, isChecked ->
            checkUpdate(buttonView, isChecked)
        }
        chkFeature4.setOnCheckedChangeListener { buttonView, isChecked ->
            checkUpdate(buttonView, isChecked)
        }
        chkFeature5.setOnCheckedChangeListener { buttonView, isChecked ->
            checkUpdate(buttonView, isChecked)
        }
        chkFeature6.setOnCheckedChangeListener { buttonView, isChecked ->
            checkUpdate(buttonView, isChecked)
        }
        chkFeature7.setOnCheckedChangeListener { buttonView, isChecked ->
            checkUpdate(buttonView, isChecked)
        }
        chkFeature8.setOnCheckedChangeListener { buttonView, isChecked ->
            checkUpdate(buttonView, isChecked)
        }
        chkFeature9.setOnCheckedChangeListener { buttonView, isChecked ->
            checkUpdate(buttonView, isChecked)
        }
        chkFeature10.setOnCheckedChangeListener { buttonView, isChecked ->
            checkUpdate(buttonView, isChecked)
        }
        chkFeature11.setOnCheckedChangeListener { buttonView, isChecked ->
            checkUpdate(buttonView, isChecked)
        }
        chkFeature12.setOnCheckedChangeListener { buttonView, isChecked ->
            checkUpdate(buttonView, isChecked)
        }
        chkFeature13.setOnCheckedChangeListener { buttonView, isChecked ->
            checkUpdate(buttonView, isChecked)
        }
        chkFeature14.setOnCheckedChangeListener { buttonView, isChecked ->
            checkUpdate(buttonView, isChecked)
        }
        chkFeature15.setOnCheckedChangeListener { buttonView, isChecked ->
            checkUpdate(buttonView, isChecked)
        }
        seekLedPwr.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        seekLedMfb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        seekLedBcb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        seekLedRev.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        updataData()
    }

    fun checkUpdate(it: View, isChecked: Boolean) {
        val i =
            when(it.id) {
                R.id.chkFeature0 -> 0
                R.id.chkFeature1 -> 1
                R.id.chkFeature2 -> 2
                R.id.chkFeature3 -> 3
                R.id.chkFeature4 -> 4
                R.id.chkFeature5 -> 5
                R.id.chkFeature6 -> 6
                R.id.chkFeature7 -> 7
                R.id.chkFeature8 -> 8
                R.id.chkFeature9 -> 9
                R.id.chkFeature10 -> 10
                R.id.chkFeature11 -> 11
                R.id.chkFeature12 -> 12
                R.id.chkFeature13 -> 13
                R.id.chkFeature14 -> 14
                R.id.chkFeature15 -> 15
                else -> 16
            }
        (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureHfp =
            if(isChecked)
                (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureHfp.or(1.shl(i))
            else
                (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureHfp.and(1.shl(i).inv())
        Logger.d(LogGbl, String.format("checkbox id:%2d feature:%4X", i, (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureHfp))
    }

    fun updataData() {
        var chkBox: CheckBox
        var feature: Int
        var ledLight = Array<Int>(4) {0}

        if(cmdGetFeatureId == CmdId.GET_AG_FEATURE_REQ.value) {
            feature = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureAg
            ledLight = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].ledLightAg
            txvFilterBda.text = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].bdaddrFilterAg
        }
        else {
            feature = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureHfp
            ledLight = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].ledLightHfp
            txvFilterBda.text = (activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].bdaddrFilterHfp
        }

        when((activity as DevUnitMsg).getBtDevUnitList()[srcDevItme].featureMode.and(0x0f000000)) {
            0x01000000 -> rdModeUsb.isChecked = true
            0x02000000 -> rdModeBt.isChecked = true
            0x04000000 -> rdModeVcs.isChecked = true
            0x08000000 -> rdModeWire.isChecked = true
        }
        for(i in 0..15) {
            chkBox =
                when(i) {
                    0 -> chkFeature0
                    1 -> chkFeature1
                    2 -> chkFeature2
                    3 -> chkFeature3
                    4 -> chkFeature4
                    5 -> chkFeature5
                    6 -> chkFeature6
                    7 -> chkFeature7
                    8 -> chkFeature8
                    9 -> chkFeature9
                    10 -> chkFeature10
                    11 -> chkFeature11
                    12 -> chkFeature12
                    13 -> chkFeature13
                    14 -> chkFeature14
                    15 -> chkFeature15
                    else -> chkFeature0
                }
            chkBox.isChecked =
                if(feature.and(1.shl(i)) == 1.shl(i))
                    true
                else
                    false
        }
        seekLedPwr.progress = ledLight[0]
        seekLedMfb.progress = ledLight[1]
        seekLedBcb.progress = ledLight[2]
        seekLedRev.progress = ledLight[3]
    }
}
