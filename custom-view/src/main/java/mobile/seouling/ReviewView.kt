package mobile.seouling

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.view_review.view.*
import mobile.seouling.com.base.R


class ReviewView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    init {
        LayoutInflater.from(context).inflate(R.layout.view_review, this, true)
    }

    fun setReview(
            nickname: String,
            profile: String,
            starCount: Int,
            content: String
    ) {
        userReview.text = content
        setUser(nickname, profile)
        setStar(starCount)
    }

    private fun setUser(
            nickname: String,
            profile: String
    ) {
        Glide.with(this)
                .load(profile)
                .apply(RequestOptions.circleCropTransform())
                .into(userProfile)
        username.text = nickname
    }

    private fun setStar(
            count: Int
    ) {
        listOf(star1, star2, star3, star4, star5)
                .map {
                    it.setBackgroundResource(R.drawable.ic_star_n)
                    it
                }
                .take(count)
                .forEach {
                    it.setBackgroundResource(R.drawable.ic_star_s)
                }
    }
}