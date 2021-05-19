package kr.ac.konkuk.wordnote

import android.content.Context
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

class WordManager private constructor(var context: Context, var onLoaded: Callback?) {
    companion object {
        private var wordManager: WordManager? = null
        fun getInstance(context: Context, onLoaded: Callback?):WordManager{
            if (wordManager == null)
                wordManager = WordManager(context, onLoaded)
            else{
                wordManager!!.apply {
                    this.context= context
                    this.onLoaded= onLoaded
                    this.loadWordList()
                }
            }

            return wordManager!!
        }
    }

    private val fileName = "wordlist.txt"
    var wordList: ArrayList<Word> = ArrayList()

    init {
        //fill wordList
        loadWordList()
    }

    interface Callback{
        fun onFinishIO(wordManager: WordManager)
    }

    private fun saveWordList(){

    }
    private fun loadWordList(){
        if(wordList.isNotEmpty()){
            onLoaded?.onFinishIO(this)
            onLoaded= null
            return
        }
        Thread{
            val file = context.getFileStreamPath(fileName)
            val scanner:Scanner= if (!file.exists()) {
                //not found wordlist.txt file
                Scanner(context.resources.openRawResource(R.raw.default_word_list))
            } else {
                Scanner(context.openFileInput(fileName))
            }
            readFromScanner(scanner)

            onLoaded?.onFinishIO(this)
            onLoaded= null
        }.start()
    }

    private fun readFromScanner(scanner: Scanner) {
        val wordList= ArrayList<Word>()
        while (scanner.hasNext()) {
            try {
                val word = scanner.nextLine()
                val meaning = scanner.nextLine()
                val tryCnt = scanner.nextLine().toInt()
                val failCnt = scanner.nextLine().toInt()

                val wordInst = Word(word, meaning, tryCnt, failCnt)
                wordList.add(wordInst)
                Log.i("word", wordInst.toString())

                scanner.nextLine()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        this.wordList= wordList
    }

}