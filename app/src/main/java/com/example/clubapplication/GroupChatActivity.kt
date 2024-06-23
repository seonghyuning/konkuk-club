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
 * 단체 채팅방
 */
class GroupChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityMessageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var clubId: String
    private lateinit var name: String
    private lateinit var title: String
    lateinit var adapter: MessageAdapter
    private var messageData: ArrayList<Message> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initRecyclerView()
        initLayout()
    }

    private fun initLayout() {
        title = intent.getStringExtra("title").toString()

        binding.messageTitle.text = title

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }

        //전송버튼 클릭
        binding.sendBtn.setOnClickListener {

            val message = binding.messageEdit.text.toString()
            val currentTime = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(Date())

            database.child("groupChat").child(clubId).push().setValue(
                Message(message, currentTime, auth.currentUser!!.uid, name, false))

            binding.messageEdit.text.clear()
        }
    }

    private fun initData() {
        clubId = intent.getStringExtra("clubId").toString()

        auth = Firebase.auth
        database = Firebase.database.reference

        //현재 user 정보 불러오기
        database.child("user").child(auth.currentUser!!.uid)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    name = user!!.name
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

        //메세지 정보 불러오기
        database.child("groupChat").child(clubId)
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
    }


    private fun initRecyclerView() {
        binding.RecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter= MessageAdapter(messageData)
        binding.RecyclerView.adapter = adapter
    }
}