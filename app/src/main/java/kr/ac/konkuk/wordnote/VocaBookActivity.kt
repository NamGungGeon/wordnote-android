package kr.ac.konkuk.wordnote

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kr.ac.konkuk.wordnote.databinding.ActivityVocaBookBinding

class VocaBookActivity : AppCompatActivity() {
    lateinit var binding: ActivityVocaBookBinding
    val BOOK_ENTIRE = "전체"
    lateinit var wordList: ArrayList<Voca>

    val tabs = ArrayList<TabLayout.Tab>()
    val fragments = ArrayList<VocaListFragment>()

    private var currentBookName: String = BOOK_ENTIRE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVocaBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "단어장 관리"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_book, menu)
        Toast.makeText(this, "메뉴를 추가하려면 우측 상단 추가 버튼을 누르세요", Toast.LENGTH_SHORT).show()
        return true
    }

    override fun onResume() {
        super.onResume()

        VocaManager.useInstance(this) { manager ->
            initTabs(arrayListOf("토익", "토플"))
        }
    }

    private fun initTabs(books: ArrayList<String>) {
        if (fragments.isNotEmpty() && tabs.isNotEmpty()) {
            var tabIterCnt = 0
            tabs.map { tab ->
                val currentPosition = tabIterCnt
                val fragment = fragments[currentPosition]

                VocaManager.useInstance(this@VocaBookActivity) { manager ->
                    fragment.setVocaList(
                        filterVocaAsBook(
                            tabs[currentPosition].contentDescription!!.toString(),
                            manager.vocaList
                        )
                    )
                }
                tabIterCnt++
            }

            return
        }

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

            val currentPosition = tabIterCnt
            VocaManager.useInstance(this@VocaBookActivity) { manager ->
                fragment.setVocaList(
                    filterVocaAsBook(
                        tabs[currentPosition].contentDescription!!.toString(),
                        manager.vocaList
                    )
                )
            }
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
        binding.booksViewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getCount(): Int {
                return fragments.size
            }

            override fun getItem(position: Int): Fragment {
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
        binding.booksViewPager.offscreenPageLimit = fragments.size
    }

    private fun filterVocaAsBook(bookName: String, vocaList: ArrayList<Voca>): ArrayList<Voca> {
        if (bookName == BOOK_ENTIRE)
            return vocaList

        return ArrayList(vocaList.filter {
            it.books != null && it.books!!.contains(bookName)
        }.toList())
    }
}