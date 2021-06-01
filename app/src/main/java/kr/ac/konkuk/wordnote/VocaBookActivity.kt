package kr.ac.konkuk.wordnote

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVocaBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "단어장 관리"

        binding.addVocaBtn.setOnClickListener {
            val intent = Intent(this, VocaAddActivity::class.java)
            if (currentBookName != BOOK_ENTIRE) {
                intent.putExtra(VocaAddActivity.EXTRA_KEY_BOOKNAME, currentBookName)
            }

            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_book, menu)
        Toast.makeText(this, "메뉴를 추가하려면 우측 상단 추가 버튼을 누르세요", Toast.LENGTH_SHORT).show()
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_create_book-> {
                val intent = Intent(this, AddVocaBookActivity::class.java)
                startActivity(intent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        VocaManager.useInstance(this) { manager ->
            initTabs(manager.getVocaBookList())
        }
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
        binding.booksViewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager, FragmentStatePagerAdapter.POSITION_NONE) {
            override fun getCount(): Int {
                return fragments.size
            }

            override fun getItem(position: Int): Fragment {
                val fragment= fragments[position]
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
        binding.booksViewPager.offscreenPageLimit= 0
        binding.booksViewPager.clearOnPageChangeListeners()
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
}