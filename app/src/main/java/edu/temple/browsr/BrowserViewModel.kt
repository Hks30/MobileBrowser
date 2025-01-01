package edu.temple.browsr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BrowserViewModel : ViewModel(){

    private val updateProxy : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().apply {
            value = 0
        }
    }

    val tabs : ArrayList<Page> by lazy {
        ArrayList<Page>().apply {
            add(Page())
        }
    }

    fun getUpdate() : LiveData<Int> {
        return updateProxy
    }

    fun addTab() {
        tabs.add(Page())
        updateProxy.value = tabs.size - 1
    }

    fun getPage(position: Int) = tabs[position]

    fun getNumberOfTabs() = tabs.size

    fun updatePage(position: Int, page: Page) {
        tabs[position] = page
        updateProxy.value = position
    }
}