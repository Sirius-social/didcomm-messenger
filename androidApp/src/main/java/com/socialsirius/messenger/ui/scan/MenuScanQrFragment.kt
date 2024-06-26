package com.socialsirius.messenger.ui.scan

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.Result
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.Invitation
import com.socialsirius.messenger.R
import com.socialsirius.messenger.base.App
import com.socialsirius.messenger.base.ui.BaseFragment
import com.socialsirius.messenger.databinding.FragmentMenuScanQrBinding
import com.socialsirius.messenger.databinding.ViewErrorBootomSheetBinding
import com.socialsirius.messenger.databinding.ViewInvitationBootomSheetBinding
import com.socialsirius.messenger.design.InvitationBottomSheet
import com.socialsirius.messenger.design.SiriusScannerView
import com.socialsirius.messenger.sirius_sdk_impl.SDKUseCase
import com.socialsirius.messenger.ui.activities.main.MainActivity
import com.socialsirius.messenger.ui.activities.message.MessageActivity
import com.socialsirius.messenger.utils.PermissionHelper

import kotlinx.android.synthetic.main.fragment_menu_scan_qr.mScannerView

class MenuScanQrFragment : BaseFragment<FragmentMenuScanQrBinding, MenuScanQrViewModel>(),
    SiriusScannerView.ResultHandler {

    companion object {
        @JvmStatic
        fun newInstance() = MenuScanQrFragment()
    }

    override fun getLayoutRes(): Int = R.layout.fragment_menu_scan_qr

    override fun setModel() {
        dataBinding!!.viewModel = model
    }

    override fun initDagger() {
        App.getInstance().appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (PermissionHelper.checkPermissionsOnlyForCamera(activity, 1098)) {
            startCamera()
        }

    }

    var dialog: AlertDialog? = null
    override fun onResume() {
        super.onResume()
        mScannerView?.resumeCameraPreview(this)
    }

    override fun onPause() {
        super.onPause()
        mScannerView?.stopCameraPreview()
    }

    override fun onDestroy() {
        mScannerView?.stopCamera()
        super.onDestroy()

    }

    private fun startCamera() {
        mScannerView?.setResultHandler(this)
        mScannerView?.startCamera()
    }

    override fun subscribe() {
        model.showInvitationBottomSheetLiveData.observe(this, Observer {
            if(it!=null){
                model.showInvitationBottomSheetLiveData.value = null
                baseActivity.model.showInvitationBottomSheetLiveData.postValue(it)
            }
        })
        model.showErrorBottomSheetLiveData.observe(this, Observer {
            if(it!=null){
                model.showErrorBottomSheetLiveData.value = null
                mScannerView?.stopCameraPreview()
                baseActivity.model.showErrorBottomSheetLiveData.postValue(it)
            }
        })

        baseActivity.model.invitationSheetDismissLiveData.observe(this, Observer {
            if(it){
                baseActivity.model.invitationSheetDismissLiveData.value = false
                mScannerView ?.resumeCameraPreview(this);
            }
        })
     /*   model.goToNewSecretChatLiveData.observe(this, Observer {
            *//*  it?.let {
                  Log.d("mylog2080", "goToNewSecretChatLiveData=" + it);
                  (baseActivity as? MainActivity)?.showChats()
                  val messageIntent = Intent(context, MessageActivity::class.java).apply {
                      putExtra(StaticFields.BUNDLE_CHAT, it)
                  }
                  startActivity(messageIntent)
                  model.goToNewSecretChatLiveData.postValue(null)
              }*//*


        })

*/

      model.goToNewSecretChatLiveData.observe(this, Observer {
            if (it != null) {
                model.goToNewSecretChatLiveData.value = null
                baseActivity.finish()
                MessageActivity.newInstance(requireContext(), it)
            }
        })

     /*   model.invitationPolicemanSuccessLiveData.observe(this, Observer {
            if (it != null) {
                model.invitationPolicemanSuccessLiveData.value = null
                val item = model.getMessage(it)
                dialog?.cancel()
                //  popPage(DocumentShareFragment.newInstance(item))
            }
        })*/


    }



    override fun handleResult(rawResult: Result?) {
        rawResult?.let {
           val success =  model.onCodeScanned(it.text.orEmpty(),0)
            if (!success){
                mScannerView?.resumeCameraPreview(this)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHelper.onRequestPermissionsResult(
            requestCode,
            1098,
            permissions,
            grantResults,
            object :
                PermissionHelper.OnRequestPermissionListener {
                override fun onRequestFail() {
                    model.onShowToastLiveData.postValue(resources.getString(R.string.camera_qr_scan_permission))
                }

                override fun onRequestSuccess() {
                    startCamera()
                }
            })
    }
}