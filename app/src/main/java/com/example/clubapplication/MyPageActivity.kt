package com.example.clubapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.clubapplication.databinding.ActivityMyPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * 마이페이지
 */
class MyPageActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyPageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    companion object{
        var myPageActivity : MyPageActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPageActivity = this
        initLayout()
    }

    private fun initLayout() {
        auth = Firebase.auth
        database = Firebase.database.reference

        //마이페이지 회원정보 넣기
        initMyInfo()

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }

        //프로필 수정 버튼
        binding.modifyBtn.setOnClickListener {
            val intent = Intent(this, ProfileModifyActivity::class.java)
            startActivity(intent)
        }

        //로그아웃 버튼
        binding.logoutBtn.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "로그아웃 되었습니다!", Toast.LENGTH_SHORT).show()

            val mainActivity = MainActivity.mainActivity
            mainActivity!!.finish()

            //로그인 페이지로 이동
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //탈퇴 버튼
        binding.deleteBtn.setOnClickListener {
            val intent = Intent(this, DeleteUserActivity::class.java)
            startActivity(intent)
        }
    }

    //회원정보 넣기
    private fun initMyInfo() {
        database.child("user").child(auth.currentUser!!.uid)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    binding.emailText.setText(user!!.email)
                    binding.nameText.setText(user.name)
                    binding.collegeText.setText(user.college)
                    binding.majorText.setText(user.major)
                    binding.numberText.setText(user.number)
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}