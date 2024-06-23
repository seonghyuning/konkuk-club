package com.example.clubapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clubapplication.databinding.ActivityMyClubBinding
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

/**
 * 내 동아리
 */
class MyClubActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyClubBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    lateinit var adapter: NoticeAdapter
    private var noticeData: ArrayList<Notice> = ArrayList()
    private lateinit var clubId:String
    private lateinit var userId:String
    private lateinit var title:String
    private lateinit var likeId: String
    private var likeCount = 0
    private var updateMap = HashMap<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyClubBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initRecyclerView()
        initLayout()
    }

    private fun initLayout() {

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }

        //운영진
        binding.clubManagerImage.setOnClickListener {
            val intent = Intent(this, ManageClubActivity::class.java)
            intent.putExtra("clubId", clubId)
            startActivity(intent)
        }

        //단체 채팅방
        binding.groupChatText.setOnClickListener {
            val intent = Intent(this, GroupChatActivity::class.java)
            intent.putExtra("clubId", clubId)
            intent.putExtra("userId", userId)
            intent.putExtra("title", title)
            startActivity(intent)
        }

        //회비 사용 내역
        binding.usageHistoryText.setOnClickListener {
            val intent = Intent(this, UsageHistoryActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("clubId", clubId)
            intent.putExtra("title", title)
            startActivity(intent)
        }

        //공지 추가
        binding.addImage.setOnClickListener {
            addNoticeDialog()
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
    }

    private fun initData() {
        clubId = intent.getStringExtra("clubId").toString()
        database = Firebase.database.reference
        auth = Firebase.auth

        database.child("club").child(clubId)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val club = snapshot.getValue(Club::class.java)
                    title = club!!.title
                    binding.titleText.text = title
                    binding.introText.text = club.introduce

                    if (club.limit == "제한없음") {
                        binding.boundText.text = club.bound
                    } else {
                        binding.boundText.text = club.limit
                    }

                    binding.typeText.text = club.type
                    likeCount = club.likeCount

                    val dec = DecimalFormat("#,###")

                    binding.likeCountText.text = dec.format(likeCount)
                    binding.memberCountText.text = club.memberCount.toString() + "명 활동중"

                    userId = club.userId

                    if(userId == auth.currentUser!!.uid) {
                        binding.addImage.visibility = View.VISIBLE
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

        database.child("notice").child(clubId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    noticeData.clear()

                    for (postSnapshot in snapshot.children) {
                        val notice = postSnapshot.getValue(Notice::class.java)
                        noticeData.add(Notice(notice!!.noticeTitle, notice.noticeContent, notice.createdDate, notice.clubId))
                    }

                    noticeData.reverse()
                    adapter.notifyDataSetChanged()
//                    binding.RecyclerView.scrollToPosition(adapter.itemCount - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

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

        database.child("clubLog")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val clubLog = postSnapshot.getValue(ClubLog::class.java)
                        if(clubLog!!.clubId == clubId && clubLog.userId == auth.currentUser!!.uid && clubLog.state == "MANAGER") {
                            binding.clubManagerImage.visibility = View.VISIBLE
                            binding.addImage.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun initRecyclerView() {
        binding.RecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = NoticeAdapter(noticeData)
        binding.RecyclerView.adapter = adapter
    }

    private fun addNoticeDialog() {
        //공지 추가 dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_addnotice, null)
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setView(dialogView)

        val complete = dialogView.findViewById<TextView>(R.id.completeText)
        val cancel = dialogView.findViewById<TextView>(R.id.cancelText)

        complete.setOnClickListener {
            val noticeTitle = dialogView.findViewById<TextView>(R.id.noticeTitleEdit).text.toString()
            val noticeContent = dialogView.findViewById<TextView>(R.id.noticeContentEdit).text.toString()
            val currentTime = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(Date())

            if(noticeTitle == "") {
                Toast.makeText(this, "공지 제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(noticeContent == "") {
                Toast.makeText(this, "공지 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else {
                database.child("notice").child(clubId).push()
                    .setValue(Notice(noticeTitle, noticeContent, currentTime, clubId))
                alertDialog.cancel()
            }
        }

        cancel.setOnClickListener {
            alertDialog.cancel()
        }

        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.edit_background)
    }
}