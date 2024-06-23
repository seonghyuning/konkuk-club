package com.example.clubapplication

/**
 * 동아리 회원 Dto
 */
data class ClubLog(
    var name: String,
    var college: String,
    var major: String,
    var number: String,
    var state: String,
    var clubId: String,
    var userId: String,
    var clubLogId: String
){
    constructor():this("","","","","","","","")
}
