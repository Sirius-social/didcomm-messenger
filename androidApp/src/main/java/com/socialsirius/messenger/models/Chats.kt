package com.socialsirius.messenger.models

import com.google.gson.annotations.SerializedName
import com.socialsirius.messenger.repository.models.LocalMessage
import java.io.Serializable
import java.util.*

class Chats : Serializable{
    constructor() {

    }

    constructor(id : String,title: String) {
        this.title = title

        this.id = id
        // this.isActionExist = isActionExist
    }


    var title: String? = null
    var id: String? = null
    var isActionExist: Boolean = false
    var unreadMessageNotInDB: Int = 0
    var allMessageCount: Int = 0
    var isInSilentMode: Boolean = false
    var isRoom: Boolean = false
    var lastMessage: LocalMessage? = null


}