package com.example.clubapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.clubapplication.databinding.ActivityMakeClubBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * 동아리 개설
 */
class MakeClubActivity : AppCompatActivity() {
    lateinit var binding: ActivityMakeClubBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var titles:ArrayList<String> = ArrayList()
    private var userMap = HashMap<String, String>()
    private var lastClubId = 0

    val collegeData = listOf("KU융합과학기술원", "건축대학", "경영대학", "공과대학", "국제대학",
                                        "문과대학", "부동산과학원", "사범대학", "사회과학대학", "상허생명과학대학",
                                        "수의과대학", "예술디자인대학", "이과대학")
    val majorData = listOf("K뷰티산업융합학과", "건축학부", "경영학과", "경제학과", "교육공학과",
                                        "교직과", "국어국문학과", "국제무역학과", "국제통상학과", "글로벌비즈니스학과",
                                        "기계항공공학부", "기술경영학과", "동물자원과학과", "리빙디자인학과",
                                        "매체연기학과", "문화미디어학과", "문화콘텐츠학과", "물리학과",
                                        "미디어커뮤니케이션학과", "미래에너지공학과", "부동산학과", "사학과",
                                        "사회환경공학부", "산림조경학과", "산업공학과", "신산업융합학과", "산업디자인학과",
                                        "생명과학특성학과", "생물공학과", "수의예과", "수의학과", "수학과", "수학교육과",
                                        "스마트운행체공학과", "스마트ICT융합공학과", "시스템생명공학과", "식량자원과학과",
                                        "식품유통공학과", "영상학과", "영어교육과", "영어영문학과", "융합생명공학과",
                                        "융합인재학과", "음악교육과", "응용통계학과", "의상디자인학과", "의생명공학과",
                                        "일어교육과", "전기전자공학부", "정치외교학과", "줄기세포재생공학과",
                                        "중어중문학과", "지리학과", "철학과", "체육교육과", "축산식품생명공학과",
                                        "커뮤니케이션디자인학과", "컴퓨터공학부", "행정학과", "현대미술학과",
                                        "화장품공학과", "화학공학부", "화학과", "환경보건과학과")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeClubBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        auth = Firebase.auth
        database = Firebase.database.reference

        initData()

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }

        //범위에 따라 제한 Spinner 추가
        binding.boundSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedBound = binding.boundSpinner.selectedItem.toString()
                addLimitSpinner(selectedBound)


            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        //동아리 개설 신청 버튼
        binding.makeClubBtn.setOnClickListener {
            val bound = binding.boundSpinner.selectedItem.toString()
            val limit = binding.limitSpinner.selectedItem.toString()
            val type = binding.typeSpinner.selectedItem.toString()
            val title = binding.titleEdit.text.toString()
            val introduce = binding.introEdit.text.toString()

            val startYear = binding.startYearSpinner.selectedItem.toString().toInt()
            val startMonth = binding.startMonthSpinner.selectedItem.toString().toInt()
            val startDay = binding.startDaySpinner.selectedItem.toString().toInt()
            val endYear = binding.endYearSpinner.selectedItem.toString().toInt()
            val endMonth = binding.endMonthSpinner.selectedItem.toString().toInt()
            val endDay = binding.endDaySpinner.selectedItem.toString().toInt()

            val content = binding.contentTextMulti.text.toString()
            val likeCount = 0;
            val memberCount = 1;
            val state = "WAIT"
            val userId = auth.currentUser!!.uid

            if(title == "") {
                Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(titles.contains(title)) {
                Toast.makeText(this, "이미 존재하는 동아리 이름입니다. 다른 이름으로 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(startYear > endYear) {
                Toast.makeText(this, "모집기간을 잘못 선택했습니다.", Toast.LENGTH_SHORT).show()
            }else if(startYear == endYear && startMonth > endMonth) {
                Toast.makeText(this, "모집기간을 잘못 선택했습니다.", Toast.LENGTH_SHORT).show()
            }else if(startYear == endYear && startMonth == endMonth && startDay > endDay) {
                Toast.makeText(this, "모집기간을 잘못 선택했습니다.", Toast.LENGTH_SHORT).show()
            }else {

                val clubId = lastClubId + 1

                database.child("club").child(clubId.toString()).setValue(Club(clubId, bound, limit, type, title,
                    introduce, startYear, startMonth, startDay, endYear, endMonth, endDay, content,
                    likeCount, memberCount, state, userId))

                val clubLogId = database.child("clubLog").push().key.toString()
                database.child("clubLog").child(clubLogId).setValue(ClubLog(userMap.get("name")!!,
                    userMap.get("college")!!, userMap.get("major")!!, userMap.get("number")!!, "MANAGER",
                    clubId.toString(), auth.currentUser!!.uid, clubLogId))

                Toast.makeText(this, "동아리 개설 신청이 완료되었습니다!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    //동아리 이름 중복 방지를 위한 제목들 초기화
    private fun initData() {
        database.child("club")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postSnapshot in snapshot.children){
                        val club = postSnapshot.getValue(Club::class.java)
                        titles.add(club!!.title)
                        lastClubId = club.clubId
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

        database.child("user").child(auth.currentUser!!.uid)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    userMap.put("name", user!!.name)
                    userMap.put("college", user.college)
                    userMap.put("major", user.major)
                    userMap.put("number", user.number)
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    //동아리 가입 제한 spinner 추가
    private fun addLimitSpinner(selectedBound: String) {
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            ArrayList<String>())

        if(selectedBound == "단과대 동아리") {
            adapter.addAll(collegeData)
        } else if(selectedBound == "과 동아리") {
            adapter.addAll(majorData)
        } else {
            adapter.add("제한없음")
        }

        binding.limitSpinner.adapter = adapter
    }
}