package kr.ac.konkuk.wordnote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kr.ac.konkuk.wordnote.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            goFlicker.setOnClickListener {
                val intent= Intent(applicationContext, FlickerActivity::class.java)
                startActivity(intent)
            }
            goExam.setOnClickListener {
                val intent= Intent(applicationContext, ExamActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private var lastBackBtnPressed:Long= 0
    override fun onBackPressed() {
        if(System.currentTimeMillis()- lastBackBtnPressed< 1000){
            super.onBackPressed()
        }else{
            Toast.makeText(this, "한번 더 뒤로가기 버튼을 누르면 앱을 종료합니다", Toast.LENGTH_SHORT).show()
            lastBackBtnPressed= System.currentTimeMillis()
        }
    }
}