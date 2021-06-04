package kr.ac.konkuk.wordnote.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.ac.konkuk.wordnote.manager.VocaManager
import kr.ac.konkuk.wordnote.databinding.ActivityUpdateVocaBookBinding

class UpdateVocaBookActivity : AppCompatActivity() {
    companion object {
        val EXTRA_KEY_BOOK_NAME = "bookName"
    }

    lateinit var binding: ActivityUpdateVocaBookBinding
    var newBookName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateVocaBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookName: String = intent.getStringExtra(EXTRA_KEY_BOOK_NAME) as String

        supportActionBar?.title = "단어장 추가"

        binding.updateVocaBookBtn.setOnClickListener {
            val newBookName = binding.bookNameInput.text.toString()
            if (newBookName == "") {
                Toast.makeText(this, "단어장의 이름을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            VocaManager.useInstance(this) { manager ->
                if (manager.getVocaBookList().contains(newBookName)) {
                    Toast.makeText(
                        this@UpdateVocaBookActivity,
                        "이미 동일한 이름의 단어장이 존재합니다",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@useInstance
                }

                this.newBookName = newBookName
                manager.updateBookName(bookName, newBookName)

                finish()
            }
        }
    }

    override fun onDestroy() {
        if (newBookName != null)
            Toast.makeText(this, "변경이 완료되었습니다", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }
}