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
 * 좋아요 리스트
 */
class LikeListActivity : AppCompatActivity() {
    lateinit var binding: ActivityListBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    lateinit var adapter: RecruitAdapter
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

        binding.listTitleText.text = "좋아요"

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }
    }

    private fun initData() {
        database = Firebase.database.reference
        auth = Firebase.auth

        var likeClubIds = intent.getStringArrayListExtra("likeClubIds")

        database.child("club")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    clubData.clear()

                    for (postSnapshot in snapshot.children) {

                        val club = postSnapshot.getValue(Club::class.java)

                        if (club!!.state == "ACTIVE") {
                            if(likeClubIds!!.contains(club!!.clubId.toString())) {
                                clubData.add(Club(club!!.clubId, club.bound, club.limit, club.type, club.title, club.introduce,
                                    club.startYear, club.startMonth, club.startDay, club.endYear, club.endMonth,
                                    club.endDay, club.content, club.likeCount, club.memberCount, club.state, club.userId))
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        //현재 user가 좋아요한 clubId 가져오기
        database.child("like")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    likeClubIds!!.clear()

                    for (postSnapshot in snapshot.children) {
                        val like = postSnapshot.getValue(Like::class.java)
                        if(like!!.userId == auth.currentUser!!.uid) {
                            likeClubIds!!.add(like.clubId)
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
        adapter = RecruitAdapter(clubData)
        adapter.itemClickListener=object : RecruitAdapter.OnItemClickListener {
            override fun OnItemClick(data: Club, position: Int) {
                val intent = Intent(this@LikeListActivity, ClubInfoActivity::class.java)
                intent.putExtra("clubId", data.clubId.toString())
                startActivity(intent)
            }
        }
        binding.RecyclerView.adapter = adapter
    }
}