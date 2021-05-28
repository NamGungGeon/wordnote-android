package kr.ac.konkuk.wordnote

import android.content.Context
import android.util.Log
import java.io.FileOutputStream
import java.io.PrintWriter
import java.util.*
import kotlin.collections.ArrayList

class VocaManager private constructor(var context: Context, var onLoaded: Callback?) {
    companion object {
        private var vocaManager: VocaManager? = null
        fun getInstance(context: Context, onLoaded: Callback?): VocaManager {
            if (vocaManager == null)
                vocaManager = VocaManager(context, onLoaded)
            else {
                vocaManager!!.apply {
                    this.context = context
                    this.onLoaded = onLoaded
                    this.loadWordList()
                }
            }

            return vocaManager!!
        }

        fun getInstance(): VocaManager? {
            return vocaManager
        }
    }

    private val fileName = "vocalist.txt"
    var vocaList: ArrayList<Voca> = ArrayList()

    init {
        //fill wordList
        loadWordList()
    }

    interface Callback {
        fun onFinishIO(vocaManager: VocaManager)
    }

    fun getMeaningWithoutDuplicated(except: String?): ArrayList<String> {
        val meaning = ArrayList<String>()
        vocaList.map {
            it.meaning
        }.toList().map {
            if (!meaning.contains(it) && except !== it)
                meaning.add(it)
        }
        meaning.shuffle()
        return meaning
    }

    fun saveWordList() {
        Thread {
            val file = context.getFileStreamPath(fileName)
            try {
                val os = PrintWriter(FileOutputStream(file))
                vocaList.map {
                    try {
                        os.println(it.word)
                        os.println(it.meaning)
                        os.println(it.tryCnt)
                        os.println(it.failCnt)
                        os.println("")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                os.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun loadWordList() {
        if (vocaList.isNotEmpty()) {
            onLoaded?.onFinishIO(this)
            onLoaded = null
            return
        }
        Thread {
            val file = context.getFileStreamPath(fileName)
            val scanner: Scanner = if (!file.exists()) {
                //not found wordlist.txt file
                Scanner(context.resources.openRawResource(R.raw.default_word_list))
            } else {
                Scanner(context.openFileInput(fileName))
            }
            readFromScanner(scanner)
            scanner.close()

            onLoaded?.onFinishIO(this)
            onLoaded = null
        }.start()
    }

    private fun readFromScanner(scanner: Scanner) {
        val wordList = ArrayList<Voca>()
        while (scanner.hasNext()) {
            try {
                val word = scanner.nextLine()
                val meaning = scanner.nextLine()
                val tryCnt = scanner.nextLine().toInt()
                val failCnt = scanner.nextLine().toInt()

                val wordInst = Voca(word, meaning, tryCnt, failCnt)
                wordList.add(wordInst)
                Log.i("word", wordInst.toString())

                scanner.nextLine()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        this.vocaList = wordList
    }

}