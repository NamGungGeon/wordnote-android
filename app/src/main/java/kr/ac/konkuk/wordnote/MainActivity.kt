package kr.ac.konkuk.wordnote

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.ac.konkuk.wordnote.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            goFlicker.setOnClickListener {
                val intent = Intent(applicationContext, FlickerActivity::class.java)
                startActivity(intent)
            }
            goExam.setOnClickListener {
                val intent = Intent(applicationContext, ExamActivity::class.java)
                startActivity(intent)
            }
            goList.setOnClickListener {
                val intent = Intent(applicationContext, VocaListActivity::class.java)
                startActivity(intent)
            }

            //시험 결과 (성적표)
            //단어외우기 페이지에서 듣기버튼 추가
            //듣기평가
        }
    }

    private var lastBackBtnPressed: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() - lastBackBtnPressed < 1000) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "한번 더 뒤로가기 버튼을 누르면 앱을 종료합니다", Toast.LENGTH_SHORT).show()
            lastBackBtnPressed = System.currentTimeMillis()
        }
    }
}