package kr.ac.konkuk.wordnote

import java.io.Serializable

data class Voca (var word: String, var meaning: String, var tryCnt: Int= 0, var failCnt: Int= 0): Serializable {

    fun getHitRate():Float{
        if(tryCnt== 0)
            return 0f
        return ((tryCnt-failCnt).toFloat()/tryCnt.toFloat())
    }
    fun reflectExam(isRight:Boolean){
        tryCnt++
        if(!isRight)
            failCnt++

        VocaManager.getInstance()?.saveWordList()
    }

    override fun toString(): String {
        return "Voca(word='$word', meaning='$meaning', tryCnt=$tryCnt, failCnt=$failCnt)"
    }

}