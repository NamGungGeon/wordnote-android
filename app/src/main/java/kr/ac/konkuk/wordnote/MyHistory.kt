package kr.ac.konkuk.wordnote

class MyHistory(
    val name: String,
    val explain: String,
    val moreInfo: String = "",
    val timeStamp: Long = System.currentTimeMillis()
) {
}