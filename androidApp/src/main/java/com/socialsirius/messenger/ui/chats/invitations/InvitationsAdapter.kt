package com.socialsirius.messenger.ui.chats.invitations

import android.view.View
import android.view.ViewGroup
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.ConnRequest
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.Invitation
import com.socialsirius.messenger.R
import com.socialsirius.messenger.base.ui.SimpleBaseRecyclerViewAdapter
import com.socialsirius.messenger.databinding.ItemChatBinding
import com.socialsirius.messenger.databinding.ItemInvitationsUnacceptedBinding
import com.socialsirius.messenger.models.ChatMessageStatus
import com.socialsirius.messenger.models.Chats
import com.socialsirius.messenger.utils.DateUtils

class InvitationsAdapter(override val layoutRes: Int = R.layout.item_invitations_unaccepted) :
    SimpleBaseRecyclerViewAdapter<Chats, InvitationsAdapter.InvitationsChatsViewHolder>() {

    inner class InvitationsChatsViewHolder(itemView: View) :
        SimpleBaseRecyclerViewAdapter.SimpleViewHolder<ItemInvitationsUnacceptedBinding, Chats>(itemView) {


        override fun bind(chat: Chats, position: Int) {
            binding?.nameTextView?.text = chat.title
            binding?.verkeyTextView?.text = chat.id
            binding?.dateTextView?.text = DateUtils.parseDateToDdMMyyyyHHMMString(chat.lastMessage?.sentTime)
            val message =  chat.lastMessage?.message()
            var isIncome = false
            if (message is ConnRequest){
                isIncome = true
            }
            if(isIncome){
                binding?. imageView?.setImageResource(R.drawable.inbox)
                binding?. statusTextView?.text = "Tap to answer for request"
            }else{
                binding?. imageView?.setImageResource(R.drawable.outbox)
                binding?. statusTextView?.text = "Wait for response"
            }
            if( chat.lastMessage?.status == ChatMessageStatus.acknowlege){
                binding?.newTextView?.visibility = View.GONE
            }else{
                binding?.newTextView?.visibility = View.VISIBLE
            }



     //       val lastMessage = chat.lastMessage?.message()
         /*   if(lastMessage is Invitation){
                binding?.statusImageView?.setImageResource(R.drawable.outbox)
            }else if(lastMessage is ConnRequest){
                binding?.statusImageView?.setImageResource(R.drawable.inbox)
            }*/

        }





    }

    override fun onBind(holder: InvitationsChatsViewHolder, item: Chats, position: Int) {
        super.onBind(holder, item, position)
        holder.bind(item, position)
        if( item.lastMessage?.status != ChatMessageStatus.acknowlege){
            onCustomBtnClick?.onBtnClick(-10,item, position)
        }
    }
    override fun getViewHolder(
        parent: ViewGroup?,
        layoutRes: Int,
        viewType: Int
    ): InvitationsChatsViewHolder {
      return InvitationsChatsViewHolder(getInflatedView(layoutRes,parent, false))
    }
}