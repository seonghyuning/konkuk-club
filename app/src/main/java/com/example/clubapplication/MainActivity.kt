package com.example.clubapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clubapplication.databinding.ActivityMainBinding
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
 * 메인
 */
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    lateinit var rankAdapter: RankAdapter
    lateinit var recruitAdapter: RecruitAdapter
    val managerId = "pS4EsvmAP6hJ0uGPHifnvm9dEJy2"  //관리자 Id
    private var clubRankData: ArrayList<Club> = ArrayList()
    private var clubRecruitData: ArrayList<Club> = ArrayList()
    private var likeClubIds: ArrayList<String> = ArrayList()
    private var myClubIds: ArrayList<String> = ArrayList()


    companion object{
        var mainActivity : MainActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainActivity = this
        initClubData()
        initRankRecyclerView()
        initRecruitRecyclerView()
        initLayout()
    }

    private fun initLayout() {
        auth = Firebase.auth

        //관리자일 경우 관리자 페이지 보이게
        if(auth.currentUser!!.uid == managerId) {
            binding.managerText.visibility = View.VISIBLE
        }

        //관리자 버튼
        binding.managerText.setOnClickListener {
            val intent = Intent(this, ApprovalManagerActivity::class.java)
            startActivity(intent)
        }

        //내동아리
        binding.myClubImage.setOnClickListener {
            val intent = Intent(this, MyClubListActivity::class.java)
            intent.putExtra("myClubIds", myClubIds)
            startActivity(intent)
        }

        //검색 버튼
        binding.searchImage.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        //좋아요 버튼
        binding.likeImage.setOnClickListener {
            val intent = Intent(this, LikeListActivity::class.java)
            intent.putExtra("likeClubIds", likeClubIds)
            startActivity(intent)
        }

        //동아리개설
        binding.makingClubImage.setOnClickListener {
            val intent = Intent(this, MakeClubActivity::class.java)
            startActivity(intent)
        }

        //동아리관리
        binding.clubManagerImage.setOnClickListener {
            val intent = Intent(this, ClubManagerListActivity::class.java)
            startActivity(intent)
        }

        //마이페이지
        binding.myPageImage.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
    }

    //동아리 data 초기화
    private fun initClubData() {
        database = Firebase.database.reference

        database.child("club")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    clubRankData.clear()
                    clubRecruitData.clear()
                    var rankCount = 0

                    for (postSnapshot in snapshot.children) {
                        val club = postSnapshot.getValue(Club::class.java)
                        if(club!!.state == "ACTIVE") {

                            //동아리 순위 data
                            if(rankCount < 100) {
                                clubRankData.add(
                                    Club(club!!.clubId, club.bound, club.limit, club.type, club.title, club.introduce,
                                        club.startYear, club.startMonth, club.startDay, club.endYear, club.endMonth,
                                        club.endDay, club.content, club.likeCount, club.memberCount, club.state, club.userId))
                            }
                            rankCount++

                            //모집중인 동아리 data
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

                            //모집 기간인 동아리 데이터만 가져오기
                            if(calcuStartDate >= 0 && calcuEndDate >= 0) {
                                clubRecruitData.add(Club(club!!.clubId, club.bound, club.limit, club.type, club.title, club.introduce,
                                        club.startYear, club.startMonth, club.startDay, club.endYear, club.endMonth,
                                        club.endDay, club.content, club.likeCount, club.memberCount, club.state, club.userId))
                            }
                        }
                    }

                    clubRankData.sortWith(compareByDescending(Club::likeCount).thenByDescending(Club::memberCount).thenBy(Club::clubId))

                    rankAdapter.notifyDataSetChanged()
                    recruitAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        //현재 user가 좋아요한 clubId 가져오기
        database.child("like")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    likeClubIds.clear()

                    for (postSnapshot in snapshot.children) {
                        val like = postSnapshot.getValue(Like::class.java)
                        if(like!!.userId == auth.currentUser!!.uid) {
                            likeClubIds.add(like.clubId)
                        }
                    }
//                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        //현재 user가 가입하고 승인받은 clubLog 가져오기
        database.child("clubLog")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    myClubIds.clear()

                    for (postSnapshot in snapshot.children) {
                        val clubLog = postSnapshot.getValue(ClubLog::class.java)
                        if(clubLog!!.userId == auth.currentUser!!.uid) {
                            if (clubLog.state == "ACTIVE" || clubLog.state == "MANAGER") {
                                myClubIds.add(clubLog.clubId)
                            }
                        }
                    }
//                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    //순위 RecyclerView
    private fun initRankRecyclerView() {
        binding.rankRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rankAdapter = RankAdapter(clubRankData)
        rankAdapter.itemClickListener=object : RankAdapter.OnItemClickListener {
            override fun OnItemClick(data: Club, position: Int) {
                val intent = Intent(this@MainActivity, ClubInfoActivity::class.java)
                intent.putExtra("clubId", data.clubId.toString())
                startActivity(intent)
            }
        }
        binding.rankRecyclerView.adapter = rankAdapter
    }

    //모집 RecyclerView
    private fun initRecruitRecyclerView() {
        binding.recruitRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recruitAdapter = RecruitAdapter(clubRecruitData)
        recruitAdapter.itemClickListener=object : RecruitAdapter.OnItemClickListener {
            override fun OnItemClick(data: Club, position: Int) {
                val intent = Intent(this@MainActivity, ClubInfoActivity::class.java)
                intent.putExtra("clubId", data.clubId.toString())
                startActivity(intent)
            }
        }
        binding.recruitRecyclerView.adapter = recruitAdapter
    }
}