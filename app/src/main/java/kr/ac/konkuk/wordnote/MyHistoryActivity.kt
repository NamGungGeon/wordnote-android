package kr.ac.konkuk.wordnote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kr.ac.konkuk.wordnote.databinding.ActivityMyHistoryBinding

class MyHistoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "업적"
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun init() {
        val fragment =
            supportFragmentManager.findFragmentById(R.id.my_history_list_fragment) as MyHistoryFragment
        MyHistoryManager.useInstance(this) { manager ->
            if (manager.historyList.isEmpty()) {
                Toast.makeText(this, "아직 업적이 없습니다\n시험을 보거나 단어를 외워 추가해보세요!", Toast.LENGTH_SHORT)
                    .show()
                return@useInstance
            }
            fragment.setHistory(manager.historyList)
        }
    }
}