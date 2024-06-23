package com.example.clubapplication

/**
 * 사용자 Dto
 */
data class User(
    var email: String,
    var name: String,
    var college: String,
    var major: String,
    var number: String
) {
    constructor():this("","","","","")
}
