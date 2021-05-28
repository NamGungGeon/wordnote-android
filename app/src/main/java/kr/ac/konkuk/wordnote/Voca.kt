package kr.ac.konkuk.wordnote

import java.io.Serializable

data class Voca(var word: String, var meaning: String, var tryCnt: Int = 0, var failCnt: Int = 0) :
    Serializable {
    companion object {
        fun copy(voca: Voca): Voca {
            return Voca(voca.word, voca.meaning, voca.tryCnt, voca.failCnt)
        }
    }

    fun getHitRate(): Float {
        if (tryCnt == 0)
            return 0f
        return ((tryCnt - failCnt).toFloat() / tryCnt.toFloat())
    }

    fun reflectExam(isRight: Boolean) {
        tryCnt++
        if (!isRight)
            failCnt++

        VocaManager.getInstance()?.saveWordList()
    }

    override fun toString(): String {
        return "Voca(word='$word', meaning='$meaning', tryCnt=$tryCnt, failCnt=$failCnt)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Voca

        if (word != other.word) return false
        if (meaning != other.meaning) return false
        if (tryCnt != other.tryCnt) return false
        if (failCnt != other.failCnt) return false

        return true
    }

}