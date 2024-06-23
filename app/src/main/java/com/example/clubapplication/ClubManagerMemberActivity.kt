package com.example.clubapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clubapplication.databinding.ActivityManagerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * 운영진 관리
 */
class ClubManagerMemberActivity : AppCompatActivity() {
    lateinit var binding: ActivityManagerBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var clubId:String
    lateinit var adapter: ClubLogAdapter
    private var clubLogData:ArrayList<ClubLog> = ArrayList()
    private var updateMap = HashMap<String, Any>()
    private var searchedClubData:ArrayList<ClubLog> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initRecyclerView()
        initLayout()
    }

    private fun initLayout() {

        binding.manageTitle.text = "운영진 관리"
        binding.searchEdit.hint = "회원 이름"
        binding.clubStateText.visibility = View.GONE
        binding.moreSpinner.visibility = View.GONE

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }

        //회원 검색
        binding.searchBtn.setOnClickListener {
            searchedClubData.clear()

            val searchedClubName = binding.searchEdit.text.toString()

            for(m in clubLogData) {
                if(m.name.contains(searchedClubName)) {
                    searchedClubData.add(m)
                }
            }
            searchedRecyclerView()
        }

    }

    private fun initData() {
        database = Firebase.database.reference
        auth = Firebase.auth
        clubId = intent.getStringExtra("clubId").toString()

        database.child("clubLog")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    clubLogData.clear()

                    for (postSnapshot in snapshot.children) {
                        val clubLog = postSnapshot.getValue(ClubLog::class.java)
                        if(clubLog!!.clubId == clubId && clubLog.state == "MANAGER" && clubLog.userId != auth.currentUser!!.uid) {
                            clubLogData.add(ClubLog(clubLog.name, clubLog.college, clubLog.major, clubLog.number,
                                clubLog.state, clubLog.clubId, clubLog.userId, clubLog.clubLogId))
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
        adapter= ClubLogAdapter(clubLogData)
        adapter.itemClickListener=object : ClubLogAdapter.OnItemClickListener {
            override fun OnItemClick(data: ClubLog, position: Int) {
                memberDialog(data.clubLogId, data.userId)
            }
        }
        binding.RecyclerView.adapter = adapter
    }

    private fun searchedRecyclerView() {
        binding.RecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter= ClubLogAdapter(clubLogData)
        adapter.itemClickListener=object : ClubLogAdapter.OnItemClickListener {
            override fun OnItemClick(data: ClubLog, position: Int) {
                memberDialog(data.clubLogId, data.userId)
            }

        }
        binding.RecyclerView.adapter = adapter
    }

    //동아리 회원 운영진 임명, 탈퇴 dialog
    private fun memberDialog(clubLogId: String, userId: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_manage_member, null)
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setView(dialogView)

        val manageMemberText1 = dialogView.findViewById<TextView>(R.id.manageMemberText1)
        val manageMemberText2 = dialogView.findViewById<TextView>(R.id.manageMemberText2)
        val approveBtn = dialogView.findViewById<TextView>(R.id.memberBtn1)
        val deleteBtn = dialogView.findViewById<TextView>(R.id.memberBtn2)

        manageMemberText1.text = "면직을 선택하시면"
        manageMemberText2.text = "활동중인 회원으로 돌아갑니다."
        approveBtn.text = "동아리 회장 인계"
        deleteBtn.text = "면직"

        approveBtn.setOnClickListener {
            updateMap.put("userId", userId)
            database.child("club").child(clubId).updateChildren(updateMap)
            updateMap.clear()

            Toast.makeText(this, "해당 회원이 동아리 회장으로 임명되었습니다.", Toast.LENGTH_SHORT).show()

            alertDialog.cancel()

            val manageClubActivity = ManageClubActivity.manageClubActivity
            manageClubActivity!!.finish()

            finish()
        }

        deleteBtn.setOnClickListener {
            updateMap.put("state", "ACTIVE")
            database.child("clubLog").child(clubLogId).updateChildren(updateMap)
            updateMap.clear()
            Toast.makeText(this, "해당 회원이 운영진 면직되었습니다.", Toast.LENGTH_SHORT).show()
            alertDialog.cancel()
        }

        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.edit_background)
    }
}