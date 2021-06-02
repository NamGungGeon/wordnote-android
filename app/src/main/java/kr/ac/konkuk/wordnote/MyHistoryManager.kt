package kr.ac.konkuk.wordnote

import android.content.Context
import android.os.Looper
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.Exception

class MyHistoryManager private constructor(val context: Context) {
    companion object {
        private var inst: MyHistoryManager? = null
        fun useInstance(
            context: Context,
            callback: ((MyHistoryManager) -> Unit)
        ) {
            val handler = android.os.Handler(Looper.getMainLooper())
            Thread {
                if (inst == null) {
                    inst = MyHistoryManager(context)
                }
                handler.post {
                    callback(inst!!)
                    inst!!.save()
                }
            }.start()
        }
    }

    private val fileName = "myHistories.txt"
    var historyList = ArrayList<MyHistory>()

    init {
        load()
    }


    private fun load() {
        if (historyList.isNotEmpty())
            return
        try {
            val inputStream = ObjectInputStream(context.openFileInput(fileName))
            historyList = inputStream.readObject() as ArrayList<MyHistory>
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun save() {
        Thread {
            val outputStream =
                ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE))
            try {
                outputStream.writeObject(historyList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}