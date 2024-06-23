package com.example.clubapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clubapplication.databinding.ActivityListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

/**
 * 1:1 문의 관리
 */
class ManageInquiryActivity : AppCompatActivity() {
    lateinit var binding: ActivityListBinding
    private lateinit var database: DatabaseReference
    lateinit var adapter: InquiryAdapter
    private var inquiryData: ArrayList<Inquiry> = ArrayList()
    private lateinit var clubId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initRecyclerView()
        initLayout()
    }

    private fun initLayout() {

        binding.listTitleText.text = "1:1 문의 관리"

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }
    }

    private fun initData() {
        database = Firebase.database.reference
        clubId = intent.getStringExtra("clubId").toString()

        database.child("inquiryChat").child(clubId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    inquiryData.clear()

                    for (postSnapshot in snapshot.children) {
                        val inquiry = postSnapshot.getValue(Inquiry::class.java)
                        inquiryData.add(Inquiry(inquiry!!.lastMessage, inquiry.checkCount, inquiry.createdDate,
                                                inquiry.userId, inquiry.clubId))
                    }

                    //가장 나중에 보낸 문의를 가장 위로
                    inquiryData.sortByDescending { Inquiry -> Inquiry.createdDate }

                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun initRecyclerView() {
        binding.RecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = InquiryAdapter(inquiryData)
        adapter.itemClickListener=object : InquiryAdapter.OnItemClickListener {
            override fun OnItemClick(data: Inquiry, position: Int) {
                val intent = Intent(this@ManageInquiryActivity, InquiryActivity::class.java)
                intent.putExtra("clubId", data.clubId)
                intent.putExtra("userId", "clubManager")
                intent.putExtra("inquiryUserId", data.userId)
                startActivity(intent)
            }
        }
        binding.RecyclerView.adapter = adapter
    }
}