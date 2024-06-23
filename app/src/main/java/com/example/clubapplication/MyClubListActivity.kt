package com.example.clubapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clubapplication.databinding.ActivityListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

/**
 * 내 동아리 리스트
 */
class MyClubListActivity : AppCompatActivity() {
    lateinit var binding: ActivityListBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    lateinit var adapter: MyClubAdapter
    private var clubData: ArrayList<Club> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initRecyclerView()
        initLayout()
    }

    private fun initLayout() {

        binding.listTitleText.text = "내동아리"

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }
    }

    private fun initData() {
        database = Firebase.database.reference
        auth = Firebase.auth

        var myClubIds = intent.getStringArrayListExtra("myClubIds")

        database.child("club")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    clubData.clear()

                    for (postSnapshot in snapshot.children) {
                        val club = postSnapshot.getValue(Club::class.java)
                        if(club!!.state == "ACTIVE") {
                            if(myClubIds!!.contains(club.clubId.toString())) {
                                clubData.add(Club(club.clubId, club.bound, club.limit, club.type, club.title,
                                    club.introduce, club.startYear, club.startMonth, club.startDay,
                                    club.endYear, club.endMonth, club.endDay, club.content, club.likeCount,
                                    club.memberCount, club.state, club.userId))
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun initRecyclerView() {
        binding.RecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = MyClubAdapter(clubData)
        adapter.itemClickListener=object : MyClubAdapter.OnItemClickListener {
            override fun OnItemClick(data: Club, position: Int) {
                val intent = Intent(this@MyClubListActivity, MyClubActivity::class.java)
                intent.putExtra("clubId", data.clubId.toString())
                startActivity(intent)
            }
        }
        binding.RecyclerView.adapter = adapter
    }
}