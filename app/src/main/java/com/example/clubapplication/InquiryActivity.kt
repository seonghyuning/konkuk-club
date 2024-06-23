package com.example.clubapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clubapplication.databinding.ActivityMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

/**
 * 1:1 문의
 */
class InquiryActivity : AppCompatActivity() {
    lateinit var binding: ActivityMessageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var clubId: String
    private lateinit var clubManagerUserId: String
    private lateinit var inquiryUserId: String
    private var name = "익명"
    private var checkCount = 0
    lateinit var adapter: MessageAdapter
    private var messageData:ArrayList<Message> = ArrayList()
    private var updateMap = HashMap<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initRecyclerView()
        initLayout()
    }

    private fun initLayout() {
        binding.messageTitle.text = "1:1 문의"

        clubManagerUserId = intent.getStringExtra("userId").toString()

        //운영진이 1:1 질문 확인하면
        if(clubManagerUserId == "clubManager") {
            updateMap.put("checkCount", checkCount)
            database.child("inquiryChat").child(clubId).child(inquiryUserId).updateChildren(updateMap)
            updateMap.clear()
            name = "운영진"
        }

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }

        //전송버튼 클릭
        binding.sendBtn.setOnClickListener {

            val message = binding.messageEdit.text.toString()
            val currentTime = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(Date())

            database.child("inquiryMessage").child(clubId).child(inquiryUserId).push().setValue(
                Message(message, currentTime, auth.currentUser!!.uid, name, false))

            //문의자가 보냄
            if(auth.currentUser!!.uid == inquiryUserId) {
                database.child("inquiryChat").child(clubId).child(auth.currentUser!!.uid).setValue(
                    Inquiry(message, ++checkCount, currentTime, auth.currentUser!!.uid, clubId))
            }

            binding.messageEdit.text.clear()
        }
    }

    private fun initData() {
        auth = Firebase.auth
        database = Firebase.database.reference
        inquiryUserId = intent.getStringExtra("inquiryUserId").toString()
        clubId = intent.getStringExtra("clubId").toString()

        if(inquiryUserId == "none") {
            inquiryUserId = auth.currentUser!!.uid
        }

        //메시지 가져오기
        database.child("inquiryMessage").child(clubId).child(inquiryUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageData.clear()
                    var index = 0

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageData.add(Message(message!!.message, message.createdDate, message.userId, message.name, message.isSameDate))

                        if(index == 0) {
                            index++
                            continue
                        }else if(messageData[index-1].userId == messageData[index].userId && messageData[index-1].createdDate == messageData[index].createdDate) {
                            messageData[index-1].isSameDate = true
                        }

                        index++
                    }
                    adapter.notifyDataSetChanged()
                    binding.RecyclerView.scrollToPosition(adapter.itemCount - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        //문의 안읽은 수 가져오기
        database.child("inquiryChat").child(clubId).child(inquiryUserId)
            .get().addOnSuccessListener{
                val inquiry = it.getValue(Inquiry::class.java)
                if(inquiry?.checkCount != null) {
                    checkCount = inquiry.checkCount
                }

            }.addOnFailureListener{
            }
    }


    private fun initRecyclerView() {
        binding.RecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter= MessageAdapter(messageData)
        binding.RecyclerView.adapter = adapter
    }
}