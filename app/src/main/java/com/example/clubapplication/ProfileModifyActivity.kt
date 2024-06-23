package com.example.clubapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.example.clubapplication.databinding.ActivityProfileModifyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * 프로필 수정
 */
class ProfileModifyActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileModifyBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var updateMap = HashMap<String, Any>()
    private var majorToCollegeMap = hashMapOf("국어국문학과" to "문과대학", "영어영문학과" to "문과대학", "중어중문학과" to "문과대학",
        "철학과" to "문과대학", "사학과" to "문과대학", "지리학과" to "문과대학", "미디어커뮤니케이션학과" to "문과대학", "문화콘텐츠학과" to "문과대학",
        "수학과" to "이과대학", "화학과" to "이과대학", "물리학과" to "이과대학",
        "건축학부" to "건축대학",
        "사회환경공학부" to "공과대학", "기계항공공학부" to "공과대학", "전기전자공학부" to "공과대학", "화학공학부" to "공과대학",
        "컴퓨터공학부" to "공과대학", "산업공학과" to "공과대학", "신산업융합학과" to "공과대학", "생물공학과" to "공과대학", "K뷰티산업융합학과" to "공과대학",
        "정치외교학과" to "사회과학대학", "경제학과" to "사회과학대학", "행정학과" to "사회과학대학", "국제무역학과" to "사회과학대학",
        "응용통계학과" to "사회과학대학", "융합인재학과" to "사회과학대학", "글로벌비즈니스학과" to "사회과학대학",
        "경영학과" to "경영대학", "기술경영학과" to "경영대학",
        "부동산학과" to "부동산과학원",
        "미래에너지공학과" to "KU융합과학기술원", "스마트운행체공학과" to "KU융합과학기술원", "스마트ICT융합공학과" to "KU융합과학기술원", "화장품공학과" to "KU융합과학기술원",
        "줄기세포재생공학과" to "KU융합과학기술원", "의생명공학과" to "KU융합과학기술원", "시스템생명공학과" to "KU융합과학기술원", "융합생명공학과" to "KU융합과학기술원",
        "생명과학특성학과" to "상허생명과학대학", "동물자원과학과" to "상허생명과학대학", "식량자원과학과" to "상허생명과학대학", "축산식품생명공학과" to "상허생명과학대학",
        "식품유통공학과" to "상허생명과학대학", "환경보건과학과" to "상허생명과학대학", "산림조경학과" to "상허생명과학대학",
        "수의예과" to "수의과대학", "수의학과" to "수의과대학",
        "커뮤니케이션디자인학과" to "예술디자인대학", "산업디자인학과" to "예술디자인대학", "의상디자인학과" to "예술디자인대학", "리빙디자인학과" to "예술디자인대학",
        "현대미술학과" to "예술디자인대학", "영상학과" to "예술디자인대학", "매체연기학과" to "예술디자인대학",
        "일어교육과" to "사범대학", "수학교육과" to "사범대학", "체육교육과" to "사범대학", "음악교육과" to "사범대학",
        "교육공학과" to "사범대학", "영어교육과" to "사범대학", "교직과" to "사범대학",
        "국제통상학과" to "국제대학", "문화미디어학과" to "국제대학")

    private var majorHashMap = hashMapOf(
        "K뷰티산업융합학과" to 0,
        "건축학부" to 1,
        "경영학과" to 2,
        "경제학과" to 3,
        "교육공학과" to 4,
        "교직과" to 5,
        "국어국문학과" to 6,
        "국제무역학과" to 7,
        "국제통상학과" to 8,
        "글로벌비즈니스학과" to 9,
        "기계항공공학부" to 10,
        "기술경영학과" to 11,
        "동물자원과학과" to 12,
        "리빙디자인학과" to 13,
        "매체연기학과" to 14,
        "문화미디어학과" to 15,
        "문화콘텐츠학과" to 16,
        "물리학과" to 17,
        "미디어커뮤니케이션학과" to 18,
        "미래에너지공학과" to 19,
        "부동산학과" to 20,
        "사학과" to 21,
        "사회환경공학부" to 22,
        "산림조경학과" to 23,
        "산업공학과" to 24,
        "신산업융합학과" to 25,
        "산업디자인학과" to 26,
        "생명과학특성학과" to 27,
        "생물공학과" to 28,
        "수의예과" to 29,
        "수의학과" to 30,
        "수학과" to 31,
        "수학교육과" to 32,
        "스마트운행체공학과" to 33,
        "스마트ICT융합공학과" to 34,
        "시스템생명공학과" to 35,
        "식량자원과학과" to 36,
        "식품유통공학과" to 37,
        "영상학과" to 38,
        "영어교육과" to 39,
        "영어영문학과" to 40,
        "융합생명공학과" to 41,
        "융합인재학과" to 42,
        "음악교육과" to 43,
        "응용통계학과" to 44,
        "의상디자인학과" to 45,
        "의생명공학과" to 46,
        "일어교육과" to 47,
        "전기전자공학부" to 48,
        "정치외교학과" to 49,
        "줄기세포재생공학과" to 50,
        "중어중문학과" to 51,
        "지리학과" to 52,
        "철학과" to 53,
        "체육교육과" to 54,
        "축산식품생명공학과" to 55,
        "커뮤니케이션디자인학과" to 56,
        "컴퓨터공학부" to 57,
        "행정학과" to 58,
        "현대미술학과" to 59,
        "화장품공학과" to 60,
        "화학공학부" to 61,
        "화학과" to 62,
        "환경보건과학과" to 63
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initLayout()
    }

    private fun initLayout() {

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }

        //전공 spinner에서 전공을 선택했을때
        binding.majorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMajor = binding.majorSpinner.selectedItem.toString()
                binding.collegeText.text = majorToCollegeMap.get(selectedMajor).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        //수정 버튼
        binding.modifyBtn.setOnClickListener {
            val email = binding.emailText.text.toString()
            val name = binding.nameEdit.text.toString()
            val college = binding.collegeText.text.toString()
            val major = binding.majorSpinner.selectedItem.toString()
            val number = binding.numberText.text.toString()

            database.child("user").child(auth.currentUser!!.uid).setValue(User(email, name, college, major, number))
            modifyMyInfoOfClubLog(name, college, major, number)
            Toast.makeText(this, "프로필 수정이 완료되었습니다!", Toast.LENGTH_SHORT).show()

            finish()
        }
    }

    //회원정보 넣기
    private fun initData() {
        auth = Firebase.auth
        database = Firebase.database.reference

        database.child("user").child(auth.currentUser!!.uid)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    binding.emailText.setText(user!!.email)
                    binding.nameEdit.setText(user.name)
                    binding.collegeText.text = user.college
                    binding.majorSpinner.setSelection(majorHashMap.get(user.major)!!)
                    binding.numberText.setText(user.number)
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun modifyMyInfoOfClubLog(name: String, college: String, major: String, number: String) {

        database.child("clubLog")
            .get().addOnSuccessListener{
                for (postSnapshot in it.children) {
                    val clubLog = postSnapshot.getValue(ClubLog::class.java)
                    if(clubLog!!.userId == auth.currentUser!!.uid) {
                        updateMap.put("name", name)
                        updateMap.put("college", college)
                        updateMap.put("major", major)
                        updateMap.put("number", number)
                        database.child("clubLog").child(clubLog.clubLogId).updateChildren(updateMap)
                        updateMap.clear()
                    }
                }
            }.addOnFailureListener{
            }
    }
}