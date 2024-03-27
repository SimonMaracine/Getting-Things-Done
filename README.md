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

The motivational quotes are stored on and received from the server. This allows more flexibility than quotes
stored locally in the application. The article link from which I took the quotes can be found in the source code.

## Server

The Getting Things Done server is written in the `Python` programming language and it is uses its own protocol
build on top of the `TCP` protocol to communicate to application clients. This time I chose Python, because
with it, it is easy to quickly write anything. But I acknowledge the fact that a lower level programming language
like `C++` or `Rust` is a better fit than Python. The lack of time was a big factor in my decisions.

This server listens for incoming connections and creates a new `thread` of execution to handle every client.
Using `async` functions instead of Python threads would have been better, but again, I didn't have enough time
to build a good and complete system.

The communication protocol is very simple. The header contains two bytes for the message type and another two
bytes for the payload size. And the payload consists of a `JSON` structure.

Here is one part of the bigger protocol, the sign up protocol:

    ClientSignUp
    ServerSignUpOk
    ServerSignUpFail

When the client sends a sign up message, the server checks to see if the email and the password are valid and
then sends either an *Ok* or a *Fail* response along with an error message.

This project is actually incomplete, as I didn't have enough time to implement all the basic requirements
of the original idea. Lists and tasks cannot be transmitted to the server from the client. But it could have
been easily done, if I had a few more days to work on this.

Clients' emails and passwords are stored in plain text, in a file from the server's file system. Passwords should
have been `cryptographically hashed` and `salted`, but because the communication is not secured by a layer like
`TLS`, it was not really worth securing the passwords.

## Gallery

![Log In Screen](/images/1.jpeg)
![Log In Failed](/images/2.jpeg)
![Lists Screen Empty](/images/3.jpeg)
![Lists Screen With Stuff](/images/4.jpeg)
![List With Tasks](/images/5.jpeg)
![List Just Created](/images/6.jpeg)
![Settings Screen 1](/images/7.jpeg)
![Settings Screen 2](/images/8.jpeg)
![Motivational Screen 1](/images/9.jpeg)
![Motivational Screen 2](/images/10.jpeg)
![Server](/images/11.png)

## Closing Thoughts

This project was a pretty good learning experience in my opinion, but still I wish that I had more time to
finish it and better implement the requirements and the network communication. This was my first big
Android project, but it was not the first time that I tinkered with network communication over the TCP
protocol. I'm looking forward one day to build a proper client-server communication in my personal projects.

