package kr.ac.konkuk.wordnote

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.ac.konkuk.wordnote.databinding.ActivityAddVocaBookBinding

class AddVocaBookActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddVocaBookBinding
    var newBookName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVocaBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "단어장 추가"

        binding.addVocaBookBtn.setOnClickListener {
            val newBookName = binding.bookNameInput.text.toString()
            if (newBookName == "") {
                Toast.makeText(this, "단어장의 이름을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            VocaManager.useInstance(this) { manager ->
                if (manager.getVocaBookList().contains(newBookName)) {
                    Toast.makeText(
                        this@AddVocaBookActivity,
                        "이미 동일한 이름의 단어장이 존재합니다",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@useInstance
                }

                this.newBookName = newBookName
                manager.vocaBookHolder.books.add(newBookName.trim().replace("\n", ""))
                manager.saveWordList()

                finish()
            }
        }
    }

    override fun onDestroy() {
        if (newBookName != null)
            Toast.makeText(this, "단어장(${newBookName}) 추가가 완료되었습니다", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }
}