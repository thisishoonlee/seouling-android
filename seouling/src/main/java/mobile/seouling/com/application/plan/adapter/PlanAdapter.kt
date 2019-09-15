package mobile.seouling.com.application.plan.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_plan.view.*
import mobile.seouling.com.R
import mobile.seouling.com.application.data.vo.Spot
import mobile.seouling.com.base_android.inflate

class PlanAdapter(
        val context: Context
) : ListAdapter<Spot, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Spot>() {
            override fun areItemsTheSame(oldItem: Spot, newItem: Spot): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Spot, newItem: Spot): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PlanViewHolder(parent.inflate(R.layout.item_plan, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is PlanViewHolder && position < itemCount) {
            val item = getItem(position)
            holder.spotTitle.text = item.name
            Glide.with(context)
                    .load(item.picture)
                    .apply(RequestOptions.centerCropTransform())
                    .into(holder.spotImage)
        }
    }

    class PlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val spotTitle = itemView.spotTitle
        val spotImage = itemView.spotImage
    }
}
