package mobile.seouling.com.application.home.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.frag_home_plan.*
import mobile.seouling.com.R
import mobile.seouling.com.application.common.fragment.BaseFragment
import mobile.seouling.com.application.data.vo.Plan
import mobile.seouling.com.application.home.plan.PlanAdapter

class HomePlanFragment : BaseFragment() {

    private lateinit var adapter: PlanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = PlanAdapter(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_home_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageUrl = "https://post-phinf.pstatic.net/MjAxNzA1MTVfMjA5/MDAxNDk0ODI3MjU5MjI1._6Ib0qzGKzS8FuX-KUWgain5I3XYMbSlrTHJJZpGRmog.p3YTe8kl62u9MkbbZbVAdspWjm29_rxf5cEE1cfuoOYg.PNG/%EA%B2%BD%EB%B3%B5%EA%B6%81_%EC%A0%84%EA%B2%BD.PNG?type=w1200"
        planRecycler.adapter = adapter
        adapter.submitList(mutableListOf(Plan(1, "test", "Dummy Data", imageUrl, "2019-09-09", "2019-09-09", "2019-09-10"),
                Plan(2, "test", "Dummy Data2", imageUrl, "2019-09-09", "2019-09-09", "2019-09-10"),
                Plan(3, "test", "Dummy Data3", imageUrl, "2019-09-09", "2019-09-10", "2019-09-11"),
                Plan(4, "test", "Dummy Data4", imageUrl, "2019-09-09", "2019-09-11", "2019-09-12")))
    }
}