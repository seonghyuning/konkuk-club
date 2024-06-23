package com.example.clubapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.example.clubapplication.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * 회원가입
 */
class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        auth = Firebase.auth
        database = Firebase.database.reference

        //전공 spinner에서 전공을 선택했을때
        binding.majorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMajor = binding.majorSpinner.selectedItem.toString()
                binding.collegeText.text = majorToCollegeMap.get(selectedMajor).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        //회원가입 버튼 클릭
        binding.signupBtn.setOnClickListener {
            var email = binding.emailEdit.text.toString()
            var password = binding.passwdEdit.text.toString()
            var passwdCheck = binding.passwdCheckEdit.text.toString()
            var name = binding.nameEdit.text.toString()
            var college = binding.collegeText.text.toString()
            var major = binding.majorSpinner.selectedItem.toString()
            var number = binding.numberEdit.text.toString()

            if(email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(!email.contains('@') || !email.contains('.')) {
                Toast.makeText(this, "이메일 형식이 맞지 않습니다.", Toast.LENGTH_SHORT).show()
            }else if(!(email.contains("@konkuk.ac.kr") || email.contains("@test.ac.kr"))) {
                Toast.makeText(this, "건국대학교 이메일을 사용해주세요.", Toast.LENGTH_SHORT).show()
            }else if(password.isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(password.length < 6) {
                Toast.makeText(this, "비밀번호는 6자 이상입니다.", Toast.LENGTH_SHORT).show()
            }else if(password != passwdCheck) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }else if(name.isEmpty()) {
                Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(number.isEmpty()) {
                Toast.makeText(this, "학번을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(number.length != 9) {
                Toast.makeText(this, "학번을 잘못 입력했습니다.", Toast.LENGTH_SHORT).show()
            }else {
                signUp(email, password, name, college, major, number)
            }
        }
    }

    //회원가입 기능
    private fun signUp(email: String, password: String, name: String, college: String, major: String, number: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //회원가입 성공
                    addUserToDatabase(auth.currentUser?.uid!!, email, name, college, major, number)
                    Toast.makeText(this, "회원가입을 완료했습니다!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    //회원가입 실패(이메일 중복 확인)
                    Toast.makeText(this, "이미 존재하는 이메일입니다!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //이용자 정보 DB에 저장
    private fun addUserToDatabase(
        userId: String,
        email: String,
        name: String,
        college: String,
        major: String,
        number: String
    ) {
        database.child("user").child(userId).setValue(User(email, name, college, major, number))
    }
}