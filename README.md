# Project 2 - *SimpleTweet*

**SimpleTweet** is an android app that allows a user to view their Twitter timeline and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: **24** hours spent in total

## User Stories

The following **required** functionality is completed:

* [ ] User can **sign in to Twitter** using OAuth login
* [ ] User can **view tweets from their home timeline**
  * [ ] User is displayed the username, name, and body for each tweet
  * [ ] User is displayed the [relative timestamp](https://gist.github.com/nesquena/f786232f5ef72f6e10a7) for each tweet "8m", "7h"
* [ ] User can ***log out of the application** by tapping on a logout button
* [ ] User can **compose and post a new tweet**
  * [ ] User can click a “Compose” icon in the Action Bar on the top right
  * [ ] User can then enter a new tweet and post this to Twitter
  * [ ] User is taken back to home timeline with **new tweet visible** in timeline
  * [ ] Newly created tweet should be manually inserted into the timeline and not rely on a full refresh
* [ ] User can **see a counter that displays the total number of characters remaining for tweet** that also updates the count as the user types input on the Compose tweet page
* [ ] User can **pull down to refresh tweets timeline**
* [ ] User can **see embedded image media within a tweet** on list or detail view.

The following **optional** features are implemented:

* [ ] User is using **"Twitter branded" colors and styles**
* [ ] User can tap a tweet to **open a detailed tweet view**
  * [ ] User can **take favorite (and unfavorite) or retweet** actions on a tweet
* [ ] User can view more tweets as they scroll with infinite pagination

The following **additional** features are implemented:

* [ ] Moved Logout button to the menu
* [ ] User can see Verified Check if a user is verified
* [ ] When a user clicks favorite or retweet icons, the icons change color and their count is incremented
	* [ ] same for unliking/unretweeting


## Video Walkthrough

Here's a walkthrough of implemented user stories:
https://youtu.be/ivqqk8Cht58

## Notes

Describe any challenges encountered while building the app.

## Open-source libraries used

* [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
* [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android

## License

    Copyright 2022 Daniel Williams

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
