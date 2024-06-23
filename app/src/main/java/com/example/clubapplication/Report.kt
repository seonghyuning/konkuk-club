package com.example.clubapplication

/**
 * 신고 Dto
 */
data class Report(
    var clubName: String,
    var reason: String,
    var createdDate: String,
    var clubId: String,
    var userId: String
) {
    constructor():this("", "","","","")
}