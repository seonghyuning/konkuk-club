package com.example.clubapplication

/**
 * 동아리 Dto
 */
data class Club(
    var clubId: Int,
    var bound: String,
    var limit: String,
    var type: String,
    var title: String,
    var introduce: String,
    var startYear: Int,
    var startMonth: Int,
    var startDay: Int,
    var endYear: Int,
    var endMonth: Int,
    var endDay: Int,
    var content: String,
    var likeCount: Int,
    var memberCount: Int,
    var state: String,
    var userId: String
) {
    constructor():this(0,"","", "", "", "", 0, 0,
        0, 0, 0, 0, "", 0, 0, "", "")
}
