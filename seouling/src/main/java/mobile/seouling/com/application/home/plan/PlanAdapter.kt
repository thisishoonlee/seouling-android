package mobile.seouling.com.application.home.plan

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_main_plan.view.*
import mobile.seouling.com.R
import mobile.seouling.com.application.data.vo.Plan
import mobile.seouling.com.base_android.inflate

class PlanAdapter(val context: Context) : ListAdapter<Plan, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        parent.inflate(R.layout.item_main_plan).let {
            return PlanViewHolder(it)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < itemCount && holder is PlanViewHolder) {
            val item = getItem(position)
            holder.planDate.text = context.getString(R.string.plan_date, item.start, item.end)
            holder.planTitle.text = item.name
            Glide.with(context)
                    .load(item.picture)
                    .apply(RequestOptions.centerCropTransform())
                    .into(holder.planImage)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Plan>() {
            override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean =
                oldItem == newItem
        }
    }

    private class PlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val planDate = itemView.planDate
        val planTitle = itemView.planTitle
        val planImage = itemView.planImage
    }
}