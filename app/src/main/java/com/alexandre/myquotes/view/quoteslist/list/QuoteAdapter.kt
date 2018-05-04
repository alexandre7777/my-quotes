package com.alexandre.myquotes.view.quoteslist.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alexandre.myquotes.R
import com.alexandre.myquotes.model.Quote
import kotlinx.android.synthetic.main.quote_item.view.*

class QuoteAdapter(private val quotes: ArrayList<Quote>?) : RecyclerView.Adapter<QuoteAdapter.ViewHolder>() {
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.txtBody.text = quotes?.get(position)?.body ?: ""
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }
}