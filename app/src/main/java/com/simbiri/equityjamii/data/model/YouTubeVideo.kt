package com.simbiri.equityjamii.data.model

data class YouTubeResponse(val items: List<YouTubeItemList>)
data class YouTubeVideoResponse(val items: List<YouTubeItem>)
data class YouTubeItemList(val id: YouTubeVideoId, val snippet: YouTubeSnippet)
data class YouTubeItem(val id: String, val snippet: YouTubeSnippet)
data class YouTubeVideoId(val videoId: String?)
data class YouTubeSnippet(val title: String, val thumbnails: YouTubeThumbnails)
data class YouTubeThumbnails(val high: YouTubeThumbnail)
data class YouTubeThumbnail(val url: String)

