package com.growd25.homework.features.postdetails

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.growd25.vkclient.R
import com.growd25.vkclient.features.global.model.PostItem
import com.growd25.vkclient.utils.argument
import com.growd25.vkclient.utils.visibility
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_post_details.*
import javax.inject.Inject

class PostDetailsFragment : Fragment(R.layout.fragment_post_details) {

    var postItem: PostItem by argument(POST_ITEM_KEY)

    private val postDetailsAdapter: PostDetailsAdapter = PostDetailsAdapter()

    @Inject
    lateinit var postDetailsStateMachine: PostDetailsStateMachine

    private val postDetailsViewModel: PostDetailsViewModel by viewModels {
        PostDetailsViewModelFactory(postDetailsStateMachine, postItem)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        retryButton.setOnClickListener {
            postDetailsViewModel.acceptAction(
                PostDetailsStateMachine.Action.LoadCommentaryAction(
                    postItem
                )
            )
        }
        sendButton.setOnClickListener {
            postDetailsViewModel.acceptAction(
                PostDetailsStateMachine.Action.SendCommentaryAction(
                    postId = postItem.id,
                    commentaryMessage = commentaryEditText.text.toString(),
                    ownerId = postItem.ownerId
                )
            )
        }
        postDetailsRecyclerView.adapter = postDetailsAdapter
        postDetailsViewModel.posts.observe(viewLifecycleOwner, ::consumeState)
        commentaryEditText.addTextChangedListener { text ->
            val isTextNotNullOrBlank = !text.isNullOrBlank()
            sendButton.isEnabled = isTextNotNullOrBlank
            sendButton.alpha = if (isTextNotNullOrBlank) 1f else 0.3f
        }

        sendButton.isEnabled = false
        sendButton.alpha = 0.3f
    }

    private fun consumeState(state: PostDetailsStateMachine.State) {
        when (state) {
            is PostDetailsStateMachine.State.Loading -> {
                detailsProgressbar.visibility(true)
            }
            is PostDetailsStateMachine.State.Error -> {
                detailsProgressbar.visibility(false)
                errorGroup.visibility(true)
            }
            is PostDetailsStateMachine.State.Success -> {
                errorGroup.visibility(false)
                detailsProgressbar.visibility(false)
                successGroup.visibility(true)
                val list = mutableListOf<Any>()
                list.add(postItem)
                list.addAll(state.comments)
                postDetailsAdapter.items = list
            }
        }
    }

    companion object {
        private const val POST_ITEM_KEY = "POST_ITEM_KEY"
        fun newInstance(postItem: PostItem) = PostDetailsFragment().apply {
            arguments = Bundle().apply {
                putParcelable(POST_ITEM_KEY, postItem)
            }
        }
    }
}
