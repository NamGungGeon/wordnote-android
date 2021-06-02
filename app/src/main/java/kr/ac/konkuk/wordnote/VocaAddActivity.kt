package kr.ac.konkuk.wordnote

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.ac.konkuk.wordnote.databinding.ActivityAddVocaBinding

class VocaAddActivity : AppCompatActivity() {

    companion object {
        val EXTRA_KEY_BOOKNAME = "bookname"
    }

    lateinit var binding: ActivityAddVocaBinding
    lateinit var vocaFragment: VocaFragment

    var voca: Voca = Voca("", "", 0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddVocaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "단어 추가"
        }
        init()
    }

    private fun init() {
        voca = Voca("", "", 0, 0)
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
                voca = Voca(
                    wordText.replace("\n", ""),
                    meaningText.replace("\n", ""),
                    0,
                    0
                )

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
            val dummyVoca = Voca("New Word", "단어 뜻", 0, 0)
            wordInput.setText("")
            wordInput.hint = dummyVoca.word
            wordInput.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    //find already existed voca
                    VocaManager.useInstance(this@VocaAddActivity) { manager ->
                        var target: Voca? = null
                        manager.vocaList.map { v ->
                            if (v.word == voca.word) {
                                target = v
                            }
                        }
                        //duplicated voca is exist
                        if (target != null) {
                            AlertDialog.Builder(this@VocaAddActivity)
                                .setTitle("${voca.word}가 이미 등록되어 있습니다")
                                .setMessage("등록된 단어를 수정하시겠습니까?")
                                .setPositiveButton("예") { dialog, i ->
                                    val intent =
                                        Intent(this@VocaAddActivity, VocaUpdateActivity::class.java)
                                    intent.putExtra("voca", target)
                                    startActivityForResult(intent, 1)
                                    dialog.dismiss()
                                }.setNegativeButton("무시") { dialog, i ->
                                    dialog.dismiss()
                                }.create().show()
                        }
                    }
                }
            }
            meaningInput.setText("")
            meaningInput.hint = dummyVoca.meaning

            wordInput.addTextChangedListener(textWatcher)
            meaningInput.addTextChangedListener(textWatcher)

            vocaFragment =
                supportFragmentManager.findFragmentById(R.id.preview_voca) as VocaFragment
            vocaFragment.setVoca(dummyVoca)

            addVocaBtn.setOnClickListener { addVoca() }
            searchBtn.setOnClickListener {
                Thread {
                    voca.loadMeaningFromDict {
                        runOnUiThread {
                            if (it) {
                                //success
                                meaningInput.setText(voca.meaning)
                                vocaFragment.setVoca(voca)

                                Toast.makeText(
                                    this@VocaAddActivity,
                                    "단어사전에서 검색한 단어의 뜻을 자동으로 입력합니다",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }.start()
            }
        }
    }

    private fun addVoca() {
        if (voca.word == "" || voca.meaning == "") {
            Toast.makeText(this, "단어와 뜻을 모두 추가해야 합니다", Toast.LENGTH_SHORT).show()
            return
        }

        VocaManager.useInstance(this) { manager ->
            val bookName = intent.getStringExtra(EXTRA_KEY_BOOKNAME)
            if (bookName != null)
                voca.books.add(bookName)
            manager.vocaList.add(0, voca)

            var toastMsg =
                if (bookName == null) "새로운 단어 ${voca.word}가 추가되었습니다" else "새로운 단어 ${voca.word}가 단어장 ${bookName}에 추가되었습니다"

            Toast.makeText(
                this@VocaAddActivity,
                toastMsg,
                Toast.LENGTH_SHORT
            ).show()

            init()
        }
    }

    override fun onBackPressed() {
        val bookName = intent.getStringExtra(EXTRA_KEY_BOOKNAME)
        val resultIntent = Intent()
        if (bookName != null)
            resultIntent.putExtra(VocaBookActivity.EXTRA_KEY_TAB_SELECTOR, bookName)
        setResult(Activity.RESULT_OK, resultIntent)
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK)
            finish()
    }
}