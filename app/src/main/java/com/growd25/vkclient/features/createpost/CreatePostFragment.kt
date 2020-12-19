package com.growd25.vkclient.features.createpost

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.growd25.vkclient.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_create_post.*
import javax.inject.Inject

class CreatePostFragment : Fragment(R.layout.fragment_create_post) {

    private var listener: OnPostWriteListener? = null

    @Inject
    lateinit var createViewModelFactory: CreateViewModelFactory

    private val createPostViewModel: CreatePostViewModel by viewModels { createViewModelFactory }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        listener = context as? OnPostWriteListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cancelImageView.setOnClickListener {
            listener?.closeCreatePostFragment()
        }
        createImageView.setOnClickListener {
            createPostViewModel.onPostWritten(text = createPostTextInputEdText.text.toString())
        }
        createPostViewModel.onPostCreated.observe(viewLifecycleOwner) {
            listener?.closeCreatePostFragment()
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnPostWriteListener {
        fun closeCreatePostFragment()
    }

    companion object {
        fun newInstance() = CreatePostFragment()
    }
}
