package com.example.clubapplication

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.clubapplication.databinding.ActivityClubInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * 동아리 상세
 */
class ClubInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityClubInfoBinding
    private lateinit var database: DatabaseReference
    private lateinit var clubId:String
    private var clubManagerId = ""  //해당 동아리의 userId
    private lateinit var auth: FirebaseAuth
    val managerId = "pS4EsvmAP6hJ0uGPHifnvm9dEJy2"  //관리자 Id
    private lateinit var likeId: String
    private var likeCount = 0
    private lateinit var state: String
    private lateinit var title: String
    private var updateMap = HashMap<String, Any>()
    private var userMap = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClubInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        addSpinner()
        initLayout()
    }

    private fun initLayout() {

        //관리자일 경우 버튼 보이게
        if(auth.currentUser!!.uid == managerId) {
            binding.managerBtn.visibility = View.VISIBLE
        }

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }

        //더보기 선택
        binding.moreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedManager = binding.moreSpinner.selectedItem.toString()

                if(selectedManager == "선택") {
                    //동아리 회장은 1:1문의, 신고 Spinner 안보이게
                    if(clubManagerId == auth.currentUser!!.uid) {
                        binding.moreSpinner.visibility = View.GONE
                    }

                } else if(selectedManager == "1:1 문의") {
                    val intent = Intent(this@ClubInfoActivity, InquiryActivity::class.java)
                    intent.putExtra("userId", "none")
                    intent.putExtra("clubId", clubId)
                    intent.putExtra("inquiryUserId", "none")
                    startActivity(intent)
                    binding.moreSpinner.setSelection(0)

                }else if(selectedManager == "신고") {
                    reportDialog()
                    binding.moreSpinner.setSelection(0)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        //빈하트(좋아요) 버튼 클릭
        binding.likeImage.setOnClickListener {
            binding.likeImage.visibility = View.GONE
            binding.filledLikeImage.visibility = View.VISIBLE
            likeId = database.child("like").push().key!!
            database.child("like").child(likeId).setValue(Like(clubId, auth.currentUser!!.uid))
            likeCount++
            updateMap.put("likeCount", likeCount)
            database.child("club").child(clubId).updateChildren(updateMap)
            updateMap.clear()
        }

        //찬하트(좋아요) 버튼 클릭
        binding.filledLikeImage.setOnClickListener {
            binding.likeImage.visibility = View.VISIBLE
            binding.filledLikeImage.visibility = View.GONE
            database.child("like").child(likeId).removeValue()
            likeCount--
            updateMap.put("likeCount", likeCount)
            database.child("club").child(clubId).updateChildren(updateMap)
            updateMap.clear()
        }

        //동아리 가입하기 버튼 클릭
        binding.joinBtn.setOnClickListener {
            val clubLogId = database.child("clubLog").push().key.toString()
            database.child("clubLog").child(clubLogId).setValue(
                ClubLog(userMap.get("name").toString(), userMap.get("college").toString(), userMap.get("major").toString(),
                    userMap.get("number").toString(), "WAIT", clubId, auth.currentUser!!.uid, clubLogId))
            Toast.makeText(this, "동아리 가입이 신청 되었습니다.", Toast.LENGTH_SHORT).show()
        }

        //활성화 버튼 클릭
        binding.activeBtn.setOnClickListener {
            if(state == "ACTIVE") {
                Toast.makeText(this, "이미 활성화 되어있습니다.", Toast.LENGTH_SHORT).show()
            }else {
                updateMap.put("state", "ACTIVE")
                database.child("club").child(clubId).updateChildren(updateMap)
                updateMap.clear()
                Toast.makeText(this, "활성화 되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        //비활성화 버튼 클릭
        binding.inactiveBtn.setOnClickListener {
            if(state == "INACTIVE") {
                Toast.makeText(this, "이미 비활성화 되어있습니다.", Toast.LENGTH_SHORT).show()
            }else {
                updateMap.put("state", "INACTIVE")
                database.child("club").child(clubId).updateChildren(updateMap)
                updateMap.clear()
                Toast.makeText(this, "비활성화 되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        //삭제 버튼 클릭
        binding.deleteBtn.setOnClickListener {
            deleteDialog()
        }

    }

    private fun initData() {
        clubId = intent.getStringExtra("clubId").toString()
        database = Firebase.database.reference
        auth = Firebase.auth

        var userCollege = ""
        var userMajor = ""

        //현재 user의 정보 가져오기
        database.child("user").child(auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    userMap.clear()

                    val user = snapshot.getValue(User::class.java)
                    userCollege = user!!.college
                    userMajor = user.major

                    userMap.put("name", user.name)
                    userMap.put("college", user.college)
                    userMap.put("major", user.major)
                    userMap.put("number", user.number)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        //해당 동아리 정보 가져오기
        database.child("club").child(clubId)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val club = snapshot.getValue(Club::class.java)
                    title = club!!.title
                    binding.titleText.setText(title)
                    binding.introText.setText(club.introduce)

                    clubManagerId = club.userId

                    if (club.limit == "제한없음") {
                        binding.boundText.text = club.bound
                    } else {
                        binding.boundText.text = club.limit
                    }

                    binding.typeText.setText(club.type)
                    likeCount = club.likeCount

                    val dec = DecimalFormat("#,###")

                    binding.likeCountText.text = dec.format(likeCount)
                    binding.memberCountText.text = club.memberCount.toString() + "명 활동중"
                    binding.period.text = (club.startYear.toString() + "년 " + club.startMonth.toString() + "월 "
                            + club.startDay.toString() + "일 ~ " + club.endYear.toString() + "년 " +
                            club.endMonth.toString() + "월 " + club.endDay.toString() + "일")
                    binding.contentText.text = club.content

                    state = club.state

                    var startYear = club.startYear.toString()
                    var startMonth = club.startMonth.toString()
                    if(startMonth.toInt() < 10)
                        startMonth = "0" + startMonth
                    var startDay = club.startDay.toString()
                    if(startDay.toInt() < 10)
                        startDay = "0" + startDay

                    var endYear = club.endYear.toString()
                    var endMonth = club.endMonth.toString()
                    if(endMonth.toInt() < 10)
                        endMonth = "0" + endMonth
                    var endDay = club.endDay.toString()
                    if(endDay.toInt() < 10)
                        endDay = "0" + endDay

                    var startDate = startYear + "-" + startMonth + "-" + startDay
                    var endDate = endYear + "-" + endMonth + "-" + endDay

                    var sf = SimpleDateFormat("yyyy-MM-dd")

                    var sfStartDate = sf.parse(startDate)
                    var sfEndDate = sf.parse(endDate)

                    var today = Calendar.getInstance()

                    var calcuStartDate = (today.time.time - sfStartDate.time) / (60 * 60 * 24 * 1000)
                    var calcuEndDate = (sfEndDate.time - today.time.time) / (60 * 60 * 24 * 1000)

                    //모집기간이 아니면 가입버튼 gone
                    if(calcuStartDate < 0 || calcuEndDate < 0) {
                        binding.joinBtn.visibility = View.GONE
                    }

                    //동아리 회장일 경우 가입버튼 gone
                    if(club.userId == auth.currentUser!!.uid) {
                        binding.joinBtn.visibility = View.GONE
                    }

                    //동아리 가입 제한이 있을 경우 가입버튼 gone
                    if(club.limit != "제한없음") {
                        if(club.bound == "단과대 동아리") {
                            if(club.limit != userCollege) {
                                binding.joinBtn.visibility = View.GONE
                            }
                        }else if(club.bound == "과 동아리") {
                            if(club.limit != userMajor) {
                                binding.joinBtn.visibility = View.GONE
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

        //해당 동아리 중 현재 user의 좋아요 여부 가져오기
        database.child("like")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val like = postSnapshot.getValue(Like::class.java)
                        if(like!!.clubId == clubId && like.userId == auth.currentUser!!.uid) {
                            binding.likeImage.visibility = View.GONE
                            binding.filledLikeImage.visibility = View.VISIBLE
                            likeId = postSnapshot.key!!
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        //현재 동아리 가입했는지 안했는지 여부 가져오기
        database.child("clubLog")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val clubLog = postSnapshot.getValue(ClubLog::class.java)
                        if(clubLog!!.clubId == clubId && clubLog.userId == auth.currentUser!!.uid) {
                            binding.joinBtn.visibility = View.GONE

                            //동아리 운영진은 1:1 문의, 신고 접근제한
                            if(clubLog.state == "MANAGER") {
                                binding.moreSpinner.visibility = View.GONE
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    //더보기 spinner 추가
    private fun addSpinner() {
        val spinnerAdapter = ArrayAdapter<String>(
            this,
            R.layout.simple_spinner_dropdown_item,
            ArrayList<String>())

        spinnerAdapter.add("선택")
        spinnerAdapter.add("1:1 문의")
        spinnerAdapter.add("신고")

        binding.moreSpinner.adapter = spinnerAdapter
    }

    //신고 dialog
    private fun reportDialog() {
        val dialogView = layoutInflater.inflate(com.example.clubapplication.R.layout.dialog_report, null)
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setView(dialogView)

        val report = dialogView.findViewById<TextView>(com.example.clubapplication.R.id.reportText)
        val cancel = dialogView.findViewById<TextView>(com.example.clubapplication.R.id.cancelText)

        report.setOnClickListener {
            val reason = dialogView.findViewById<TextView>(com.example.clubapplication.R.id.reasonTextMulti).text.toString()
            val currentTime = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(Date())

            database.child("report").push().setValue(Report(title, reason, currentTime, clubId, auth.currentUser!!.uid))
            Toast.makeText(this, "신고처리가 완료되었습니다.", Toast.LENGTH_SHORT).show()
            alertDialog.cancel()
        }

        cancel.setOnClickListener {
            alertDialog.cancel()
        }

        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(com.example.clubapplication.R.drawable.edit_background)
    }

    //진짜 삭제할지 dialog
    private fun deleteDialog() {
        val dialogView = layoutInflater.inflate(com.example.clubapplication.R.layout.dialog_delete, null)
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setView(dialogView)

        val deleteText1 = dialogView.findViewById<TextView>(com.example.clubapplication.R.id.deleteText1)
        val deleteText2 = dialogView.findViewById<TextView>(com.example.clubapplication.R.id.deleteText2)
        val delete = dialogView.findViewById<TextView>(com.example.clubapplication.R.id.deleteText)
        val cancel = dialogView.findViewById<TextView>(com.example.clubapplication.R.id.cancelText)

        deleteText1.text = "삭제하시면 복구가 불가능해집니다"
        deleteText2.text = "그래도 삭제하시겠습니까?"
        delete.text = "삭제"

        delete.setOnClickListener {
            updateMap.put("state", "DELETED")
            database.child("club").child(clubId).updateChildren(updateMap)
            updateMap.clear()
            Toast.makeText(this, "동아리가 삭제 되어있습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        cancel.setOnClickListener {
            alertDialog.cancel()
        }

        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(com.example.clubapplication.R.drawable.edit_background)
    }
}