# Getting Things Done

## What Is It

Getting Things Done is yet another *TODO* application on the `Android` platform, made as a university course project.

The idea of this project is quite simple: an Android application that lets you create lists and add tasks to them
and mark those tasks as completed. Besides that, everything is saved in a remote server that lets you later
retrieve your lists and tasks by logging into your Getting Things Done account.

Even though the idea seems simple for this time and age, or even trivial, it is actually not. The difficulty comes
from implementing the requirements correctly, making the server and connection secure, authentic and rock-solid,
and handling all errors gracefully.

The project was made solely by me.

## Application (Client)

The Getting Things Done application is made for the Android platform using `Android Studio`. I chose to write
it in the `Java` programming language, because I'm not very familiar with the `Kotlin` programming language and
I didn't want to learn a new language in the very short amount of time I had for this project.

The application starts with the *Login* activity, where you can log into your account and then start using the
app, or create a new account by signing up. For an account you need an email and a password.

After logging in, the *Main* activity starts. Here you have a navigation bar at the bottom, which you can use to
switch between different sections of the *Main* activity using `fragments`. There are four different fragments:
*Lists*, *Motivational*, *Settings* and *List*. In the *Lists* fragment you can create and view lists of tasks.
Viewing a list take you into the *List* fragment where you may rename it, add tasks and mark them as completed/not
completed. In the *Motivational* fragment there is a motivational quote that changes every time you enter in that
section. These quotes are grabbed from the server. And lastly, the *Settings* fragment lets you delete lists and
tasks and log out of your account.

Overall this application looks okay, as I paid some attention to the colors I used and the padding and margin I
put between the views (widgets).

I made use of `TreeMap`s from Java to manage lists and tasks in memory. The first choice should always be to
use `arrays` or `contiguous data structures`, but this time I often needed to delete certain items from these
data structures (lists or tasks) and also refer to them at any point. This is the reason why in the end I used
sorted hash maps. Every list or task is assigned an integer index that increments from zero. Every list has its
own counter for its tasks and the application has a counter for all the lists.

## Server
