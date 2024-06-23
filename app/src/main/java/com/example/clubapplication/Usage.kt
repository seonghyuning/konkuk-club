package com.example.clubapplication

/**
 * 회비 사용 내역 Dto
 */
data class Usage(
    var createdDate: String,
    var usageContent: String,
    var deposit: Int,
    var withdraw: Int,
    var rest: Int
){
    constructor():this("","",0,0,0)
}
