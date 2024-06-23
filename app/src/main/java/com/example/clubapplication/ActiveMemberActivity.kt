package com.example.clubapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clubapplication.databinding.ActivityManagerBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * 활동중인 회원 관리
 */
class ActiveMemberActivity : AppCompatActivity() {
    lateinit var binding: ActivityManagerBinding
    private lateinit var database: DatabaseReference
    lateinit var adapter: ClubLogAdapter
    private lateinit var clubId:String  //현재 동아리의 clubId
    private var clubLogData:ArrayList<ClubLog> = ArrayList()    //현재 동아리의 활동중인 회원들
    private var updateMap = HashMap<String, Any>()  //data를 update 하기 위한 hashmap
    private var searchedClubData:ArrayList<ClubLog> = ArrayList()   //검색된 동아리 data
    private var memberCount = 0     //동아리 회원 수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initRecyclerView()
        initLayout()
    }

    private fun initLayout() {

        binding.manageTitle.text = "동아리 회원 관리"
        binding.clubStateText.text = "활동중인 회원"
        binding.searchEdit.hint = "회원 이름"
        binding.moreSpinner.visibility = View.GONE  //더보기 spinner는 안보이게

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
        clubId = intent.getStringExtra("clubId").toString()

        database.child("clubLog")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    clubLogData.clear()

                    for (postSnapshot in snapshot.children) {
                        val clubLog = postSnapshot.getValue(ClubLog::class.java)
                        //현재 동아리의 활동중인 회원들의 data만 가져오기
                        if(clubLog!!.clubId == clubId && clubLog.state == "ACTIVE") {
                            clubLogData.add(ClubLog(clubLog.name, clubLog.college, clubLog.major, clubLog.number,
                                clubLog.state, clubLog.clubId, clubLog.userId, clubLog.clubLogId))
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        //현재 동아리의 활동중인 회원들의 수 가져오기
        database.child("club").child(clubId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val club = snapshot.getValue(Club::class.java)
                    memberCount = club!!.memberCount
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
                //운영진 임명 or 탈퇴 dialog
                memberDialog(data.clubLogId)
            }
        }
        binding.RecyclerView.adapter = adapter
    }

    //검색된 동아리의 RecyclerView
    private fun searchedRecyclerView() {
        binding.RecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter= ClubLogAdapter(clubLogData)
        adapter.itemClickListener=object : ClubLogAdapter.OnItemClickListener {
            override fun OnItemClick(data: ClubLog, position: Int) {
                //운영진 임명 or 탈퇴 dialog
                memberDialog(data.clubLogId)
            }

        }
        binding.RecyclerView.adapter = adapter
    }

    //동아리 회원 운영진 임명 or 탈퇴 dialog
    private fun memberDialog(clubLogId: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_manage_member, null)
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setView(dialogView)

        val manageMemberText1 = dialogView.findViewById<TextView>(R.id.manageMemberText1)
        val manageMemberText2 = dialogView.findViewById<TextView>(R.id.manageMemberText2)
        val approveBtn = dialogView.findViewById<TextView>(R.id.memberBtn1)
        val deleteBtn = dialogView.findViewById<TextView>(R.id.memberBtn2)

        manageMemberText1.text = "운영진 임명을 선택하시면"
        manageMemberText2.text = "회원이 운영진으로 활동합니다."
        approveBtn.text = "운영진 임명"
        deleteBtn.text = "탈퇴"

        //운영진 임명 버튼 선택
        approveBtn.setOnClickListener {
            updateMap.put("state", "MANAGER")
            database.child("clubLog").child(clubLogId).updateChildren(updateMap)
            updateMap.clear()

            Toast.makeText(this, "해당 회원이 운영진으로 임명되었습니다.", Toast.LENGTH_SHORT).show()
            alertDialog.cancel()
        }

        //탈퇴 버튼 선택
        deleteBtn.setOnClickListener {
            database.child("clubLog").child(clubLogId).removeValue()

            //현재 동아리의 회원수 -1
            memberCount--
            updateMap.put("memberCount", memberCount)
            database.child("club").child(clubId).updateChildren(updateMap)
            updateMap.clear()

            Toast.makeText(this, "해당 회원이 탈퇴되었습니다.", Toast.LENGTH_SHORT).show()
            alertDialog.cancel()
        }

        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.edit_background)
    }
}