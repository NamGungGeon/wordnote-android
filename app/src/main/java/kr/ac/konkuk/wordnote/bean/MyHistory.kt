package kr.ac.konkuk.wordnote.bean

import java.io.Serializable
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MyHistory(
    val name: String,
    val explain: String,
    val moreInfo: String = "",
    val timeStamp: Long = System.currentTimeMillis()
) : Serializable {
    companion object {
        val NAME_INSTANCE_VOCA = "단어 하나를 눈에 익혔습니다"
        val NAME_FLIKER_VOCA = "깜빡이로 단어 외우기"
        val NAME_EXAM_VOCA = "단어 시험 보기"
        val NAME_EXAM_LISTENING_REQUIRE_MEANING = "단어 시험 보기(듣기)"
    }

    fun getDateString(): String? {
        return try {
            val format = SimpleDateFormat("MM-dd")
            format.format(Date(timeStamp))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}