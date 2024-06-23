package com.example.clubapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clubapplication.databinding.ActivityUsageHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * 회비 사용 내역
 */
class UsageHistoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityUsageHistoryBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    lateinit var adapter: UsageAdapter
    private var usageData: ArrayList<Usage> = ArrayList()
    private lateinit var clubId:String
    private lateinit var userId:String
    private lateinit var title:String
    private var rest = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsageHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initRecyclerView()
        initLayout()
    }

    private fun initLayout() {
        title = intent.getStringExtra("title").toString()

        // 제목 동아리 이름으로 바꾸기
        binding.clubNameText.text = title

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }

        //회비 사용 내역 추가
        binding.addImage.setOnClickListener {
            addUsageDialog()
        }
    }

    private fun initData() {
        clubId = intent.getStringExtra("clubId").toString()
        userId = intent.getStringExtra("userId").toString()

        database = Firebase.database.reference
        auth = Firebase.auth

        database.child("usageHistory").child(clubId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    usageData.clear()

                    for (postSnapshot in snapshot.children) {
                        val usage = postSnapshot.getValue(Usage::class.java)
                        usageData.add(Usage(usage!!.createdDate, usage.usageContent, usage.deposit,
                                            usage.withdraw, usage.rest))

                        rest = usage.rest

                        val dec = DecimalFormat("#,###")

                        binding.restText.text = dec.format(rest)
                    }

                    usageData.reverse()

                    adapter.notifyDataSetChanged()
//                    binding.RecyclerView.scrollToPosition(adapter.itemCount - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        if(userId == auth.currentUser!!.uid) {
            binding.addImage.visibility = View.VISIBLE
        }

        database.child("clubLog")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val clubLog = postSnapshot.getValue(ClubLog::class.java)
                        if(clubLog!!.clubId == clubId && clubLog.userId == auth.currentUser!!.uid && clubLog.state == "MANAGER") {
                            binding.addImage.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun initRecyclerView() {
        binding.RecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = UsageAdapter(usageData)
        binding.RecyclerView.adapter = adapter
    }

    private fun addUsageDialog() {
        //회비 사용 내역 추가 dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_usage, null)
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setView(dialogView)

        val complete = dialogView.findViewById<TextView>(R.id.completeText)
        val cancel = dialogView.findViewById<TextView>(R.id.cancelText)

        complete.setOnClickListener {
            val deposit = dialogView.findViewById<TextView>(R.id.depositEdit).text.toString()
            val withdraw = dialogView.findViewById<TextView>(R.id.withdrawEdit).text.toString()
            val content = dialogView.findViewById<TextView>(R.id.contentEdit).text.toString()
            val currentTime = SimpleDateFormat("YY/MM/dd", Locale.getDefault()).format(Date())

            if(deposit == "" && withdraw == "") {
                Toast.makeText(this, "입금 또는 출금을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(deposit != "" && withdraw != "") {
                Toast.makeText(this, "입금 또는 출금 중 하나만 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(content == "") {
                Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else {
                if(deposit != "") {
                    rest += deposit.toInt()
                    database.child("usageHistory").child(clubId).push().setValue(
                        Usage(currentTime, content, deposit.toInt(), 0, rest))
                    alertDialog.cancel()
                }else if(withdraw != "") {
                    if(rest < withdraw.toInt()) {
                        Toast.makeText(this, "돈이 부족합니다.", Toast.LENGTH_SHORT).show()
                    }else {
                        rest -= withdraw.toInt()
                        database.child("usageHistory").child(clubId).push().setValue(
                            Usage(currentTime, content, 0, withdraw.toInt(), rest)
                        )
                        alertDialog.cancel()
                    }
                }
            }
        }

        cancel.setOnClickListener {
            alertDialog.cancel()
        }

        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.edit_background)
    }
}