package kr.ac.konkuk.wordnote

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kr.ac.konkuk.wordnote.databinding.ActivityVocaBookBinding

class VocaBookActivity : AppCompatActivity() {
    lateinit var binding: ActivityVocaBookBinding
    val BOOK_ENTIRE = "전체"

    val tabs = ArrayList<TabLayout.Tab>()
    val fragments = ArrayList<VocaListFragment>()

    private var currentBookName: String = BOOK_ENTIRE
    private var mode: String? = null
    private val selectedVocaLost = ArrayList<Voca>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVocaBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "단어장 관리"
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun init() {
        mode = null
        selectedVocaLost.clear()
        binding.booksTabLayout.visibility = View.VISIBLE

        VocaManager.useInstance(this) { manager ->
            initTabs(manager.getVocaBookList())

            binding.addVocaBtn.text = "단어 추가"
            binding.customActionBtn.text = ""
            binding.customActionBtn.visibility = View.GONE
            binding.addVocaBtn.setOnClickListener {
                if (mode != VocaRecylcerViewAdapter.MODE_CHECK) {
                    mode = VocaRecylcerViewAdapter.MODE_CHECK
                    val entireBookFragment = fragments[0]
                    tabs[0].select()
                    binding.booksTabLayout.visibility = View.GONE
                    binding.booksViewPager.adapter = object : FragmentStatePagerAdapter(
                        supportFragmentManager,
                        FragmentStatePagerAdapter.POSITION_NONE
                    ) {
                        override fun getCount(): Int {
                            return 1
                        }

                        override fun getItem(position: Int): Fragment {
                            VocaManager.useInstance(this@VocaBookActivity) { manager ->
                                entireBookFragment.setVocaList(manager.vocaList)
                                entireBookFragment.startListeningSelectedVoca {
                                    selectedVocaLost.clear()
                                    selectedVocaLost.addAll(it)
                                }
                            }
                            return entireBookFragment
                        }
                    }


                    binding.addVocaBtn.text = "다른 단어장에 추가"

                    binding.customActionBtn.visibility = View.VISIBLE
                    binding.customActionBtn.text = "새 단어 추가"
                    binding.customActionBtn.setOnClickListener {
                        val openVocaAddActivity= {
                            val intent = Intent(this, VocaAddActivity::class.java)
                            if (currentBookName != BOOK_ENTIRE) {
                                intent.putExtra(VocaAddActivity.EXTRA_KEY_BOOKNAME, currentBookName)
                            }
                            startActivity(intent)
                        }
                        if(selectedVocaLost.isNotEmpty()){
                            AlertDialog.Builder(this)
                                .setTitle("페이지 이동")
                                .setMessage("선택한 단어가 있습니다\n\n페이지를 벗어나면 선택된 단어 정보가 사라집니다. 계속하시겠습니까?")
                                .setPositiveButton("이동"){dialog, i->
                                    openVocaAddActivity()
                                    dialog.dismiss()
                                }.setNegativeButton("취소"){dialog, i->
                                    dialog.dismiss()
                                }.create().show()
                        }else{
                            openVocaAddActivity()
                        }

                    }

                    return@setOnClickListener
                } else {
                    //actually add action
                    if (selectedVocaLost.isEmpty()) {
                        Toast.makeText(this, "선택한 단어가 없습니다", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    val dialogView =
                        layoutInflater.inflate(R.layout.dialog_select_book_name, null, false)

                    val msgView: TextView = dialogView.findViewById(R.id.msg)
                    msgView.text =
                        "선택한 ${selectedVocaLost.size}개의 단어를 다음의 단어장에 추가합니다\n해당 단어장에 이미 존재하는 중복된 단어는 제외됩니다"

                    val msg2View: TextView = dialogView.findViewById(R.id.msg2)
                    var msg2 = "== 선택한 단어 리스트 == \n"
                    selectedVocaLost.map { voca ->
                        msg2 += voca.word + "\n"
                    }
                    msg2View.text = msg2

                    val spinner: Spinner = dialogView.findViewById(R.id.bookNameSpinner)
                    VocaManager.useInstance(this) { manager ->
                        spinner.adapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_dropdown_item_1line,
                            manager.getVocaBookList()
                        )

                        AlertDialog.Builder(this)
                            .setTitle("다른 단어장에 추가")
                            .setView(dialogView)
                            .setPositiveButton("이동") { dialog, i ->
                                val selectedBook = spinner.selectedItem as String
                                selectedVocaLost.map { voca ->
                                    if (!voca.books.contains(selectedBook))
                                        voca.books.add(selectedBook)
                                }
                                manager.saveWordList()
                                init()
                                dialog.dismiss()
                            }
                            .setNegativeButton("닫기") { dialog, i ->
                                dialog.dismiss()
                            }
                            .create().show()
                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_book, menu)
        Toast.makeText(this, "메뉴를 추가하려면 우측 상단 추가 버튼을 누르세요", Toast.LENGTH_SHORT).show()
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_create_book -> {
                val intent = Intent(this, AddVocaBookActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_update_book -> {
                if (currentBookName == BOOK_ENTIRE) {
                    Toast.makeText(this, "이 단어장의 이름은 변경할 수 없습니다", Toast.LENGTH_SHORT).show()
                    return false
                }

                val intent = Intent(this, UpdateVocaBookActivity::class.java)
                intent.putExtra(UpdateVocaBookActivity.EXTRA_KEY_BOOK_NAME, currentBookName)
                startActivity(intent)
                return true
            }
            R.id.menu_remove_book -> {
                if (currentBookName == BOOK_ENTIRE) {
                    Toast.makeText(this, "이 단어장은 삭제할 수 없습니다", Toast.LENGTH_SHORT).show()
                    return false
                }
                AlertDialog.Builder(this)
                    .setTitle("단어장(${currentBookName}) 삭제")
                    .setMessage("해당 단어장을 삭제합니다\n단어장에 등록된 단어들은 이 단어장과 연결이 해제됩니다")
                    .setPositiveButton("삭제") { dialog, i ->
                        VocaManager.useInstance(this) { manager ->
                            manager.removeBook(currentBookName)
                            currentBookName = BOOK_ENTIRE
                            Toast.makeText(this, "삭제되었습니다", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()


                            initTabs(manager.getVocaBookList())
                        }
                    }
                    .setNegativeButton("닫기") { dialog, i ->
                        dialog.dismiss()
                    }.create().show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initTabs(books: ArrayList<String>) {
        tabs.map { tab ->
            binding.booksTabLayout.removeTab(tab)
        }
        tabs.clear()
        fragments.clear()

        val tab = binding.booksTabLayout.newTab().setText(BOOK_ENTIRE)
        tabs.add(tab)

        books.map { book ->
            val tab = binding.booksTabLayout.newTab().setText(book)
            tab.contentDescription = book
            tabs.add(tab)
        }

        var tabIterCnt = 0
        tabs.map { tab ->
            binding.booksTabLayout.addTab(tab)

            val fragment = VocaListFragment()
            fragments.add(fragment)

            tabIterCnt++
        }
        binding.booksTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.apply {
                    if (tab.contentDescription != null)
                        currentBookName = tab.contentDescription.toString()
                }

                var tabIdx = -1
                for (idx in 0..tabs.size) {
                    if (tabs[idx] == tab) {
                        tabIdx = idx
                        break
                    }
                }
                if (tabIdx != -1) {
                    binding.booksViewPager.currentItem = tabIdx
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding.booksViewPager.adapter = object : FragmentStatePagerAdapter(
            supportFragmentManager,
            FragmentStatePagerAdapter.POSITION_NONE
        ) {
            override fun getCount(): Int {
                return fragments.size
            }

            override fun getItem(position: Int): Fragment {
                val fragment = fragments[position]
                VocaManager.useInstance(this@VocaBookActivity) { manager ->
                    fragment.setVocaList(
                        filterVocaAsBook(
                            tabs[position].contentDescription!!.toString(),
                            manager.vocaList
                        )
                    )
                }
                return fragments[position]
            }
        }
        binding.booksViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.booksTabLayout.selectTab(tabs[position])
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    private fun filterVocaAsBook(bookName: String, vocaList: ArrayList<Voca>): ArrayList<Voca> {
        if (bookName == BOOK_ENTIRE)
            return vocaList

        return ArrayList(vocaList.filter {
            it.books.contains(bookName)
        }.toList())
    }

    override fun onBackPressed() {
        if (mode != null) {
            if(selectedVocaLost.isNotEmpty()){
                AlertDialog.Builder(this)
                    .setTitle("단어 선택 모드 종료")
                    .setMessage("${selectedVocaLost.size}개의 선택한 단어가 있습니다\n\n저장하지 않고 돌아가시겠습니까?")
                    .setPositiveButton("종료"){dialog, i->
                        init()
                        dialog.dismiss()
                    }.setNegativeButton("취소"){dialog, i->
                        dialog.dismiss()
                    }.create().show()
                return
            }
            init()
            return
        }
        super.onBackPressed()
    }
}