package mobile.seouling.com.framework.mvp

interface BaseView<in T> {
    fun setPresenter(presenter: T)
}