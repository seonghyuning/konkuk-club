package com.example.clubapplication

/**
 * 공지 Dto
 */
data class Notice(
    var noticeTitle: String,
    var noticeContent: String,
    var createdDate: String,
    var clubId: String
){
    constructor():this("","","","")
}
