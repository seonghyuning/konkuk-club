package com.example.clubapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.clubapplication.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * 로그인
 */
class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    companion object{
        var loginActivity : LoginActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginActivity = this
        initLayout()
    }

    private fun initLayout() {
        auth = Firebase.auth

        //자동로그인(이전에 로그인 한적이 있으면)
        if(auth.currentUser?.uid != null && auth.currentUser!!.email!!.contains("@test.ac.kr")){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else if(auth.currentUser?.uid != null && auth.currentUser!!.isEmailVerified){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //로그인 버튼 선택
        binding.loginBtn.setOnClickListener {
            val email = binding.emailEdit.text.toString()
            val password = binding.passwdEdit.text.toString()

            if(email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(!email.contains('@') || !email.contains('.')) {
                Toast.makeText(this, "이메일 형식이 맞지 않습니다.", Toast.LENGTH_SHORT).show()
            }else if(!(email.contains("@konkuk.ac.kr") || email.contains("@test.ac.kr"))) {
                Toast.makeText(this, "건국대학교 이메일을 사용해주세요.", Toast.LENGTH_SHORT).show()
            }else if(password.isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(password.length < 6){
                Toast.makeText(this, "비밀번호는 6자 이상입니다.", Toast.LENGTH_SHORT).show()
            }else {
                login(email, password)
            }
        }

        //비밀번호 재설정 선택
        binding.resetPasswd.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
            clearInput()
        }

        //회원가입 선택
        binding.signup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            clearInput()
        }
    }

    //로그인 기능
    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    //로그인 성공
                    if(auth.currentUser!!.email!!.contains("@test.ac.kr")) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else {
                        verification()
                    }

                } else {
                    //로그인 실패
                    Toast.makeText(this, "이메일 또는 비밀번호가 잘못됐습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //이메일 인증
    private fun verification() {
        //이메일 인증 된 계정
        if(auth.currentUser!!.isEmailVerified){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }else {
            //이메일 인증 안된 계정
            auth.currentUser!!.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "확인메일을 보냈습니다", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "이미 확인메일을 보냈습니다.", Toast.LENGTH_LONG).show()
                }
            }

            val intent = Intent(this, VerificationActivity::class.java)
            startActivity(intent)
        }
    }

    //로그인 editText 비우기
    private fun clearInput() {
        binding.apply {
            emailEdit.text.clear()
            passwdEdit.text.clear()
        }
    }
}