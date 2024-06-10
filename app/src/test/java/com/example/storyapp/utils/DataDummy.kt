package com.example.storyapp.utils

import com.example.storyapp.data.model.Story

object DataDummy {

    fun generateDummyListStory(): List<Story> {
        val items = arrayListOf<Story>()

        for (i in 0 until 10) {
            val story = Story(
                id = "story-FvU4u0Vp2S3PMsFg",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                name = "Dimas",
                description = "Lorem Ipsum",
                lon = -16.002,
                lat = -10.212
            )

            items.add(story)
        }

        return items
    }

}
