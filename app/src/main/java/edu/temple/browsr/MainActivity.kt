package edu.temple.browsr

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), TabFragment.ControlInterface{

    private val viewPager: ViewPager2 by lazy {
        findViewById(R.id.viewPager)
    }

    private val recyclerView: RecyclerView? by lazy {
        findViewById(R.id.recyclerView)
    }

    private val tabLayout: TabLayout? by lazy {
        findViewById(R.id.tabLayout)
    }

    private val browserViewModel : BrowserViewModel by lazy {
        ViewModelProvider(this)[BrowserViewModel::class.java]
    }
    private lateinit var bookmarkManager: BookmarkManager

    private val bookmarkLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val url = result.data?.getStringExtra("bookmark_url")
            if (!url.isNullOrEmpty()) {
                // new tab first
                browserViewModel.addTab()

                // get the index of the newly created tab
                val newTabIndex = browserViewModel.getNumberOfTabs() - 1

                // set the ViewPager to show the new tab
                viewPager.setCurrentItem(newTabIndex, true)

                viewPager.post {
                    // Get the new fragment
                    val newFragment = supportFragmentManager.findFragmentByTag("f$newTabIndex")
                    if (newFragment is TabFragment) {
                        newFragment.loadUrl(url)
                    } else {
                        Toast.makeText(this, "Failed to load bookmark. Try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Invalid bookmark URL.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bookmarkManager = BookmarkManager(this)

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = browserViewModel.getNumberOfTabs()

            // Each TabFragment maintains an ID assigned at instantiation.
            // This is used to notify the parent that a specific tab wants to update its title
            override fun createFragment(position: Int) = TabFragment.newInstance(position)
        }

        findViewById<ImageView>(R.id.bookmarkButton).setOnClickListener {
            addBookmark()
        }

        findViewById<ImageView>(R.id.showbookmarkButton).setOnClickListener {
            showBookmarks()
        }

        findViewById<ImageView>(R.id.shareButton).setOnClickListener {
            shareCurrentPage()
        }
        // Only if present (portrait)
        tabLayout?.run{

            // Keeps ViewPager and TabLayout selection in sync automatically
            // lambda updates title
            TabLayoutMediator(this, viewPager) { tab, position ->
                tab.text = browserViewModel.getPage(position).title
            }.attach()
        }

        // Only if present (landscape)
        recyclerView?.run{
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = PageAdapter(browserViewModel.tabs){
                viewPager.setCurrentItem(it, true)
            }
        }

        // Observe titles and update TabLayout or RecyclerView
        browserViewModel.getUpdate().observe(this) {
            viewPager.adapter?.notifyItemChanged(it)
            recyclerView?.adapter?.notifyItemChanged(it)
        }

    } private fun addBookmark() {
        val currentTab = viewPager.currentItem
        val currentPage = browserViewModel.getPage(currentTab)

        if (currentPage.url.isEmpty()) {
            Toast.makeText(this, "No webpage to bookmark", Toast.LENGTH_SHORT).show()
            return
        }

        val bookmark = Bookmark(currentPage.title, currentPage.url)
        val result = bookmarkManager.saveBookmark(bookmark)

        if (result) {
            Toast.makeText(this, "Bookmark saved", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Bookmark already exists", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showBookmarks() {
        val intent = Intent(this, BookmarkActivity::class.java)
        bookmarkLauncher.launch(intent)
    }

    private fun shareCurrentPage() {
        val currentPage = browserViewModel.getPage(viewPager.currentItem)

        if (currentPage.url.isEmpty()) {
            Toast.makeText(this, "No webpage to share", Toast.LENGTH_SHORT).show()
            return
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, currentPage.url)
            putExtra(Intent.EXTRA_TITLE, currentPage.title)
        }
        startActivity(Intent.createChooser(shareIntent, "Share webpage"))
    }

    // TabFragment.ControlInterface callback
    override fun newPage() {
        browserViewModel.addTab()
        viewPager.setCurrentItem(browserViewModel.getNumberOfTabs() - 1, true)
    }
}