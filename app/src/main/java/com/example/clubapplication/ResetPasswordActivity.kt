package com.example.clubapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.clubapplication.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * 비밀번호 재설정
 */
class ResetPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        binding.resetPasswdBtn.setOnClickListener {
            val email = binding.emailEdit.text.toString()

            if(email.isEmpty()){
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(!email.contains('@') || !email.contains('.')) {
                Toast.makeText(this, "이메일을 형식이 맞지 않습니다.", Toast.LENGTH_SHORT).show()
            }else if(!email.contains("@konkuk.ac.kr")) {
                Toast.makeText(this, "건국대학교 이메일을 사용해주세요.", Toast.LENGTH_SHORT).show()
            }else {
                Firebase.auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //이메일 보내기 성공
                            Toast.makeText(this, "이메일을 보냈습니다.", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            //이메일 보내기 실패(가입되지 않은 이메일)
                            Toast.makeText(this, "가입되지 않은 이메일 입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}