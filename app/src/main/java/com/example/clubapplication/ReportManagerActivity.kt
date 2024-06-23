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
 * 신고된 동아리 관리
 */
class ReportManagerActivity : AppCompatActivity() {
    lateinit var binding: ActivityManagerBinding
    private lateinit var database: DatabaseReference
    lateinit var adapter: ReportAdapter
    private var searchedReportData:ArrayList<Report> = ArrayList()
    private var reportData:ArrayList<Report> = ArrayList()

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

        binding.clubStateText.text = "신고된 동아리"

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }

        //더보기 선택
        binding.moreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedManager = binding.moreSpinner.selectedItem.toString()

                if(selectedManager == "승인대기") {
                    val intent = Intent(this@ReportManagerActivity, ApprovalManagerActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }else if(selectedManager == "활동중") {
                    val intent = Intent(this@ReportManagerActivity, ActiveManagerActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }else if(selectedManager == "비활성화") {
                    val intent = Intent(this@ReportManagerActivity, InactiveManagerActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        //동아리 검색
        binding.searchBtn.setOnClickListener {
            searchedReportData.clear()

            val searchedClubName = binding.searchEdit.text.toString()

            for(c in reportData) {
                if(c.clubName.contains(searchedClubName)) {
                    searchedReportData.add(c)
                }
            }

            searchedRecyclerView()
        }
    }

    private fun addSpinner() {
        val spinnerAdapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_dropdown_item,
            ArrayList<String>())

        spinnerAdapter.add("선택")
        spinnerAdapter.add("승인대기")
        spinnerAdapter.add("활동중")
        spinnerAdapter.add("비활성화")

        binding.moreSpinner.adapter = spinnerAdapter
    }

    private fun initData() {
        database = Firebase.database.reference

        database.child("report")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val report = postSnapshot.getValue(Report::class.java)
                        reportData.add(Report(report!!.clubName, report.reason, report.createdDate, report.clubId, report.userId))
                    }
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun initRecyclerView() {
        binding.RecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter= ReportAdapter(reportData)
        adapter.itemClickListener=object : ReportAdapter.OnItemClickListener {
            override fun OnItemClick(data: Report, position: Int) {
                val intent = Intent(this@ReportManagerActivity, ClubInfoActivity::class.java)
                intent.putExtra("clubId", data.clubId)
                startActivity(intent)
            }
        }
        binding.RecyclerView.adapter = adapter
    }

    private fun searchedRecyclerView() {
        binding.RecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter= ReportAdapter(searchedReportData)
        adapter.itemClickListener=object : ReportAdapter.OnItemClickListener {
            override fun OnItemClick(data: Report, position: Int) {
                val intent = Intent(this@ReportManagerActivity, ClubInfoActivity::class.java)
                intent.putExtra("clubId", data.clubId)
                startActivity(intent)
            }
        }
        binding.RecyclerView.adapter = adapter
    }
}