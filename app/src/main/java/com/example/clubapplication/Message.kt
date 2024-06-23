package com.example.clubapplication

/**
 * 채팅 Dto
 */
data class Message(
    var message: String,
    var createdDate: String,
    var userId: String,
    var name: String,
    var isSameDate: Boolean
){
    constructor():this("","","","", false)
}
