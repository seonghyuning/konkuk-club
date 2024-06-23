package com.example.clubapplication

/**
 * 좋아요 Dto
 */
data class Like(
    var clubId: String,
    var userId: String
) {
    constructor():this("","")
}
