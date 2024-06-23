package com.example.clubapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.clubapplication.databinding.ActivityManageClubBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat
import java.util.*

/**
 * 동아리 관리
 */
class ManageClubActivity : AppCompatActivity() {
    lateinit var binding: ActivityManageClubBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var clubId:String
    private lateinit var likeId: String
    private var likeCount = 0
    private var updateMap = HashMap<String, Any>()

    companion object{
        var manageClubActivity : ManageClubActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageClubBinding.inflate(layoutInflater)
        setContentView(binding.root)
        manageClubActivity = this
        initData()
        initLayout()
    }

    private fun initLayout() {

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }

        //동아리 입장
        binding.enterText.setOnClickListener {
            val intent = Intent(this, MyClubActivity::class.java)
            intent.putExtra("clubId", clubId)
            startActivity(intent)
        }

        //운영진 회의
        binding.clubManageChatText.setOnClickListener {
            val intent = Intent(this, ClubManagerChatActivity::class.java)
            intent.putExtra("clubId", clubId)
            startActivity(intent)
        }

        //1:1 문의 관리
        binding.manageInquiryText.setOnClickListener {
            val intent = Intent(this, ManageInquiryActivity::class.java)
            intent.putExtra("clubId", clubId)
            startActivity(intent)
        }

        //동아리 수정
        binding.modifyClubText.setOnClickListener {
            val intent = Intent(this, ModifyClubActivity::class.java)
            intent.putExtra("clubId", clubId)
            startActivity(intent)
        }

        //활동중인 회원
        binding.activeMemberText.setOnClickListener {
            val intent = Intent(this, ActiveMemberActivity::class.java)
            intent.putExtra("clubId", clubId)
            startActivity(intent)
        }

        //대기중인 회원
        binding.waitedMemberText.setOnClickListener {
            val intent = Intent(this, WaitedMemberActivity::class.java)
            intent.putExtra("clubId", clubId)
            startActivity(intent)
        }

        //운영진 관리
        binding.manageClubManagerText.setOnClickListener {
            val intent = Intent(this, ClubManagerMemberActivity::class.java)
            intent.putExtra("clubId", clubId)
            startActivity(intent)
        }

        //동아리 삭제
        binding.deleteClubText.setOnClickListener {
            deleteDialog()
        }

        //빈하트(좋아요) 버튼 클릭
        binding.likeImage.setOnClickListener {
            binding.likeImage.visibility = View.GONE
            binding.filledLikeImage.visibility = View.VISIBLE
            likeId = database.child("like").push().key!!
            database.child("like").child(likeId).setValue(Like(clubId, auth.currentUser!!.uid))
            likeCount++
            updateMap.put("likeCount", likeCount)
            database.child("club").child(clubId).updateChildren(updateMap)
            updateMap.clear()
        }

        //찬하트(좋아요) 버튼 클릭
        binding.filledLikeImage.setOnClickListener {
            binding.likeImage.visibility = View.VISIBLE
            binding.filledLikeImage.visibility = View.GONE
            database.child("like").child(likeId).removeValue()
            likeCount--
            updateMap.put("likeCount", likeCount)
            database.child("club").child(clubId).updateChildren(updateMap)
            updateMap.clear()
        }
    }

    private fun initData() {
        clubId = intent.getStringExtra("clubId").toString()
        database = Firebase.database.reference
        auth = Firebase.auth

        database.child("club").child(clubId)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val club = snapshot.getValue(Club::class.java)
                    title = club!!.title
                    binding.titleText.text = title
                    binding.introText.text = club.introduce

                    if (club.limit == "제한없음") {
                        binding.boundText.text = club.bound
                    } else {
                        binding.boundText.text = club.limit
                    }

                    binding.typeText.text = club.type
                    likeCount = club.likeCount

                    val dec = DecimalFormat("#,###")

                    binding.likeCountText.text = dec.format(likeCount)
                    binding.memberCountText.text = club.memberCount.toString() + "명 활동중"

                    if(club.userId == auth.currentUser!!.uid) {
                        binding.manageClubManagerText.visibility = View.VISIBLE
                        binding.deleteClubText.visibility = View.VISIBLE
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

        database.child("like")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val like = postSnapshot.getValue(Like::class.java)
                        if(like!!.clubId == clubId && like.userId == auth.currentUser!!.uid) {
                            binding.likeImage.visibility = View.GONE
                            binding.filledLikeImage.visibility = View.VISIBLE
                            likeId = postSnapshot.key!!
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    //진짜 삭제할지 dialog
    private fun deleteDialog() {
        val dialogView = layoutInflater.inflate(com.example.clubapplication.R.layout.dialog_delete, null)
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setView(dialogView)

        val deleteText1 = dialogView.findViewById<TextView>(com.example.clubapplication.R.id.deleteText1)
        val deleteText2 = dialogView.findViewById<TextView>(com.example.clubapplication.R.id.deleteText2)
        val delete = dialogView.findViewById<TextView>(com.example.clubapplication.R.id.deleteText)
        val cancel = dialogView.findViewById<TextView>(com.example.clubapplication.R.id.cancelText)

        deleteText1.text = "삭제하면 복구가 불가능해집니다"
        deleteText2.text = "그래도 삭제하시겠습니까?"
        delete.text = "삭제"

        delete.setOnClickListener {
            updateMap.put("state", "DELETED")
            database.child("club").child(clubId).updateChildren(updateMap)
            updateMap.clear()

            Toast.makeText(this, "동아리가 삭제 되어있습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        cancel.setOnClickListener {
            alertDialog.cancel()
        }

        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(com.example.clubapplication.R.drawable.edit_background)
    }
}