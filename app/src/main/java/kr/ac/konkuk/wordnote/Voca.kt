package kr.ac.konkuk.wordnote

import androidx.annotation.WorkerThread
import org.jsoup.Jsoup
import java.io.Serializable

data class Voca(
    var word: String,
    var meaning: String,
    var tryCnt: Int = 0,
    var failCnt: Int = 0,
    var checkCnt: Int = 0
) :
    Serializable {
    companion object {
        val BOOK_NAME_ENTIRE = "전체"
        val ID_VOCA_HOLDER = "JUST_VOCA_HOLDER"
        fun copy(voca: Voca): Voca {
            return Voca(voca.word, voca.meaning, voca.tryCnt, voca.failCnt)
        }

        fun bookHolder(): Voca {
            return Voca(ID_VOCA_HOLDER, "NO_NORMAL_VOCA")
        }
    }

    val books: ArrayList<String> = ArrayList()

    fun getHitRate(): Float {
        if (tryCnt == 0)
            return 0f
        return ((tryCnt - failCnt).toFloat() / tryCnt.toFloat())
    }

    fun reflectExam(isRight: Boolean) {
        tryCnt++
        if (!isRight)
            failCnt++

        VocaManager.useInstance()?.saveWordList()
    }

    @WorkerThread
    fun loadMeaningFromDict(@WorkerThread callback: ((Boolean) -> Unit)) {
        if (word == "") {
            callback(false)
            return
        }
        try {
            val wordSearchUrl = "https://dic.daum.net/search.do?q=${word}"
            var crawler = Jsoup.connect(wordSearchUrl).get()
            val searchedUrl =
                crawler.select(".card_word .search_cleanword a").first().absUrl("href")

            crawler = Jsoup.connect(searchedUrl).get()
            val meaningList = crawler.select(".list_mean .txt_mean").map {
                it.text()
            }

            var meaning = ""
            for (idx in meaningList.indices) {
                meaning += meaningList[idx].trim()
                if (idx != meaningList.size - 1) {
                    meaning += ", "
                }
            }

            this.meaning = meaning
            callback(true)
        } catch (e: Exception) {
            e.printStackTrace()
            callback(false)
        }
    }

    override fun toString(): String {
        return "Voca(word='$word', meaning='$meaning', tryCnt=$tryCnt, failCnt=$failCnt)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Voca

        if (word == ID_VOCA_HOLDER && other.word == ID_VOCA_HOLDER) return true

        if (word != other.word) return false
        if (meaning != other.meaning) return false
        if (tryCnt != other.tryCnt) return false
        if (failCnt != other.failCnt) return false

        return true
    }

}