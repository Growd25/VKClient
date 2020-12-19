package com.growd25.vkclient.data.net.model

data class NewsFeedResponse(
    var response: NewsFeed? = null
)

data class NewsFeed(
    var items: List<Post>? = null,
    var groups: List<Group>? = null,
    var profiles: List<Profile>? = null,
    var next_from: String? = null
)

data class Post(
    var id: Int? = null,
    var post_id: Int? = null,
    var source_id: Int? = null,
    var owner_id: Int? = null,
    var from_id: Int? = null,
    var date: Int? = null,
    var likes: Likes? = null,
    var comments: CommentaryCount? = null,
    var is_favorite: Boolean? = null,
    var post_type: String? = null,
    var text: String? = null,
    var copy_history: List<CopyHistory>? = null,
    var attachments: List<Attachment>? = null
)

data class Profile(
    var id: Int? = null,
    var first_name: String? = null,
    var last_name: String? = null,
    var photo_100: String? = null,
)

data class Group(
    var id: Int? = null,
    var name: String? = null,
    var screen_name: String? = null,
    var is_closed: Int? = null,
    var type: String? = null,
    var is_admin: Int? = null,
    var is_member: Int? = null,
    var is_advertiser: Int? = null,
    var photo_50: String? = null,
    var photo_100: String? = null,
    var photo_200: String? = null,
)

data class CommentaryCount(
    var count: Int? = null
)

data class Sizes(
    var url: String? = null,
    var height: Int? = null,
    var width: Int? = null,
    var type: String? = null,
)

data class Attachment(
    var type: String? = null,
    var photo: Photo? = null,
)

data class Photo(
    var album_id: Int? = null,
    var date: Int? = null,
    var id: Int? = null,
    var owner_id: Int? = null,
    var has_tags: Boolean? = null,
    var access_key: String? = null,
    var post_id: Int? = null,
    var sizes: List<Sizes>? = null,
    var text: String? = null,
    var user_id: Int? = null,
)

data class Likes(
    var count: Int? = null,
    var user_likes: Int? = null,
    var can_like: Int? = null,
    var can_publish: Int? = null,
)

data class LikesResponse(
    var response: LikesCount? = null
)

data class LikesCount(
    var likes: Int? = null
)

data class ProfilePostsResponse(
    var response: ProfileItems? = null
)

data class ProfileItems(
    var count: Int? = null,
    var profiles: List<Profile>? = null,
    var items: List<Post>? = null,
)

data class CopyHistory(
    var id: Int? = null,
    var date: Int? = null,
    var text: String? = null,
    var attachments: List<Attachment>? = null,
    var owner_id: Int? = null,
    var from_id: Int? = null,
    var post_type: String? = null,
)

//response after create comment
data class CreatePostResponse(
    val response: CreatedPost? = null
)

data class CreatedPost(
    var post_id: Int? = null
)

class ProfileResponse(var response: List<ProfileItemResponse>? = null)

data class ProfileItemResponse(
    var first_name: String? = null,
    var last_name: String? = null,
    var can_access_closed: Boolean? = null,
    var is_closed: Boolean? = null,
    var photo_200: String? = null,
    var last_seen: LastSeen? = null,
    var university_name: String? = null,
    var faculty_name: String? = null,
    var id: Int,
    var domain: String,
    var bdate: String,
    var city: City,
    var country: Country?,
    var about: String,
    var career: List<Career>?,
    var university: Int,
    var faculty: Int,
    var graduation: Int
)

data class Career(
    var city_id: Int? = null,
    var country_id: Int? = null,
    var from: Int? = null,
    var group_id: Int? = null,
    var position: String? = null,
)

data class LastSeen(
    var platform: Int? = null,
    var time: Int? = null,
)

data class Country(
    var id: Int? = null,
    var title: String? = null,
)

data class City(
    var id: Int? = null,
    var title: String? = null,
)

data class PostDetailsResponse(
    val response: CommentsResponse? = null
)

data class CommentsResponse(
    var items: List<CommentItem>? = null,
    var groups: List<Group>? = null,
    var profiles: List<Profile>? = null,
)

data class CommentItem(
    var source_id: Int? = null,
    var id: Int? = null,
    var post_id: Int? = null,
    var from_id: Int? = null,
    var date: Int? = null,
    var text: String? = null,
)

//response after create comment
data class CommentaryResponse(
    var response: Commentary? = null
)

data class Commentary(
    var comment_id: Int? = null
)

