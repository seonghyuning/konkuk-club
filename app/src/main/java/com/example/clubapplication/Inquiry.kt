package com.example.clubapplication

/**
 * 1:1 문의 Dto
 */
data class Inquiry(
    var lastMessage: String,
    var checkCount: Int,
    var createdDate: String,
    var userId: String,
    var clubId: String
){
    constructor():this("",0,"","","")
}
