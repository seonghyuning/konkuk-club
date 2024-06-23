package com.example.clubapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.clubapplication.databinding.ActivityDeleteUserBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * 탈퇴
 */
class DeleteUserActivity : AppCompatActivity() {
    lateinit var binding: ActivityDeleteUserBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        auth = Firebase.auth
        database = Firebase.database.reference

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }

        //탈퇴 버튼
        binding.deleteBtn.setOnClickListener {
            val email = binding.emailEdit.text.toString()
            val password = binding.passwdEdit.text.toString()

            if(email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(!email.contains('@') || !email.contains('.')) {
                Toast.makeText(this, "이메일 형식이 맞지 않습니다.", Toast.LENGTH_SHORT).show()
            }else if(!email.contains("@konkuk.ac.kr")) {
                Toast.makeText(this, "건국대학교 이메일을 사용해주세요.", Toast.LENGTH_SHORT).show()
            }else if(password.isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(password.length < 6) {
                Toast.makeText(this, "비밀번호는 6자 이상입니다.", Toast.LENGTH_SHORT).show()
            }else if(email == auth.currentUser!!.email) {
                reAuthenticate(email, password)
                deleteDialog()
            } else {
                Toast.makeText(this, "현재 로그인한 이메일과 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    //진짜 탈퇴할지 dialog
    private fun deleteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete, null)
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setView(dialogView)

        val delete = dialogView.findViewById<TextView>(R.id.deleteText)
        val cancel = dialogView.findViewById<TextView>(R.id.cancelText)

        delete.setOnClickListener {
            auth.currentUser!!.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        database.child(auth.currentUser!!.uid).removeValue()    //Realtime DB의 user정보 삭제
                        Toast.makeText(this, "탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()

                        val mainActivity = MainActivity.mainActivity
                        mainActivity!!.finish()

                        val myPageActivity = MyPageActivity.myPageActivity
                        myPageActivity!!.finish()

                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else
                        Toast.makeText(this, "탈퇴가 실패했습니다. 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                }
        }

        cancel.setOnClickListener {
            alertDialog.cancel()
        }

        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.edit_background)
    }

    //회원정보 재인증
    private fun reAuthenticate(email: String, password: String) {
        val credential = EmailAuthProvider
            .getCredential(email, password)

        auth.currentUser!!.reauthenticate(credential).addOnCompleteListener {}
    }
}