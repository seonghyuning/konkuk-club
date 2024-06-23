package com.example.clubapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.clubapplication.databinding.ActivityVerificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * 이메일 인증
 */
class VerificationActivity : AppCompatActivity() {
    lateinit var binding: ActivityVerificationBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        auth = Firebase.auth

        binding.emailText.setText(auth.currentUser!!.email)

        binding.sendEmail.setOnClickListener {
            auth.currentUser!!.reload()

            //이메일 인증 완료
            if(auth.currentUser!!.isEmailVerified){
                Toast.makeText(this, "이메일 인증이 완료되었습니다.", Toast.LENGTH_LONG).show()

                val loginActivity = LoginActivity.loginActivity
                loginActivity!!.finish()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            }else {
                //이메일 인증 안됨
                Toast.makeText(this, "이메일 인증이 완료되지 않았습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }
}