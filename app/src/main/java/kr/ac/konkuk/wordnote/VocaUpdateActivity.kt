package kr.ac.konkuk.wordnote

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kr.ac.konkuk.wordnote.databinding.ActivityUpdateVocaBinding

class VocaUpdateActivity : AppCompatActivity() {
    companion object {
        val EXTRA_KEY_MODE = "MODE"
        val EXTRA_VALUE_MODE_ONLY_ONCE = "ONLY_ONCE"
    }

    lateinit var binding: ActivityUpdateVocaBinding
    lateinit var vocaFragment: VocaFragment

    lateinit var voca: Voca
    lateinit var originVoca: Voca

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpdateVocaBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onResume() {
        super.onResume()

        val voca = intent.getSerializableExtra("voca")
            ?: throw IllegalStateException("Voca is not passed")

        this.voca = voca as Voca
        this.originVoca = Voca.copy(voca)


        supportActionBar?.apply {
            title = voca.word
        }

        init()
    }

    private fun init() {
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
                voca.word = wordText.replace("\n", "")
                voca.meaning = meaningText.replace("\n", "")

                vocaFragment.setVoca(voca)

                if (wordText.contains("\n") || meaningText.contains("\n")) {
                    binding.wordInput.setText(wordText.replace("\n", ""))
                    binding.meaningInput.setText(meaningText.replace("\n", ""))
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        }
        binding.apply {
            wordInput.setText(voca.word)
            wordInput.hint = "New Word"
            meaningInput.setText(voca.meaning)
            meaningInput.hint = "단어 뜻"

            wordInput.addTextChangedListener(textWatcher)
            meaningInput.addTextChangedListener(textWatcher)

            vocaFragment =
                supportFragmentManager.findFragmentById(R.id.preview_voca) as VocaFragment
            vocaFragment.setVoca(voca)

            updateVocaBtn.setOnClickListener { updateVoca() }
            removeVocaBtn.setOnClickListener { removeVoca() }
        }
    }

    private fun updateVoca() {
        if (voca.word == "" || voca.meaning == "") {
            Toast.makeText(this, "단어와 뜻이 모두 존재해야 합니다", Toast.LENGTH_SHORT).show()
            return
        }

        VocaManager.useInstance(this) { manager ->
            for (idx in 0 until manager.vocaList.size - 1) {
                val currentVoca = manager.vocaList[idx]
                if (currentVoca == originVoca) {
                    currentVoca.word = voca.word
                    currentVoca.meaning = voca.meaning
                    break
                }
            }
            manager.saveWordList()
            Toast.makeText(
                this@VocaUpdateActivity,
                "변경되었습니다",
                Toast.LENGTH_SHORT
            ).show()
            setResult(Activity.RESULT_OK)
            finish()
        }


    }

    private fun removeVoca() {
        AlertDialog.Builder(this).setTitle("단어 삭제")
            .setMessage("${voca.word}/${voca.meaning}\n\n단어를 삭제하면 해당 단어가 포함된 단어장에서도 모두 삭제됩니다. 계속하시겠습니까?\n")
            .setPositiveButton("삭제") { dialog, i ->
                VocaManager.useInstance(this) { manager ->
                    val iterator = manager.vocaList.iterator()
                    while (iterator.hasNext()) {
                        if (iterator.next() == originVoca) {
                            iterator.remove()
                            break
                        }
                    }
                    manager.saveWordList()

                    Toast.makeText(
                        this@VocaUpdateActivity,
                        "${originVoca.word}가 삭제되었습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("닫기") { dialog, i ->
                dialog.dismiss()
            }.create().show()

    }


}