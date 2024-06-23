package com.example.clubapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.clubapplication.databinding.ActivityModifyClubBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * 동아리 수정
 */
class ModifyClubActivity : AppCompatActivity() {
    lateinit var binding: ActivityModifyClubBinding
    private lateinit var database: DatabaseReference
    private lateinit var clubId: String
    private val subYear = 2024
    private var updateMap = HashMap<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyClubBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initLayout()
    }

    private fun initLayout() {

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }

        //동아리 수정 버튼
        binding.modifyClubBtn.setOnClickListener {

            val introduce = binding.introEdit.text.toString()
            val startYear = binding.startYearSpinner.selectedItem.toString().toInt()
            val startMonth = binding.startMonthSpinner.selectedItem.toString().toInt()
            val startDay = binding.startDaySpinner.selectedItem.toString().toInt()
            val endYear = binding.endYearSpinner.selectedItem.toString().toInt()
            val endMonth = binding.endMonthSpinner.selectedItem.toString().toInt()
            val endDay = binding.endDaySpinner.selectedItem.toString().toInt()
            val content = binding.contentTextMulti.text.toString()

            if(startYear > endYear) {
                Toast.makeText(this, "모집기간을 잘못 선택했습니다.", Toast.LENGTH_SHORT).show()
            }else if(startYear == endYear && startMonth > endMonth) {
                Toast.makeText(this, "모집기간을 잘못 선택했습니다.", Toast.LENGTH_SHORT).show()
            }else if(startYear == endYear && startMonth == endMonth && startDay > endDay) {
                Toast.makeText(this, "모집기간을 잘못 선택했습니다.", Toast.LENGTH_SHORT).show()
            }else {
                updateMap.put("introduce", introduce)
                updateMap.put("startYear", startYear)
                updateMap.put("startMonth", startMonth)
                updateMap.put("startDay", startDay)
                updateMap.put("endYear", endYear)
                updateMap.put("endMonth", endMonth)
                updateMap.put("endDay", endDay)
                updateMap.put("content", content)

                database.child("club").child(clubId).updateChildren(updateMap)

                updateMap.clear()

                Toast.makeText(this, "동아리 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()

                finish()
            }
        }
    }

    private fun initData() {
        database = Firebase.database.reference
        clubId = intent.getStringExtra("clubId").toString()

        database.child("club").child(clubId)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val club = snapshot.getValue(Club::class.java)
                    binding.clubBoundText.text = club!!.bound
                    binding.clubLimitText.text = club.limit
                    binding.clubTypeText.text = club.type
                    binding.clubNametext.text = club.title
                    binding.introEdit.setText(club.introduce)

                    binding.startYearSpinner.setSelection(club.startYear-subYear)
                    binding.startMonthSpinner.setSelection(club.startMonth-1)
                    binding.startDaySpinner.setSelection(club.startDay-1)

                    binding.endYearSpinner.setSelection(club.endYear-subYear)
                    binding.endMonthSpinner.setSelection(club.endMonth-1)
                    binding.endDaySpinner.setSelection(club.endDay-1)

                    binding.contentTextMulti.setText(club.content)
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}