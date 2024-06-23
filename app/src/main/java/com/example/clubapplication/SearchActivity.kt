package com.example.clubapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.clubapplication.databinding.ActivitySearchBinding

/**
 * 동아리 검색
 */
class SearchActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {

        //뒤로가기
        binding.backImage.setOnClickListener {
            finish()
        }

        //찾기 버튼 클릭
        binding.searchBtn.setOnClickListener {
            val clubName = binding.searchEdit.text.toString()

            val intent = Intent(this, SearchListActivity::class.java)
            intent.putExtra("clubName", clubName)
            intent.putExtra("bound", "null")
            intent.putExtra("type", "null")
            startActivity(intent)
        }

        //중앙 동아리 클릭
        binding.centralClubText.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            intent.putExtra("clubName", "null")
            intent.putExtra("bound", "중앙 동아리")
            intent.putExtra("type", "null")
            startActivity(intent)
        }

        //단과대 동아리 클릭
        binding.collegeClubText.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            intent.putExtra("clubName", "null")
            intent.putExtra("bound", "단과대 동아리")
            intent.putExtra("type", "null")
            startActivity(intent)
        }

        //과 동아리 클릭
        binding.majorClubText.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            intent.putExtra("clubName", "null")
            intent.putExtra("bound", "과 동아리")
            intent.putExtra("type", "null")
            startActivity(intent)
        }

        //소모임 클릭
        binding.smallClubText.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            intent.putExtra("clubName", "null")
            intent.putExtra("bound", "소모임")
            intent.putExtra("type", "null")
            startActivity(intent)
        }

        //친목/여행 클릭
        binding.friendshipText.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            intent.putExtra("clubName", "null")
            intent.putExtra("bound", "null")
            intent.putExtra("type", "친목/여행")
            startActivity(intent)
        }

        //봉사 클릭
        binding.serviceText.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            intent.putExtra("clubName", "null")
            intent.putExtra("bound", "null")
            intent.putExtra("type", "봉사")
            startActivity(intent)
        }

        //체육 클릭
        binding.physicalText.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            intent.putExtra("clubName", "null")
            intent.putExtra("bound", "null")
            intent.putExtra("type", "체육")
            startActivity(intent)
        }

        //노래/댄스 클릭
        binding.musicText.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            intent.putExtra("clubName", "null")
            intent.putExtra("bound", "null")
            intent.putExtra("type", "노래/댄스")
            startActivity(intent)
        }

        //게임 클릭
        binding.gameText.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            intent.putExtra("clubName", "null")
            intent.putExtra("bound", "null")
            intent.putExtra("type", "게임")
            startActivity(intent)
        }

        //문화예술 클릭
        binding.artText.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            intent.putExtra("clubName", "null")
            intent.putExtra("bound", "null")
            intent.putExtra("type", "문화예술")
            startActivity(intent)
        }

        //공예/만들기 클릭
        binding.craftText.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            intent.putExtra("clubName", "null")
            intent.putExtra("bound", "null")
            intent.putExtra("type", "공예/만들기")
            startActivity(intent)
        }

        //언어 클릭
        binding.languageText.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            intent.putExtra("clubName", "null")
            intent.putExtra("bound", "null")
            intent.putExtra("type", "언어")
            startActivity(intent)
        }

        //미디어/사진 클릭
        binding.mediaText.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            intent.putExtra("clubName", "null")
            intent.putExtra("bound", "null")
            intent.putExtra("type", "미디어/사진")
            startActivity(intent)
        }

        //학술/스터디 클릭
        binding.studyText.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            intent.putExtra("clubName", "null")
            intent.putExtra("bound", "null")
            intent.putExtra("type", "학술/스터디")
            startActivity(intent)
        }

        //반려동물 클릭
        binding.animalText.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            intent.putExtra("clubName", "null")
            intent.putExtra("bound", "null")
            intent.putExtra("type", "반려동물")
            startActivity(intent)
        }

        //기타 클릭
        binding.etcText.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            intent.putExtra("clubName", "null")
            intent.putExtra("bound", "null")
            intent.putExtra("type", "기타")
            startActivity(intent)
        }
    }
}