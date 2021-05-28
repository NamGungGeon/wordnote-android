package kr.ac.konkuk.wordnote

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.ac.konkuk.wordnote.databinding.ActivityAddVocaBinding

class AddVocaActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddVocaBinding
    lateinit var vocaFragment: VocaFragment

    var word: Word = Word("", "", 0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddVocaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        word = Word("", "", 0, 0)
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val wordText = binding.wordInput.text.toString()
                val meaningText = binding.meaningInput.text.toString()
                word = Word(
                    wordText.toString().replace("\n", ""),
                    meaningText.toString().replace("\n", ""),
                    0,
                    0
                )

                vocaFragment.setVoca(word)

                if (wordText.contains("\n") || meaningText.contains("\n")) {
                    binding.wordInput.setText(wordText.replace("\n", ""))
                    binding.meaningInput.setText(meaningText.replace("\n", ""))

                    addVoca()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        }
        binding.apply {
            wordInput.setText("")
            wordInput.hint = "New Word"
            meaningInput.setText("")
            meaningInput.hint = "단어 뜻"

            wordInput.addTextChangedListener(textWatcher)
            meaningInput.addTextChangedListener(textWatcher)

            vocaFragment =
                supportFragmentManager.findFragmentById(R.id.preview_voca) as VocaFragment
            vocaFragment.setVoca(Word("New Word", "단어 뜻", 0, 0))

            addVocaBtn.setOnClickListener { addVoca() }
        }
    }

    private fun addVoca() {
        if (word.word == "" || word.meaning == "") {
            Toast.makeText(this, "단어와 뜻을 모두 추가해야 합니다", Toast.LENGTH_SHORT).show()
            return
        }

        WordManager.getInstance(this, object : WordManager.Callback {
            override fun onFinishIO(wordManager: WordManager) {
                wordManager.wordList.add(word)

                Toast.makeText(
                    this@AddVocaActivity,
                    "새로운 단어 ${word.word}가 추가되었습니다",
                    Toast.LENGTH_SHORT
                ).show()
                init()
            }
        })
    }
}