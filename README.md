# WebWork Brute Force

A multithread Selenium based Java application to help make math homework provided on WebWork a tab bit easier.

This project was the result of me getting more enjoyment from programming than math homework. 
Fortunately, most of the WebWork assignments I had allowed for unlimited attempts.

The Main class acts as a hub, which launches multiple instances of the "Brute Force Instance" inner class. Each instance
communicates with the hub constantly. While this creates somewhat of a "race condition", it doesn't cause an issue
as the variables in the hub are constantly being updated for the next instance that is ready. The algorithm is
designed to never repeat values. Works most effecitvely on questions that have a smaller range of potential answers.
The more of an idea you have on the range of the answers, the better off this works. Whenever a correct answer is found,
all instances focus their attention on the next box. Once all the required answers are found- the program automatically
submits all correct answers for you.


This program requires Selenium library with Java and currently is intended for use with Chromedriver.
Using FireFox would require changes to the browser launch code.


Config instructions:

url:
The link to the question you would like to run the program on.
At the time of this project, WebWork conveniently allowed for URL based authentication.
This eliminated the need for signing in or loading cookies.


totalBoxes:
This represents the total number of answer boxes on the question to attempt guessing on.
Once a correct answer is found for one box, it will move onto the next.


startBox and endBox:
Box counts start at 1 for this application. These options were added as a way to target one specific
box in circumstances where only a select few boxes were reasonably brute-forcible.

includeNegative:
Attempt negative numbers or only positives.
Useful for questions where you know the answer is always positive.

initialPrecision:
The "precision" level of guessing to start at. Normally, you would start with 1 (whole number incrementation).

increasePrecisionAt:
This option tells the program where to automatically start increasing the precision of it's guesses.
It is helpful to have a general idea of the range that answers might fall in. Each time the guess
value reaches this number, the precision is doubled (initial precision value is cut in half).
The algorithms are designed to never repeat answers.

instances:
The number of browser instances to run. Higher number of instances generally means faster results, however
going overboard may overload your computer and have a negative effect on performance. Select a number of instances
that works best for the particiular machine you are running this on.

showAttempts:
Whether or not you would like the numbers being attempted to be displayed as output to the console.

headless:
whether or not you want the browser instances to run in headless mode.
