package kr.ac.konkuk.wordnote

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kr.ac.konkuk.wordnote.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "워드노트"

        binding.apply {
            val menuList = ArrayList<MaterialMenuItemRecyclerView.MaterialMenuItem>()
            menuList.add(
                MaterialMenuItemRecyclerView.MaterialMenuItem(
                    R.drawable.ic_baseline_flash_on_24,
                    "단어 외우기",
                    "깜빡이를 이용해 단어를 학습합니다"
                ) {
                    useVocaSelecterDialog("단어 외우기") { bookName, vocaCnt ->
                        val intent = Intent(applicationContext, FlickerActivity::class.java)
                        intent.putExtra(FlickerActivity.KEY_NAME_BOOK_NAME, bookName)
                        intent.putExtra(FlickerActivity.KEY_NAME_VOCA_CNT, vocaCnt)
                        startActivity(intent)
                    }
                })
            menuList.add(
                MaterialMenuItemRecyclerView.MaterialMenuItem(
                    R.drawable.ic_baseline_bookmarks_24,
                    "단어 시험 보기",
                    "단어가 주어지면 뜻을 맞춰야 합니다"
                ) {
                    useVocaSelecterDialog("단어 시험 보기") { bookName, vocaCnt ->
                        val intent = Intent(applicationContext, ExamActivity::class.java)
                        intent.putExtra(ExamActivity.KEY_NAME_BOOK_NAME, bookName)
                        intent.putExtra(ExamActivity.KEY_NAME_VOCA_CNT, vocaCnt)
                        startActivity(intent)
                    }
                })
            menuList.add(
                MaterialMenuItemRecyclerView.MaterialMenuItem(
                    R.drawable.ic_baseline_volume_up_24,
                    "단어 시험 보기 (듣기)",
                    "단어의 발음을 듣고 뜻을 맞춰야 합니다"
                ) {
                    useVocaSelecterDialog("단어 시험 보기 (듣기)") { bookName, vocaCnt ->
                        val intent =
                            Intent(applicationContext, ExamListenRequireMeaningActivity::class.java)
                        intent.putExtra(
                            ExamListenRequireMeaningActivity.KEY_NAME_BOOK_NAME,
                            bookName
                        )
                        intent.putExtra(ExamListenRequireMeaningActivity.KEY_NAME_VOCA_CNT, vocaCnt)
                        startActivity(intent)
                    }
                })
            menuList.add(
                MaterialMenuItemRecyclerView.MaterialMenuItem(
                    R.drawable.ic_baseline_playlist_add_check_24,
                    "업적",
                    "외운 단어와 시험 결과를 확인할 수 있습니다"
                ) {
                    val intent = Intent(applicationContext, MyHistoryActivity::class.java)
                    startActivity(intent)
                })
            menuList.add(
                MaterialMenuItemRecyclerView.MaterialMenuItem(
                    R.drawable.ic_baseline_book_24,
                    "단어장 관리",
                    "단어장을 관리합니다"
                ) {
                    val intent = Intent(applicationContext, VocaBookActivity::class.java)
                    startActivity(intent)
                })
            val adapter = MaterialMenuItemRecyclerView(menuList)
            mainMenuListView.adapter = adapter
        }
    }

    private fun useVocaSelecterDialog(
        title: String,
        onPositive: ((bookName: String, vocaCnt: Int) -> Unit)
    ) {
        VocaManager.useInstance(this) { manager ->
            val childView =
                layoutInflater.inflate(R.layout.dialog_target_voca_selector, null, false)
            val bookNameSpinner = childView.findViewById<Spinner>(R.id.bookNameSpinner)
            val bookList = ArrayList<String>()
            bookList.add(Voca.BOOK_NAME_ENTIRE)
            bookList.addAll(manager.getVocaBookList())
            bookNameSpinner.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                bookList
            )
            val vocaCntSpinner = childView.findViewById<Spinner>(R.id.vocaCntSpinner)
            val cntList = ArrayList<String>()
            for (cnt in 1..5) {
                cntList.add("${cnt * 5}")
            }
            vocaCntSpinner.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                cntList
            )

            AlertDialog.Builder(this@MainActivity).setTitle(title)
                .setView(childView)
                .setPositiveButton("선택") { dialog, i ->
                    val selectedBookName = bookNameSpinner.selectedItem as String
                    val selectedVocaCnt = (vocaCntSpinner.selectedItem as String).toInt()
                    onPositive(selectedBookName, selectedVocaCnt)
                    dialog.dismiss()
                }.setNegativeButton("닫기") { dialog, i ->
                    dialog.dismiss()
                }.create().show()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_dev_info -> {
                AlertDialog.Builder(this)
                    .setTitle("정보")
                    .setMessage("201611259 컴퓨터공학과 남궁건")
                    .setPositiveButton("닫기") { dialog, i ->
                        dialog.dismiss()
                    }.create().show()
                return true
            }
        }



        return super.onOptionsItemSelected(item)
    }


    private var lastBackBtnPressed: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() - lastBackBtnPressed < 1000) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "한번 더 뒤로가기 버튼을 누르면 앱을 종료합니다", Toast.LENGTH_SHORT).show()
            lastBackBtnPressed = System.currentTimeMillis()
        }
    }
}