package kr.ac.konkuk.wordnote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kr.ac.konkuk.wordnote.databinding.ActivityFlickerBinding
import java.util.*

class FlickerActivity : AppCompatActivity() {
    lateinit var binding:ActivityFlickerBinding

    private val wordList= Stack<Word>()
    lateinit var currentWord:Word
    private var lastClicked:Long= 0
    private var originWordListSize:Int= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFlickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        WordManager.getInstance(this, object: WordManager.Callback{
            override fun onFinishIO(wordManager: WordManager) {
                wordList.addAll(wordManager.wordList)
                originWordListSize= wordList.size
                wordList.shuffle()

                Toast.makeText(applicationContext, "두번 터치하면 다음 단어로 이동합니다", Toast.LENGTH_SHORT).show()
                nextWord()
                binding.root.setOnClickListener{
                    //next
                    if(System.currentTimeMillis()- lastClicked< 1000){
                        lastClicked= 0
                        nextWord()
                    }else{
                        lastClicked= System.currentTimeMillis()
                    }
                }
            }
        })
    }

    private fun nextWord(){
        if(wordList.isEmpty()){
            //end
            Toast.makeText(this, "더 이상 단어가 없습니다", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        Toast.makeText(this, "다음 단어를 표시합니다", Toast.LENGTH_SHORT).show()
        currentWord= wordList.pop()
        binding.apply {
            status.text= "${originWordListSize- wordList.size} / ${originWordListSize}"
            word.text = currentWord.word
            meaning.text= currentWord.meaning
        }
    }
}