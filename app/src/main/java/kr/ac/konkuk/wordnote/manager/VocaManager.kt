package kr.ac.konkuk.wordnote.manager

import android.content.Context
import android.util.Log
import kr.ac.konkuk.wordnote.R
import kr.ac.konkuk.wordnote.bean.Voca
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*
import kotlin.collections.ArrayList

class VocaManager private constructor(
    var context: Context,
    var onLoaded: (((VocaManager) -> Unit)?)
) {
    companion object {
        private var vocaManager: VocaManager? = null
        fun useInstance(context: Context, onLoaded: (((VocaManager) -> Unit)?)): VocaManager {
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

        fun useInstance(): VocaManager? {
            return vocaManager
        }
    }

    private val fileName = "vocalist3213245.txt"
    var vocaList: ArrayList<Voca> = ArrayList()
    var vocaBookHolder: Voca = Voca.bookHolder()

    init {
        //fill wordList
        loadWordList()
    }

    fun getVocaList(bookName: String): ArrayList<Voca> {
        return if (bookName == Voca.BOOK_NAME_ENTIRE)
            vocaList
        else
            ArrayList(vocaList.filter {
                it.books.contains(bookName)
            })
    }

    fun getVocaBookList(): ArrayList<String> {
        val books = ArrayList<String>()
        vocaBookHolder?.books.map {
            books.add(it)
        }

        return books
    }

    fun updateBookName(bookName: String, newBookName: String) {
        vocaBookHolder.books.remove(bookName)
        vocaBookHolder.books.add(newBookName)

        vocaList.map {
            val bookNameOfCurrentVoca = it.books.find {
                it == bookName
            }
            if (bookNameOfCurrentVoca != null) {
                it.books.remove(bookNameOfCurrentVoca)
                it.books.add(newBookName)
            }
        }

        saveWordList()
    }

    fun removeBook(bookName: String) {
        vocaBookHolder.books.remove(bookName)
        saveWordList()
    }

    private fun finishUse() {
        onLoaded?.run {
            this(this@VocaManager)
            saveWordList()

            onLoaded = null
        }
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

    @Synchronized
    fun saveWordList() {
        Thread {
            val file = context.getFileStreamPath(fileName)
            try {
                val os = ObjectOutputStream(FileOutputStream(file))
                val targetObject = ArrayList<Voca>(vocaList)
                targetObject.add(vocaBookHolder)
                vocaList.map {
                    try {
                        os.writeObject(targetObject)
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

    @Synchronized
    private fun loadWordList() {
        if (vocaList.isNotEmpty()) {
            onLoaded?.run {
                this(this@VocaManager)

                onLoaded = null
            }
            return
        }
        Thread {
            val file = context.getFileStreamPath(fileName)
            if (!file.exists()) {
                //not found wordlist.txt file
                val scanner = Scanner(context.resources.openRawResource(R.raw.default_word_list))
                readFromScanner(scanner)
                scanner.close()
            } else {
                readFromObjectFiles(context)
            }

            onLoaded?.run {
                this(this@VocaManager)

                onLoaded = null
            }
        }.start()
    }

    private fun readFromObjectFiles(context: Context) {
        val file = context.getFileStreamPath(fileName)
        val inputStream = ObjectInputStream(file.inputStream())

        try {
            val vocaList = inputStream.readObject() as ArrayList<Voca>
            val vocaBookHolder = vocaList.find {
                it == Voca.bookHolder()
            }
            if (vocaBookHolder != null) {
                this.vocaBookHolder = vocaBookHolder
                vocaList.remove(vocaBookHolder)
            }

            this.vocaList = vocaList
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun readFromScanner(scanner: Scanner) {
        val wordList = ArrayList<Voca>()
        while (scanner.hasNext()) {
            try {
                val word = scanner.nextLine()
                val meaning = scanner.nextLine()
                val tryCnt = scanner.nextLine().toInt()
                val failCnt = scanner.nextLine().toInt()
                val checkCnt = scanner.nextLine().toInt()

                val wordInst = Voca(word, meaning, tryCnt, failCnt, checkCnt)
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