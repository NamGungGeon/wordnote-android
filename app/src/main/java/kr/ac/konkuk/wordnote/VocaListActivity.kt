package kr.ac.konkuk.wordnote

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.ac.konkuk.wordnote.databinding.ActivityVocaListBinding

class VocaListActivity : AppCompatActivity() {
    lateinit var binding: ActivityVocaListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVocaListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        binding.apply {
            addVocaBtn.setOnClickListener {
                val intent= Intent(applicationContext, VocaAddActivity::class.java)
                startActivity(intent)
            }
        }

        VocaManager.getInstance(this, object : VocaManager.Callback {
            override fun onFinishIO(vocaManager: VocaManager) {
                if (isDestroyed)
                    return

                runOnUiThread {
                    binding.apply {
                        val adapter = VocaRecylcerViewAdapter(vocaManager.vocaList)
                        adapter.onItemSelected = {
                            //update
                            val intent =
                                Intent(this@VocaListActivity, VocaUpdateActivity::class.java)
                            intent.putExtra("voca", it)
                            startActivity(intent)
                        }

                        vocaList.layoutManager = LinearLayoutManager(this@VocaListActivity, RecyclerView.VERTICAL, false)
                        vocaList.adapter = adapter
                    }
                }
            }
        })
    }
}