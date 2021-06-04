package kr.ac.konkuk.wordnote.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.ac.konkuk.wordnote.manager.VocaManager
import kr.ac.konkuk.wordnote.databinding.ActivityVocaListBinding

class VocaListActivity : AppCompatActivity() {
    lateinit var binding: ActivityVocaListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVocaListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.apply {
            title = "단어 리스트"
        }
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun init() {
        binding.apply {
            addVocaBtn.setOnClickListener {
                val intent = Intent(applicationContext, VocaAddActivity::class.java)
                startActivity(intent)
            }
        }

        VocaManager.useInstance(this) { manager ->

            if (isDestroyed)
                return@useInstance

            runOnUiThread {
                binding.apply {
                    val adapter = VocaRecylcerViewAdapter(manager.vocaList)
                    adapter.onItemSelected = {
                        //update
                        val intent =
                            Intent(this@VocaListActivity, VocaUpdateActivity::class.java)
                        intent.putExtra("voca", it)
                        startActivity(intent)
                    }

                    vocaList.layoutManager =
                        LinearLayoutManager(this@VocaListActivity, RecyclerView.VERTICAL, false)
                    vocaList.adapter = adapter

                    vocaCnt.text = "${manager.vocaList.size}개의 단어가 있습니다"
                }
            }
        }
    }
}