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

        supportActionBar?.title= "워드노트"

        binding.apply {
            val menuList= ArrayList<MaterialMenuItemRecyclerView.MaterialMenuItem>()
            menuList.add(MaterialMenuItemRecyclerView.MaterialMenuItem(R.drawable.ic_baseline_flash_on_24, "단어 외우기", "깜빡이를 이용해 단어를 학습합니다"){
                val intent = Intent(applicationContext, FlickerActivity::class.java)
                startActivity(intent)
            })
            menuList.add(MaterialMenuItemRecyclerView.MaterialMenuItem(R.drawable.ic_baseline_bookmarks_24, "단어 시험 보기", "단어가 주어지면 뜻을 맞춰야 합니다"){
                val intent = Intent(applicationContext, ExamActivity::class.java)
                startActivity(intent)
            })
            menuList.add(MaterialMenuItemRecyclerView.MaterialMenuItem(R.drawable.ic_baseline_volume_up_24, "단어 시험 보기 (듣기)", "단어의 발음을 듣고 뜻을 맞춰야 합니다"){
                val intent = Intent(applicationContext, ExamListenRequireMeaningActivity::class.java)
                startActivity(intent)
            })
            menuList.add(MaterialMenuItemRecyclerView.MaterialMenuItem(R.drawable.ic_baseline_menu_book_24, "단어 관리", "등록된 단어를 관리합니다"){
                val intent = Intent(applicationContext, VocaListActivity::class.java)
                startActivity(intent)
            })
            val adapter= MaterialMenuItemRecyclerView(menuList)
            mainMenuListView.adapter=adapter

            //시험 결과 (성적표)
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