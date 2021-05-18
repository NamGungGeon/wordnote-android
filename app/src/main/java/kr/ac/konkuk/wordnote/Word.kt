package kr.ac.konkuk.wordnote

import java.io.Serializable

data class Word (val word: String, val meaning: String, val tryCnt: Int, val failCnt: Int): Serializable {

    fun getHitRate():Float{
        return (failCnt.toFloat()/tryCnt.toFloat())
    }

    override fun toString(): String {
        return "Word(word='$word', meaning='$meaning', tryCnt=$tryCnt, failCnt=$failCnt)"
    }

}