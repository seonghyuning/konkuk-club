package com.example.clubapplication

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clubapplication.databinding.ActivityManagerBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * 비활성화 동아리 관리
 */
class InactiveManagerActivity : AppCompatActivity() {
    lateinit var binding: ActivityManagerBinding
    private lateinit var database: DatabaseReference
    lateinit var adapter: ManagerAdapter
    private var clubData:ArrayList<Club> = ArrayList()
    private var searchedClubData:ArrayList<Club> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        addSpinner()
        initLayout()
        initRecyclerView()
    }

    private fun initLayout() {

        binding.clubStateText.text = "비활성화 동아리"

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }

        //더보기 선택
        binding.moreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedManager = binding.moreSpinner.selectedItem.toString()

                if(selectedManager == "승인대기") {
                    val intent = Intent(this@InactiveManagerActivity, ApprovalManagerActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }else if(selectedManager == "활동중") {
                    val intent = Intent(this@InactiveManagerActivity, ActiveManagerActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }else if(selectedManager == "신고") {
                    val intent = Intent(this@InactiveManagerActivity, ReportManagerActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        //동아리 검색 선택
        binding.searchBtn.setOnClickListener {
            searchedClubData.clear()

            val searchedClubName = binding.searchEdit.text.toString()

            for(c in clubData) {
                if(c.title.contains(searchedClubName)) {
                    searchedClubData.add(c)
                }
            }

            searchedRecyclerView()
        }
    }

    //더보기 spinner 추가
    private fun addSpinner() {
        val spinnerAdapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_dropdown_item,
            ArrayList<String>())

        spinnerAdapter.add("선택")
        spinnerAdapter.add("승인대기")
        spinnerAdapter.add("활동중")
        spinnerAdapter.add("신고")

        binding.moreSpinner.adapter = spinnerAdapter
    }

    private fun initData() {
        database = Firebase.database.reference

        database.child("club")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    clubData.clear()

                    for (postSnapshot in snapshot.children) {
                        val club = postSnapshot.getValue(Club::class.java)
                        if(club!!.state == "INACTIVE") {
                            clubData.add(Club(club!!.clubId, club.bound, club.limit, club.type, club.title, club.introduce,
                                club.startYear, club.startMonth, club.startDay, club.endYear, club.endMonth,
                                club.endDay, club.content, club.likeCount, club.memberCount, club.state, club.userId))
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
        adapter= ManagerAdapter(clubData)
        adapter.itemClickListener=object : ManagerAdapter.OnItemClickListener {
            override fun OnItemClick(data: Club, position: Int) {
                val intent = Intent(this@InactiveManagerActivity, ClubInfoActivity::class.java)
                intent.putExtra("clubId", data.clubId.toString())
                startActivity(intent)
            }

        }
        binding.RecyclerView.adapter = adapter
    }

    private fun searchedRecyclerView() {
        binding.RecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter= ManagerAdapter(searchedClubData)
        adapter.itemClickListener=object : ManagerAdapter.OnItemClickListener {
            override fun OnItemClick(data: Club, position: Int) {
                val intent = Intent(this@InactiveManagerActivity, ClubInfoActivity::class.java)
                intent.putExtra("clubId", data.clubId.toString())
                startActivity(intent)
            }

        }
        binding.RecyclerView.adapter = adapter
    }
}