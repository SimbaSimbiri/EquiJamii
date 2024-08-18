package com.simbiri.equityjamii.data.model

import com.google.firebase.Timestamp

data class WorkspaceMention(
    var mentionId: String?,
    var workspaceId: String?,
    var appreciatorId: String?,
    var recipientId: String,
    var timeMentioned: Timestamp? = null,
    var keyWordMention: String,
    var mentionMainText: String
) {
    constructor() : this("", "", "", "", null, "", "")
}