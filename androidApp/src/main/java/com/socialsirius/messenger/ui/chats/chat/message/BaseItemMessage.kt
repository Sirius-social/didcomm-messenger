package com.socialsirius.messenger.ui.chats.chat.message

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData


import com.sirius.library.agent.listener.Event
import com.sirius.library.messaging.Message
import com.socialsirius.messenger.base.App
import com.socialsirius.messenger.models.ChatMessageStatus
import com.socialsirius.messenger.repository.MessageRepository
import com.socialsirius.messenger.repository.models.LocalMessage


import com.socialsirius.messenger.utils.DateUtils

import java.util.*

enum class MessageType{
    Base,
    Text,
    Connect,
    Connected,
    ConnectError,
    Offer,
    OfferAccepted,
    OfferError,
    Prover,
    ProverAccepted,
    ProverError,
    Question,
    QuestionAccepted,
    QuestionError,
    ProposeCredential,
    ProposeCredentialAccepted
}

abstract class BaseItemMessage : IChatItem {

    interface NotifyDataListener{
        fun notifyData()
        fun notifyItem(item : BaseItemMessage)
    }

    override fun getMessageId(): String {
        return id ?:""
    }
/*
    val messageRepository: MessageRepository? =
        App.getInstance().appComponent.provideMessageRepository()
*/

    var detailsVisibilityLiveData  = MutableLiveData<Int>(View.GONE)
    fun startLoading(id : String){
        isLoading = true
        notifyItem()
       // messageRepository?.startLoading(id )
  //      notifyData()
    }

    fun stopLoading(id : String){
        isLoading = false
       notifyItem()
      //  messageRepository?.updateErrorAccepted(id , isAccepted,isError,errorString,commentString)
    //    notifyData()
    }

    constructor(){}
    constructor(event: Event?){
        setupFromEvent(event)
    }
    constructor(localMessage: LocalMessage?){
        setupFromLocalMessage(localMessage)
    }

    var isAccepted: Boolean = false
    var id: String? = null
    var isLoading: Boolean = false
    var isMine: Boolean = false
    var message: Message? = null
    var errorString: String? = null
    var commentString: String? = null
    var isError: Boolean = false
    var date: Date? = null
    var status: ChatMessageStatus? = null
    var notifyDataListener : NotifyDataListener? = null


    fun canBeRead() : Boolean{
        return !isMine && status != ChatMessageStatus.acknowlege
    }

    fun notifyItem(){
        notifyDataListener?.notifyItem(this)
    }

    fun notifyData(){
        notifyDataListener?.notifyData()
    }

    fun getStatusString():String{
        if (isError){
          return "Error"
        }
        if (isAccepted){
            return "Accepted"
        }
        return "Not accepted"
    }

   open fun setupFromEvent(event: Event?){
        event?.let {

            if(it.messageObjectHasKey("tags")){
                val tags = it.messageObj.optJSONObject("tags")
                isAccepted =  tags?.optBoolean("isAccepted",false) ?: false
            }
            if(it.messageObjectHasKey("me")){
                isMine = true
            }
            if(it.message()?.messageObjectHasKey("sent_time") == true){
                val sentTime = it.message()?.getStringFromJSON("sent_time")
                date = DateUtils.getDateFromString(
                    sentTime,
                  DateUtils.PATTERN_ROSTER_STATUS_RESPONSE2,true)

            }
            message = it.message()
            id = message?.getId() ?:""
        }

    }

    open fun setupFromLocalMessage(localMessage: LocalMessage?){
        localMessage?.let {
            isAccepted = localMessage.isAccepted
            isMine = localMessage.isMine
            date = localMessage.sentTime
            message = localMessage.message()
            isLoading = localMessage.isLoading ?: false
            isError = localMessage.isCanceled
            errorString = localMessage.canceledCause
            commentString = localMessage.acceptedComment
            id = message?.getId() ?:""
            status = localMessage.status
        }
    }



    abstract fun getType() : MessageType

    abstract fun accept(comment : String? = null)

    abstract fun cancel()


    abstract fun getText() : String

    abstract fun getTitle() : String

}