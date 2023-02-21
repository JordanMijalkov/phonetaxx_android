package com.phonetaxx.adapter

import android.text.TextUtils
import android.util.Log
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.phonetaxx.R
import com.phonetaxx.listener.BaseRecyclerListener

/**
 * Generic Recycler Adapter which handles filtering and contains default reusable methods.
 *
 * @param <ItemType>     Data type of Your List
 * @param <ListenerType> Recycler Item Listener Data Type
</ListenerType></ItemType> */
abstract class FilterableAdapter<ItemType, ListenerType : BaseRecyclerListener<ItemType>>(listener: ListenerType) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var listener: ListenerType
        protected set
    var allItems: ArrayList<ItemType?>
        private set
    var filteredItems: ArrayList<ItemType?>
        private set
    private lateinit var itemFilter: ItemFilter
    var filteredString: String? = null

    var emptyErrorMsg: Int = 0
    var noSearchDataFoundMsg: Int = 0

    init {
        this.listener = listener
        this.filteredString = ""
        this.allItems = ArrayList()
        this.filteredItems = ArrayList()

        this.emptyErrorMsg = R.string.no_data_available
        this.noSearchDataFoundMsg = R.string.no_search_result_available

        val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()

                if (allItems.size == 0) {
                    listener.showEmptyDataView(emptyErrorMsg)
                } else {
                    listener.showEmptyDataView(noSearchDataFoundMsg)
                }
            }
        }

        this.registerAdapterDataObserver(adapterDataObserver)
    }

    /**
     * Method is used for displaying values to UI
     *
     * @param holder Generic RequestViewHolder
     * @param val    Generic DataType
     */
    abstract fun onBindData(holder: RecyclerView.ViewHolder, item: ItemType?)

    /**
     * Overriding default oncreateViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return onBindViewHolder(parent, viewType)
    }

    /**
     * Define raw layouts inside this method.
     *
     * @param parent
     * @param viewType
     * @return
     */
    abstract fun onBindViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    /**
     * Overriding default onBindViewHolder
     *
     * This will always call onBindData method to display value
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener { v ->
            listener.onRecyclerItemClick(
                v,
                holder.adapterPosition,
                filteredItems[holder.adapterPosition]
            )
        }
        // implementation of recycler item click event. It will trigger onRecyclerItemClick() method always.
        onBindData(holder, filteredItems[holder.adapterPosition])
    }

    /**
     * @return returns items size
     */
    override fun getItemCount(): Int {
        return filteredItems.size
    }

    fun getItemAt(position: Int): ItemType? {
        return if (filteredItems.size > position) {
            filteredItems[position]
        } else null
    }

    /**
     * Set Items to list
     *
     * @param items
     */
    fun setItems(items: List<ItemType?>) {
        this.filteredItems = ArrayList(items)
        this.allItems = ArrayList(items)
        notifyDataSetChanged()
    }

    /**
     * Add All Items to list
     *
     * @param items
     */
    fun addItems(items: ArrayList<ItemType?>) {
        this.filteredItems.addAll(items)
        this.allItems.addAll(items)
        notifyDataSetChanged()
    }

    fun addItemAtFirst(item: ItemType?) {
        this.filteredItems.add(0, item)
        this.allItems.add(0, item)
        notifyDataSetChanged()
    }

    fun addAllItemAtFirst(item: ArrayList<ItemType?>) {
        this.filteredItems.addAll(0, item)
        this.allItems.addAll(0, item)
        notifyDataSetChanged()
    }

    fun addItemsWithFilter(items: ArrayList<ItemType?>) {
        this.allItems.addAll(items)
        if (TextUtils.isEmpty(filteredString)) {
            this.filteredItems.addAll(items)
            notifyDataSetChanged()
        } else {
            filter.filter(filteredString)
        }
    }


    /**
     * Add Item at Particular index
     *
     * @param position
     * @param item
     */
    fun addItem(position: Int, item: ItemType?) {
        allItems.add(item)
        if (position > filteredItems.size) {
            filteredItems.add(item)
        } else {
            filteredItems.add(position, item)
        }
        notifyDataSetChanged()
    }

    /**
     * Add item at last index
     *
     * @param item
     */
    fun addItem(item: ItemType?) {
        this.filteredItems.add(item)
        this.allItems.add(item)
        notifyDataSetChanged()
    }

    /**
     * Remove Last element of list
     */
    fun removeLastItem() {
        var removedItem: ItemType? = null
        if (filteredItems.size > 0) {
            removedItem = filteredItems[filteredItems.size - 1]
            filteredItems.removeAt(filteredItems.size - 1)
            notifyDataSetChanged()
        }
        if (removedItem != null) {
            allItems.remove(removedItem)
        }
    }

    /**
     * Remove Element from particular index
     *
     * @param position
     */
    fun removeItemAt(position: Int) {
        var removedItem: ItemType? = null
        if (filteredItems.size > position) {
            removedItem = filteredItems[position]
            filteredItems.removeAt(position)
            notifyDataSetChanged()
        }
        if (removedItem != null) {
            allItems.remove(removedItem)
        }
    }

    fun remove(removedItem: ItemType?) {
        filteredItems.remove(removedItem)
        allItems.remove(removedItem)
    }

    fun removeItemAtWithAnim(position: Int) {
        var removedItem: ItemType? = null
        if (filteredItems.size > position) {
            removedItem = filteredItems[position]
            filteredItems.removeAt(position)
            notifyItemRemoved(position)
        }
        if (removedItem != null) {
            allItems.remove(removedItem)
        }
    }

    fun addProgressView() {
        filteredItems.add(null)
        allItems.add(null)
        notifyDataSetChanged()
    }

    fun removeProgressView() {
        filteredItems.remove(null)
        allItems.remove(null)
        notifyDataSetChanged()
    }

    /**
     * remove All items of list
     */
    fun removeAllItems() {
        allItems.clear()
        filteredItems.clear()
        notifyDataSetChanged()
    }

    /**
     * Remove Element from particular index
     *
     * @param position
     */
    fun updateItemAt(position: Int, itemType: ItemType?) {
        if (filteredItems.size > position) {
            filteredItems.set(position, itemType)
            notifyDataSetChanged()
        }
        if (itemType != null) {
            for (i in allItems.indices) {
                if (allItems[i] == itemType) {
                    allItems[i] = itemType
                    break
                }
            }
        }
    }

    fun getFilteredAllItems(): ArrayList<ItemType?> {
        return filteredItems
    }

    override fun getFilter(): Filter {
        return itemFilter
    }

    /**
     * Put String value which you want to compare inside filtering
     *
     * If you want to compare multiple fields value specify them in comma seperated string
     *
     * @param item
     * @return
     */
    abstract fun compareFieldValue(item: ItemType?, searchItemList: ArrayList<String>): ArrayList<String>

    /**
     * Generic Filterable class which will trigger events
     * occording to input string.
     */
    private inner class ItemFilter : Filter() {

        override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {

            filteredString = charSequence.toString().trim { it <= ' ' }

            val filterString = filteredString!!.toLowerCase()
            val filterResults = Filter.FilterResults()

            val mMasterList = allItems

            val mResultedList = ArrayList<ItemType?>()

            if (filterString.isEmpty()) {
                mResultedList.clear()
                mResultedList.addAll(mMasterList)
            } else {
                var filterableString: String

                for (i in mMasterList.indices) {

                    if (mMasterList[i] == null) {
                        continue
                    }
                    val compareFields = compareFieldValue(mMasterList[i], ArrayList())

                    for (itemValue in compareFields) {
                        filterableString = itemValue.toLowerCase()
                        if (filterableString.contains(filterString)) {
                            mResultedList.add(mMasterList[i])
                            break
                        }
                    }
                }
            }
            filterResults.values = mResultedList

            return filterResults
        }


        override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
            filteredItems = filterResults.values as ArrayList<ItemType?>
            Log.e("*****", "publishResults: " + filteredItems.size)
            notifyDataSetChanged()
        }
    }
}
