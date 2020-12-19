package com.growd25.vkclient.features.createpost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.growd25.vkclient.repository.PostRepository
import com.growd25.vkclient.repository.ProfileRepository
import com.growd25.vkclient.utils.connect
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class CreatePostViewModel(
    private val postRepository: PostRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _onPostCreated: MutableLiveData<Boolean> = MutableLiveData()
    val onPostCreated: LiveData<Boolean> = _onPostCreated
    private val compositeDisposable = CompositeDisposable()

    fun onPostWritten(text: String) {
        profileRepository.getProfileId()
            .flatMapCompletable { ids ->
                val id: Int? = ids.firstOrNull()
                if (id != null) {
                    postRepository.createPost(id, text)
                        .onErrorComplete()
                } else {
                    Completable.complete()
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                _onPostCreated.value = true
            }
            .connect(compositeDisposable)

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
