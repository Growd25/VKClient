package com.growd25.homework.features.global.views

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.growd25.vkclient.R
import com.growd25.vkclient.features.global.model.PostItem
import com.growd25.vkclient.utils.dpToPixel

class PostView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private val contentPaddingTop = 16.dpToPixel
    private val contentPaddingHorizontal = 16.dpToPixel
    private val bottomButtonMargin = 8.dpToPixel
    private val communityAvatarMarginRight = 8.dpToPixel
    private val communityAvatarMarginBottom = 8.dpToPixel

    var onFavoritesButtonClicked: (() -> Unit)? = null
    private var postItem: PostItem? = null
    private var isImageLoaded: Boolean = false

    private val communityAvatar = ImageView(context).apply {
        id = R.id.community_avatar
        val communityAvatarSize = 52.dpToPixel
        layoutParams = LayoutParams(communityAvatarSize, communityAvatarSize)
    }

    private val communityTitle = TextView(context).apply {
        id = R.id.community_title
        ellipsize = TextUtils.TruncateAt.END
        setSingleLine()
        setTextAppearance(R.style.TextAppearance_MaterialComponents_Subtitle1)
    }

    private val postDate = TextView(context).apply {
        id = R.id.post_date
        setTextAppearance(R.style.TextAppearance_MaterialComponents_Caption)
    }
    private val postText = TextView(context).apply {
        setPadding(contentPaddingHorizontal, 0, contentPaddingHorizontal, 8.dpToPixel)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1)
        id = R.id.post_text
    }
    private val postImage = ImageView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        id = R.id.post_image
    }
    private val favoritesButton = TextView(context).apply {
        setOnClickListener { onFavoritesButtonClicked?.invoke() }
        setPadding(
            16.dpToPixel,
            8.dpToPixel,
            8.dpToPixel,
            8.dpToPixel
        )
        id = R.id.favorites_button
        gravity = Gravity.CENTER
        setTextAppearance(R.style.TextAppearance_MaterialComponents_Subtitle2)
        compoundDrawablePadding = 4.dpToPixel
        setTextColor(ContextCompat.getColor(context, R.color.colorTextPostActionButton))
        textSize = 16f
        setCompoundDrawablesWithIntrinsicBounds(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_favorite_border,
                null
            ),
            null,
            null,
            null
        )
    }

    private val commentsButton = TextView(context).apply {
        id = R.id.comments_button
        setPadding(8.dpToPixel)
        gravity = Gravity.CENTER
        compoundDrawablePadding = 4.dpToPixel
        setTextAppearance(R.style.TextAppearance_MaterialComponents_Subtitle2)
        textSize = 16f
        setTextColor(ContextCompat.getColor(context, R.color.colorTextPostActionButton))
        setCompoundDrawablesWithIntrinsicBounds(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_chat_bubble_outline_black_24dp,
                null
            ),
            null,
            null,
            null
        )
    }

    init {
        addView(communityAvatar)
        addView(communityTitle)
        addView(postDate)
        addView(postText)
        addView(postImage)
        addView(favoritesButton)
        addView(commentsButton)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val postImageModelHeight = postItem?.postImage?.postImageHeight ?: 0
        val postImageModelWidth = postItem?.postImage?.postImageWidth ?: 0

        val usedWidth = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val postImageHeight =
            if (postImageModelWidth != 0) (postImageModelHeight * usedWidth) / postImageModelWidth else 0
        val postImageHeightMeasureSpec = MeasureSpec.makeMeasureSpec(postImageHeight, MeasureSpec.EXACTLY)
        val postImageWidthMeasureSpec = MeasureSpec.makeMeasureSpec(usedWidth, MeasureSpec.EXACTLY)

        measureChild(communityAvatar, widthMeasureSpec, heightMeasureSpec)

        val communityTitleWidth =
            usedWidth - (contentPaddingHorizontal * 2) - communityAvatar.measuredWidth - communityAvatarMarginRight

        val communityTitleWidthSpec = MeasureSpec.makeMeasureSpec(communityTitleWidth, MeasureSpec.EXACTLY)

        communityTitle.measure(communityTitleWidthSpec, heightMeasureSpec)
        measureChild(postDate, communityTitleWidth, heightMeasureSpec)
        measureChild(postText, widthMeasureSpec, heightMeasureSpec)

        postImage.measure(postImageWidthMeasureSpec, postImageHeightMeasureSpec)

        measureChild(favoritesButton, widthMeasureSpec, heightMeasureSpec)
        measureChild(commentsButton, widthMeasureSpec, heightMeasureSpec)
        val bottomButtonHeight = favoritesButton.measuredHeight
        val usedHeight =
            contentPaddingTop +
                    communityAvatar.measuredHeight +
                    communityAvatarMarginBottom +
                    postText.measuredHeight +
                    postImage.measuredHeight +
                    bottomButtonHeight +
                    (bottomButtonMargin * 2)

        setMeasuredDimension(usedWidth, usedHeight)
        onMeasured()
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val topBorder = contentPaddingTop
        val leftBorder = contentPaddingHorizontal

        val communityAvatarRight = leftBorder + communityAvatar.measuredWidth
        val communityAvatarBottom = topBorder + communityAvatar.measuredHeight

        communityAvatar.layout(
            leftBorder,
            topBorder,
            communityAvatarRight,
            communityAvatarBottom
        )
        val communityAvatarCenterY = (topBorder + communityAvatarBottom) / 2

        val communityTitleLeft = communityAvatarRight + communityAvatarMarginRight
        communityTitle.layout(
            communityTitleLeft,
            communityAvatarCenterY - communityTitle.measuredHeight,
            communityTitleLeft + communityTitle.measuredWidth,
            communityAvatarCenterY
        )

        postDate.layout(
            communityTitleLeft,
            communityAvatarCenterY,
            communityTitleLeft + postDate.measuredWidth,
            communityAvatarCenterY + postDate.measuredHeight
        )
        val postTextTop = communityAvatarBottom + communityAvatarMarginBottom
        val postTextBottom = postTextTop + postText.measuredHeight
        postText.layout(
            0,
            postTextTop,
            postText.measuredWidth,
            postTextBottom
        )
        postImage.layout(
            0,
            postTextBottom,
            postImage.measuredWidth,
            postTextBottom + postImage.measuredHeight
        )
        val bottomButtonBottom = measuredHeight - bottomButtonMargin
        val bottomButtonTop = bottomButtonBottom - favoritesButton.measuredHeight
        val favoritesButtonRight = favoritesButton.measuredWidth
        favoritesButton.layout(
            0,
            bottomButtonTop,
            favoritesButtonRight,
            bottomButtonBottom
        )
        val bottomButtonMargin = 16.dpToPixel
        val commentsButtonLeft = favoritesButtonRight + bottomButtonMargin
        val commentsButtonRight = commentsButtonLeft + commentsButton.measuredWidth
        commentsButton.layout(
            commentsButtonLeft,
            bottomButtonTop,
            commentsButtonRight,
            bottomButtonBottom
        )
    }

    fun bind(post: PostItem) {
        setFavoritesIcon(post)
        isImageLoaded = false
        postItem = post
        Glide.with(communityAvatar)
            .load(post.groupImageUrl)
            .circleCrop()
            .placeholder(R.drawable.ic_unicorn)
            .into(communityAvatar)
        communityTitle.text = post.groupName
        postDate.text = post.date
        postText.text = post.postText
        favoritesButton.text = post.likesCount.toString()
        commentsButton.text = post.commentaryCount.toString()
        requestLayout()
    }

    private fun setFavoritesIcon(post: PostItem) {
        val favoritesImageResId: Int = if (post.isFavorite) {
            R.drawable.ic_favorite
        } else {
            R.drawable.ic_favorite_border
        }
        val drawable = ResourcesCompat.getDrawable(resources, favoritesImageResId, null)
        drawable?.setBounds(0, 0, 24.dpToPixel, 24.dpToPixel)
        favoritesButton.setCompoundDrawables(drawable, null, null, null)
    }

    private fun onMeasured() {
        if (!isImageLoaded) {
            Glide.with(postImage)
                .load(postItem?.postImage?.postImageUrl)
                .into(postImage)
            isImageLoaded = true
        }
    }
}
