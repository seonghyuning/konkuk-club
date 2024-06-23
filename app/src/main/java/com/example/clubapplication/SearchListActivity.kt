package com.example.clubapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clubapplication.databinding.ActivityListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

/**
 * 검색된 동아리 리스트
 */
class SearchListActivity : AppCompatActivity() {
    lateinit var binding: ActivityListBinding
    lateinit var adapter: RecruitAdapter
    private lateinit var database: DatabaseReference
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

        //제목 검색으로 변경
        binding.listTitleText.text = "검색"

        //어떤 검색 결과인지 보이게
        binding.searchResultText.visibility = View.VISIBLE

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }
    }

    //동아리 data 초기화
    private fun initData() {
        database = Firebase.database.reference

        var clubName = intent.getStringExtra("clubName").toString()
        var bound = intent.getStringExtra("bound").toString()
        var type = intent.getStringExtra("type").toString()

        database.child("club")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    clubData.clear()

                    for (postSnapshot in snapshot.children) {
                        val club = postSnapshot.getValue(Club::class.java)
                        if(club!!.state == "ACTIVE") {
                            if(clubName != "null") {
                                if(club.title.contains(clubName)) {
                                    clubData.add(
                                        Club(club!!.clubId, club.bound, club.limit, club.type, club.title,
                                            club.introduce, club.startYear, club.startMonth, club.startDay,
                                            club.endYear, club.endMonth, club.endDay, club.content, club.likeCount,
                                            club.memberCount, club.state, club.userId
                                        )
                                    )
                                }
                            }else if(bound != "null") {
                                if(club.bound == bound) {
                                    clubData.add(
                                        Club(club!!.clubId, club.bound, club.limit, club.type, club.title,
                                            club.introduce, club.startYear, club.startMonth, club.startDay,
                                            club.endYear, club.endMonth, club.endDay, club.content, club.likeCount,
                                            club.memberCount, club.state, club.userId
                                        )
                                    )
                                }
                            }else if(type != "null") {
                                if(club.type == type) {
                                    clubData.add(
                                        Club(club!!.clubId, club.bound, club.limit, club.type, club.title,
                                            club.introduce, club.startYear, club.startMonth, club.startDay,
                                            club.endYear, club.endMonth, club.endDay, club.content, club.likeCount,
                                            club.memberCount, club.state, club.userId
                                        )
                                    )
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        if(clubName != "null") {
            binding.searchResultText.text = "\"" + clubName + "\" 검색 결과"
        }else if(bound != "null") {
            binding.searchResultText.text = "\"" + bound + "\" 검색 결과"
        }else if(type != "null") {
            binding.searchResultText.text = "\"" + type + "\" 검색 결과"
        }
    }

    //RecyclerView 초기화
    private fun initRecyclerView() {
        binding.RecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = RecruitAdapter(clubData)
        adapter.itemClickListener=object : RecruitAdapter.OnItemClickListener {
            override fun OnItemClick(data: Club, position: Int) {
                val intent = Intent(this@SearchListActivity, ClubInfoActivity::class.java)
                intent.putExtra("clubId", data.clubId.toString())
                startActivity(intent)
            }
        }
        binding.RecyclerView.adapter = adapter
    }
}