package com.alexandre.myquotes.view.quoteslist.list

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alexandre.myquotes.R
import com.alexandre.myquotes.model.Quote
import kotlinx.android.synthetic.main.quote_item.view.*

class QuoteAdapter(private val quotes: ArrayList<Quote>?, updatePosition: (fromPosition: Int, toPosition: Int) -> Unit) : RecyclerView.Adapter<QuoteAdapter.ViewHolder>() {
    val updatePosition = updatePosition

    override fun getItemCount(): Int {
        if (quotes != null) {
            return quotes.size
        }
        else
            return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
                .inflate(R.layout.quote_item, parent, false)
        return ViewHolder(inflatedView)
    }

    fun onItemMove(fromPosition: Int?, toPosition: Int?): Boolean {
        if (fromPosition != null && toPosition != null) {
            notifyItemMoved(fromPosition, toPosition)
            updatePosition.invoke(fromPosition, toPosition)
        }
        return true
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.txt_body.text = quotes?.get(position)?.body ?: ""
        if(quotes?.get(position)?.dialogue ?: false)
        {
            holder.itemView.txt_dialog.text = holder.itemView.context.getString(R.string.dialogue)
        }
        else
        {
            holder.itemView.txt_dialog.text = holder.itemView.context.getString(R.string.not_dialogue)
        }

        if(quotes?.get(position)?.dialogue ?: false)
        {
            holder.itemView.txt_private.text = holder.itemView.context.getString(R.string.private_quote)
        }
        else
        {
            holder.itemView.txt_private.text = holder.itemView.context.getString(R.string.not_private_quote)
        }

        holder.itemView.txt_tags.text = holder.itemView.context.getString(R.string.tags, quotes?.get(position)?.tags?.joinToString())

        holder.itemView.txt_url.text = quotes?.get(position)?.url ?: ""

        holder.itemView.txt_fav_nb.text = holder.itemView.context.getString(R.string.favorites_count, quotes?.get(position)?.favorites_count)

        holder.itemView.txt_upvotes.text = holder.itemView.context.getString(R.string.upvotes_count, quotes?.get(position)?.upvotes_count)

        holder.itemView.txt_downvotes.text = holder.itemView.context.getString(R.string.downvotes_count, quotes?.get(position)?.downvotes_count)

        holder.itemView.txt_author.text = quotes?.get(position)?.author ?: ""

        holder.itemView.txt_author_permalink.text = quotes?.get(position)?.author_permalink ?: ""

        holder.itemView.txt_id.text = holder.itemView.context.getString(R.string.id, quotes?.get(position)?.id)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }

    class QuoteTouchHelperCallback(adapter: QuoteAdapter) : ItemTouchHelper.Callback() {

        private val mAdapter: QuoteAdapter = adapter

        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

        override fun isItemViewSwipeEnabled(): Boolean {
            return false
        }

        override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
            mAdapter.onItemMove(viewHolder?.getAdapterPosition(), target?.getAdapterPosition());
            return true;
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
        }

    }
}