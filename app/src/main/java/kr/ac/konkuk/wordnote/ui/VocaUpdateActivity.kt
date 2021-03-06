package kr.ac.konkuk.wordnote.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kr.ac.konkuk.wordnote.R
import kr.ac.konkuk.wordnote.bean.Voca
import kr.ac.konkuk.wordnote.manager.VocaManager
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
            meaningInput.hint = "?????? ???"

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
            Toast.makeText(this, "????????? ?????? ?????? ???????????? ?????????", Toast.LENGTH_SHORT).show()
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
                "?????????????????????",
                Toast.LENGTH_SHORT
            ).show()
            setResult(RESULT_OK)
            finish()
        }


    }

    private fun removeVoca() {
        AlertDialog.Builder(this).setTitle("?????? ??????")
            .setMessage("${voca.word}/${voca.meaning}\n\n????????? ???????????? ?????? ????????? ????????? ?????????????????? ?????? ???????????????. ?????????????????????????\n")
            .setPositiveButton("??????") { dialog, i ->
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
                        "${originVoca.word}??? ?????????????????????",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("??????") { dialog, i ->
                dialog.dismiss()
            }.create().show()

    }


}